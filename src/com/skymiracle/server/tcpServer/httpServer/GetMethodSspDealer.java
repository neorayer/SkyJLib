package com.skymiracle.server.tcpServer.httpServer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class GetMethodSspDealer implements SspDealer {

	public void deal(Socket socket, String path, String query)
			throws Exception {
		Map<String, String> paraMap = new HashMap<String, String>();
		if (query != null) {
			String[] ss = query.split("&");
			if (ss.length != 0) {
				for (String s : ss) {
					int eqIdx = s.indexOf('=');
					if (eqIdx <= 0)
						continue;
					String k = s.substring(0, eqIdx);
					String v = s.substring(eqIdx + 1);
					paraMap.put(k, v);
				}
			}
		}

		deal(socket, path, paraMap);
	}

	protected abstract void deal(Socket socket, String path,
			Map<String, String> paraMap) throws IOException, Exception;

}
