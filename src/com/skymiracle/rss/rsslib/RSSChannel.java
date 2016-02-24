package com.skymiracle.rss.rsslib;

import java.util.LinkedList;
import com.skymiracle.mdo4.NullKeyException;

/**
 * RSSChannel's definitions class.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSChannel extends RSSObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1150906240898914150L;
	private LinkedList rss_items;
	private RSSImage img;
	private RSSSequence seq;
	private RSSTextInput input;
	private RSSSyndicationModule sy;
	private String lang;
	private String copy;
	private String master;
	private String bdate;
	private String gen;
	private String t;
	private String docs;

	public RSSChannel() {
		this.rss_items = new LinkedList();
	}

	/**
	 * Set the language of channel
	 * 
	 * @param language
	 *            The language the channel is written in
	 */
	public void setLanguage(String language) {
		this.lang = language;
	}

	/**
	 * Set channel's copyright
	 * 
	 * @param copyright
	 *            Copyright notice for content in the channel
	 */
	public void setCopyright(String copyright) {
		this.copy = copyright;
	}

	/**
	 * Set the lastBuildDate
	 * 
	 * @param lastBuildDate
	 *            The last time the content of the channel changed
	 */
	public void setLastBuildDate(String lastBuildDate) {
		this.bdate = lastBuildDate;
	}

	/**
	 * Set the managingEditor
	 * 
	 * @param managingEditor
	 *            Email address for person responsible for editorial content
	 */
	public void setManagingEditor(String managingEditor) {
	}

	/**
	 * Set the webMaster
	 * 
	 * @param webMaster
	 *            Email address for person responsible for technical issues
	 *            relating to channel.
	 */
	public void setWebMaster(String webMaster) {
		this.master = webMaster;
	}

	/**
	 * Set the gerator
	 * 
	 * @param generator
	 *            A string indicating the program used to generate the channel
	 */
	public void setGenerator(String generator) {
		this.gen = generator;
	}

	/**
	 * Set the TTL time
	 * 
	 * @param ttl
	 *            the time to live
	 */
	public void setTTL(String ttl) {
		this.t = ttl;
	}

	/**
	 * Set the documentator
	 * 
	 * @param docs
	 *            thw documentator
	 */
	public void setDocs(String docs) {
		this.docs = docs;
	}

	/**
	 * Set a RSSImage object associated to the channel
	 * 
	 * @param im
	 *            Specifies a GIF, JPEG or PNG image that can be displayed with
	 *            the channel.
	 */
	public void setRSSImage(RSSImage im) {
		this.img = im;
	}

	/**
	 * Set a RSSTextInput object to a channel
	 * 
	 * @param in
	 *            Specifies a text input box that can be displayed with the
	 *            channel
	 */
	public void setRSSTextInput(RSSTextInput in) {
		this.input = in;
	}

	/**
	 * Get channel's lastBuildDate
	 * 
	 * @return lastBuildDate
	 */
	public String getLastBuildDate() {
		return this.bdate;
	}

	/**
	 * Get the chyannel's copyright
	 * 
	 * @return copyright (optional)
	 */
	public String getCopyright() {
		return this.copy;
	}

	/**
	 * Get the generator program's channel
	 * 
	 * @return generator (optional)
	 */
	public String getGenerator() {
		return this.gen;
	}

	/**
	 * Return the TTL's channel
	 * 
	 * @return TTL (optional)
	 */
	public String getTTL() {
		return this.t;
	}

	/**
	 * Get the docs url about Rss specifications
	 * 
	 * @return the url (optional)
	 */
	public String getDocs() {
		return this.docs;
	}

	/**
	 * Get the language of channell
	 * 
	 * @return language (optional)
	 */
	public String getLanguage() {
		return this.lang;
	}

	/**
	 * Get the webmaster email
	 * 
	 * @return email of webmaster (optional)
	 */
	public String getWebMaster() {
		return this.master;
	}

	/**
	 * Get a RSSTextInput object from the channel
	 * 
	 * @return the RSSTextInput or null
	 */
	public RSSTextInput getRSSTextInput() {
		return this.input;
	}

	/**
	 * Add an RSSItem to a channel object
	 * 
	 * @param itm
	 *            the RSSItem item
	 */
	public void addItem(RSSItem itm) {
		this.rss_items.add(itm);
	}

	/**
	 * Set the channel's item's sequece
	 * 
	 * @param s
	 *            The RSSSequence
	 */
	public void addRSSSequence(RSSSequence s) {
		this.seq = s;
	}

	/**
	 * Get a RSSImage from the channel
	 * 
	 * @return RSSImage if exists (optional)
	 */
	public RSSImage getRSSImage() {
		return this.img;
	}

	/**
	 * Get a linkedList wich contains the Channel's RSSItem
	 * 
	 * @return the RSSItems's list
	 */
	public LinkedList getItems() {
		return this.rss_items;
	}

	public void setItems(LinkedList rss_items) {
		this.rss_items = rss_items;
	}

	/**
	 * Get the sequnce from the channel<br>
	 * This element should be always present
	 * 
	 * @return the RSSSequence
	 */
	public RSSSequence getItemsSequence() {
		return this.seq;
	}

	/**
	 * Set syndication module for channel's
	 * 
	 * @param s
	 *            syndication namespaces module
	 */
	public void setSyndicationModule(RSSSyndicationModule s) {
		this.sy = s;
	}

	/**
	 * Get the syndication module object from the RSS object
	 * 
	 * @return The object or null
	 */
	public RSSSyndicationModule getRSSSyndicationModule() {
		return this.sy;
	}

	/**
	 * Useful for debug
	 * 
	 * @return An info string about channel
	 */
	@Override
	public String toString() {
		String info = "ABOUT ATTRIBUTE: " + this.about + "\n" + "TITLE: "
				+ this.title + "\n" + "LINK: " + this.link + "\n"
				+ "DESCRIPTION: " + this.description + "\nLANGUAGE: "
				+ this.lang;
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