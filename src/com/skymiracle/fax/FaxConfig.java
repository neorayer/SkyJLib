package com.skymiracle.fax;

public class FaxConfig {
	// 中转邮箱
	private String relayEmail;

	// 网络传真服务器host
	private String faxServerHost;

	// 网络传真服务器port
	private int faxServerPort;

	public String getRelayEmail() {
		return relayEmail;
	}

	public void setRelayEmail(String relayEmail) {
		this.relayEmail = relayEmail;
	}

	public String getFaxServerHost() {
		return faxServerHost;
	}

	public void setFaxServerHost(String faxServerHost) {
		this.faxServerHost = faxServerHost;
	}

	public int getFaxServerPort() {
		return faxServerPort;
	}

	public void setFaxServerPort(int faxServerPort) {
		this.faxServerPort = faxServerPort;
	}

}
