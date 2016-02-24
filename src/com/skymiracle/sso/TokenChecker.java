package com.skymiracle.sso;

public interface TokenChecker {

	public boolean checkToken(String token) throws Exception;
}
