package com.skymiracle.mdo4.confDao;

public class LdapConf extends ConfDao {

	private String host = "127.0.0.1";

	private int port = 389;

	private String bindDN = "cn=Manager,o=wpx";

	private String bindPassword = "1qw2azxs";

	private String baseDN = "o=wpx";

	public LdapConf() {
	}

	public LdapConf(String host, int port, String bindDN, String bindPassword,
			String baseDN) {
		this();
		this.host = host;
		this.port = port;
		this.bindDN = bindDN;
		this.bindPassword = bindPassword;
		this.baseDN = baseDN;
	}

	public String getBaseDN() {
		return this.baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	public String getBindDN() {
		return this.bindDN;
	}

	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	public String getBindPassword() {
		return this.bindPassword;
	}

	public void setBindPassword(String bindPassword) {
		this.bindPassword = bindPassword;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
