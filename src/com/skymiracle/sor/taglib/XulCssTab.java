package com.skymiracle.sor.taglib;

public class XulCssTab extends DeployVerSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1986924037493590173L;

	@Override
	public String getPrefix() {
		return "<?xml-stylesheet  type=\"text/css\" href=\"";
	}

	@Override
	public String getSuffix() {
		return "\" ?>";
	}




}