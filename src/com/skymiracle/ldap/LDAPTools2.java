package com.skymiracle.ldap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.novell.ldap.LDAPAddRequest;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSchema;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPDeleteRequest;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPMessageQueue;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPModifyDNRequest;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.LDAPObjectClassSchema;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPSchema;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPSearchResultReference;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.util.LDAPWriter;
import com.novell.ldap.util.LDIFReader;
import com.novell.ldap.util.LDIFWriter;
import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.BeanAttrPool;
import com.skymiracle.mdo4.DList;
import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.NotEmptyException;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;

public class LDAPTools2 {

	public static LDAPConnection getConn(String host, int port, String dn,
			String password) throws LDAPException,
			UnsupportedEncodingException, InterruptedException {
		LDAPConnection lc = new LDAPConnection();
		lc.connect(host, port);
		lc.bind(LDAPConnection.LDAP_V3, dn, password.getBytes("UTF8"));
		return lc;
	}

	public static void releaseConn(LDAPConnection con) {
		if (con == null)
			return;
		try {
			con.disconnect();
		} catch (LDAPException e) {
			Logger.error("LDAPTools.releaseConn()", e);
		}
	}

	public static boolean auth(String host, int port, String dn, String password) {
		if (password == null || password.equals(""))
			return false;
		Logger.debug("LDAPTools2.auth. dn=" + dn + " password=" + password);
		boolean flag = false;
		LDAPConnection lc = new LDAPConnection();
		try {
			lc.connect(host, port);
			lc.bind(LDAPConnection.LDAP_V3, dn, password.getBytes("UTF8"));
			flag = true;
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			try {
				lc.disconnect();
			} catch (LDAPException e) {
				e.printStackTrace();
				Logger.debug("", e);
			}
		}
		return flag;
	}

