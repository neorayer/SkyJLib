package com.skymiracle.mdo4;

import java.lang.reflect.InvocationTargetException;

public abstract class RichDao extends Dao {

	private String metaAddDate;

	private String metaAddDateTime;

	public String getMetaAddDate() {
		return metaAddDate;
	}

	public String getMetaAddDateTime() {
		return metaAddDateTime;
	}

	public void setMetaAddDateTime(String metaAddDateTime) {
		this.metaAddDateTime = metaAddDateTime;
	}

	public void setMetaAddDate(String metaAddDate) {
		this.metaAddDate = metaAddDate;
	}

	@Override
	public DaoAttrSet toDaoAttrSet() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		DaoAttrSet daoAttrSet = super.toDaoAttrSet();
		daoAttrSet.remove("metaadddate");
		daoAttrSet.remove("metaadddatetime");
		return daoAttrSet;
	}

}
