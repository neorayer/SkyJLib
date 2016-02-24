package com.skymiracle.server.tcpServer.mailServer.testCase;

import com.skymiracle.auth.MailUser;
import com.skymiracle.mdo5.Mdo_X;

public class TestMailUser extends MailUser<TestMailUser> {

	public TestMailUser(Mdo_X<TestMailUser> x) {
		super(x);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGroupUser() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
