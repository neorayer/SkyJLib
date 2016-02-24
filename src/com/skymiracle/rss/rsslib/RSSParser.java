package com.skymiracle.rss.rsslib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RSS Parser.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSParser {

	private SAXParserFactory factory = RSSFactory.getInstance();
	private DefaultHandler hnd;
	private File f;
	private URL u;
	private InputStream in;
	private boolean validate;

	public RSSParser() {
		this.validate = false;
	}

	/**
	 * Set the event handler
	 * 
	 * @param h
	 *            the DefaultHandler
	 * 
	 */
	public void setHandler(DefaultHandler h) {
		this.hnd = h;
	}

	/**
	 * Set rss resource by local file name
	 * 
	 * @param file_name
	 *            loca file name
	 * @throws RSSException
	 */
	public void setXmlResource(String file_name) throws RSSException {
		this.f = new File(file_name);
		try {
			this.in = new FileInputStream(this.f);
		} catch (Exception e) {
			throw new RSSException("RSSParser::setXmlResource fails: "
					+ e.getMessage());
		}

	}

	/**
	 * Set rss resource by URL
	 * 
	 * @param ur
	 *            the remote url
	 * @throws RSSException
	 */
	public void setXmlResource(URL ur) throws RSSException {
		this.u = ur;
		try {
			URLConnection con = this.u.openConnection();
			con.setRequestProperty("User-agent","Mozilla/4.0");
			this.in = con.getInputStream();
			if (con.getContentLength() == -1) {
				this.fixZeroLength();
			}

		} catch (IOException e) {
			throw new RSSException("RSSParser::setXmlResource fails: "
					+ e.getMessage());
		}
	}

	/**
	 * set true if parse have to validate the document defoult is false
	 * 
	 * @param b
	 *            true or false
	 */
	public void setValidate(boolean b) {
		this.validate = b;
	}

	/**
	 * Parse rss file
	 * 
	 * @param filename
	 *            local file name
	 * @param handler
	 *            the handler
	 * @param validating
	 *            validate document??
	 * @throws RSSException
	 */
	public static void parseXmlFile(String filename, DefaultHandler handler,
			boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.setXmlResource(filename);
		p.setHandler(handler);
		p.setValidate(validating);
		p.parse();
	}

	/**
	 * Parse rss file from a url
	 * 
	 * @param remote_url
	 *            remote rss file
	 * @param handler
	 *            the handler
	 * @param validating
	 *            validate document??
	 * @throws RSSException
	 */
	public static void parseXmlFile(URL remote_url, DefaultHandler handler,
			boolean validating) throws RSSException {
		RSSParser p = new RSSParser();
		p.setXmlResource(remote_url);
		p.setHandler(handler);
		p.setValidate(validating);
		p.parse();
	}

	/**
	 * Try to fix null length bug
	 * 
	 * @throws IOException
	 * @throws RSSException
	 */
	private void fixZeroLength() throws IOException, RSSException {

		File ft = File.createTempFile(".rsslib4jbugfix", ".tmp");
		ft.deleteOnExit();
		FileWriter fw = new FileWriter(ft);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				this.in));
		BufferedWriter out = new BufferedWriter(fw);
		String line = "";
		while ((line = reader.readLine()) != null) {
			out.write(line + "\n");
		}
		out.flush();
		out.close();
		reader.close();
		fw.close();
		setXmlResource(ft.getAbsolutePath());

	}

	/**
	 * Call it at the end of the work to preserve memory
	 */
	public void free() {
		this.factory = null;
		this.f = null;
		this.in = null;
		this.hnd = null;
		System.gc();
	}

	/**
	 * Parse the documen
	 * 
	 * @throws RSSException
	 */
	public void parse() throws RSSException {
		try {
			this.factory.setValidating(this.validate);
			// Create the builder and parse the file
			this.factory.newSAXParser().parse(this.in, this.hnd);
		} catch (SAXException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		} catch (IOException e) {
			throw new RSSException("RSSParser::parse fails: " + e.getMessage());
		}

	}

}