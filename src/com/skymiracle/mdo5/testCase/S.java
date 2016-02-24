package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.*;

import java.io.Serializable;

import com.skymiracle.mdo5.Mdo;
import com.skymiracle.validate.*;

public class S extends Mdo<S> implements Serializable{



	/**
	 * 
	 */
	private static final long serialVersionUID = -2410086717127523531L;

		public String uid;

	public String password;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public String[] keyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
