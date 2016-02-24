package com.skymiracle.system;

import java.io.IOException;
import java.net.InetAddress;

public class NetTools {

	public static boolean isReachable(String host) throws IOException {
		InetAddress inetAddress = InetAddress.getByName(host);
		return inetAddress.isReachable(2000);
	}
}
