package com.skymiracle.rss.rsslib;

import com.skymiracle.mdo4.NullKeyException;

/**
 * RSSTextInput's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSTextInput extends RSSObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5779591107742699964L;
	private String name;

	/**
	 * Set the input type name
	 * 
	 * @param String
	 *            n the input type name
	 */
	public void setInputName(String n) {
		this.name = n;
	}

	/**
	 * Get the form input name
	 * 
	 * @return the name
	 */
	public String getInputName() {
		return this.name;
	}

	/**
	 * Get the form action
	 * 
	 * @return the action
	 */
	public String getFormAction() {
		return super.getLink();
	}

	/**
	 * Info
	 * 
	 * @return info string
	 */
	@Override
	public String toString() {
		String info = "FORM ACTION: " + getFormAction() + "\n" + "INPUT NAME: "
				+ getInputName() + "\n" + "DESCRIPTION: "
				+ super.getDescription();
		return info;
	}

	/**
	 * A basic rendering in html
	 * 
	 * @return the html form
	 */
	public String toHTML() {
		String html = "<form method\"GET\" action=\"" + getFormAction()
				+ "\">\n" + super.getDescription() + "<br>\n"
				+ "<input type=\"text\" name=\"" + getInputName()
				+ "\">\n</form>";
		return html;
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
