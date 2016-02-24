package com.skymiracle.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Charset {
	public static final byte[] ZW_UTF8_BS = new byte[] { -28, -72, -83, -26,
			-106, -121 };

	public static final byte[] ZW_GB2312_BS = new byte[] { -42, -48, -50, -60 };

	protected static String defaultCharsetForJvm14() {
		java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(
				new java.io.ByteArrayOutputStream());
		String charset = osw.getEncoding();
		return charset;
	}

	protected static String defaultCharsetForJvm15() {
		return java.nio.charset.Charset.defaultCharset().name();
	}

	public static String defaultCharset() {
		return defaultCharsetForJvm15();
	}

	private static Properties props = null;

	public static String utf8ToPinyin(String utf8Str) throws IOException {
		if (props == null) {
			props = new Properties();
			String path = Charset.class.getPackage().getName();
			path = '/' + path.replace('.', '/')+ "/Utf8Pinyin.props";
			InputStream is = Charset.class.getResourceAsStream(path);
			props.load(is);
			is.close();
		}
		StringBuffer pinyinSb = new StringBuffer();
		byte[] bs = utf8Str.getBytes("UTF-8");
		String asciiStr = "";
		int c = bs.length;
		int i = 0;
		while (i < c) {
			byte b = bs[i++];
			if (b < 0) {
				asciiStr += (256 + b);
				b = bs[i++];
				asciiStr += (256 + b);
				b = bs[i++];
				asciiStr += (256 + b);

				String py = props.getProperty(asciiStr);
				if (py != null) {
					pinyinSb.append(py);
				} else {
				}
			} else {
				pinyinSb.append((char) b);
			}
			asciiStr = "";
		}
		return pinyinSb.toString();
	}

	public static String gbToUtf8(String str)
			throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			if (s.charAt(0) > 0x80) {
				byte[] bytes = s.getBytes("Unicode");
				String binaryStr = "";
				for (int j = 2; j < bytes.length; j += 2) {
					// the first byte
					String hexStr = getHexString(bytes[j + 1]);
					String binStr = getBinaryString(Integer.valueOf(hexStr, 16));
					binaryStr += binStr;
					// the second byte
					hexStr = getHexString(bytes[j]);
					binStr = getBinaryString(Integer.valueOf(hexStr, 16));
					binaryStr += binStr;
				}
				// convert unicode to utf-8
				String s1 = "1110" + binaryStr.substring(0, 4);
				String s2 = "10" + binaryStr.substring(4, 10);
				String s3 = "10" + binaryStr.substring(10, 16);
				byte[] bs = new byte[3];
				bs[0] = Integer.valueOf(s1, 2).byteValue();
				bs[1] = Integer.valueOf(s2, 2).byteValue();
				bs[2] = Integer.valueOf(s3, 2).byteValue();
				String ss = new String(bs, "UTF-8");
				sb.append(ss);
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	private static String getHexString(byte b) {
		String hexStr = Integer.toHexString(b);
		int m = hexStr.length();
		if (m < 2) {
			hexStr = "0" + hexStr;
		} else {
			hexStr = hexStr.substring(m - 2);
		}
		return hexStr;
	}

	private static String getBinaryString(int i) {
		String binaryStr = Integer.toBinaryString(i);
		int length = binaryStr.length();
		for (int l = 0; l < 8 - length; l++) {
			binaryStr = "0" + binaryStr;
		}
		return binaryStr;
	}

	public final static String utf8ToGbk(byte[] data) throws IOException {
        int utflen = data.length;
        StringBuffer str = new StringBuffer(utflen);
        byte bytearr[] = data;
        int c, char2, char3;
        int count = 0;
        while (count < utflen) {
            c = bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx*/
                    count++;
                    str.append( (char) c);
                    break;
                case 12:
                case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen) {
                        throw new UTFDataFormatException(
                            "UTF Data Format Exception");
                    }
                    char2 = bytearr[count - 1];
                    if ( (char2 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException();
                    }
                    str.append( (char) ( ( (c & 0x1F) << 6) | (char2 & 0x3F)));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen) {
                        throw new UTFDataFormatException(
                            "UTF Data Format Exception");
                    }
                    char2 = bytearr[count - 2];
                    char3 = bytearr[count - 1];
                    if ( ( (char2 & 0xC0) != 0x80) || ( (char3 & 0xC0) != 0x80)) {
                        throw new UTFDataFormatException();
                    }
                    str.append( (char) ( ( (c & 0x0F) << 12)
                        | ( (char2 & 0x3F) << 6) | ( (char3 & 0x3F) << 0)));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException(
                        "UTF Data Format Exception");
            }
        }
        // The number of chars produced may be less than utflen
        return new String(str);
    }
	
	/**
	 * @param args
	 */
	public static void main(String args[]) throws Exception {
		System.out.println(utf8ToPinyin("Jenny张"));
		System.out.println(utf8ToGbk("中文".getBytes("UTF-8")));
	}

}
