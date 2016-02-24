package com.skymiracle.server.tcpServer.mailServer.queue;

import com.skymiracle.client.tcpClient.smtpClient.SmtpClient;
import com.skymiracle.logger.Logger;

/**
 *	定时外网发送队列 
 */
public class ForeignDelayQueue extends MailDelayQueue {

	private String smtpRoutes = null;

	public void setSmtpRoutes(String smtpRoutes) {
		this.smtpRoutes = smtpRoutes;
	}

	public ForeignDelayQueue(int level, long delay, String queuePath)
			throws Exception {
		super(level, delay, queuePath, MailQueue.NAME_FOREIGN_DELAY_PRE + level);
	}

	private void bounce(MailMessage mm, Exception e) {
		try {
			Logger.info(new StringBuffer(this.name).append(
					": Sent failed, to=<").append(mm.getToStr()).append(
					">, reason=[").append(e.getMessage()).append("]"));
			this.mailQueueManager.putInBounceQueue(mm, e.getMessage());
		} catch (Exception e1) {
			Logger.info("Bounce failed.", e1);
		}
	}

	/**
	 * foreign message deliver use smtp client deliver the message
	 * 
	 * @param message
	 */
	@Override
	public void deliver(String uuid, Object message) {
		MailMessage mm = (MailMessage) message;
		smtpClient
				.setFromEmail(mm.getFromUsername() + '@' + mm.getFromDomain());
		try {
			smtpClient.send(this.smtpRoutes, 25, mm.getToStr(), mm
					.getDataLineList());
		} catch (Exception e) {
			String returnCode = e.getMessage();
			// sent failed -need not retry
			if (!returnCode.startsWith("4")) {
				// TODO : ANALYZED
				this.logFail(uuid, mm, e);
				bounce(mm, e);
				return;
			}
			// Delay To Next DelayQueue
			try {
				boolean res = this.delay(mm, e);
				if (!res) {
					bounce(mm, e);
					this.logFail(uuid, mm, e);
					return;
				}
			} catch (Exception e2) {
				bounce(mm, e);
				this.logFail(uuid, mm, e);
				return;
			}
			return;
		}
		StringBuffer resSb = new StringBuffer(this.name).append(": Sent, to=<")
				.append(mm.getToStr()).append(">");
		Logger.info(resSb);

	}

	private boolean delay(MailMessage mm, Exception e) throws Exception {
		try {
			Logger.info(new StringBuffer(this.name).append(": Delay, to=<")
					.append(mm.getToStr()).append(">, reason=[").append(
							e.getMessage()).append("]"));
			return this.mailQueueManager.addNextForeignDelayQueue(mm);
		} catch (Exception e1) {
			Logger.info("Delay failed.", e1);
			throw e1;
		}

	}

	private void logFail(String uuid, MailMessage mm, Exception e) {
		StringBuffer resSb = new StringBuffer(this.name).append(
				": Send failed, to=<").append(mm.getToStr()).append(">");
		Logger.info(resSb, e);
	}
}
