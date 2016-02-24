package com.skymiracle.bin;

import java.io.*;

import com.skymiracle.util.ByteUtils;

public class BinCompare {

	public InputStream is1;

	public InputStream is2;

	public BinCompare(InputStream is1, InputStream is2) {
		super();
		this.is1 = is1;
		this.is2 = is2;
	}

	public void compare() throws IOException {
		int pos = -1;
		int diff = 0;
		while (true) {
			pos++;
			byte[] bs1 = new byte[1];
			byte[] bs2 = new byte[1];
			if (is1.read(bs1) <=0)
				break;
			if (is2.read(bs2) <=0)
				break;

			byte b1 = bs1[0];
			byte b2 = bs2[0];

			if (b1 == b2)
				continue;

//			System.out.println("-------------------");
//			System.out.printf("%d\tpos=%d %s %s\r\n", diff, i, ByteUtils
//			.b2Hex(b1), ByteUtils.b2Hex(b2));
			System.out.printf("kns[%d].pos = %d;\r\n", diff, pos);
			System.out.printf("kns[%d].val = 0x%s;\r\n", diff, ByteUtils
					.b2Hex(b2));
//			System.out.printf("kns[%d].val = 0x%s;\r\n", diff, ByteUtils
//					.b2Hex(b2));
			diff++;

		}
	}

	public static void main(String[] args) throws Exception {
		FileInputStream fis1 = new FileInputStream("c:\\temp\\del1\\game.dll");
		FileInputStream fis2 = new FileInputStream(
				"c:\\temp\\del1\\aysGame.dll");
		BufferedInputStream bis1 = new BufferedInputStream(fis1);
		BufferedInputStream bis2 = new BufferedInputStream(fis2);

		BinCompare bc = new BinCompare(bis1, bis2);
		bc.compare();

		bis1.close();
		bis2.close();
		fis1.close();
		fis2.close();
	}
}
