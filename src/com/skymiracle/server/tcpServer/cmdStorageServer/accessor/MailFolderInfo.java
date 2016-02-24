package com.skymiracle.server.tcpServer.cmdStorageServer.accessor;

import com.skymiracle.mdo4.*;

/**
 * 邮件文件夹信息
 */
public class MailFolderInfo extends Dao {

	// 文件夹名
	private String idxName;

	// 名称
	private String dispName;

	// 新邮件数
	private int newCount;

	// 总邮件数
	private int count;

	// 序号
	private int sortTag;

	public String getIdxName() {
		return idxName;
	}

	public void setIdxName(String idxName) {
		this.idxName = idxName;
	}

	public String getDispName() {
		return dispName;
	}

	public void setDispName(String dispName) {
		this.dispName = dispName;
	}

	public int getNewCount() {
		return newCount;

	}

	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSortTag() {
		return sortTag;
	}

	public void setSortTag(int sortTag) {
		this.sortTag = sortTag;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "name" };
	}

	@Override
	public String[] objectClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String selfDN() throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

}
