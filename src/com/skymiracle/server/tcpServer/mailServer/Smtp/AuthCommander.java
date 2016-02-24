package com.skymiracle.server.tcpServer.mailServer.Smtp;

import com.skymiracle.auth.MailUser;
import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;
import com.skymiracle.util.Base64;

public class AuthCommander extends SmtpAbsCommander {

	public AuthCommander(CmdConnHandler connHandler) {
		super(connHandler);
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (shStatus.isAuthPass())
			return getBytesCRLF(returnCode(503));

		String[] args = tail.trim().split(" ");
		String machanism = args[0];
		String username = null;
		String password = null;
		if ("LOGIN".equalsIgnoreCase(machanism)) {
			if (args.length == 2) {
				String usernameBase64 = args[1];
				username = new String(Base64.decode(usernameBase64));
				println(returnCode(334, "UGFzc3dvcmQ6"));
				password = fetchPassword();
			} else {
				println(returnCode(334, "VXNlcm5hbWU6"));
				username = fetchUsername();
				println(returnCode(334, "UGFzc3dvcmQ6"));
				password = fetchPassword();
			}
			String res = doAuth(username, password);
			String returnStr = res == null ? returnCode(235) : res;
			debugAuthRes(username, password, returnStr, res);
			return getBytesCRLF(returnStr);
		} else if ("PLAIN".equalsIgnoreCase(machanism)) {
			if (args.length == 2) {
				return getBytesCRLF(doPlainAuth(args[1]));
			} else {
				shStatus.setCmdpos(SmtpHandleStatus.CMDPOS_AFTER_AUTH);
				println(returnCode(334, "OK. Continue authentication"));
				username = fetchUsername();
				println(returnCode(334, "UGFzc3dvcmQ6"));
				password = fetchPassword();
				String res = doAuth(username, password);
				String returnStr = res == null ? returnCode(235) : res;
				debugAuthRes(username, password, returnStr, res);
				return getBytesCRLF(returnStr);
			}
		} else {
			return getBytesCRLF(returnCode(504,
					"Unrecognized authentication type."));
		}
	}

	// 日志记录认证信息
	private void debugAuthRes(String username, String password, String returnStr, String res) {
		if (res == null) {
			password = "******";
		}
		this.getSmtpServer().debug(
				"Auth Result user=" + username + " pass=" + password
						+ " return=" + returnStr);
	}

	private String fetchUsername() throws Exception {
		String s = readln();
		return new String(Base64.decode(s));
	}

	private String fetchPassword() throws Exception {
		String s = readln();
		return new String(Base64.decode(s));
	}

	/**
	 * 
	 * @param rfc2595PlainAuthStr
	 * @return
	 * @throws Exception
	 */
	private String doPlainAuth(String rfc2595PlainAuthStr) throws Exception {
		String user = null, pass = null;
		byte[] decodeBytes;
		decodeBytes = Base64.decode(rfc2595PlainAuthStr);

		int pos0 = -1;
		int pos1 = -1;
		for (int i = 0; i < decodeBytes.length; i++) {
			byte b = decodeBytes[i];
			if (b == 0)
				if (pos0 == -1)
					pos0 = i;
				else
					pos1 = i;
		}
		if (pos1 == -1) {
			user = new String(decodeBytes, 0, pos0);
			pass = new String(decodeBytes, pos0 + 1, decodeBytes.length - pos0
					- 1);
		} else {
			user = new String(decodeBytes, 1, pos1 - 1);
			pass = new String(decodeBytes, pos1 + 1, decodeBytes.length - pos1
					- 1);
		}

		String returnStr = null;
		String res = null;
		if ((user == null) || (pass == null)) {
			returnStr = returnCode(501,
					"Could not decode parameters for AUTH PLAIN");
		} else {
			res = doAuth(user, pass);
			returnStr = res == null ? returnCode(235) : res;
		}

		debugAuthRes(user, pass, returnStr, res);
		return returnStr;
	}

	private String doAuth(String user, String pass) {
		MailUser authMailUser = getSmtpServer().authMail(user, pass, "SMTP",
				getRemoteIP());
		if (authMailUser == null)
			return returnCode(535);
		if (!getSmtpServer().hasNativeSmtpPermission(authMailUser))
			return returnCode(550, "The account has not SMTP permisition.");
		if (!authMailUser.isOpen())
			return returnCode(550, "The account status is not opened.");

		shStatus.setAuthPass(true);
		shStatus.setAuthMailUser(authMailUser);
		return null;

	}

	public static void main(String[] args) throws Exception {
		String a = "UGFzc3dvcmQ6";
		System.out.println(new String(Base64.decode(a)));
	}

}
