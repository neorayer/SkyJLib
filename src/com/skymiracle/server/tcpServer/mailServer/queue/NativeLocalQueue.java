package com.skymiracle.server.tcpServer.mailServer.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.skymiracle.auth.MailUser;
import com.skymiracle.filter.keywords.ContentFilter;
import com.skymiracle.filter.keywords.KeyWordsCache;
import com.skymiracle.logger.Logger;
import com.skymiracle.mime.RichMailAddress;
import com.skymiracle.queue.Queue;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;
import com.skymiracle.server.tcpServer.mailServer.AutoMailMaker;
import com.skymiracle.util.Base64;
import com.skymiracle.util.Rfc2047Codec;
import com.skymiracle.util.UUID;

/**
 * 本地发送队列
 */
public class NativeLocalQueue extends MailQueue implements Queue {

	public NativeLocalQueue() throws Exception {
		super(MailQueue.NAME_NATIVE_LOCAL);
	}

	public NativeLocalQueue(String queueName) throws Exception {
		super(queueName);
	}

	/**
	 * NativeLocal message deliver create the receiver's mailbox
	 * instance,receive message
	 * 
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deliver(String uuid, Object message) {
		// TODO miss missionUUID track, see also NativeRemoteQueue
		MailMessage mm = (MailMessage) message;
		debug("begin send message to : " + mm.getToStr());

		// content scan

		if (KeyWordsCache.getInstance().isOpen()) {
			try {
				Class filterClass = Class.forName(KeyWordsCache.getInstance()
						.getFilterClassName());

				ContentFilter contentFilter = (ContentFilter) (filterClass
						.newInstance());
				boolean res = contentFilter.contentscan(mm);

				if (!res) {
					Logger
							.debug(uuid + ": delete "
									+ contentFilter.getReason());
					return;
				}
			} catch (Exception e) {
				Logger.info("content filter in NativeLocalQueue", e);
			}
		}

		List<String> srcHeader = AutoMailMaker.getMailHeaderList(mm.getDataLineList());
		HashMap<String, String> srcHeaderMap = AutoMailMaker.getMailHeaderElementMap(srcHeader);
		StringBuffer rejectSb = new StringBuffer();

		String[] tos = mm.getToStr().split("\\|");
		for (int i = 0; i < tos.length; i++) {
			if (tos[i].length() == 0)
				continue;
			String[] ss = tos[i].split("@");
			if (ss.length != 2)
				continue;

			long begin = System.currentTimeMillis();
			String toUsername = ss[0];
			String toDomain = ss[1];

			try {
				MailUser toMailUser = getMailQueueManager().getSmtpServer()
						.getAuthMail().getMailUser(toUsername, toDomain);
				// HEAD FILTER
				if (srcHeaderMap.get("from") != null) {
					RichMailAddress rFrom = new RichMailAddress(srcHeaderMap
							.get("from"));
					// 检验发信人所在域是否在收信人的黑名单中
					int pos = -1;
					if ((pos = rFrom.getMailAddress().indexOf("@")) != -1) {
						if (toMailUser.isRejectDomain(rFrom.getMailAddress()
								.substring(pos + 1))) {
							Logger.debug("HEAD filter.domain filter " + tos[i]);
							rejectSb.append(tos[i]).append("|");
							continue;
						}
					}
					// 检验发信人邮箱是否在收信人的黑名单中
					if (toMailUser.isRejectEmail(rFrom.getMailAddress())) {
						Logger.debug("HEAD filter.email filter " + tos[i]);
						rejectSb.append(tos[i]).append("|");
						continue;
					}
				}
				// 检验邮件标题是否在收信人的拒绝词典中
				if (srcHeaderMap.get("subject") != null) {
					if (toMailUser.isRejectSubject(Rfc2047Codec
							.decode(srcHeaderMap.get("subject")))) {
						Logger.debug("HEAD filter.subject filter " + tos[i]);
						rejectSb.append(tos[i]).append("|");
						continue;
					}
				}

				// stor mail message
				IMailAccessor usma = getMailQueueManager().getSmtpServer()
						.getUsMailAccessor(toUsername, toDomain,
								mm.getTargetLocation());
				usma.mailStor("inbox", uuid, mm, true);

				// space alert
				long spaceAlert = toMailUser.getSpaceAlert();
				if (spaceAlert > 0) {
					long storageSizeUsed = usma.mailGetStorageSizeUsed();
					long mm_size = mm.getSize();
					if (mm_size + storageSizeUsed > spaceAlert) {
						usma.mailCreateAlertMail(
								mm.getSize() + storageSizeUsed, spaceAlert);
					}
				}

				long end = System.currentTimeMillis();
				StringBuffer logSB = new StringBuffer(this.name).append(
						": Delivered, to=<").append(toUsername).append('@')
						.append(toDomain).append("> spend=")
						.append(end - begin).append("(ms)");
				Logger.info(logSB.toString());

				// // TODO : ANALYZED
				// if (this.mailQueueManager.getSmtpServer().isNeedLogAnaly()) {
				//
				// StringBuffer analySB = new StringBuffer(this.name).append(
				// "[").append(uuid).append("]").append(" from=")
				// .append(mm.getFromUsername()).append("@").append(
				// mm.getFromDomain()).append(",to=").append(
				// tos[i]).append(",").append(mm.getSize())
				// .append(",S,").append("OK");
				// }
				// SMS NOTIFY
				// TODO
				// if (toMailUser.isOpenNotify()) {
				// if (attributeOfMail.needNotify(hm)) {
				// ArrayList smsList = AutoSMSMaker.createNotifySMS(
				// attributeOfMail.getUser(), attributeOfMail
				// .getDomain(), hm);
				// // TODO
				// SmsClientThread sct = new SmsClientThread("",
				// attributeOfMail.getNotifynum(), smsList);
				// sct.start();
				// }
				// }
				// -->
				if (toMailUser.isAutoreply()) {
					// if (srcHeader == null)
					// srcHeader = AutoMailMaker.getMailHeaderList(mm
					// .getDataLineList());
					List<String> newReplyHeader = AutoMailMaker.getReplyHeader(
							srcHeaderMap, mm.getFromUsername() + "@"
									+ mm.getFromDomain(), toUsername + "@"
									+ toDomain);
					if (newReplyHeader != null) {
						try {
							newReplyHeader.add("\r\n");
							newReplyHeader.add("\r\n");
							// TODO
							// newReplyHeader.add(replyMailUser.getReplyMail());
							newReplyHeader
									.add(Base64.encodeToString(toMailUser.getAutoreplycontent(),"UTF-8"));

							MailUser replyMailUser = getMailQueueManager()
									.getMailUser(mm.getFromUsername(),
											mm.getFromDomain());

							ArrayList<MailUser> replyMailUserList = new ArrayList<MailUser>();
							replyMailUserList.add(replyMailUser);

							this.mailQueueManager.putMailtoQueue(toMailUser
									.getUid(), toMailUser.getDc(),
									replyMailUserList, new ArrayList(
											newReplyHeader), new UUID()
											.toShortString());
						} catch (Exception e) {
							Logger.info("Bounce faile" + "d.", e);
							this.saveLog(mm, "RELAY", "F", "RELAY failed.");
						}
					}
				}
				// <--

				// sendmaillog
				this.saveLog(mm, "RELAY", "S", "RELAY OK");

			} catch (Exception e) {
				try {

					StringBuffer resSb = new StringBuffer(this.name).append(
							": RELAY failed, to=<").append(toUsername).append(
							'@').append(toDomain).append(">");
					Logger.info(resSb, e);
					this.mailQueueManager.putInBounceQueue(mm, e.getMessage());
				} catch (Exception e1) {
					Logger.info("Bounce faile" + "d.", e1);
				}

				this.saveLog(mm, "RELAY", "F", "RELAY failed.");
			}
		}
		mm = null;
	}
}
