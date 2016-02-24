package com.skymiracle.ldap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

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
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.util.LDAPWriter;
import com.novell.ldap.util.LDIFReader;
import com.novell.ldap.util.LDIFWriter;
import com.skymiracle.logger.Logger;

public class LdapTools {

	public static LDAPConnection getConn(String host, int port, String dn,
			String password) throws LDAPException,
			UnsupportedEncodingException, InterruptedException {
		LDAPConnection lc = new LDAPConnection();
		lc.connect(host, port);
		lc.bind(LDAPConnection.LDAP_V3, dn, password.getBytes("UTF8"));
		return lc;
	}

	public static void releaseConn(LDAPConnection con) throws LDAPException {
		if (con == null)
			return;
		con.disconnect();
	}

	public static boolean auth(String host, int port, String dn, String password) {
		if (password == null || password.equals(""))
			return false;
		// Logger.debug("LdapTools.auth. dn=" + dn + " password=" + password);
		boolean flag = false;
		byte[] bs;
		try {
			bs = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e2) {
			Logger.error("", e2);
			return flag;
		}

		LDAPConnection lc = new LDAPConnection();
		try {
			lc.connect(host, port);
		} catch (LDAPException e1) {
			Logger.error("", e1);
			return flag;
		}

		try {
			lc.bind(LDAPConnection.LDAP_V3, dn, bs);
			flag = true;
		} catch (Exception e) {
		}

		try {
			lc.disconnect();
		} catch (LDAPException e) {
			e.printStackTrace();
			Logger.debug("", e);
		}

		return flag;
	}

	public static boolean compareAttributeValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn,
			String attributeName, String value) {
		// Logger.debug("LdapTools.compareAttributeValue. dn=" + dn
		// + " attributeName=" + attributeName + " value=" + value);
		boolean flag = false;
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			flag = lc.compare(dn, new LDAPAttribute(attributeName, value));
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return flag;
	}

	public static boolean dnExists(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn) {
		// Logger.debug("LdapTools.dnExists. dn=" + dn);
		boolean flag = false;
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			lc.read(dn);
			flag = true;
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return flag;
	}

	public static LDAPEntry getLDAPEntry(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			LDAPEntry entry = lc.read(dn);
			return entry;
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return null;
	}

	public static String getAttributeValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn, String attributeName) {
		String result = null;
		LDAPEntry entry = getLDAPEntry(ldapHost, ldapPort, bindDn,
				bindPassword, dn);
		if (entry != null) {
			LDAPAttribute le = entry.getAttribute(attributeName);
			if (le != null)
				result = le.getStringValue();
		}
		return result;

	}

	public static String setAttributeValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn,
			String attributeName, String attributeValue) {
		// Logger.debug("LdapTools.setAttributeValue. dn=" + dn
		// + " attributeName=" + attributeName);
		String result = null;
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);

			lc.modify(dn, new LDAPModification(LDAPModification.REPLACE,
					new LDAPAttribute(attributeName, attributeValue)));
		} catch (Exception e) {
			// Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return result;
	}

	public static void addAttributeValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn,
			String attributeName, String attributeValue) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			lc.modify(dn, new LDAPModification(LDAPModification.ADD,
					new LDAPAttribute(attributeName, attributeValue)));
		} catch (Exception e) {
			Logger.error("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
	}

	public static void addNode(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, LDAPEntry entry) {
		addNode(ldapHost, ldapPort, bindDn, bindPassword,
				new LDAPEntry[] { entry });
	}

	public static void addNode(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, LDAPEntry[] entrys) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			for (int i = 0; i < entrys.length; i++)
				lc.add(entrys[i]);
		} catch (Exception e) {
			Logger.error("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}

	}

	public static void delAttributeValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn,
			String attributeName, String[] attributeValues) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			for (int i = 0; i < attributeValues.length; i++)
				lc.modify(dn, new LDAPModification(LDAPModification.DELETE,
						new LDAPAttribute(attributeName, attributeValues[i])));
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
	}

	public static boolean delNode(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			lc.delete(dn);
			return true;
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return false;
	}

	public static String[] getSubNodeAttributeValues(String ldapHost,
			int ldapPort, String bindDn, String bindPassword, String dn,
			String attributeName) {
		return getSubNodeAttributeValues(ldapHost, ldapPort, bindDn,
				bindPassword, dn, attributeName, "(objectClass=*)");
	}

	public static String[] getSubNodeAttributeValues(String ldapHost,
			int ldapPort, String bindDn, String bindPassword, String dn,
			String attributeName, String filter) {
		String[] values = new String[0];
		ArrayList<String> valueList = new ArrayList<String>();
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			// LDAPSearchConstraints cons = new LDAPSearchConstraints();
			LDAPSearchResults searchResults = null;
			try {
				searchResults = lc.search(dn, LDAPConnection.SCOPE_ONE, filter,
						null, false);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return valueList.toArray(new String[0]);
	}

	public static String[] getSubNodes(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String fatherDn) {
		String[] values = new String[0];
		ArrayList<String> valueList = new ArrayList<String>();
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			LDAPSearchResults searchResults = null;
			try {
				searchResults = lc.search(fatherDn, LDAPConnection.SCOPE_ONE,
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
					valueList.add(entry.getDN());
				} catch (LDAPException e) {
					Logger.debug("", e);
				}
			}

		} catch (Exception e) {
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return valueList.toArray(new String[0]);
	}

	public static void putLDIF(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, String ldif) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}

	}

	public static String getLDIF(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return null;
	}

	public static void importLDIF(String host, int port, String bindDn,
			String password, String content) {
		LDAPConnection lc = null;
		LDAPMessage msg, retMsg;
		LDAPEntry entry;
		try {
			lc = Connection.getCon(host, port, bindDn, password);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.error("", e);
			}
		}
	}

	public static LDAPAttributeSet getAttributeSet(String ldapHost,
			int ldapPort, String bindDn, String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			LDAPEntry entry = lc.read(dn);
			if (entry != null) {
				return entry.getAttributeSet();
			}
		} catch (Exception e) {
			Logger.debug("", e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return null;
	}

	public static boolean isExist(String ldapHost, int ldapPort, String bindDn,
			String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return false;
	}

	public static String getAliasValue(String ldapHost, int ldapPort,
			String bindDn, String bindPassword, String dn) {
		LDAPConnection lc = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
			// System.out.println((ldapHost +","+ ldapPort +","+ bindDn +","+
			// bindPassword));
			LDAPEntry entry = lc.read(dn);
			if (entry != null) {
				LDAPAttribute le = entry.getAttribute("aliasedObjectName");
				if (le != null)
					dn = le.getStringValue();
			}
		} catch (Exception e) {
			Logger.debug("dn:" + dn, e);
		} finally {
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return dn;
	}

	public static String[] listObjectClassSchemaNames(String ldapHost,
			int ldapPort, String bindDn, String bindPassword, int type) {
		LDAPConnection lc = null;
		LDAPSchema schema = null;
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return new String[0];
	}

	public static ObjectClassSchema getObjectClassSchema(String ldapHost,
			int ldapPort, String bindDn, String bindPassword, String schemaName) {
		LDAPConnection lc = null;
		LDAPSchema schema = null;
		ObjectClassSchema ocSchema = new ObjectClassSchema(schemaName);
		try {
			lc = Connection.getCon(ldapHost, ldapPort, bindDn, bindPassword);
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
			try {
				Connection.releaseCon(lc);
			} catch (LDAPException e) {
				Logger.debug("", e);
			}
		}
		return ocSchema;
	}

}
