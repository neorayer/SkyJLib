package com.skymiracle.server.tcpServer.mailServer.queue;

import com.skymiracle.auth.MailUser;
import com.skymiracle.logger.Logger;
import com.skymiracle.queue.Queue;

/**
 * 本地退信队列
 */
public class BounceToNativeQueue extends MailQueue implements Queue {

	public BounceToNativeQueue() throws Exception {
		super(MailQueue.NAME_BOUNCE_TO_NATIVE);
	}

	@Override
	protected void deliver(String uuid, Object message) {
		MailMessage mm = (MailMessage) message;
		String[] tos = mm.getToStr().split("\\|");
		for (int i = 0; i < tos.length; i++) {
			if (tos[i].length() == 0)
				continue;
			String[] ss = tos[i].split("@");
			if (ss.length != 2)
				continue;
			String toUsername = ss[0];
			String toDomain = ss[1];

			try {
				MailUser mailUser = getMailQueueManager().getMailUser(
						toUsername, toDomain);
				getMailQueueManager().getSmtpServer().getUsMailAccessor(
						mailUser.getUid(), mailUser.getDc(),
						mailUser.getStorageLocation()).mailStor("inbox", uuid,
						mm, true);

				Logger.info(new StringBuffer(this.name).append(
						": Delivered, to=<").append(toUsername).append('@')
						.append(toDomain).append(">"));
			} catch (Exception e) {
				// TODO Bounce
				Logger.warn(new StringBuffer(this.name).append(
						": Deliver failed, to=<").append(toUsername)
						.append('@').append(toDomain).append(">"), e);
			}
		}
	}
}
