package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.Map;

import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;
import com.skymiracle.mime.RichMailAddress;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.util.Checker;
import com.skymiracle.util.UsernameWithDomain;
import com.skymiracle.dns.RBLChecker;

/**
 * 标识单个的邮件接收人；常在MAIL命令后面可有多个rcpt to: <xxx>
 */
public class RcptCommander extends SmtpAbsCommander {

	public RcptCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	/**
	 * 
	 * @return null if RBL check pass. Reason if RBL check no pass.
	 * 
	 */
	private String rblCheck() {
		if (shStatus.isSmtpTrustIP())
			return null;
		if (!shStatus.getFromMailUser().getStorageLocation()
				.equals(Authable.LOCATION_FOREIGN))
			return null;
		RBLChecker rblChecker = getSmtpServer().getRblChecker();
		return rblChecker.check(getRemoteIP());
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		// 基本语法检查——冒号
		int pos = tail.indexOf(':');
		if (pos < 0)
			return null;

		// 基本语法检查——RCPT后面的 to
		String head2 = tail.substring(0, pos).toLowerCase().trim();
		if (!head2.equals("to"))
			return null;

		// SMTP对话协议顺序检查 Sequence Check
		if (shStatus.getCmdpos() != SmtpHandleStatus.CMDPOS_AFTER_MAIL
				&& shStatus.getCmdpos() != SmtpHandleStatus.CMDPOS_AFTER_RCPT)
			return getBytesCRLF(returnCode(503));

		// 解析出RCPT TO的目标地址
		String rcptTo = tail.substring(pos + 1).trim();

		// RCPT TO的目标地址语法检查 Syntax Check
		if (!Checker.isGoodEmailAddress(rcptTo))
			return getBytesCRLF(returnCode(553));

		// 将字符串类型的RCPT TO目标转化为UsernameWithDomain格式，同时补充缺省域
		RichMailAddress rma = new RichMailAddress(rcptTo);
		UsernameWithDomain uwd = new UsernameWithDomain(rma.getMailAddress(),
				getSmtpServer().getDefaultDomain());

		// 获得一个目标地址的MailUser对象
		MailUser toMailUser = getSmtpServer().getMailUser(uwd.getUsername(),
				uwd.getDomain());
		
		byte[] goodReturn = getBytesCRLF(returnCode(250, toMailUser.getUid() + '@'
				+ toMailUser.getDc()));
		
		// according to the mail user's person_addressbook to filter the mail
		// 如果打开了MailUser.addrbookfilter开关，则进行严格的收件人限制，即只接收地址部中存在的收件人
		// TODO:我(zhourui)认为此处的实现方式需要商榷。
		if (toMailUser.getAddrbookfilter() == MailUser.ADDRBOOK_FILTER_YES) {
			if (!toMailUser.getDc().equals(shStatus.getFromMailUser().getDc())) {
				AddrFilter addrbookFilter = getSmtpServer().getAddrBookFilter();
				Map<String, String> addrFilterMap = addrbookFilter
						.getFilterMap(toMailUser.getUid(), toMailUser.getDc());
				if (addrFilterMap.size() > 0) {
					String fromaddr = shStatus.getFromMailUser().getUid() + "@"
							+ shStatus.getFromMailUser().getDc();
					if (addrFilterMap.get(fromaddr) == null)
						return getBytesCRLF(returnCode(550));
				}
			}
		}

		// Reject rcptTo user location EXCEPTION
		if (toMailUser.getStorageLocation().equals(Authable.LOCATION_EXCEPTION))
			return getBytesCRLF(returnCode(550,
					"Invalid User: " + toMailUser.toEmail()));

		MailUser fromMailUser = shStatus.getFromMailUser();

		// Check - fromMailUser maxCC
		if (!fromMailUser.getStorageLocation()
				.equals(Authable.LOCATION_FOREIGN)) {
			// getSmtpServer().debug("from user: " + fromMailUser.toEmail());
			// getSmtpServer().debug("current rcpt size: " +
			// shStatus.getRcptToList().size());
			// getSmtpServer().debug("from user maxcc: " +
			// fromMailUser.getMaxcc());

			if (shStatus.getRcptToList().size() > fromMailUser.getMaxcc())
				return getBytesCRLF(returnCode(554,
						"Rcpt to count exceed user Maxcc."));
		}

		if (toMailUser.getStorageLocation().equals(Authable.LOCATION_FOREIGN)) {
			// Location FOREIGN
			if (fromMailUser.getStorageLocation().equals(
					Authable.LOCATION_FOREIGN))
				return getBytesCRLF(returnCode(554,
						"Smtp server does not open replay."));
		} else if (toMailUser.getStorageLocation().equals(Authable.LOCATION_GROUP)) {
			// Location GROUP
			// 只有本地用户可发往本地组用户
			if (fromMailUser.getStorageLocation().equals(
					Authable.LOCATION_FOREIGN))
				return getBytesCRLF(returnCode(554,
						"Sender must be local user if target is GroupUser."));
		}else{
			// Location NATIVE
			
			// check use status, if to local user
			if (!MailUser.STATUS_OPEN.equals(toMailUser.getStatus())) {
				return getBytesCRLF(returnCode(554,
						"Status of <" + uwd.toEmail() + "> is NOT open."));
			}

			// Reject - if domain rejected
			if (toMailUser.isRejectDomain(fromMailUser.getDc()))
				return getBytesCRLF(returnCode(554,
						"Receiver rejected the from domain."));
			// Reject - if email rejected
			if (toMailUser.isRejectEmail(fromMailUser.getUid() + '@'
					+ fromMailUser.getDc()))
				return getBytesCRLF(returnCode(554,
						"Receiver rejected the from email."));
			// Reject - if mailbox has been full
			if (getSmtpServer().isMailBoxFull(toMailUser))
				return getBytesCRLF(returnCode(554,
						"Mailbox of receiver is full."));
		}

		shStatus.addRcptTo(toMailUser);
		shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_RCPT);

		return goodReturn;

	}
}
