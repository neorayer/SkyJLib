package com.skymiracle.io;

import java.io.*;
import java.util.*;

public class PlainFile {

	public static ArrayList<String> readLines(File file, String charset) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		String line = null;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		return lines;
	};


}
