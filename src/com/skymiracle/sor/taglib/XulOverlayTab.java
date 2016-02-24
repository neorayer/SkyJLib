package com.skymiracle.sor.taglib;

public class XulOverlayTab extends DeployVerSupport {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3721304687906773659L;


	@Override
	public String getPrefix() {
		return "<?xul-overlay href=\"";
	}

	@Override
	public String getSuffix() {
		return "\" ?>";
	}


}