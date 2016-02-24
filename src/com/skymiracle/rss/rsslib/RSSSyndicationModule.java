package com.skymiracle.rss.rsslib;

/**
 * Handler for Syndycation information.
 * 
 * <blockquote> <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 * 
 * 
 * <h2><a name="namespaces">Namespace Declarations</a></h2>
 * <ul>
 * <li><b>xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"</b></li>
 * </ul>
 * <h2><a name="model">Model</a></h2>
 * <p>
 * <em>&lt;channel&gt; Elements:</em>
 * </p>
 * <ul>
 * <li><b>&lt;sy:updatePeriod&gt;</b> ( 'hourly' | 'daily' | 'weekly' |
 * 'monthly' | 'yearly' )</li>
 * <li><b>&lt;sy:updateFrequency&gt;</b> ( a positive integer )</li>
 * <li><b>&lt;sy:updateBase&gt;</b> ( #PCDATA ) [<a
 * href="http://www.w3.org/TR/NOTE-datetime">W3CDTF</a>]</li>
 * </ul>
 * 
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSSyndicationModule {

	private String updatePeriod, updateFrequency, updateBase;

	/**
	 * Set the feed update period
	 * 
	 * @param t (
	 *            'hourly' | 'daily' | 'weekly' | 'monthly' | 'yearly' )
	 */
	public void setSyUpdatePeriod(String t) {
		this.updatePeriod = t;
	}

	/**
	 * Set the update frequency
	 * 
	 * @param t
	 *            could be an integer value
	 */
	public void setSyUpdateFrequency(String t) {
		this.updateFrequency = t;
	}

	/**
	 * The date of updateBase
	 * 
	 * @param t
	 *            the date
	 */
	public void setSyUpdateBase(String t) {
		this.updateBase = t;
	}

	/**
	 * Get the period
	 * 
	 * @return the period
	 */
	public String getSyUpdatePeriod() {
		return this.updatePeriod;
	}

	/**
	 * Get the update frequecy
	 * 
	 * @return the frequency
	 */
	public String getSyUpdateFrequency() {
		return this.updateFrequency;
	}

	/**
	 * Get the date
	 * 
	 * @return the date
	 */
	public String getSyUpdateBase() {
		return this.updateBase;
	}

	/**
	 * Information string
	 * 
	 * @return an info
	 */
	@Override
	public String toString() {
		String info = "UPD_PERIOD: " + this.updatePeriod + "\nUPD_FREQ: "
				+ this.updateFrequency + "\nUPD_BASE: " + this.updateBase;
		return info;
	}
}