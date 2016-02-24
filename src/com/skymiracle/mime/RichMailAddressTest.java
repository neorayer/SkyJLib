package com.skymiracle.mime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skymiracle.util.Rfc2047Codec;

public class RichMailAddressTest {
	static Pattern compile = Pattern.compile("(\"([^\"]+)\")?\\s*(<([^>]+)>)?");
	
	public static void main(String[] args) {
		String src = "\"田良\" <test@test.com>";

		System.out.println("regx calc");
		regexProcessNT(src);
		System.out.println();
		System.out.println("str +- calc");
		strProcessNT(src);
 
	}
	 
	private static void regexProcessNT(String src) {
		long begin = System.currentTimeMillis();
		for(int i = 0; i< 10000; i++) {
			regexProcess(src); 
		}  
		System.out.println("共用 " + (System.currentTimeMillis() - begin)  + " ms");
		
	} 

	private static void strProcessNT(String src) {
		long begin = System.currentTimeMillis();
		for(int i = 0; i< 10000; i++) {
			strProcess(src);
		} 
		System.out.println("共用 " + (System.currentTimeMillis() - begin) + " ms");
	}

	private static void regexProcess(String src) {
		String mailAddress = null;
		String displayName = null;
		if (src == null)  
			return;   
		Matcher matcher = compile.matcher(src);  
		if(matcher.find()) {    
			displayName = matcher.group(2);
			mailAddress = matcher.group(4); 
		}
	}
 
	private static void strProcess(String src) {
		String mailAddress = null;
		String displayName = null;
		if (src == null)
			return; 
		int pos1B = src.lastIndexOf('<');
		int pos1E = src.lastIndexOf('>');
		int pos2B = src.indexOf('"');
		int pos2E = src.lastIndexOf('"');
		
		// get the mail address
		if (pos1B >= 0 && pos1E >= 0 && pos1B < pos1E)
			mailAddress = src.substring(pos1B + 1, pos1E).toLowerCase();
		// get the name
		if (pos2B >= 0 && pos2E >= 0 && pos2B < pos2E) {
			displayName = src.substring(pos2B + 1, pos2E); 
		}
	}
}
