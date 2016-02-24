package com.skymiracle.filter.keywords;

import com.skymiracle.server.tcpServer.mailServer.queue.MailMessage;

public abstract class ContentFilter {

	protected String reason;

	public ContentFilter() {

	}

	public abstract boolean contentscan(MailMessage mm);

	public abstract String getReason();
}
