package com.skymiracle.smartUpgrade;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.skymiracle.io.Dir;
import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.DaoService;
import com.skymiracle.mdo4.XmlDaoStorage;
import com.skymiracle.util.CalendarUtil;

public class SmartUpgrade {

	private String upgradeFeedURL;

	private String upgradeFeedLocalPath;

	private String tmpPath;

	private boolean scheduled;

	private String time;

	private long interval;

	private UpgradeFeed oldFeed;

	private UpgradeFeed newFeed;

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getInterval() {
		return this.interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public boolean getScheduled() {
		return this.scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public String getTmpPath() {
		return this.tmpPath;
	}

	public void setTmpPath(String tmpPath) {
		this.tmpPath = tmpPath;
	}

	public String getUpgradeFeedLocalPath() {
		return this.upgradeFeedLocalPath;
	}

	public void setUpgradeFeedLocalPath(String upgradeFeedLocalPath) {
		this.upgradeFeedLocalPath = upgradeFeedLocalPath;
	}

	public String getUpgradeFeedURL() {
		return this.upgradeFeedURL;
	}

	public void setUpgradeFeedURL(String upgradeFeedURL) {
		this.upgradeFeedURL = upgradeFeedURL;
	}

	public void start() throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String today = CalendarUtil.getLocalDateTime("yyyy-MM-dd");
		Date scheduleTime = simpleDateFormat.parse(today + " " + this.time);
		if (this.scheduled) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						myStart();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, scheduleTime, this.interval);
		} else
			myStart();
	}

	private void myStart() throws Exception {
		Logger.info("Begin to remote upgrade...");

		new File(this.tmpPath).mkdirs();
		String tmpFeedPath = this.tmpPath + "/upgradefeed.xml";
		File file = new File(tmpFeedPath);
		if (file.exists())
			file.delete();

		Logger.info("Downloading feed " + this.upgradeFeedURL);
		StreamPipe.urlToFile(this.upgradeFeedURL, tmpFeedPath);
		DaoService newFeedService = new DaoService();
		newFeedService
				.setDaoStorage(new XmlDaoStorage(tmpFeedPath, "//upgrade"));
		this.newFeed = (UpgradeFeed) newFeedService.getDaos(UpgradeFeed.class)[0];

		DaoService oldFeedService = new DaoService();
		oldFeedService.setDaoStorage(new XmlDaoStorage(
				this.upgradeFeedLocalPath, "//upgrade"));
		if (oldFeedService.getDaos(UpgradeFeed.class).length > 0)
			this.oldFeed = (UpgradeFeed) oldFeedService
					.getDaos(UpgradeFeed.class)[0];
		else
			this.oldFeed = null;

		doUpdate();
	}

	private boolean needUpgrade() throws Exception {
		Logger.info("Begin to compare new and old versions.");
		if (null == this.oldFeed)
			return true;

		double newVersion = this.newFeed.getVersion();

		double oldVersion = this.oldFeed.getVersion();

		return newVersion > oldVersion;
	}

	private void updateFeedFile() throws Exception {
		File file = new File(this.upgradeFeedLocalPath);
		file.delete();
		StreamPipe.fileToFile(this.tmpPath + "upgradefeed.xml",
				this.upgradeFeedLocalPath);
	}

	private void downloadAllFile() throws Exception {
		Logger.info("Downloading all upgrade files.");
		File dir = new File(this.tmpPath);
		if (!dir.exists())
			dir.mkdirs();
		String[] fileURLs = this.newFeed.getFileUrl().split(",");
		for (String fileURL : fileURLs) {
			String filePath = this.tmpPath + new File(fileURL).getName();

			Logger.info("Downloading upgrade file " + fileURL);
			StreamPipe.urlToFile(fileURL, filePath);
		}
	}

	private void clearFiles() throws Exception {
		Logger.info("Clear temp upgrade files.");
		Dir dir = new Dir(this.tmpPath);
		dir.empty();
	}

	private void doUpdate() throws Exception {
		if (!needUpgrade()) {
			return;
		}
		downloadAllFile();

		String backuperName = this.newFeed.getBackuper();
		if ((null != backuperName) && (backuperName.length() > 0)) {
			Backuper backuper = (Backuper) Class.forName(backuperName)
					.newInstance();
			Logger.info("Backuping ....");
			backuper.start();

			// Not very good here
			// waiting backup finished
			Thread.sleep(10000);
		}
		try {
			String installerName = this.newFeed.getInstaller();
			if ((null != installerName) && (installerName.length() > 0)) {
				Installer installer = (Installer) Class.forName(installerName)
						.newInstance();
				Logger.info("Installing ....");
				installer.start(this.newFeed.getFileList(), this.tmpPath);
			}
		} catch (Exception e) {
			Logger.error("", e);
			Logger.info("Install failed, Restore ....");
			String recoverName = this.newFeed.getRecover();
			if ((null != recoverName) && (recoverName.length() > 0)) {
				Recover recover = (Recover) Class.forName(recoverName)
						.newInstance();
				recover.start();
				clearFiles();
				return;
			}
		}
		String killerName = this.newFeed.getKiller();
		if ((null != killerName) && (killerName.length() > 0)) {
			Killer killer = (Killer) Class.forName(killerName).newInstance();
			Logger.info("Install OK! Killing running time.");
			killer.start();
		}
		String starterName = this.newFeed.getStarter();
		if ((null != starterName) && (starterName.length() > 0)) {
			Starter starter = (Starter) Class.forName(starterName)
					.newInstance();
			Logger.info("Start new version!!! :)");
			starter.start();
		}
		updateFeedFile();
		clearFiles();
	}
}
