package com.skymiracle.server.tcpServer.mailServer.queue;

import com.skymiracle.logger.Logger;

/**
 *	对外网退信队列
 */
public class BounceToForeignQueue extends MailQueue {
	private String smtpRoutes = null;

	public void setSmtpRoutes(String smtpRoutes) {
		this.smtpRoutes = smtpRoutes;
	}

	public BounceToForeignQueue() throws Exception {
		super(MailQueue.NAME_BOUNCE_TO_FOREIGN);
	}

	@Override
	protected void deliver(String uuid, Object message) {
		MailMessage mm = (MailMessage) message;
		smtpClient
				.setFromEmail(mm.getFromUsername() + '@' + mm.getFromDomain());
		try {
			smtpClient.send(this.smtpRoutes, 25, mm.getToStr(), mm
					.getDataLineList());
			Logger.info(new StringBuffer(this.name).append(": Sent, to=<")
					.append(mm.getToStr()).append(">"));
		} catch (Exception e) {
			// try {
			// this.mailQueueManager.putInBounceQueue(mm, e.getMessage());
			// Logger.info(new StringBuffer(this.name).append(
			// ": Sent failed, to=<").append(mm.getToStr()).append(
			// ">, reason=[").append(e.getMessage()).append("]"));
			// } catch (Exception e1) {
			// Logger.info("BounceToNativeForeignQueue failed.", e1);
			// }
		}
	}

}
