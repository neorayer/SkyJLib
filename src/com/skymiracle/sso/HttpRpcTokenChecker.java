package com.skymiracle.sso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.logger.Logger;

public class HttpRpcTokenChecker implements TokenChecker {

	private String urlStr;

	private String trueResult;

	private String tokenArgName;

	public HttpRpcTokenChecker() {
		super();
	}

	public HttpRpcTokenChecker(String urlStr, String tokenArgName,
			String trueResult) throws MalformedURLException {
		this.urlStr = urlStr;
		this.trueResult = trueResult;
		this.tokenArgName = tokenArgName;
	}

	public String getTokenArgName() {
		return this.tokenArgName;
	}

	public void setTokenArgName(String tokenArgName) {
		this.tokenArgName = tokenArgName;
	}

	public String getTrueResult() {
		return this.trueResult;
	}

	public void setTrueResult(String trueResult) {
		this.trueResult = trueResult;
	}

	public String getUrlStr() {
		return this.urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public boolean checkToken(String token) throws IOException {
		URL url = new URL(this.urlStr + "&" + this.tokenArgName + "=" + token);
		Logger.debug(url.toString());
		String respStr = StreamPipe.inputToString(url.openStream(), "UTF-8",
				true);
		Logger.debug("response:" + respStr);
		return this.trueResult.trim().equals(respStr.trim());
	}

	public static void main(String[] args) throws Exception {
		Logger.setLevel(Logger.LEVEL_DEBUG);
		TokenChecker tc = new HttpRpcTokenChecker(
				"http://127.0.0.1:8080/SkySSO/pg/rpc/login/checkToken.jsp?",
				"token", "1");
		System.out.println(tc.checkToken("123456789"));
	}
}
