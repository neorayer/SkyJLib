package com.skymiracle.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A UUID (from java.rmi.server.UID)
 * 
 * @version $Revision: 1.1 $
 * @author
 */
public final class UUID {

	/**
	 * @serial Integer that helps create a unique UID.
	 */
	private int unique;

	/**
	 * @serial Long used to record the time. The <code>time</code> will be
	 *         used to create a unique UID.
	 */
	private long time;

	/**
	 * InetAddress to make the UID globally unique
	 */
	private static String address;

	/**
	 * a random number
	 */
	private static int hostUnique;

	/**
	 * Used for synchronization
	 */
	private static Object mutex;

	private static long lastTime;

	private static long DELAY;

	private static String generateNoNetworkID() {
		String nid = Thread.activeCount() + System.getProperty("os.version")
				+ System.getProperty("user.name")
				+ System.getProperty("java.version");
		System.out.print(nid + "\r\n");
		Md5 md5 = new Md5(nid);
		md5.processString();
		return md5.getStringDigest();
	}

	static {
		hostUnique = (new Object()).hashCode();
		mutex = new Object();
		lastTime = System.currentTimeMillis();
		DELAY = 1; // in milliseconds
		try {
			String s = InetAddress.getLocalHost().getHostAddress();
			Md5 md5 = new Md5(s);
			md5.processString();
			address = md5.getStringDigest();
		} catch (UnknownHostException ex) {
			address = generateNoNetworkID();
		}
	}

	private java.util.UUID jUuid;
	public UUID() {
		jUuid = java.util.UUID.randomUUID();
//		synchronized (mutex) {
//			boolean done = false;
//			while (!done) {
//				this.time = System.currentTimeMillis();
//				if (this.time < lastTime + DELAY) {
//					// pause for a second to wait for time to change
//					try {
//						// Thread.currentThread().wait(DELAY);
//						Thread.sleep(DELAY);
//					} catch (java.lang.InterruptedException e) {
//						e.printStackTrace();
//					} // ignore exception
//					continue;
//				} else {
//					lastTime = this.time;
//					done = true;
//				}
//			}
//			this.unique = hostUnique;
//		}
	}

	@Override
	public String toString() {
		return jUuid.toString();
//		return Integer.toString(this.unique, 16) + "-"
//				+ Long.toString(this.time, 16) + "-" + address;
	}

	public String toShortString() {
		return jUuid.toString();
//		return Long.toString(this.time, 16) + address;
	}

	public String toTinyString() {
		return Long.toString(this.time, 16);
	}

	// public boolean equals(Object obj) {
	// if ((obj != null) && (obj instanceof UUID)) {
	// UUID uuid = (UUID) obj;
	// return (unique == uuid.unique && time == uuid.time && address
	// .equals(UUID.address));
	// } else {
	// return false;
	// }
	// }

	public static void main(String args[]) {
		new UUID();
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
//		System.out.print(new UUID().toShortString() + "\r\n");
		System.out.print(new UUID().toNumString());
		
		System.out.println(java.util.UUID.randomUUID().toString());
	}

	public String toNumString() {
		return Long.toString(this.time);
	}

}