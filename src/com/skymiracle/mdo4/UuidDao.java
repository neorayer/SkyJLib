package com.skymiracle.mdo4;

import com.skymiracle.util.UUID;

/**
 * 一个以Uuid作为唯一主键的Domain对象
 * 
 * @author skymiracle
 *
 */
public abstract class UuidDao extends Dao {

	public String uuid;

	public UuidDao() {
		this.uuid = new UUID().toShortString();
	}

	public UuidDao(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "uuid" };
	}

}
