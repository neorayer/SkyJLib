package com.skymiracle.auth;

import java.util.List;
import java.util.Map;

import com.skymiracle.sor.exception.AppException;

public interface AuthMail extends AuthStorage {

	/**
	 * To authenticate a mail user with username domain and password.
	 * 
	 * @param username
	 *            The username
	 * @param domain
	 *            The domain name
	 * @param password
	 *            The password
	 * @param modeName
	 *            The mode name of the authentication action. etc. POP3 SMTP or
	 *            WEB
	 * @param remoteIP
	 *            The IP address of remote client.
	 * @return The MailUser object if authenticate successfully, or null if
	 *         failed.
	 */
	public MailUser<?> authMail(String username, String domain,
			String password, String modeName, String remoteIP);

	public void setMailUserAttr(MailUser<?> mailUser,
			Map<String, Object> attrMap) throws Exception;

	/**
	 * Check whether a mail user has pop3 permission.
	 * 
	 * @param mailUser
	 * @return true or false.
	 */
	boolean hasPermissionPop3(MailUser<?> mailUser);

	/**
	 * Get a MailUser object by username and domain.
	 * 
	 * @param username
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public MailUser<?> getMailUser(String username, String domain)
			throws Exception;

	/**
	 * Check whether a domain has SMTP permission.
	 * 
	 * @param domain
	 * @return
	 */
	public boolean hasPermissionSmtp(String domain);

	/**
	 * Check whether a mail user has SMTP permission.
	 * 
	 * @param mailUser
	 * @return
	 */
	public boolean hasPermissionSmtp(MailUser<?> mailUser);

	//这里的mailUser实际上是个group
	public List<MailUser<?>> getGrpMembers(MailUser<?> mailUser) ;

}
