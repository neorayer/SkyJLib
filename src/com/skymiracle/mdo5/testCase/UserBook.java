package com.skymiracle.mdo5.testCase;

import com.skymiracle.mdo5.Mdo;

public class UserBook extends Mdo<UserBook>{
	
	public String dc;
	
	public String uid;
	
	public String bookid;

	@Override
	public String[] keyNames() {
		return new String[]{"dc", "uid", "bookid"};
	}

	@Override
	public String table() {
		return "tb_userbook";
	}

}