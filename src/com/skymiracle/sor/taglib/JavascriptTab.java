package com.skymiracle.sor.taglib;

public class JavascriptTab extends DeployVerSupport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2528777530892180871L;

	
	@Override
	public String getPrefix() {
		return "<script type=\"text/javascript\" src=\"";
	}

	@Override
	public String getSuffix() {
		return "\"></script>";
	}


}