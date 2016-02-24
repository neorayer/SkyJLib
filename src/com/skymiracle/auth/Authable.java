package com.skymiracle.auth;

import com.skymiracle.util.UsernameWithDomain;

public interface Authable {
	public final static String LOCATION_NATIVE_LOCAL = "local";

	public final static String LOCATION_FOREIGN = "foreign";

	public final static String LOCATION_EXCEPTION = "exception";

	public final static String LOCATION_GROUP = "group";

	/**
	 * Do authentic with username, domain and simple plain password.
	 * 
	 * @param username
	 * @param domain
	 * @param password
	 * @return null if auth failed,otherwise is realName and domain;
	 * @throws Exception
	 */
	public UsernameWithDomain auth(String username, String domain,
			String password, String modeName, String remoteIP);

	public boolean hasPermission(String username, String domain,
			String permissionName) throws Exception;

	public String getDefaultDomain();

	public boolean chgPassword(String uid, String dc, String oldPass, String newPass)
			throws Exception;
}
