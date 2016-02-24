package com.skymiracle.server.tcpServer.mailServer.Smtp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;
import com.skymiracle.io.PlainFile;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

/**
 * 在单个或多个RCPT命令后，表示所有的邮件接收人已标识，初始化数据传输，以.结束。
 */
public class DataCommander extends SmtpAbsCommander {

	public DataCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		// Sequence Check
		if (shStatus.getCmdpos() != SmtpHandleStatus.CMDPOS_AFTER_RCPT)
			return getBytesCRLF(returnCode(503));

		println(returnCode(354));

		MailUser fromMailUser = shStatus.getFromMailUser();

		String s = null;
		long dataSize = 0;
		List<String> dataLineList = new LinkedList<String>();

		while (true) {
			s = readln();
			this.getSmtpServer().detail("<- " + s);
//			if (s.startsWith("-")) {
//				byte[] bs = s.getBytes("ISO-8859-1");
//				StringBuffer hex = ByteUtils.bs2Hex(bs, bs.length);
//				Logger.error(hex);
//			}
			if (s == null)
				break;
			if (s.equals("."))
				break;
			dataSize += s.length();

			// if deliver , System conf size
			// if relay ,User conf size
			if (fromMailUser.getStorageLocation().equals(
					Authable.LOCATION_FOREIGN)) {
				long maxMessageSize = getSmtpServer().getMaxMessageSize();
				if (dataSize > maxMessageSize) {
					setQuiting(true);
					return getBytesCRLF(returnCode(552,
							"Message size exceeds system setting "
									+ maxMessageSize));
				}
			} else {
				long fromMaxMessageSize = fromMailUser.getMessageSize();
				if (dataSize > fromMaxMessageSize) {
					setQuiting(true);
					return getBytesCRLF(returnCode(550,
							"Message size exceeds user message size setting "
									+ fromMaxMessageSize));
				}
			}
			dataLineList.add(s);
		}
		
		
		if (dataSize == 0) {
			setQuiting(true);
			return getBytesCRLF(returnCode(550, "Message size is 0"));
		}
		// 本地客户端连接过的且dataLineList.size() 小于3, 表示传过来的是dataLineList的文件路径
		if("127.0.0.1".equals(connHandler.getRemoteIP()) && dataLineList.size() < 3) {
			String dataPathInFs = dataLineList.get(0);
			Logger.debug("the dataLineList is file name : " + dataPathInFs);
			try{
				File mimeFile = new File(dataPathInFs);
				dataSize = mimeFile.length();
				dataLineList = PlainFile.readLines(mimeFile, "UTF-8");
				mimeFile.delete();
			}catch(Exception e) {
				setQuiting(true);
				return getBytesCRLF(returnCode(550, "Message data file path is not exists : " + dataPathInFs));
			}
			
			// if deliver , System conf size
			// if relay ,User conf size
			if (fromMailUser.getStorageLocation().equals(
					Authable.LOCATION_FOREIGN)) {
				long maxMessageSize = getSmtpServer().getMaxMessageSize();
				if (dataSize > maxMessageSize) {
					setQuiting(true);
					return getBytesCRLF(returnCode(552,
							"Message size exceeds system setting "
									+ maxMessageSize));
				}
			} else {
				long fromMaxMessageSize = fromMailUser.getMessageSize();
				if (dataSize > fromMaxMessageSize) {
					setQuiting(true);
					return getBytesCRLF(returnCode(550,
							"Message size exceeds user message size setting "
									+ fromMaxMessageSize));
				}
			}
		}

		String res = getSmtpServer().getDataFilter().doFilter(this.connHandler,
				shStatus, dataLineList);
		if (res != null)
			return getBytesCRLF(s);

		getSmtpServer().putMailtoQueue(fromMailUser, shStatus.getRcptToList(),
				dataLineList, shStatus.getMissionUUID());

		// 该日志记录暂不起作用
		getSmtpServer().getSmtpLogger().addFromNativeMission(shStatus.getMissionUUID(),
				fromMailUser, shStatus.getRcptToList(), dataSize, dataLineList);

		shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_DATADOT);
		return getBytesCRLF(returnCode(250, "id=" + shStatus.getMissionUUID()));
	}
}
