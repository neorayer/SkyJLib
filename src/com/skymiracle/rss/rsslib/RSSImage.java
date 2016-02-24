//$Id: RSSImage.java,v 1.1 2011/10/31 12:52:06 skymiracle Exp $
package com.skymiracle.rss.rsslib;

import com.skymiracle.mdo4.NullKeyException;

/**
 * RSS image's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSImage extends RSSObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3912443549392646900L;
	private String url;
	private String w;
	private String h;

	/**
	 * Set url of the image
	 * 
	 * @param u
	 *            The image's url
	 */
	public void setUrl(String u) {
		this.url = u;
	}

	/**
	 * Get the url of the image
	 * 
	 * @return the image's url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Set the image's width
	 * 
	 * @param width
	 *            width
	 */
	public void setWidth(String width) {
		this.w = width;
	}

	/**
	 * Set the image's height
	 * 
	 * @param height
	 *            height
	 */
	public void setHeight(String height) {
		this.h = height;
	}

	/**
	 * Get the image's width
	 * 
	 * @return width (could be null)
	 */
	public String getWidth() {
		return this.w;
	}

	/**
	 * Get the image's height
	 * 
	 * @return height (could be null)
	 */
	public String getHeight() {
		return this.h;
	}

	/**
	 * Return a html img tag with link associated
	 * 
	 * @return html
	 */
	public String toHTML() {
		String html = "<a href=\"" + this.link + "\">";
		html += "<img src=\"" + this.url + "\" border=\"0\" ";
		html += (this.w != null) ? "width=\"" + this.w + " \"" : " ";
		html += (this.h != null) ? "height=\"" + this.h + " \"" : " ";
		html += (this.title != null) ? "alt=\"" + this.title + "\"" : "";
		html += "/>";
		html += "</a>";
		return html;
	}

	/**
	 * Useful for debug
	 * 
	 * @return information
	 */
	@Override
	public String toString() {
		String info = "TITLE: " + this.title + "\n" + "LINK: " + this.link
				+ "\n" + "URL: " + this.url;
		return info;
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

}