package com.skymiracle.rss.rsslib;

import java.util.LinkedList;

/**
 * RSSSequences's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSSequence {

	private LinkedList list;

	public RSSSequence() {
		this.list = new LinkedList();
	}

	/**
	 * Add an element to a sequence
	 * 
	 * @param el
	 *            the RSSSequenceElement elment
	 */
	public void addElement(RSSSequenceElement el) {
		this.list.add(el);
	}

	/**
	 * Return the element of a squence into a LinkedList
	 * 
	 * @return The list
	 */
	public LinkedList getElementList() {
		return this.list;
	}

	/**
	 * Return the size of a sequence
	 * 
	 * @return the size
	 */
	public int getListSize() {
		return this.list.size();
	}

	/**
	 * Useful for debug
	 * 
	 * @return information
	 */
	@Override
	public String toString() {
		String info = "SEQUENCE HAS " + getListSize() + " ELEMENTS.\n";
		for (int i = 0; i < this.list.size(); i++) {
			RSSSequenceElement e = (RSSSequenceElement) this.list.get(i);
			info += e.toString() + "\n";
		}
		return info;
	}

}