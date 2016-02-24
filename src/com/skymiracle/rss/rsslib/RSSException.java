package com.skymiracle.rss.rsslib;

/**
 * RSSlib exception handler.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7182070855400278413L;

	public RSSException(String err) {
		super(err);
	}

}