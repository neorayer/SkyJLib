package com.skymiracle.util.simpleMap;

import junit.framework.TestCase;

public class Test extends TestCase{


	private void testSimpleMap(SimpleMap<String, String> map) {
		map.put("One", "TheOne");
		assertEquals("TheOne", map.get("One"));

		map.put("Two", "TheTwo");
		assertEquals("TheTwo", map.get("Two"));
		
		map.remove("One");
		assertNull(map.get(("One")));
		assertNull(map.get(("One")));

		map.put("Two", "TheTwo");
		assertEquals("TheTwo", map.get("Two"));		
	}
	
	public void testMcMap() {
		MemCachedMap<String, String> mcMap = new MemCachedMap<String, String>();
		mcMap.setServers(new String[]{"10.1.1.153:11211"});
		testSimpleMap(mcMap);
	}

	public void testSyncMap() {
		SyncRemoteMap<String, String> syncMap = new SyncRemoteMap<String, String>();
		MemCachedMap<String, String> rMap = new MemCachedMap<String, String>();
		syncMap.setRemoteMap(rMap);
		
		testSimpleMap(syncMap);
		
		syncMap.clearLocal();
		assertEquals("TheTwo", syncMap.get("Two"));
		
	}
}
