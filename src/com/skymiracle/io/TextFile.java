package com.skymiracle.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.skymiracle.util.Base64;
import com.skymiracle.util.BytesLine;
import com.skymiracle.util.BytesLineImpl;
import com.skymiracle.util.BytesLinesSectionImpl;
import com.skymiracle.util.LinesSection;
import com.skymiracle.util.QuotedPrintableCodec;

/**
 * offer the methods to create file to store data and to get the data from the
 * file
 * 
 * @author Administrator
 * 
 */

public class TextFile {

	public final static int LNSEP_R = 0;

	public final static int LNSEP_N = 1;

	public final static int LNSEP_RN = 2;

	public final static int LNSEP_NR = 3;

	public static void save(String filePath, String[] lines) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		for (int i = 0; i < lines.length; i++) {
			bw.write(lines[i]);
			bw.write("\r\n");
		}
		bw.close();

	}

	public static void save(String filePath, String str) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		bw.write(str);
		bw.close();
	}

	public static void save(String filePath, byte[] bytes) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		fos.write(bytes);
		fos.close();
	}

	public static String[] loadLines(String filePath) throws IOException {
		String[] result = null;
		ArrayList<String> lineList = TextFile.loadLinesList(filePath);
		result = lineList.toArray(new String[0]);
		return result;
	}

	public static ArrayList<String> loadValidLinesList(String filePath)
			throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader((new FileReader(filePath)));
			String line = null;
			ArrayList<String> linesList = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				linesList.add(line);
			}
			br.close();
			return linesList;
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null)
				br.close();
		}
	}

	public static String[] loadValidLines(String filePath) throws IOException {
		String[] result = null;
		ArrayList<String> lineList = TextFile.loadValidLinesList(filePath);
		result = lineList.toArray(new String[0]);
		return result;
	}

	public static StringBuffer loadValidStringBuffer(String filePath, String sep)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		ArrayList<String> lineList = TextFile.loadValidLinesList(filePath);
		for (int i = 0; i < lineList.size(); i++) {
			sb.append(lineList.get(i)).append(sep);
		}
		return sb;
	}

	public static ArrayList<String> loadLinesList(String filePath)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;
		ArrayList<String> linesList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			linesList.add(line);
		}
		br.close();
		return linesList;
	}

	public static ArrayList<String> loadLinesList(String filePath,
			String charset) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = null;
		ArrayList<String> linesList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			linesList.add(new String(line.getBytes(), charset));
		}
		br.close();
		return linesList;
	}

	public static void lineDealWith(String filePath, StringLineDealWith dealWith)
			throws Exception {
		BufferedReader br = new BufferedReader((new FileReader(filePath)));
		String line = null;
		while ((line = br.readLine()) != null) {
			dealWith.dealWith(line);
		}
		br.close();
	}

	public static ArrayList<String> loadFirstSectionLinesList(String filePath)
			throws IOException {
		// BufferedReader br = new BufferedReader((new FileReader(filePath)));

		// 对wpx1时 主题名直接GBK编码的 用此方法
		File file = new File(filePath);
		InputStreamReader isr = new InputStreamReader(
				new FileInputStream(file), get_charset(file));
		BufferedReader br = new BufferedReader(isr);

		String line = null;
		ArrayList<String> linesList = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (line.length() == 0)
				break;
			linesList.add(line);
		}
		br.close();
		return linesList;
	}

	public static String get_charset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				// int len = 0;
				int loc = 0;

				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
							// (0x80
							// - 0xBF),也可能在GB编码内
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
				// System.out.println( loc + " " + Integer.toHexString( read )
				// );
			}

			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return charset;
	}

	public static ArrayList<BytesLine> loadBytesLinesList(String filePath)
			throws IOException {
		ArrayList<BytesLine> linesList = new ArrayList<BytesLine>();
		byte[] bytes = TextFile.loadBytes(filePath);

		// Find line end is /r /n or /r/n or /n/r

		int lnSep = -1;
		for (int i = 0; i < bytes.length - 1; i++) {
			byte b = bytes[i];
			byte bn = bytes[i + 1];
			if (b == '\n')
				if (bn == '\r') {
					lnSep = LNSEP_NR;
					break;
				} else {
					lnSep = LNSEP_N;
					break;
				}
			else if (b == '\r')
				if (bn == '\n') {
					lnSep = LNSEP_RN;
					break;
				} else {
					lnSep = LNSEP_R;
					break;
				}
		}
		int prePos = 0;
		switch (lnSep) {
		case LNSEP_N:
			for (int i = 0; i < bytes.length - 1; i++) {
				byte b = bytes[i];

				if (b == '\n') {
					BytesLine bs = new BytesLineImpl(bytes, prePos, i);
					prePos = i + 1;
					linesList.add(bs);
				}
			}
			break;
		case LNSEP_R:
			for (int i = 0; i < bytes.length - 1; i++) {
				byte b = bytes[i];

				if (b == '\r') {
					BytesLine bs = new BytesLineImpl(bytes, prePos, i);
					prePos = i + 1;
					linesList.add(bs);
				}
			}
			break;
		case LNSEP_RN:
			for (int i = 0; i < bytes.length - 1; i++) {
				byte b = bytes[i];

				if (b == '\r') {
					BytesLine bs = new BytesLineImpl(bytes, prePos, i);
					prePos = i + 2;
					linesList.add(bs);
				}
			}
			break;
		case LNSEP_NR:
			for (int i = 0; i < bytes.length - 1; i++) {
				byte b = bytes[i];

				if (b == '\n') {
					BytesLine bs = new BytesLineImpl(bytes, prePos, i);
					prePos = i + 2;
					linesList.add(bs);
				}
			}
			break;
		}
		if (prePos < bytes.length - 1) {
			BytesLine bs = new BytesLineImpl(bytes, prePos,
					bytes.length == 0 ? 0 : bytes.length - 1);
			linesList.add(bs);
		}
		return linesList;
	}

	public static BytesLine[] loadBytesLines(String filePath)
			throws IOException {
		ArrayList<BytesLine> linesList = TextFile.loadBytesLinesList(filePath);
		return linesList.toArray(new BytesLine[linesList.size()]);
	}

	public static byte[] loadBytes(String srcPath) throws IOException {
		File file = new File(srcPath);
		FileInputStream fis = new FileInputStream(file);
		int len = (int) ((new File(srcPath)).length());
		byte[] buf = new byte[len];
		fis.read(buf);
		fis.close();
		return buf;
		// String charset = get_charset(file);
		// return new String(buf, charset).getBytes();
	}

	public static String loadString(String filePath, String sep)
			throws IOException {
		return TextFile.loadStringBuffer(filePath, sep).toString();
	}

	public static StringBuffer loadStringBuffer(String filePath, String sep)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader((new FileReader(filePath)));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(sep);
		}
		br.close();
		return sb;
	}

	public static StringBuffer loadMailHeaderStringBuffer(String filePath,
			String sep) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader((new FileReader(filePath)));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(sep);
			if (line.length() == 0)
				break;
		}
		br.close();
		return sb;
	}

	public static StringBuffer loadMailBodyStringBuffer(String filePath,
			String sep, int i) throws IOException {
		int count = 0;
		boolean begin = false;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader((new FileReader(filePath)));
		String line = null;
		while ((line = br.readLine()) != null) {

			sb.append(line);
			sb.append(sep);
			if (line.length() == 0 && count == 0)
				begin = true;
			if (count == i)
				break;
			if (begin)
				count++;
		}
		br.close();
		return sb;
	}

	public static void decodeBase64(ArrayList<BytesLine> linesList,
			String filePath) throws IOException {
		LinesSection ls = new BytesLinesSectionImpl(linesList);
		TextFile.decodeBase64(ls, filePath);
	}

	public static void decodeBase64(LinesSection ls, String filePath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		for (int i = ls.getBegin(); i < ls.getEnd(); i++) {
			BytesLine bl = ls.getBytesLine(i);
			byte[] bytes = Base64.decode(bl.getBytes(), bl.getBegin(), bl
					.getEnd());
			if (bytes == null)
				continue;

			bos.write(bytes);
		}
		bos.close();
	}

	public static void decodeBase64withLines(String srcPath, String decPath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(decPath));
		BufferedReader br = new BufferedReader((new FileReader(srcPath)));
		String line = null;
		while ((line = br.readLine()) != null) {
			bos.write(Base64.decode(line.getBytes()));
		}
		br.close();
		bos.close();
	}

	public static void decodeBase64withAll(String srcPath, String decPath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(decPath));
		byte[] buf = TextFile.loadBytes(srcPath);
		bos.write(Base64.decode(buf));
		bos.close();
	}

	public static byte[] decodeBase64(String srcPath) throws IOException {
		return Base64.decode(TextFile.loadString(srcPath, "").getBytes());
	}

	public static void decodeQP(String srcPath, String decPath, String charset)
			throws Exception {
		TextFile.save(decPath, TextFile.decodeQP(srcPath, charset));
	}

	public static void decodeQP(LinesSection ls, String filePath, String charset)
			throws Exception {
		FileOutputStream fos = new FileOutputStream(filePath);
		QuotedPrintableCodec qpCodec = new QuotedPrintableCodec(charset);
		// byte[] bytes = ls.getBytesLine(ls.getBegin()).getBytes();
		// int begin = ls.getBytesLine(ls.getBegin()).getBegin();
		// int end = ls.getBytesLine(ls.getEnd()).getEnd();
		// fos
		// .write(qpCodec.decode(bytes, begin, end));

		int realBegin = ls.getBegin();
		boolean isNewStart = true;
		for (int i = ls.getBegin(); i < ls.getEnd(); i++) {
			BytesLine bl = ls.getBytesLine(i);
			if (isNewStart)
				realBegin = bl.getBegin();
			byte lastByte = bl.getBytes()[bl.getEnd() - 1];
			if (lastByte == '=') {
				isNewStart = false;
				if (i == ls.getEnd() - 1)
					fos.write(qpCodec.decode(bl.getBytes(), realBegin, bl
							.getEnd()));
				continue;
			} else {
				fos
						.write(qpCodec.decode(bl.getBytes(), realBegin, bl
								.getEnd()));
				isNewStart = true;
				fos.write('\r');
				fos.write('\n');
			}
		}
		fos.write('\r');
		fos.write('\n');
		fos.close();
	}

	public static String decodeQP(String srcPath, String charset)
			throws IOException, Exception {
		QuotedPrintableCodec qpCodec = new QuotedPrintableCodec(charset);
		return qpCodec.decode(TextFile.loadString(srcPath, "\r\n"));
	}

	public static void save(String filePath, String[] lines, int begin, int end)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		for (int i = begin; i < end; i++) {
			bw.write(lines[i]);
			bw.write("\r\n");
		}
		bw.close();
	}

	public static void save(String filePath, List<String> linesList)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		for (String s : linesList) {
			bos.write(s.getBytes());
			bos.write("\r\n".getBytes());
		}
		bos.close();
		fos.close();
	}

	public static void save(String filePath, List<String> linesList,
			String charset) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		for (String s : linesList) {
			bos.write(s.getBytes(charset));
			bos.write("\r\n".getBytes(charset));
		}
		bos.close();
		fos.close();
	}

	public static void save(String filePath, ArrayList<String> linesList,
			String sep) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		for (String s : linesList) {
			bos.write(s.getBytes());
			bos.write(sep.getBytes());
		}
		bos.close();
		fos.close();
	}

	public static void save(String filePath, ArrayList<String> linesList,
			int begin, int end) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		for (int i = begin; i < end; i++) {
			bw.write(linesList.get(i));
			bw.write("\r\n");
		}
		bw.close();
	}

	public static void append(String filePath, ArrayList<String> linesList)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
		for (int i = 0; i < linesList.size(); i++) {
			bw.write(linesList.get(i));
			bw.write("\r\n");
		}
		bw.close();
	}

	public static void append(String filePath, String line, String sep)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
		bw.write(line);
		bw.write(sep);
		bw.close();
	}

	public static boolean exists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static Properties getProperties(String filePath) throws IOException {
		String propsFilePath = filePath;
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propsFilePath);
			props.load(fis);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}
		}
		return props;
	}

	public static void storeProperties(Properties prop, String filePath)
			throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			prop.store(fos, "");
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				throw new IOException(e.getMessage());
			}
		}
	}

	public static String[][] getPropertiesString2s(String filePath)
			throws IOException {
		Properties props = getProperties(filePath);
		Set<Object> keySet = props.keySet();
		String[] keys = keySet.toArray(new String[0]);
		String[][] s2s = new String[keys.length][2];
		for (int i = 0; i < keys.length; i++) {
			String value = props.getProperty(keys[i]);
			String[] ss = new String[2];
			ss[0] = keys[i];
			ss[2] = value;
			s2s[0] = ss;
		}
		return s2s;
	}

	public static void main(String[] args) {

	}

	public static StringBuffer loadStringBuffer(String filePath, String sep,
			int maxLine) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader((new FileReader(filePath)));
		int lineNum = 0;
		String line = null;
		while (lineNum < maxLine && (line = br.readLine()) != null) {
			sb.append(line);
			sb.append(sep);
		}
		br.close();
		return sb;
	}

}
