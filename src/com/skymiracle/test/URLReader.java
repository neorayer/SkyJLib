package com.skymiracle.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class URLReader {
	public static void main(String[] args) throws Exception {
		URL yahoo = new URL("http://www.google.com.tw/");
		BufferedReader in = new BufferedReader(new InputStreamReader(yahoo
				.openStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}

		in.close();
	}
}