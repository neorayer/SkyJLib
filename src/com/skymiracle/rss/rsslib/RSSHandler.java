package com.skymiracle.rss.rsslib;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for SAX Parser.
 * <p>
 * This elements are <em>not</em> handled yet:<br>
 * <br>
 * cloud<br>
 * rating<br>
 * skipHours<br>
 * skipDays<br>
 * category<br>
 * </p>
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'StealthP' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSHandler extends DefaultHandler {

	private StringBuffer buff;
	private String current_tag;
	private RSSChannel chan;
	private RSSItem itm;
	private RSSImage img;
	private RSSSequence seq;
	private RSSSequenceElement seq_elem;
	private RSSTextInput input;
	private RSSSyndicationModule sy;

	private boolean reading_chan;
	private boolean reading_item;
	private boolean reading_image;
	private boolean reading_input;
	public static final String CHANNEL_TAG = "channel";
	public static final String TITLE_TAG = "title";
	public static final String LINK_TAG = "link";
	public static final String DESCRIPTION_TAG = "description";
	public static final String ITEM_TAG = "item";
	public static final String IMAGE_TAG = "image";
	public static final String IMAGE_W_TAG = "width";
	public static final String IMAGE_H_TAG = "height";
	public static final String URL_TAG = "url";
	public static final String SEQ_TAG = "rdf:seq";
	public static final String SEQ_ELEMENT_TAG = "rdf:li";
	public static final String TEXTINPUT_TAG = "textinput";
	public static final String NAME_TAG = "name";
	public static final String LANGUAGE_TAG = "language";
	public static final String MANAGING_TAG = "managingEditor";
	public static final String WMASTER_TAG = "webMaster";
	public static final String COPY_TAG = "copyright";
	public static final String PUB_DATE_TAG = "pubDate";
	public static final String LAST_B_DATE_TAG = "lastBuildDate";
	public static final String GENERATOR_TAG = "generator";
	public static final String DOCS_TAG = "docs";
	public static final String TTL_TAG = "ttl";
	public static final String AUTHOR_TAG = "author";
	public static final String COMMENTS_TAG = "comments";
	public static final String CLOUD_TAG = "cloud"; // TODO
	public static final String RATING_TAG = "rating"; // TODO
	public static final String SKIPH_TAG = "skipHours"; // TODO
	public static final String SKIPD_TAG = "skipDays"; // TODO
	public static final String CATEGORY_TAG = "category"; // TODO

	public static final String DC_TITLE_TAG = "dc:title";
	public static final String DC_CREATOR_TAG = "dc:creator";
	public static final String DC_SUBJECT_TAG = "dc:subject";
	public static final String DC_DESCRIPTION_TAG = "dc:description";
	public static final String DC_PUBLISHER_TAG = "dc:publisher";
	public static final String DC_CONTRIBUTOR_TAG = "dc:contributor";
	public static final String DC_DATE_TAG = "dc:date";
	public static final String DC_TYPE_TAG = "dc:type";
	public static final String DC_FORMAT_TAG = "dc:format";
	public static final String DC_IDENTIFIER_TAG = "dc:identifier";
	public static final String DC_SOURCE_TAG = "dc:source";
	public static final String DC_LANGUAGE_TAG = "dc:language";
	public static final String DC_RELATION_TAG = "dc:relation";
	public static final String DC_COVERAGE_TAG = "dc:coverage";
	public static final String DC_RIGHTS_TAG = "dc:rights";

	public static final String SY_PERIOD_TAG = "sy:updatePeriod";
	public static final String SY_FREQ_TAG = "sy:updateFrequency";
	public static final String SY_BASE_TAG = "sy:updateBase";

	public RSSHandler() {

		this.buff = new StringBuffer();
		this.current_tag = null;
		this.chan = new RSSChannel();
		this.reading_chan = false;
		this.reading_item = false;
		this.reading_image = false;
		this.reading_input = false;

	}

	/**
	 * Receive notification of the start of an element.
	 * 
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed
	 * @param qName
	 *            The qualified name (with prefix), or the empty string if
	 *            qualified names are not available
	 * @param attributes
	 *            The attributes attached to the element. If there are no
	 *            attributes, it shall be an empty Attributes object
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		if (tagIsEqual(qName, CHANNEL_TAG)) {
			this.reading_chan = true;
			processChanAboutAttribute(attributes);
		}

		if (tagIsEqual(qName, ITEM_TAG)) {
			this.reading_item = true;
			this.reading_chan = false;
			this.itm = new RSSItem();
			processItemAboutAttribute(attributes);
		}

		if (tagIsEqual(qName, IMAGE_TAG)) {
			this.reading_image = true;
			this.reading_chan = false;
			this.img = new RSSImage();
		}

		if (tagIsEqual(qName, SEQ_TAG)) {
			this.seq = new RSSSequence();
		}

		if (tagIsEqual(qName, TEXTINPUT_TAG)) {
			this.reading_input = true;
			this.reading_chan = false;
			this.input = new RSSTextInput();
		}

		if (tagIsEqual(qName, SEQ_ELEMENT_TAG))
			processSeqElement(attributes);

		if (qName.toUpperCase().startsWith("SY:"))
			this.sy = new RSSSyndicationModule();

		this.current_tag = qName;

	}

	/**
	 * Receive notification of the end of an element
	 * 
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed
	 * @param qName
	 *            The qualified name (with prefix), or the empty string if
	 *            qualified names are not available
	 */
	@Override
	public void endElement(String uri, String localName, String qName) {

		String data = this.buff.toString().trim();

		if (qName.equals(this.current_tag)) {
			data = this.buff.toString().trim();
			this.buff = new StringBuffer();
		}

		if (this.reading_chan)
			processChannel(qName, data);

		if (this.reading_item)
			processItem(qName, data);

		if (this.reading_image)
			processImage(qName, data);

		if (this.reading_input)
			processTextInput(qName, data);

		if (tagIsEqual(qName, CHANNEL_TAG)) {
			this.reading_chan = false;
			this.chan.setSyndicationModule(this.sy);
		}

		if (tagIsEqual(qName, ITEM_TAG)) {
			this.reading_item = false;
			this.chan.addItem(this.itm);
		}

		if (tagIsEqual(qName, IMAGE_TAG)) {
			this.reading_image = false;
			this.chan.setRSSImage(this.img);
		}

		if (tagIsEqual(qName, SEQ_TAG)) {
			this.chan.addRSSSequence(this.seq);
		}

		if (tagIsEqual(qName, TEXTINPUT_TAG)) {
			this.reading_input = false;
			this.chan.setRSSTextInput(this.input);
		}

	}

	/**
	 * Receive notification of character data inside an element
	 * 
	 * @param ch
	 *            The characters.
	 * @param start
	 *            The start position in the character array.
	 * @param length
	 *            The number of characters to use from the character array.
	 */
	@Override
	public void characters(char[] ch, int start, int length) {

		String data = new String(ch, start, length);

		// Jump blank chunk
		if (data.trim().length() == 0)
			return;

		this.buff.append(data);

	}

	/**
	 * Receive notification when parse are scannering an image
	 * 
	 * @param qName
	 *            The tag name
	 * @param data
	 *            The tag Value
	 */
	private void processImage(String qName, String data) {
		// System.out.println("RSSHandler:processImage():: TAG: " + qName);
		if (tagIsEqual(qName, TITLE_TAG))
			this.img.setTitle(data);

		if (tagIsEqual(qName, LINK_TAG))
			this.img.setLink(data);

		if (tagIsEqual(qName, URL_TAG))
			this.img.setUrl(data);

		if (tagIsEqual(qName, IMAGE_W_TAG))
			this.img.setWidth(data);

		if (tagIsEqual(qName, IMAGE_H_TAG))
			this.img.setHeight(data);

		if (tagIsEqual(qName, DESCRIPTION_TAG))
			this.img.setDescription(data);

		if (qName.toUpperCase().startsWith("DC:"))
			processDoublinCoreTags(qName, data, this.img);

	}

	/**
	 * Receive notification when parse are scannering a textinput
	 * 
	 * @param qName
	 *            The tag name
	 * @param data
	 *            The tag Value
	 */

	private void processTextInput(String qName, String data) {

		if (tagIsEqual(qName, TITLE_TAG))
			this.input.setTitle(data);

		if (tagIsEqual(qName, LINK_TAG))
			this.input.setLink(data);

		if (tagIsEqual(qName, NAME_TAG))
			this.input.setInputName(data);

		if (tagIsEqual(qName, DESCRIPTION_TAG))
			this.input.setDescription(data);

		if (qName.toUpperCase().startsWith("DC:"))
			processDoublinCoreTags(qName, data, this.input);

	}

	/**
	 * Receive notification when parse are scannering an Item
	 * 
	 * @param qName
	 *            The tag name
	 * @param data
	 *            The tag Value
	 */
	private void processItem(String qName, String data) {

		if (tagIsEqual(qName, TITLE_TAG))
			this.itm.setTitle(data);

		if (tagIsEqual(qName, LINK_TAG))
			this.itm.setLink(data);

		if (tagIsEqual(qName, DESCRIPTION_TAG))
			this.itm.setDescription(data);

		if (tagIsEqual(qName, PUB_DATE_TAG))
			this.itm.setPubDate(data);

		if (tagIsEqual(qName, PUB_DATE_TAG))
			this.itm.setPubDate(data);

		if (tagIsEqual(qName, AUTHOR_TAG))
			this.itm.setAuthor(data);

		if (tagIsEqual(qName, COMMENTS_TAG))
			this.itm.setComments(data);

		if (qName.toUpperCase().startsWith("DC:"))
			processDoublinCoreTags(qName, data, this.itm);

	}

	/**
	 * Receive notification when parse are scannering the Channel
	 * 
	 * @param qName
	 *            The tag name
	 * @param data
	 *            The tag Value
	 */
	private void processChannel(String qName, String data) {

		if (tagIsEqual(qName, TITLE_TAG))
			this.chan.setTitle(data);

		if (tagIsEqual(qName, LINK_TAG))
			this.chan.setLink(data);

		if (tagIsEqual(qName, DESCRIPTION_TAG))
			this.chan.setDescription(data);

		if (tagIsEqual(qName, COPY_TAG))
			this.chan.setCopyright(data);

		if (tagIsEqual(qName, PUB_DATE_TAG))
			this.chan.setPubDate(data);

		if (tagIsEqual(qName, LAST_B_DATE_TAG))
			this.chan.setLastBuildDate(data);

		if (tagIsEqual(qName, GENERATOR_TAG))
			this.chan.setGenerator(data);

		if (tagIsEqual(qName, DOCS_TAG))
			this.chan.setDocs(data);

		if (tagIsEqual(qName, TTL_TAG))
			this.chan.setTTL(data);

		if (tagIsEqual(qName, LANGUAGE_TAG))
			this.chan.setLanguage(data);

		if (qName.toUpperCase().startsWith("DC:"))
			processDoublinCoreTags(qName, data, this.chan);

		if (qName.toUpperCase().startsWith("SY:"))
			processSyndicationTags(qName, data);

	}

	/**
	 * Receive notification when parse are scannering a doublin core element
	 * 
	 * @param qName
	 *            tag name
	 * @param data
	 *            tag value
	 * @param o
	 *            RSSObject
	 */
	private void processDoublinCoreTags(String qName, String data, RSSObject o) {
		o.addDoublinCoreElement(qName.toLowerCase(), data);
	}

	private void processSyndicationTags(String qName, String data) {

		if (tagIsEqual(qName, RSSHandler.SY_BASE_TAG))
			this.sy.setSyUpdateBase(data);

		if (tagIsEqual(qName, RSSHandler.SY_FREQ_TAG))
			this.sy.setSyUpdateFrequency(data);

		if (tagIsEqual(qName, RSSHandler.SY_PERIOD_TAG))
			this.sy.setSyUpdatePeriod(data);
	}

	/**
	 * Receive notification when parse are scannering a Sequence Item
	 * 
	 * @param a
	 *            The Atrribute of the tag
	 */
	private void processSeqElement(Attributes a) {

		String res = a.getValue(0);
		this.seq_elem = new RSSSequenceElement();
		this.seq_elem.setResource(res);
		this.seq.addElement(this.seq_elem);

	}

	/**
	 * Receive notification when parse are scannering an Item attribute
	 * 
	 * @param a
	 *            the attribute
	 */
	private void processItemAboutAttribute(Attributes a) {

		String res = a.getValue(0);
		this.itm.setAboutAttribute(res);

	}

	/**
	 * Receive notification when parse are scannering a Chan attribute
	 * 
	 * @param a
	 *            the attribute
	 */
	private void processChanAboutAttribute(Attributes a) {

		String res = a.getValue(0);
		this.chan.setAboutAttribute(res);

	}

	/**
	 * Check against non-casesentive tag name
	 * 
	 * @param a
	 *            The first tag
	 * @param b
	 *            The tag to check
	 * @return True if the tags are the same
	 */
	protected static boolean tagIsEqual(String a, String b) {

		return a.equalsIgnoreCase(b);

	}

	/**
	 * Get the RSSChannel Object back from the parser
	 * 
	 * @return The RSSChannell Object
	 */
	public RSSChannel getRSSChannel() {

		return this.chan;

	}

}