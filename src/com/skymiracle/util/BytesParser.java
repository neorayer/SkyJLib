package com.skymiracle.util;

import java.util.ArrayList;

import com.skymiracle.logger.Logger;

/**
 * 
 * @author Wang-jinpeng
 * 
 */
public class BytesParser {

	/**
	 * Parse bytes to StringBuffers, spilt bytes by 0, get StringBuffers from
	 * the bytes.
	 * 
	 * @param bytes
	 * @return StringBuffer[]
	 */
	public static StringBuffer[] getStrings(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		ArrayList arrayList = new ArrayList();
		int j = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0) {
				arrayList.add(j, sb);
				sb = new StringBuffer();
				j++;
				continue;
			}
			sb.append((char) bytes[i]);
			if (i == bytes.length - 1) {
				arrayList.add(j, sb);
			}
		}
		return (StringBuffer[]) arrayList.toArray(new StringBuffer[arrayList
				.size()]);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] bytes = { 0, 0, 'a', 'b', ' ', '1', 'a', 0, '2' };
		Logger.info("Begin");

		StringBuffer[] sb;
		sb = getStrings(bytes);
		for (int i = 0; i < sb.length; i++) {
			System.out.print(sb[i] + "\r\n");
		}

		Logger.info("End.");

	}

}
