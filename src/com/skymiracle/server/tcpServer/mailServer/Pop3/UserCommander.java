package com.skymiracle.server.tcpServer.mailServer.Pop3;

import com.skymiracle.server.tcpServer.cmdServer.CmdConnHandler;

public class UserCommander extends Pop3AbsCommander{

	public UserCommander(CmdConnHandler connHandler) {
		super(connHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] doCmd(String head, String tail) throws Exception {
		if (phStatus.getStep() != Pop3HandleStatus.STEP_INIT)
			return null;
		
		String username = tail;
		
		phStatus.setUsername(username);
		phStatus.setStep(Pop3HandleStatus.STEP_USER);

		return getBytesCRLF("+OK Password required.");
	}

}
