package com.skymiracle.server.tcpServer.mailServer.Pop3;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.auth.MailUser;
import com.skymiracle.fileBox.MailBoxLsItem;
import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdServer.Commander;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessor;

public abstract class Pop3AbsCommander extends Commander {

	protected Pop3HandleStatus phStatus;

	public Pop3AbsCommander(CmdConnHandler connHandler) {
		super(connHandler);
		this.phStatus = getHandleStatus();
	}

	protected Pop3Server getPop3Server() {
		return (Pop3Server) getCmdServer();
	}

	private Pop3HandleStatus getHandleStatus() {
		Pop3HandleStatus hs = (Pop3HandleStatus) this.connHandler
				.getHandleStatus();
		if (hs == null) {
			hs = new Pop3HandleStatus(getPop3Server());
		}
		this.connHandler.setHandleStatus(hs);
		return hs;
	}

	public class Pop3HandleStatus {

		private String username;

		private String password;

		private boolean isLogin;

		private Pop3Server pop3Server;

		public final static int STEP_INIT = 10;
		public final static int STEP_USER = 20;
		public final static int STEP_PASS = 30;

		private int step = STEP_INIT;

		private MailUser mailUser;

		private Mail[] mails;

		private IMailAccessor usMailAccessor;

		public Pop3HandleStatus(Pop3Server pop3Server) {
			this.pop3Server = pop3Server;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public boolean isLogin() {
			return isLogin;
		}

		public void setLogin(boolean isLogin) {
			this.isLogin = isLogin;
		}

		public int getStep() {
			return step;
		}

		public void setStep(int step) {
			this.step = step;
		}

		public MailUser getMailUser() {
			return mailUser;
		}

		public void setMailUser(MailUser mailUser) throws Exception {
			this.mailUser = mailUser;

			this.usMailAccessor = pop3Server.getUsMailAccessor(mailUser
					.getUid(), mailUser.getDc(), mailUser.getStorageLocation());

			List<MailBoxLsItem> items = this.usMailAccessor.mailLsMail("inbox");
			this.mails = new Mail[items.size()];
			int i = 0;
			for (MailBoxLsItem item : items) {
				Mail mail = new Mail();
				mail.setUuid(item.getUuid());
				mail.setDeleted(false);
				mail.setSize(item.getSize());
				mails[i++] = mail;
			}
		}

		public Mail[] getMails() {
			return mails;
		}

		public StringBuffer getMailHeader(String id) {
			StringBuffer sb = new StringBuffer();
			try {
				String filePath = this.usMailAccessor.mailRetr("inbox", id);
				sb = TextFile.loadMailHeaderStringBuffer(filePath, "\r\n");
			} catch (Exception e) {
				sb = new StringBuffer();
				Logger.error("", e);
			}
			return sb;

		}

		public StringBuffer getMailBody(String id, int i) {
			StringBuffer sb = new StringBuffer();
			try {
				String filePath = this.usMailAccessor.mailRetr("inbox", id);
				sb = TextFile.loadMailBodyStringBuffer(filePath, "\r\n", i);
			} catch (Exception e) {
				sb = new StringBuffer();
				Logger.error("", e);
			}
			return sb;
		}

		public class Mail {
			private String uuid;

			private boolean isDeleted;

			private long size;

			public String getUuid() {
				return uuid;
			}

			public void setUuid(String uuid) {
				this.uuid = uuid;
			}

			public boolean isDeleted() {
				return this.isDeleted;
			}

			public void setDeleted(boolean isDeleted) {
				this.isDeleted = isDeleted;
			}

			public long getSize() {
				return this.size;
			}

			public void setSize(long size) {
				this.size = size;
			}

		}

		public IMailAccessor getUsMailAccessor() {
			return usMailAccessor;
		}

		public long getMailBoxSize() {
			long size = 0;
			for (int i = 0; i < this.mails.length; i++)
				size += this.mails[i].getSize();
			return size;
		}

		public void doDelete() {
			if (this.mails == null)
				return;
			List<String> idList = new ArrayList<String>();
			for (Mail mail : this.mails)
				if (mail.isDeleted())
					idList.add(mail.getUuid());
			if (idList.size() > 0) {
				try {
					this.usMailAccessor.mailDelMail("inbox", idList
							.toArray(new String[0]));
				} catch (Exception e) {
					Logger.error("", e);
				}
			}
		}
	}

}
