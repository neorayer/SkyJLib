package com.skymiracle.rss.rsslib;

import com.skymiracle.mdo4.NullKeyException;

/**
 * RSSItems's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSItem extends RSSObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9005841007744807157L;
	private String date;
	private String auth;
	private String comm;

	/**
	 * Get the date
	 * 
	 * @return the date as string
	 */
	public String getDate() {
		if (super.getDoublinCoreElements() == null) {
			if (super.getPubDate() == null) {
				this.date = null;
				return null;
			} else {
				this.date = super.getPubDate();
				return this.date;
			}
		} else {
			this.date = (String) super.getDoublinCoreElements().get(
					RSSHandler.DC_DATE_TAG);
			return this.date;
		}
	}

	/**
	 * Set the date of the item
	 * 
	 * @param d
	 *            the date
	 */
	public void setDate(String d) {
		this.date = d;
		if (super.getDoublinCoreElements() != null) {
			if (super.getDoublinCoreElements().containsKey(
					RSSHandler.DC_DATE_TAG)) {
				super.addDoublinCoreElement(RSSHandler.DC_DATE_TAG, d);
			} else {
				if (super.getPubDate() != null)
					super.setPubDate(d);
				this.date = d;
			}
		}
		this.date = d;
	}

	/**
	 * Set the item's author
	 * 
	 * @param author
	 *            Email address of the author of the item.
	 */
	public void setAuthor(String author) {
		this.auth = author;
	}

	/**
	 * Set the item's comment
	 * 
	 * @param comment
	 *            URL of a page for comments relating to the item
	 */
	public void setComments(String comment) {
		this.comm = comment;
	}

	/**
	 * Get the comments url
	 * 
	 * @return comments url (optional)
	 */
	public String getComments() {
		return this.comm;
	}

	/**
	 * Get the item's author
	 * 
	 * @return author (optional)
	 */
	public String getAuthor() {
		return this.auth;
	}

	/**
	 * Useful for debug
	 * 
	 * @return the info string
	 */
	@Override
	public String toString() {
		String info = "ABOUT ATTRIBUTE: " + this.about + "\n" + "TITLE: "
				+ this.title + "\n" + "LINK: " + this.link + "\n"
				+ "DESCRIPTION: " + this.description + "\n" + "DATE: "
				+ getDate();
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