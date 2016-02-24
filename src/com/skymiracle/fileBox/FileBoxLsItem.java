package com.skymiracle.fileBox;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo5.Mdo.Title;
import com.skymiracle.util.CalendarUtil;

/**
 * When you list the file items in FileBox, you need not return any java.io.File
 * objects. The java.io.File object is dangerous maybe.
 * 
 * @author neora
 * 
 */
public class FileBoxLsItem extends Dao{

	private String uuid;
	
	private String fileName;

	private long lastModified;

	private long size;

	@Title("经过计算可视的")
	private String currentSize;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public FileBoxLsItem(String uuid, String fileName, long size, long lastModified) {
		this.uuid = uuid;
		this.fileName = fileName;
		this.lastModified = lastModified;
		this.size = size;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public String getLastModifiedTime() {
		return CalendarUtil.getLongFormat(lastModified);
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
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

	public String getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(String currentSize) {
		this.currentSize = currentSize;
	}

}
