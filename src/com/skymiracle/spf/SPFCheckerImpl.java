package com.skymiracle.spf;

import org.apache.james.jspf.executor.SPFResult;
import org.apache.james.jspf.impl.DefaultSPF;


public class SPFCheckerImpl implements SPFChecker {
	
	public int doCheck(String ip, String domain) {
		DefaultSPF spf = new DefaultSPF();
		SPFResult res = spf.checkSPF(ip,"",domain);
		if (res.getResult().equalsIgnoreCase("pass")) {
			return SPFChecker.RESULT_SUCC;
		} else if (res.getResult().equalsIgnoreCase("none")) {
			return SPFChecker.RESULT_NONE;
		}else return SPFChecker.RESULT_FAIL;
	}

}
