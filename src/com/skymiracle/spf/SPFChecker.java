package com.skymiracle.spf;

public interface SPFChecker {

	public final static int RESULT_SUCC = 10;
	public final static int RESULT_NONE = 20;
	public final static int RESULT_FAIL = 30;

	/**
	 * Get the SPF checking result with matching the IP address and email domain
	 * name.
	 * 
	 * @param ip
	 *            The IP address
	 * @param domain
	 *            The email domain name
	 * @return RESULT_SUCC if SPF is checked pass. RESULT_FAIL if not.
	 *         RESULT_NONE if there is not SPF setting for the domain OR DNS
	 *         resolve failed.
	 */
	public int doCheck(String ip, String domain);
}
