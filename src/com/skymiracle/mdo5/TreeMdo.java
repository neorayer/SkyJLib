package com.skymiracle.mdo5;

public abstract class TreeMdo<T extends TreeMdo<T>> extends Mdo<T>{

	protected abstract String getParentFieldName();
	

}
