package com.skymiracle.server.tcpServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;

public class SimpleFileIpFilterListener extends IpFilterListener {

	private String allowIpFilePath;

	private String rejectIpFilePath;

	private long allowIpFileLastModified;

	private long rejectIpFileLastModified;

	public Event checkEvent() {
		int changeType = ChangedEvent.CHANGE_TYPE_NONE;
		ChangedEvent changeEvent = new ChangedEvent();
		long curModified = new File(allowIpFilePath).lastModified();
		if (curModified != allowIpFileLastModified) {
			allowIpFileLastModified = curModified;
			changeEvent.setAllowMap(getIpMap(allowIpFilePath));
			changeType = ChangedEvent.CHANGE_TYPE_ALLOW;
		}

		curModified = new File(rejectIpFilePath).lastModified();
		if (curModified != rejectIpFileLastModified) {
			rejectIpFileLastModified = curModified;
			changeEvent.setAllowMap(getIpMap(rejectIpFilePath));

			if (changeType == ChangedEvent.CHANGE_TYPE_ALLOW)
				changeType = ChangedEvent.CHANGE_TYPE_ALL;
			changeType = ChangedEvent.CHANGE_TYPE_REJECT;
		}

		if (changeType == ChangedEvent.CHANGE_TYPE_NONE)
			return null;
		return changeEvent;
	}

	private Map<String, String> getIpMap(String filePath) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] lines = TextFile.loadLines(filePath);
			for (String line : lines)
				map.put(line, line);
		} catch (IOException e) {
			Logger.error("", e);
		}
		return map;
	}

	public String getAllowIpFilePath() {
		return allowIpFilePath;
	}

	public void setAllowIpFilePath(String allowIpFilePath) {
		this.allowIpFilePath = allowIpFilePath;
	}

	public String getRejectIpFilePath() {
		return rejectIpFilePath;
	}

	public void setRejectIpFilePath(String rejectIpFilePath) {
		this.rejectIpFilePath = rejectIpFilePath;
	}

	public void initStart() {
		// Do Nothing
	}

}
