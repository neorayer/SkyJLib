package com.skymiracle.smartUpgrade;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;

public class UpgradeFeed extends Dao {
	private double version;

	private String fileList;

	private String fileUrl;

	private String backuper;

	private String killer;

	private String installer;

	private String starter;

	private String recover;

	public String getFileList() {
		return this.fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	public String getFileUrl() {
		return this.fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getInstaller() {
		return this.installer;
	}

	public void setInstaller(String installer) {
		this.installer = installer;
	}

	public String getKiller() {
		return this.killer;
	}

	public void setKiller(String killer) {
		this.killer = killer;
	}

	public String getStarter() {
		return this.starter;
	}

	public void setStarter(String starter) {
		this.starter = starter;
	}

	public double getVersion() {
		return this.version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public String getBackuper() {
		return this.backuper;
	}

	public void setBackuper(String backuper) {
		this.backuper = backuper;
	}

	public String getRecover() {
		return this.recover;
	}

	public void setRecover(String recover) {
		this.recover = recover;
	}

	@Override
	public String fatherDN(String baseDN) throws NullKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] keyNames() {
		// TODO Auto-generated method stub
		return null;
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
