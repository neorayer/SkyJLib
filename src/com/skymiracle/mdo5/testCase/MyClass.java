package com.skymiracle.mdo5.testCase;

import java.util.HashMap;
import java.util.Map;

import com.danga.MemCached.*;

public class MyClass {

	// create a static client as most installs only need
	// a single instance
	protected static MemCachedClient mcc = new MemCachedClient();

	// set up connection pool once at class load
	static {

		// server list and weights
		String[] servers = { "10.1.1.1:11211" };

		Integer[] weights = { 3, 3, 2 };

		// grab an instance of our connection pool
		SockIOPool pool = SockIOPool.getInstance();

		// set the servers and the weights
		pool.setServers(servers);
		pool.setWeights(weights);

		// set some basic pool settings
		// 5 initial, 5 min, and 250 max conns
		// and set the max idle time for a conn
		// to 6 hours
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);

		// set the sleep for the maint thread
		// it will wake up every x seconds and
		// maintain the pool size
		pool.setMaintSleep(30);

		// set some TCP settings
		// disable nagle
		// set the read timeout to 3 secs
		// and don't set a connect timeout
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);

		// initialize the connection pool
		pool.initialize();

		// lets set some compression on for the client
		// compress anything larger than 64k
		mcc.setCompressEnable(true);
		mcc.setCompressThreshold(64 * 1024);
	}

	// from here on down, you can call any of the client calls
	public static void main(String[] args) {
		Mdo5User m = new Mdo5User();
		m.setUid("asd");
		m.setDc("asdf");
		
		S s = new S();
		Map map = new HashMap();
		
		System.out.println(mcc.set("foo5", m));
//		Mdo5User bar = (Mdo5User) mcc.get("foo5");
		System.out.println(mcc.get("foo5"));
	}
}
