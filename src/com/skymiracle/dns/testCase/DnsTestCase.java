package com.skymiracle.dns.testCase;

import javax.naming.NamingException;

import com.skymiracle.dns.Dns;

import junit.framework.TestCase;

public class DnsTestCase extends TestCase {

	private void resolveTextRecord(String domain) throws NamingException {
		String[] texts = Dns.resolveTextRecord("", domain);
		for (String txt : texts) {
			System.out.println(domain + ":\n" + txt + "\n");
		}
	}

	public void testResolveTextRecord() throws NamingException {
		String[] domains = { "163.com", "google.com", "sina.com", "yahoo.com",
				"hotmail.com", "21cn.com", "skymiracle.com", "sohu.com", "msn.com", "mail.com", "dell.com" };
		for(String domain: domains)
			resolveTextRecord(domain);
	}
}
