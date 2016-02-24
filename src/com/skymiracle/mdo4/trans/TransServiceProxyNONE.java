package com.skymiracle.mdo4.trans;

public class TransServiceProxyNONE implements
		TransServiceProxy {

	private Object target;

	@SuppressWarnings("unchecked")
	public  void setTarget(Object target) {
		this.target  =  target;
	}
	
	@SuppressWarnings("unchecked")
	public Object getTargetProxied() {
		return this.target;
	}




}