	public static boolean auth(String host, int port, String baseDN, Dao dao,
			String password) throws NullKeyException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String dn = dao.dn(baseDN);
		return auth(host, port, dn, password);
	}

	public static boolean compareAttributeValue(String host, int port,
			String bindDN, String bindPassword, String dn,
			String attributeName, String value) {
		// Logger.debug("LdapTools.compareAttributeValue. dn=" + dn
		// + " attributeName=" + attributeName + " value=" + value);
		boolean flag = false;
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			flag = lc.compare(dn, new LDAPAttribute(attributeName, value));
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return flag;
	}

	public static boolean dnExists(String host, int port, String bindDN,
			String bindPassword, String dn) throws LDAPException,
			UnsupportedEncodingException, InterruptedException {
		boolean flag = true;
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			try {
				Logger.debug("LdapTools.dnExists. dn:" + dn);
				lc.read(dn);
			} catch (LDAPException e1) {
				flag = false;
			}
			return flag;
		} catch (LDAPException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
	}

	public static String getAttributeValue(String host, int port,
			String bindDN, String bindPassword, String dn, String attributeName)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		String result = null;
		LDAPEntry entry = getLDAPEntry(host, port, bindDN, bindPassword, dn);
		if (entry != null) {
			LDAPAttribute le = entry.getAttribute(attributeName);
			if (le != null)
				result = le.getStringValue();
		}
		return result;

	}

	public static String setAttributeValue(String host, int port,
			String bindDN, String bindPassword, String dn,
			String attributeName, String attributeValue) {
		// Logger.debug("LdapTools.setAttributeValue. dn=" + dn
		// + " attributeName=" + attributeName);
		String result = null;
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);

			lc.modify(dn, new LDAPModification(LDAPModification.REPLACE,
					new LDAPAttribute(attributeName, attributeValue)));
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return result;
	}

	public static void modDao(Dao[] daos, String host, int port, String bindDN,
			String bindPassword, String baseDN, DaoAttrSet[] daoAttrSets)
			throws LDAPException, UnsupportedEncodingException,
			InterruptedException, NullKeyException {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);

			for (int di = 0; di < daos.length; di++) {
				List<LDAPModification> lmodList = new ArrayList<LDAPModification>();
				BeanAttr[] attrs = BeanAttrPool.getAttrs(daos[di].getClass());
				for (int i = 0; i < attrs.length; i++) {
					Object value = daoAttrSets[di].get(attrs[i].fieldname);
					if (value == null)
						continue;
					String strValue = value.toString();
					if (strValue.trim().length() == 0)
						continue;
					LDAPAttribute lAttr = null;
					if (attrs[i].type == String[].class)
						lAttr = new LDAPAttribute(attrs[i].fieldname,
								(String[]) value);
					else
						lAttr = new LDAPAttribute(attrs[i].fieldname, strValue);
					LDAPModification lmod = new LDAPModification(
							LDAPModification.REPLACE, lAttr);
					lmodList.add(lmod);
					Logger.debug("Will modify " + lmod);
				}
				lc.modify(daos[di].dn(baseDN), lmodList
						.toArray(new LDAPModification[0]));
			}
		} catch (LDAPException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
	}

	public static void addAttributeValue(String host, int port, String bindDN,
			String bindPassword, String dn, String attributeName,
			String attributeValue) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			lc.modify(dn, new LDAPModification(LDAPModification.ADD,
					new LDAPAttribute(attributeName, attributeValue)));
		} catch (Exception e) {
			Logger.error("", e);
		} finally {
			releaseConn(lc);
		}
	}

	public static void addNode(String host, int port, String bindDN,
			String bindPassword, LDAPEntry entry)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		addNode(host, port, bindDN, bindPassword, new LDAPEntry[] { entry });
	}

	public static void addNode(String host, int port, String bindDN,
			String bindPassword, LDAPEntry[] entrys)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			for (int i = 0; i < entrys.length; i++) {
				Logger.debug("Will add: " + entrys[i]);
				lc.add(entrys[i]);
				Logger.debug("A LDAPEntry is added: " + entrys[i]);
			}
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (LDAPException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
	}

	public static void delAttributeValue(String host, int port, String bindDN,
			String bindPassword, String dn, String attributeName,
			String[] attributeValues) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			for (int i = 0; i < attributeValues.length; i++)
				lc.modify(dn, new LDAPModification(LDAPModification.DELETE,
						new LDAPAttribute(attributeName, attributeValues[i])));
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
	}

	public static void delNode(String host, int port, String bindDN,
			String bindPassword, String[] dns)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException, NotEmptyException {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			for (int i = 0; i < dns.length; i++) {
				Logger.debug("Will delete dn:" + dns[i]);
				lc.delete(dns[i]);
				Logger.debug("A LDAPEntry is deleted. dn:" + dns[i]);
			}
		} catch (LDAPException e) {
			if (e.getResultCode() == LDAPException.NOT_ALLOWED_ON_NONLEAF)
				throw new NotEmptyException(e);
			else
				throw e;
		} finally {
			releaseConn(lc);

		}
	}

	public static String[] getSubNodeAttributeValues(String host, int port,
			String bindDN, String bindPassword, String dn, String attributeName) {
		String[] values = new String[0];
		ArrayList<String> valueList = new ArrayList<String>();
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			// LDAPSearchConstraints cons = new LDAPSearchConstraints();
			LDAPSearchResults searchResults = null;
			try {
				searchResults = lc.search(dn, LDAPConnection.SCOPE_ONE,
						"(objectClass=*)", null, false);
			} catch (LDAPException e) {
				Logger.debug("", e);
				return values;
			}
			if (searchResults == null)
				return values;
			while (searchResults.hasMore()) {
				try {
					LDAPEntry entry = searchResults.next();
					LDAPAttribute laTemp = entry.getAttribute(attributeName);
					if (laTemp == null)
						continue;
					String value = laTemp.getStringValue();
					valueList.add(value);
				} catch (LDAPException e) {
					Logger.debug("", e);
				}
			}

		} catch (Exception e) {
		} finally {
			releaseConn(lc);
		}
		return valueList.toArray(new String[0]);
	}

	public static <T extends Dao> DList<T> getDaos(Class<T> T, String host,
			int port, String bindDN, String bindPassword, String baseDN,
			DaoAttrSet daoAttrSet, String filter)
			throws UnsupportedEncodingException, IllegalArgumentException,
			LDAPException, InterruptedException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		return getDaos(T, host, port, bindDN, bindPassword, baseDN, daoAttrSet,
				LDAPConnection.SCOPE_SUB, filter);
	}

	public static <T extends Dao> DList<T> getDaos(Class<T> daoClass, String host,
			int port, String bindDN, String bindPassword, String baseDN,
			DaoAttrSet daoAttrSet, int scope, String filter)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		LDAPEntry[] entrys = getLDAPEntrys(host, port, bindDN, bindPassword,
				baseDN, scope, getFilter(daoClass, daoAttrSet, filter), null);
		return getDaos(entrys, daoClass);
	}

	private static String getFilter(Class daoClass, DaoAttrSet daoAttrSet,
			String filter) throws InstantiationException,
			IllegalAccessException {
		Dao dao = (Dao) daoClass.newInstance();
		StringBuffer sb = new StringBuffer();
		sb.append("(&");
		String[] ocs = dao.objectClasses();
		for (int i = 0; i < ocs.length; i++)
			sb.append("(objectclass=").append(ocs[i]).append(')');
		if (daoAttrSet != null) {
			Set keySet = daoAttrSet.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object value = daoAttrSet.get(key);
				sb.append("(").append(key).append("=").append(value)
						.append(')');
			}
		}
		if (filter != null)
			sb.append(filter);
		sb.append(')');
		return sb.toString();
	}

	public static <T extends Dao> T getDao(Class<T> daoClass, String host,
			int port, String bindDN, String bindPassword, String dn)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		LDAPEntry entry = getLDAPEntry(host, port, bindDN, bindPassword, dn);
		List<T> daos = LDAPTools2.getDaos(new LDAPEntry[] { entry }, daoClass);
		if (daos.size() == 0)
			return null;
		return daos.get(0);
	}

	public static void loadDao(Dao dao, String host, int port, String bindDN,
			String bindPassword, String baseDN)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NullKeyException {
		Class daoClass = dao.getClass();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
		LDAPEntry entry = getLDAPEntry(host, port, bindDN, bindPassword, dao
				.dn(baseDN));
		for (int i = 0; i < attrs.length; i++) {
			LDAPAttribute ldapAttr = entry.getAttribute(attrs[i].fieldname);
			if (ldapAttr == null)
				continue;
			Object arg = null;
			Class type = attrs[i].type;
			if (type == String[].class)
				arg = ldapAttr.getStringValueArray();
			else if (type == String.class)
				arg = ldapAttr.getStringValue();
			else if (type == long.class)
				arg = new Long(Long.parseLong(ldapAttr.getStringValue()));
			else if (type == int.class)
				arg = new Integer(Integer.parseInt(ldapAttr.getStringValue()));
			else if (type == short.class)
				arg = new Short(Short.parseShort(ldapAttr.getStringValue()));
			else if (type == byte.class)
				arg = new Byte(Byte.parseByte(ldapAttr.getStringValue()));
			else if (type == double.class)
				arg = new Double(Double.parseDouble(ldapAttr.getStringValue()));
			else if (type == float.class)
				arg = new Float(Float.parseFloat(ldapAttr.getStringValue()));
			else if (type == boolean.class)
				arg = new Boolean(Boolean.parseBoolean(ldapAttr
						.getStringValue()));
			attrs[i].setMethod.invoke(dao, new Object[] { arg });
		}
	}

	public static LDAPEntry getLDAPEntry(String host, int port, String bindDN,
			String bindPassword, String dn)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			Logger.debug("Get a LDAPEntry dn:" + dn);
			LDAPEntry entry = lc.read(dn);
			return entry;
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (LDAPException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
	}

	public static void empty(String host, int port, String bindDN,
			String bindPassword, String baseDN, Class daoClass)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException, InstantiationException,
			IllegalAccessException {
		String filter = getFilter(daoClass, null, null);
		empty(host, port, bindDN, bindPassword, baseDN, filter);
	}

	public static void empty(String host, int port, String bindDN,
			String bindPassword, String baseDN, String filter)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			LDAPSearchResults searchResults = null;
			searchResults = lc.search(baseDN, LDAPConnection.SCOPE_ONE, filter,
					null, false);
			Logger.debug("Will empty  baseDN:" + baseDN + ". filter=" + filter);
			if (searchResults == null)
				return;
			while (searchResults.hasMore()) {
				LDAPEntry entry = searchResults.next();
				Logger.debug("Will delete  dn:" + entry.getDN());
				lc.delete(entry.getDN());
			}
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (LDAPException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
	}

	public static LDAPEntry[] getLDAPEntrys(String host, int port,
			String bindDN, String bindPassword, String baseDN, int scope,
			String filter, String[] fields)
			throws UnsupportedEncodingException, LDAPException,
			InterruptedException {
		ArrayList<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			LDAPSearchResults searchResults = null;
			Logger.debug("Search ldap server. baseDN=" + baseDN + ". filter="
					+ filter);
			searchResults = lc.search(baseDN, scope, filter, fields, false);
			if (searchResults == null)
				return new LDAPEntry[0];
			while (searchResults.hasMore()) {
				try {
					LDAPEntry entry = searchResults.next();
					entryList.add(entry);
				} catch (LDAPException e) {
					Logger.debug("", e);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (LDAPException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			releaseConn(lc);
		}
		return entryList.toArray(new LDAPEntry[0]);
	}

	public static void putLDIF(String host, int port, String bindDN,
			String bindPassword, String ldif) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			ByteArrayInputStream baio;
			try {
				baio = new ByteArrayInputStream(ldif.getBytes("UTF-8"));
				LDIFReader reader = new LDIFReader(baio);
				LDAPMessage msg;
				while ((msg = reader.readMessage()) != null) {
					lc.sendRequest(msg, null, null);
				}
			} catch (Exception e) {
				Logger.debug("", e);
			}
		} catch (Exception e) {
		} finally {
			releaseConn(lc);
		}

	}

	public static String getLDIF(String host, int port, String bindDN,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			LDAPWriter writer = new LDIFWriter(baos);
			LDAPEntry entry = lc.read(dn);
			writer.writeEntry(entry);
			writer.finish();
			String s = baos.toString("UTF-8");
			baos.close();
			return s;
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return null;
	}

	public static void exportLDIFFile(String host, int port, String bindDN,
			String bindPassword, String dn, String filePath) {
		LDAPConnection lc = null;
		LDAPMessage msg;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			LDAPSearchQueue queue = lc.search(dn, // container to search
					LDAPConnection.SCOPE_SUB,// search container only
					"(objectClass=*)", // search filter
					null, // return all attrs
					false, // return attrs and values
					(LDAPSearchQueue) null,// default search queue
					(LDAPSearchConstraints) null);// default
			// constraints

			fos = new FileOutputStream(filePath);
			bos = new BufferedOutputStream(fos);
			LDAPWriter writer = new LDIFWriter(bos);
			while ((msg = queue.getResponse()) != null) {
				if (msg instanceof LDAPSearchResultReference) {
					String urls[] = ((LDAPSearchResultReference) msg)
							.getReferrals();
					Logger.info("Search result references:");
					for (int i = 0; i < urls.length; i++)
						Logger.info(urls[i]);
				}
				// the message is a search result
				else if (msg instanceof LDAPSearchResult) {
					writer.writeMessage(msg);
				}
				// the message is a search response
				else {
					LDAPResponse response = (LDAPResponse) msg;
					int status = response.getResultCode();
					// the return code is LDAP success

					if (status == LDAPException.SUCCESS) {
						Logger.info("Asynchronous search succeeded.");
					}
					// the reutrn code is referral exception
					else if (status == LDAPException.REFERRAL) {
						String urls[] = ((LDAPResponse) msg).getReferrals();
						Logger.info("Referrals:");
						for (int i = 0; i < urls.length; i++)
							Logger.info(urls[i]);
					} else {
						Logger.info("Asynchronous search failed.");
						throw new LDAPException(response.getErrorMessage(),
								status, response.getMatchedDN());
					}
				}
			}

			writer.finish();
		} catch (Exception e) {
			Logger.error("", e);
		} finally {
			try {
				bos.close();
				fos.close();
			} catch (IOException e) {
				Logger.error("", e);
			}
			releaseConn(lc);
		}
	}

	public static void importLDIF(String host, int port, String bindDN,
			String password, String content) {
		LDAPConnection lc = null;
		LDAPMessage msg, retMsg;
		LDAPEntry entry;
		try {
			lc = getConn(host, port, bindDN, password);
			ByteArrayInputStream bais = new ByteArrayInputStream(content
					.getBytes("UTF-8"));
			LDIFReader reader = new LDIFReader(bais);
			if (!reader.isRequest()) {
				while ((msg = reader.readMessage()) != null) {
					entry = ((LDAPSearchResult) msg).getEntry();
					Logger.info("LdapTools.importLDIF, DN:" + entry.getDN());
				}
			} else {
				while ((msg = reader.readMessage()) != null) {
					if (msg instanceof LDAPAddRequest) {
						Logger.debug("Adding entry...");
					} else if (msg instanceof LDAPDeleteRequest) {
						Logger.debug("Deleting entry...");
					} else if (msg instanceof LDAPModifyDNRequest) {
						Logger.debug("Modifying entry's RDN...");
					} else if (msg instanceof LDAPModifyRequest) {
						Logger.debug("Modifying entry's attribute(s)...");
					}

					LDAPMessageQueue queue = lc.sendRequest(msg, null, null);
					if ((retMsg = queue.getResponse()) != null) {
						LDAPResponse response = (LDAPResponse) retMsg;
						int status = response.getResultCode();

						// the return code is LDAP success
						if (status == LDAPException.SUCCESS) {
							Logger.debug("Directory information has been"
									+ " modified.");
						}
						// the reutrn code is referral exception

						else if (status == LDAPException.REFERRAL) {
							String urls[] = ((LDAPResponse) retMsg)
									.getReferrals();
							Logger.debug("Referrals:");
							for (int i = 0; i < urls.length; i++)
								Logger.debug("    " + urls[i]);
						} else {
							Logger.error(response.getErrorMessage());
						}
					}
				}
			}
			bais.close();

		} catch (Exception e) {
			Logger.error("", e);
		} finally {
			releaseConn(lc);
		}
	}

	public static LDAPAttributeSet getAttributeSet(String host, int port,
			String bindDN, String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			LDAPEntry entry = lc.read(dn);
			if (entry != null) {
				return entry.getAttributeSet();
			}
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return null;
	}

	public static boolean isExist(String host, int port, String bindDN,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			if (lc == null)
				return true;
		} catch (Exception e) {
			return true;
		}
		try {
			lc.read(dn);
			return true;
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return false;
	}

	public static String getAliasValue(String host, int port, String bindDN,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			LDAPEntry entry = lc.read(dn);
			if (entry != null) {
				LDAPAttribute le = entry.getAttribute("aliasedObjectName");
				if (le != null)
					dn = le.getStringValue();
			}
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return dn;
	}

	public static String[] DListObjectClassSchemaNames(String host, int port,
			String bindDN, String bindPassword, int type) {
		LDAPConnection lc = null;
		LDAPSchema schema = null;
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			schema = lc.fetchSchema(lc.getSchemaDN());
			LDAPObjectClassSchema nextOCSchema = null;
			ArrayList<String> objectClassNameList = new ArrayList<String>();
			Enumeration OCSchema = schema.getObjectClassSchemas();
			while (OCSchema.hasMoreElements()) {
				nextOCSchema = (LDAPObjectClassSchema) OCSchema.nextElement();
				if (type < 1 || type == nextOCSchema.getType() || type > 2) {
					String[] names = nextOCSchema.getNames();
					for (int i = 0; i < names.length; i++)
						objectClassNameList.add(names[i]);
				}
			}

			String[] objClassNames = new String[objectClassNameList.size()];
			for (int i = 0; i < objClassNames.length; i++)
				objClassNames[i] = objectClassNameList.get(i);
			Arrays.sort(objClassNames);
			return objClassNames;

		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return new String[0];
	}

	public static ObjectClassSchema getObjectClassSchema(String host, int port,
			String bindDN, String bindPassword, String schemaName) {
		LDAPConnection lc = null;
		LDAPSchema schema = null;
		ObjectClassSchema ocSchema = new ObjectClassSchema(schemaName);
		try {
			lc = getConn(host, port, bindDN, bindPassword);
			schema = lc.fetchSchema(lc.getSchemaDN());
			LDAPObjectClassSchema locSchema = schema
					.getObjectClassSchema(schemaName);
			ocSchema.setType(locSchema.getType());
			ocSchema.setDescription(locSchema.getDescription());
			ocSchema.setValue(locSchema.getStringValue());
			String[] reqAttrNames = locSchema.getRequiredAttributes();
			String[] optAttrNames = locSchema.getOptionalAttributes();

			ArrayList<AttributeSchema> reqAttrSchemaList = new ArrayList<AttributeSchema>();
			if (reqAttrNames != null)
				for (int i = 0; i < reqAttrNames.length; i++) {
					LDAPAttributeSchema attrSchema = schema
							.getAttributeSchema(reqAttrNames[i]);
					AttributeSchema as = new AttributeSchema(reqAttrNames[i]);
					as.setValue(attrSchema.getStringValue());
					reqAttrSchemaList.add(as);
				}
			ocSchema.setReqAttrList(reqAttrSchemaList);

			ArrayList<AttributeSchema> optAttrSchemaList = new ArrayList<AttributeSchema>();
			if (optAttrNames != null)
				for (int i = 0; i < optAttrNames.length; i++) {
					LDAPAttributeSchema attrSchema = schema
							.getAttributeSchema(optAttrNames[i]);
					AttributeSchema as = new AttributeSchema(optAttrNames[i]);
					as.setValue(attrSchema.getStringValue());
					optAttrSchemaList.add(as);
				}
			ocSchema.setOptAttrList(optAttrSchemaList);
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			releaseConn(lc);
		}
		return ocSchema;
	}

	public static void addDaos(String host, int port, String bindDN,
			String bindPassword, Dao[] daos, String baseDN)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, UnsupportedEncodingException,
			LDAPException, InterruptedException, NullKeyException {
		LDAPEntry[] entrys = getLDAPEntrys(daos, baseDN);
		addNode(host, port, bindDN, bindPassword, entrys);
	}

	public static void addDao(String host, int port, String bindDN,
			String bindPassword, Dao dao, String baseDN)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, UnsupportedEncodingException,
			LDAPException, InterruptedException, NullKeyException {
		LDAPEntry entry = getLDAPEntrys(dao, baseDN);
		addNode(host, port, bindDN, bindPassword, entry);
	}

	private static LDAPEntry getLDAPEntrys(Dao dao, String baseDN)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NullKeyException {
		return getLDAPEntrys(new Dao[] { dao }, baseDN)[0];
	}

	private static LDAPEntry[] getLDAPEntrys(Dao[] daos, String baseDN)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NullKeyException {
		if (daos.length == 0)
			return new LDAPEntry[0];
		LDAPEntry[] entrys = new LDAPEntry[daos.length];
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daos[0].getClass());
		for (int di = 0; di < daos.length; di++) {
			Dao dao = daos[di];
			LDAPAttributeSet laSet = new LDAPAttributeSet();
			for (int i = 0; i < attrs.length; i++) {
				String name = attrs[i].fieldname;
				Method getterMethod = attrs[i].getMethod;
				Object value = getterMethod.invoke(dao, new Object[0]);
				if (value == null)
					continue;
				String strValue = value.toString();
				if (strValue.trim().length() == 0)
					continue;
				if (attrs[i].type == String[].class)
					laSet.add(new LDAPAttribute(name, (String[]) value));
				else
					laSet.add(new LDAPAttribute(name, strValue));

			}
			String dn = dao.dn(baseDN);
			laSet.add(new LDAPAttribute("objectClass", dao.objectClasses()));
			entrys[di] = new LDAPEntry(dn, laSet);
		}
		return entrys;
	}

	private static <T extends Dao> DList<T> getDaos(LDAPEntry[] entrys,
			Class<? extends Dao> T) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		DList<T> list = new DList<T>();
		if (entrys.length == 0)
			return list;
		BeanAttr[] attrs = BeanAttrPool.getAttrs(T);
		for (int ei = 0; ei < entrys.length; ei++) {
			LDAPEntry entry = entrys[ei];
			T newDao = (T) T.newInstance();
			for (int i = 0; i < attrs.length; i++) {
				LDAPAttribute ldapAttr = entry.getAttribute(attrs[i].fieldname);
				if (ldapAttr == null)
					continue;
				Object arg = null;
				Class<? extends Object> type = attrs[i].type;
				try {
					if (type == String[].class)
						arg = ldapAttr.getStringValueArray();
					else if (type == String.class)
						arg = ldapAttr.getStringValue();
					else if (type == long.class)
						arg = new Long(Long
								.parseLong(ldapAttr.getStringValue()));
					else if (type == int.class)
						arg = new Integer(Integer.parseInt(ldapAttr
								.getStringValue()));
					else if (type == short.class)
						arg = new Short(Short.parseShort(ldapAttr
								.getStringValue()));
					else if (type == byte.class)
						arg = new Byte(Byte
								.parseByte(ldapAttr.getStringValue()));
					else if (type == double.class)
						arg = new Double(Double.parseDouble(ldapAttr
								.getStringValue()));
					else if (type == float.class)
						arg = new Float(Float.parseFloat(ldapAttr
								.getStringValue()));
					else if (type == boolean.class)
						arg = new Boolean(Boolean.parseBoolean(ldapAttr
								.getStringValue()));
				} catch (java.lang.NumberFormatException e) {
					Logger.error(attrs[i].fieldname, e);
				}
				attrs[i].setMethod.invoke(newDao, new Object[] { arg });
			}
			list.add(newDao);
		}
		return list;
	}

}
