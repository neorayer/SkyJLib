package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.util.List;

import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;
import com.skymiracle.dns.RBLChecker;
import com.skymiracle.mime.RichMailAddress;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.util.Checker;
import com.skymiracle.util.UUID;
import com.skymiracle.util.UsernameWithDomain;

/**
 * 初始化邮件传输mail from
 */
public class MailCommander extends SmtpAbsCommander {

	public MailCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		int pos = tail.indexOf(':');
		if (pos < 0)
			return null;

		String head2 = tail.substring(0, pos).toLowerCase().trim();
		if (!head2.equals("from"))
			return null;

		// Sequence Check
		// no helo/ehlo
		if (shStatus.getCmdpos() != SmtpHandleStatus.CMDPOS_AFTER_HELO
				&& shStatus.getCmdpos() != SmtpHandleStatus.CMDPOS_AFTER_EHLO
				&& shStatus.getHeloType() == SmtpHandleStatus.HELLO_TYPE_NO)
			return getBytesCRLF(returnCode(503));
		if (shStatus.getCmdpos() == SmtpHandleStatus.CMDPOS_AFTER_DATADOT)
			shStatus.reset();
		else if (shStatus.getCmdpos() >= SmtpHandleStatus.CMDPOS_AFTER_MAIL)
			return getBytesCRLF(returnCode(503));

		String mailFrom = tail.substring(pos + 1).trim();
		// Syntax Check
		if (!Checker.isGoodEmailAddress(mailFrom))
			return getBytesCRLF(returnCode(553));

		// deal with mail from: <>
		boolean isEmptyFrom = false;
		if (mailFrom.equals("")) {
			mailFrom = "@";
			isEmptyFrom = true;
		}

		SmtpServer smtpServer = getSmtpServer();

		// prepare fromMailUser
		RichMailAddress rma = new RichMailAddress(mailFrom);
		UsernameWithDomain uwd = new UsernameWithDomain(rma.getMailAddress(),
				smtpServer.getDefaultDomain());
		MailUser fromMailUser = smtpServer.getMailUser(uwd.getUsername(),
				uwd.getDomain());
		mailFrom = uwd.getUsername() + "@" + uwd.getDomain();
		shStatus.setFromMailUser(fromMailUser);

		// 检查auth的用户是否匹配mail from
		if (shStatus.isAuthPass()) {
			MailUser authUser = shStatus.getAuthMailUser();
			if (authUser == null)
				return getBytesCRLF(returnCode(554, "Auth User cann't be null."));
			String authUserEmail = authUser.toEmail();
			if (!authUserEmail.equalsIgnoreCase(mailFrom)) {
				String s = "Auth User <" + authUserEmail
						+ "> is NOT same with MAIL FROM <" + mailFrom + ">";
				getSmtpServer().info(s);
				return getBytesCRLF(returnCode(554, s));
			}
		}

		// 当前IP是否在白名单中
		boolean isTrustIP = getSmtpServer().isSmtpTrustIP(getRemoteIP())
				|| getSmtpServer().isSmtpTrustIpCnet(getRemoteIP());

		// Reject - fromLocation EXCEPTION
		// 本域用户不存在，且不在白名单中
		// 对于白名单的IP， 不管mail from 信息是否错误
		if (fromMailUser.getStorageLocation().equals(
				Authable.LOCATION_EXCEPTION)
				&& !isTrustIP)
			return getBytesCRLF(returnCode(550));

		// Reject - fromLocation NATIVE and no authPass
		// 本域用户， 如果没有验证， 且不在白名单中
		// 在本域中， 对于白名单的IP， 不管是否验证（即在白名单IP上, 本域用户不需要验证）
		if (!fromMailUser.getStorageLocation()
				.equals(Authable.LOCATION_FOREIGN)
				&& !shStatus.isAuthPass()
				&& !(isTrustIP))
			return getBytesCRLF(returnCode(554, "Local user need auth."));

		// 邮件服务器强制验证时， 如果不在白名单中
		if (this.getSmtpServer().getForceAuth()) {
			if (!shStatus.isAuthPass()
					&& !(getSmtpServer().isSmtpTrustIP(getRemoteIP()) || getSmtpServer()
							.isSmtpTrustIpCnet(getRemoteIP())))
				return getBytesCRLF(returnCode(554,
						"The Server is setted ForceAuth"));
		}

		if (fromMailUser.getStorageLocation().equals(Authable.LOCATION_FOREIGN)) {
			// Reject - fromLocation FOREIGN AND authPass
			if (shStatus.isAuthPass())
				return getBytesCRLF(returnCode(554,
						"only native user can auth."));

			// Reject - fromLocation FOREIGN AND RBL Listed
			if (getSmtpServer().isRblCheck()) {
				RBLChecker rblChecker = getSmtpServer().getRblChecker();
				String s = rblChecker.check(getRemoteIP());
				if (s != null)
					return getBytesCRLF(returnCode(554, s));
			}
		}

		// MailCmdFilters
		List<SmtpCommanderFilter> mailCmdFilters = getSmtpServer()
				.getMailCmdFilters();
		for (SmtpCommanderFilter filter : mailCmdFilters) {
			String res = filter.doFilter(this.connHandler, shStatus);
			if (res != null)
				return getBytesCRLF(res);
		}

		String missionUUID = new UUID().toShortString();
		shStatus.setMissionUUID(missionUUID);
		shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_MAIL);
		return getBytesCRLF(returnCode(250));

	}
}
