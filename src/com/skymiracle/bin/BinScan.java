package com.skymiracle.bin;

import java.io.IOException;
import com.skymiracle.util.ByteUtils;

public class BinScan {

	public static void main(String[] args) throws Exception, IOException {
		// byte[] bs = StreamPipe.fileToBytes(new File(
		// "c:\\Applications\\wc3\\replay\\LastReplay.w3g"));
		// byte[] srcBs = new byte[] { (byte) 0xE6, (byte) 0x98, (byte) 0xAF };
		// for (int i = 0; i < bs.length; i++) {
		// boolean isSame = ByteUtils.memcmp(bs, i, srcBs);
		// if (isSame)
		// System.out.println(true);
		// }

		// byte[] bs = new byte[] { (byte) 0x31, (byte) 0x31, (byte) 0x32,
		// (byte) 0x32, (byte) 0x33, (byte) 0x33, (byte) 0x34,
		// (byte) 0x34, (byte) 0x35, (byte) 0x35, (byte) 0x36,
		// (byte) 0x36, (byte) 0x37, (byte) 0x37, (byte) 0xE6,
		// (byte) 0x98, (byte) 0xAF, (byte) 0xE8, (byte) 0x83,
		// (byte) 0x9C, (byte) 0xE5, (byte) 0x88, (byte) 0xA9,
		// (byte) 0xE8, (byte) 0x80, (byte) 0x85, (byte) 0xE3,
		// (byte) 0x80, (byte) 0x82 };
		// byte[] bs = new byte[] { (byte) 0xE8, (byte) 0x83, (byte) 0x9C,
		// (byte) 0xE5, (byte) 0x88, (byte) 0xA9 };

		// 蜀國勝利了！
		byte tag_bs_ar[] = { (byte) 0xE8, (byte) 0x9C, (byte) 0x80,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81 };
		System.out.println(new String(tag_bs_ar, "UTF-8"));
		//byte[] bs2 = { (byte) 0xE3, (byte) 0x80, (byte) 0x82 };
//		byte[] bs3 = { (byte) 0xE8, (byte) 0x9C, (byte) 0x80, (byte) 0xE5,
//				(byte) 0x9C, (byte) 0x8B, (byte) 0xE5, (byte) 0x8B,
//				(byte) 0x9D, (byte) 0xE5, (byte) 0x88, (byte) 0xA9,
//				(byte) 0xE4, (byte) 0xBA, (byte) 0x86, (byte) 0xEF,
//				(byte) 0xBC, (byte) 0x81
//
//		};
//		byte[] bs5 = new byte[] { (byte) 0x88, (byte) 0xa9, (byte) 0xe4,
//				(byte) 0xef, (byte) 0xbc, (byte) 0x81 };
		byte[] bs6 = new byte[] { (byte) 0xe4, (byte) 0xbd, (byte) 0xa0,
				(byte) 0xe8, (byte) 0xa2, (byte) 0xab, (byte) 0xe6,
				(byte) 0xbb, (byte) 0x85, (byte) 0xe5, (byte) 0x9c,
				(byte) 0x8b, (byte) 0xe4, (byte) 0xba, (byte) 0x86 };
		String s = new String(bs6, "UTF-8");
		System.out.println(s);
		s = "魏國勝利了！";
		// s = "你要出的武";
		// s = "你被滅國了";
		// s = "武具行";
		// s = "总公司";
		// s = "蜀國勝利了！";
		// s = "魏国胜利了！";
		// s = "魔手猿人已经被歼灭";
		// s = "你没能达到胜利";
		// s = "SkyMiracle是胜利者。";
		// s = "SkyMiracle是胜利";
		// s = "SkyMiracle";
		// s = "V3.9d";
		// s = "|c0020c000天灾军团|r 取得了胜";
		// s = "|c00ff0303近卫军团|r 取得了胜";
		// s = "蜀国胜利了";
		// s = "取得了胜";
		byte[] bs4 = s.getBytes("UTF-8");
		System.out.println(ByteUtils.bs2Hex(bs4, bs4.length, ""));
		System.out.print(ByteUtils.bs2Hex(bs4, bs4.length, ",(byte)0x"));

		byte[] bst = new byte[] { (byte) 0x18, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE8, (byte) 0xA3, (byte) 0x85,
				(byte) 0xE8, (byte) 0xBD, (byte) 0xBD, (byte) 0xE6,
				(byte) 0xB8, (byte) 0xB8, (byte) 0xE6, (byte) 0x88,
				(byte) 0x8F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE4, (byte) 0xBF, (byte) 0x9D,
				(byte) 0xE5, (byte) 0xAD, (byte) 0x98, (byte) 0xE6,
				(byte) 0xB8, (byte) 0xB8, (byte) 0xE6, (byte) 0x88,
				(byte) 0x8F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE8, (byte) 0xA6, (byte) 0x86,
				(byte) 0xE7, (byte) 0x9B, (byte) 0x96, (byte) 0xE5,
				(byte) 0xAD, (byte) 0x98, (byte) 0xE6, (byte) 0xA1,
				(byte) 0xA3, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE4, (byte) 0xBD, (byte) 0xA0,
				(byte) 0xE8, (byte) 0xA2, (byte) 0xAB, (byte) 0xE6,
				(byte) 0xBB, (byte) 0x85, (byte) 0xE5, (byte) 0x9C,
				(byte) 0x8B, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0x20, (byte) 0xE9, (byte) 0x81, (byte) 0x8A,
				(byte) 0xE6, (byte) 0x88, (byte) 0xB2, (byte) 0xE7,
				(byte) 0xB5, (byte) 0x90, (byte) 0xE6, (byte) 0x9D,
				(byte) 0x9F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE4, (byte) 0xBD, (byte) 0xA0,
				(byte) 0xE8, (byte) 0xA2, (byte) 0xAB, (byte) 0xE6,
				(byte) 0xBB, (byte) 0x85, (byte) 0xE5, (byte) 0x9C,
				(byte) 0x8B, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0x20, (byte) 0xE9, (byte) 0x81, (byte) 0x8A,
				(byte) 0xE6, (byte) 0x88, (byte) 0xB2, (byte) 0xE7,
				(byte) 0xB5, (byte) 0x90, (byte) 0xE6, (byte) 0x9D,
				(byte) 0x9F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x03,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0xAD, (byte) 0x8F,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81, (byte) 0xE4,
				(byte) 0xBA, (byte) 0x82, (byte) 0xE4, (byte) 0xB8,
				(byte) 0x96, (byte) 0x4F, (byte) 0x4F, (byte) 0x4F,
				(byte) 0xE9, (byte) 0x9B, (byte) 0x84, (byte) 0xE6,
				(byte) 0x89, (byte) 0x8D, (byte) 0xE6, (byte) 0x98,
				(byte) 0xAF, (byte) 0xE7, (byte) 0x8E, (byte) 0x8B,
				(byte) 0xE8, (byte) 0x80, (byte) 0x85, (byte) 0x0D,
				(byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x05,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0xAD, (byte) 0x8F,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81, (byte) 0xE4,
				(byte) 0xBA, (byte) 0x82, (byte) 0xE4, (byte) 0xB8,
				(byte) 0x96, (byte) 0x4F, (byte) 0x4F, (byte) 0x4F,
				(byte) 0xE9, (byte) 0x9B, (byte) 0x84, (byte) 0xE6,
				(byte) 0x89, (byte) 0x8D, (byte) 0xE6, (byte) 0x98,
				(byte) 0xAF, (byte) 0xE7, (byte) 0x8E, (byte) 0x8B,
				(byte) 0xE8, (byte) 0x80, (byte) 0x85, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x05,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0xAD, (byte) 0x8F,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81, (byte) 0xE4,
				(byte) 0xBA, (byte) 0x82, (byte) 0xE4, (byte) 0xB8,
				(byte) 0x96, (byte) 0x4F, (byte) 0x4F, (byte) 0x4F,
				(byte) 0xE9, (byte) 0x9B, (byte) 0x84, (byte) 0xE6,
				(byte) 0x89, (byte) 0x8D, (byte) 0xE6, (byte) 0x98,
				(byte) 0xAF, (byte) 0xE7, (byte) 0x8E, (byte) 0x8B,
				(byte) 0xE8, (byte) 0x80, (byte) 0x85, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x04,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0x80, (byte) 0x80,
				(byte) 0xE5, (byte) 0x87, (byte) 0xBA, (byte) 0xE6,
				(byte) 0x88, (byte) 0x98, (byte) 0xE5, (byte) 0xBD,
				(byte) 0xB9, (byte) 0x28, (byte) 0x7C, (byte) 0x43,
				(byte) 0x46, (byte) 0x46, (byte) 0x46, (byte) 0x46,
				(byte) 0x46, (byte) 0x46, (byte) 0x46, (byte) 0x46,
				(byte) 0x51, (byte) 0x7C, (byte) 0x52, (byte) 0x29,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x05,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0xAD, (byte) 0x8F,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81, (byte) 0xE4,
				(byte) 0xBA, (byte) 0x82, (byte) 0xE4, (byte) 0xB8,
				(byte) 0x96, (byte) 0x4E, (byte) 0x4E, (byte) 0x4E,
				(byte) 0xE9, (byte) 0x9B, (byte) 0x84, (byte) 0xE6,
				(byte) 0x89, (byte) 0x8D, (byte) 0xE6, (byte) 0x98,
				(byte) 0xAF, (byte) 0xE7, (byte) 0x8E, (byte) 0x8B,
				(byte) 0xE8, (byte) 0x80, (byte) 0x85, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x05,
				(byte) 0x00, (byte) 0x08, (byte) 0x05, (byte) 0x6D,
				(byte) 0x6F, (byte) 0xE9, (byte) 0xAD, (byte) 0x8F,
				(byte) 0xE5, (byte) 0x9C, (byte) 0x8B, (byte) 0xE5,
				(byte) 0x8B, (byte) 0x9D, (byte) 0xE5, (byte) 0x88,
				(byte) 0xA9, (byte) 0xE4, (byte) 0xBA, (byte) 0x86,
				(byte) 0xEF, (byte) 0xBC, (byte) 0x81, (byte) 0xE4,
				(byte) 0xBA, (byte) 0x82, (byte) 0xE4, (byte) 0xB8,
				(byte) 0x96, (byte) 0x4E, (byte) 0x4E, (byte) 0x4E,
				(byte) 0xE9, (byte) 0x9B, (byte) 0x84, (byte) 0xE6,
				(byte) 0x89, (byte) 0x8D, (byte) 0xE6, (byte) 0x98,
				(byte) 0xAF, (byte) 0xE7, (byte) 0x8E, (byte) 0x8B,
				(byte) 0xE8, (byte) 0x80, (byte) 0x85 };
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(new String(bst, "UTF-8"));
	}
}
