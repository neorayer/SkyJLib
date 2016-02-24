package com.skymiracle.util.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.*;
import java.util.zip.*;
import com.skymiracle.io.*;

public class JarUtils {

	public static void copyDir(File src, String dirPath, JarOutputStream out) throws IOException {
		JarFile jf = new JarFile(src);
		Enumeration<JarEntry> en =  jf.entries();
		while(en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			if (!je.getName().startsWith(dirPath))
				continue;
			
			
			System.out.println(je);
			out.putNextEntry(je);
			InputStream is = jf.getInputStream(je);
			StreamPipe.inputToOutput(is,true, out, false);
		}
	}
	
	public static List<String>  getEntryNamesStartWith(File src, String dirPath) throws IOException {
		List<String> names = new LinkedList<String>();
		JarFile jf = new JarFile(src);
		Enumeration<JarEntry> en =  jf.entries();
		while(en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			if (!je.getName().startsWith(dirPath))
				continue;
			
			names.add(je.getName());
		}
		return names;
	}
	public static void main(String[] args) throws IOException {
		getEntryNamesStartWith(new File("R:\\jre6\\lib\\rt.jar"), "sun/security");
	
	}
}
