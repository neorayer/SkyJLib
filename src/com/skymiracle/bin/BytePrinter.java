package com.skymiracle.bin;

import java.io.IOException;
import com.skymiracle.util.ByteUtils;

public class BytePrinter {

	public static void main(String[] args) throws Exception, IOException {
		{
			String s = "猿人";
			byte[] bs = s.getBytes("UTF-8");
			for (byte b : bs) {
				System.out.print(String.format("%02x ", b));
			}
			System.out.println();
		}
		{
			byte[] bs = new byte[]{(byte)0xe5, (byte)0xb0, (byte)0x8f ,(byte)0xe6 ,(byte)0x9c ,(byte)0xb1 ,(byte)0xe9,
					(byte)0x9b, (byte)0x80};
			System.out.println(new String(bs, "UTF-8"));
		}
	}
}
