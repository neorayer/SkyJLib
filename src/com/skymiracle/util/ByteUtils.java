package com.skymiracle.util;

import java.nio.ByteBuffer;

public class ByteUtils {
	public final static byte[] IPBUF_BROADCAST = new byte[] { (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff };

	public static boolean isBroadCastIp(byte[] ipbuf) {
		return ipbuf[3] == (byte)0xff;
//		return memcmp(ipbuf, IPBUF_BROADCAST, 4);
	}

	public static String b2s(byte b) {
		return "" + (0x000000ff & b);
	}

	public static String bs2ip(byte[] bs, int offset) {
		return b2s(bs[offset + 0]) + "." + b2s(bs[offset + 1]) + "."
				+ b2s(bs[offset + 2]) + "." + b2s(bs[offset + 3]);
	}

	public static String uint2ip(int v) {
		byte[] bs = uint2bs(v);
		return bs2ip(bs, 0);
	}

	public static int b2int(byte b) {
		return 0x000000ff & b;
	}

	public static int bs2ushort(byte[] bs, int offset) {
		return (b2int(bs[offset]) << 8) + b2int(bs[offset + 1]);
	}

	public static int bs2ushortReverse(byte[] bs, int offset) {
		return (b2int(bs[offset + 1]) << 8) + b2int(bs[offset]);
	}

	public static byte[] ushort2bs(int v) {
		byte[] bs = new byte[2];
		bs[0] = (byte) ((v & 0x0000ff00) >> 8);
		bs[1] = (byte) (v & 0x000000ff);
		return bs;
	}

	public static byte[] uint2bs(int v) {
		byte[] bs = new byte[4];
		bs[0] = (byte) ((v & 0xff000000) >> 24);
		bs[1] = (byte) ((v & 0x00ff0000) >> 16);
		bs[2] = (byte) ((v & 0x0000ff00) >> 8);
		bs[3] = (byte) (v & 0x000000ff);
		return bs;
	}

	public static byte[] ulong2bs(long v) {
		byte[] bs = new byte[8];
		bs[0] = (byte) ((v & 0xff00000000000000L) >> 56);
		bs[1] = (byte) ((v & 0x00ff000000000000L) >> 48);
		bs[2] = (byte) ((v & 0x0000ff0000000000L) >> 40);
		bs[3] = (byte) ((v & 0x000000ff00000000L) >> 32);
		bs[4] = (byte) ((v & 0x00000000ff000000L) >> 24);
		bs[5] = (byte) ((v & 0x0000000000ff0000L) >> 16);
		bs[6] = (byte) ((v & 0x000000000000ff00L) >> 8);
		bs[7] = (byte) (v & 0x00000000000000ffL);

		return bs;
	}

	public static long bs2ulong(byte[] bs) {
		return ByteBuffer.wrap(bs).asLongBuffer().get();
	}

	public static int bs2int(byte[] bs, int offset) {
		return (b2int(bs[offset + 0]) << 24) + (b2int(bs[offset + 1]) << 16)
				+ (b2int(bs[offset + 2]) << 8) + (b2int(bs[offset + 3]));
	}

	public static Integer ipbs2Integer(byte[] ipbs) {
		return new Integer(bs2int(ipbs, 0));
	}

	public static void memset(byte[] buf, byte b, int len) {
		for (int i = 0; i < len; i++) {
			buf[i] = b;
		}
	}

	public static void memcpy(byte[] desc, byte[] src, int len) {
		for (int i = 0; i < len; i++) {
			desc[i] = src[i];
		}
	}

	public static void memcpy(byte[] desc, int destOffset, byte[] src,
			int srcOffset, int len) {
		for (int i = 0; i < len; i++) {
			desc[i + destOffset] = src[i + srcOffset];
		}
	}

	public static boolean memcmp(byte[] desc, byte[] src, int len) {
		for (int i = 0; i < len; i++) {
			if (desc[i] != src[i])
				return false;
		}
		return true;
	}

	public static boolean memcmp(byte[] desc, int descOff, byte[] src) {
		if (desc.length - descOff < src.length)
			return false;
		for (int i = 0; i < src.length; i++) {
			if (desc[descOff + i] != src[i])
				return false;
		}
		return true;
	}

	public static byte[] cloneBytes(byte[] src, int len) {
		byte[] bs = new byte[len];
		memcpy(bs, src, len);
		return bs;
	}

	public static String b2Hex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF"
				.charAt(b & 0xf));
	}

	public static StringBuffer bs2Hex(byte[] bs, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append("0x" + b2Hex(bs[i]) + " ");
		}
		return sb;
	}
	
	public static StringBuffer bs2Hex(byte[] bs, int len, String prefix) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(prefix + b2Hex(bs[i]) + " ");
		}
		return sb;
	}

	public static int bs2intReverse(byte[] bs, int off) {
		return Integer.reverseBytes(bs2int(bs, off));
	}

	public static void main(String[] args) {
		System.out.println(ipbs2Integer(new byte[] { 10, 100, 1, 2 }));
		long l = System.currentTimeMillis();
		byte[] bs =ByteUtils.ulong2bs(l);
		long l2 = ByteUtils.bs2ulong(bs);
		System.out.println(l + " =?= " + l2);
	}

}
