package com.skymiracle.mdo5.trans;

public class TransServiceProxyNONE implements
		TransServiceProxy {

	private Object target;

	public  void setTarget(Object target) {
		this.target  =  target;
	}
	
	public Object getTargetProxied() {
		return this.target;
	}




}
