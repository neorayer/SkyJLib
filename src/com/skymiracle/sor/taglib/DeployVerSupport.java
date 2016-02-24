package com.skymiracle.sor.taglib;

import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.el.core.OutTag;

public abstract class DeployVerSupport extends OutTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9141684964255833631L;

	public void setUrl(String url) {
		super.setValue(getPrefix() + getUrlWithDeployVer(url) + getSuffix());
	}
	
	public abstract String getPrefix();

	public abstract String getSuffix();

    @Override
	public int doStartTag() throws JspException {
    	setEscapeXml("false");
    	return super.doStartTag();
    }

	public String getUrlWithDeployVer(String url) {
		String deployVer = (String)pageContext.getRequest().getAttribute("DEPLOY_VER");
		url = url.trim();
        if (url.indexOf("?") > 0)
        	url += "&" + deployVer;
        else
        	url += "?" + deployVer;
		return url;
	}
}