package com.skymiracle.mdo4.testCase;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoDefine;
import com.skymiracle.mdo4.NullKeyException;

public class Domain extends Dao {

	@DaoDefine (len = 128)
	private String dc;

	private long defaultBoxSize;

	@DaoDefine (len = 128)
	private String template;

	private String[] alias;

	private long size;

	private long sizeLocate;

	private long userMax;

	private long userLocate;

	public Domain() {
		super();
	}

	public String getDc() {
		return this.dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "dc" };
	}

	@Override
	public String[] objectClasses() {
		return new String[] { "dcObject", "organization", "mailDomain" };
	}

	@Override
	public String table() {
		return "tb_domain";
	}

	public String[] getAlias() {
		return this.alias;
	}

	public void setAlias(String[] alias) {
		this.alias = alias;
	}

	public long getDefaultBoxSize() {
		return this.defaultBoxSize;
	}

	public void setDefaultBoxSize(long defaultBoxSize) {
		this.defaultBoxSize = defaultBoxSize;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getSizeLocate() {
		return this.sizeLocate;
	}

	public void setSizeLocate(long sizeLocate) {
		this.sizeLocate = sizeLocate;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public long getUserLocate() {
		return this.userLocate;
	}

	public void setUserLocate(long userLocate) {
		this.userLocate = userLocate;
	}

	public long getUserMax() {
		return this.userMax;
	}

	public void setUserMax(long userMax) {
		this.userMax = userMax;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		return baseDN;
	}

	@Override
	public String selfDN() throws NullKeyException {
		if (this.dc == null)
			throw new NullKeyException("dc");
		return "dc=" + this.dc;
	}

}
