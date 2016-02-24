package com.skymiracle.server.tcpServer.mailServer.queue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.fax.FaxConfig;
import com.skymiracle.fax.FaxIOClient;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.mime.Attachment;
import com.skymiracle.mime.Mime;
import com.skymiracle.mime.MimeImpl;
import com.skymiracle.queue.Queue;

public class FaxQueue extends MailQueue implements Queue {

	private FaxConfig faxConfig;

	private String cachedRootPath = "/wpx/faxcache/";

	public FaxQueue() throws Exception {
		super(MailQueue.NAME_FAX);
	}

	@Override
	protected void deliver(String uuid, Object message) {
		MailMessage mm = (MailMessage) message;
		String sender = mm.getFromUsername() + "@" + mm.getFromDomain();
		debug("begin send fax to ufax from: " + sender);

		String cachedPath = cachedRootPath + uuid;
		File dir = new File(cachedPath);
		dir.mkdirs();

		try {
			String datafilepath = cachedPath + "/message";
			TextFile.save(datafilepath, mm.getDataLineList());
			new MimeImpl(datafilepath, true).save(cachedPath);

			Mime mime = new MimeImpl(cachedPath);
			String subject = mime.getSubject();
			String[] faxnumbers = subject.split(",");
			List<String> faxnumberlist = new LinkedList<String>();
			for (String faxnumber : faxnumbers) {
				try {
					Long.parseLong(faxnumber);
					faxnumberlist.add(faxnumber);
				} catch (Exception e) {
					continue;
				}
			}
			faxnumbers = faxnumberlist.toArray(new String[0]);
			if (faxnumbers.length > 0) {
				List<Attachment> attachments = mime.getDownableAttachments();
				for (Attachment att : attachments) {
					File file = new File(att.getFilePath());
					if (!file.exists())
						continue;
					String filename = att.getFileName();
					File realFile = new File(file.getParent() + "/fax"
							+ filename.substring(filename.lastIndexOf(".")));
					file.renameTo(realFile);
					FaxIOClient faxIOClient = new FaxIOClient(faxConfig
							.getFaxServerHost(), faxConfig.getFaxServerPort());
					faxIOClient.doOutFax(realFile, sender, faxnumbers);
				}
			}
		} catch (Exception e) {
			bounce(mm, e.getMessage());
		}
		dir.delete();
	}

	private void bounce(MailMessage mm, String msg) {
		try {
			saveLog(mm, "DELIVER", "F", msg);
			this.mailQueueManager.putInBounceQueue(mm, msg);

		} catch (Exception e1) {
			Logger.info("Bounce failed.", e1);
		}
	}

	public FaxConfig getFaxConfig() {
		return faxConfig;
	}

	public void setFaxConfig(FaxConfig faxConfig) {
		this.faxConfig = faxConfig;
	}

}
