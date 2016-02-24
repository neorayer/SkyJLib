package com.skymiracle.util.jdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipFile;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.io.TextFile;
import com.skymiracle.util.jar.JarUtils;

public class ClassFinder {
	private static class Item {
		String classPath;
		String from;
	}

	String path;

	Map<String, List<Item>> itemsMap = new HashMap<String, List<Item>>();
	List<Item> fixedItems = new LinkedList<Item>();
	List<String> fixedDirs = new LinkedList<String>();

	public ClassFinder(String path) {
		this.path = path;

		addFixedItem("sun/misc/URLClassPath$FileLoader.class");
		addFixedItem("sun/net/ConnectionResetException.class");

		fixedDirs.add("sun/security/provider/");
		fixedDirs.add("sun/reflect/");
		fixedDirs.add("java/lang/");
		fixedDirs.add("java/net/");
	}

	private void addFixedItem(String classPath) {
		Item item = new Item();
		item.classPath = classPath;
		fixedItems.add(item);
	}

	private List<Item> getItems(String filePath) {
		List<Item> items = itemsMap.get(filePath);
		if (items == null) {
			items = new LinkedList<Item>();
			itemsMap.put(filePath, items);
		}
		return items;

	}

	public void run() throws Exception {
		String rootDir = "/tmp/ws1/jar";
		File rdFile = new File(rootDir);
		rdFile.mkdirs();

		String[] ss = TextFile.loadLines(path);
		for (String s : ss) {
			// 去掉非Loaded开头的
			if (!s.startsWith("[Loaded"))
				continue;
			{
				int idx = "[Loaded".length() + 1;
				s = s.substring(idx);
			}

			Item item = new Item();
			{
				int idx = s.indexOf(' ');
				String className = s.substring(0, idx);
				item.from = s.substring(idx + 6, s.length() - 1); // ' from ';
				item.from = URLDecoder.decode(item.from, "UTF-8");
				if (item.from.startsWith("file:/"))
					item.from = new File(item.from.substring(6))
							.getAbsolutePath();
				item.classPath = className;
				item.classPath = item.classPath.replace('.', '/');
				item.classPath += ".class";
			}
			getItems(item.from).add(item);
		}

		for (Map.Entry<String, List<Item>> e : itemsMap.entrySet()) {
			System.out.println(e.getKey());
		}

		// 把标记为from shared objects file的加入rt.jar的list
		List<Item> rtItems = null;
		List<Item> soItems = null;
		File rtJarFile = null;
		for (Map.Entry<String, List<Item>> e : itemsMap.entrySet()) {
			if (e.getKey().endsWith("rt.jar")) {
				rtItems = e.getValue();
				rtJarFile = new File(e.getKey());
			}
			if (e.getKey().equals("shared objects file"))
				soItems = e.getValue();
		}
		if (soItems != null)
			rtItems.addAll(soItems);

		// 把预设的目录下的所有条目都加入rt.jar里
		for (String dir : fixedDirs) {
			List<String> paths = JarUtils
					.getEntryNamesStartWith(rtJarFile, dir);
			for (String path : paths)
				addFixedItem(path);
		}

		// 把预设的fixedItems加入rtItems
		rtItems.addAll(fixedItems);

		File resFile = new File("/tmp/ws1/lib/rt.jar");
		resFile.delete();
		FileOutputStream fos = new FileOutputStream(resFile);
		JarOutputStream jos = new JarOutputStream(fos);

		Map<String, String> entriesMap = new HashMap<String, String>();
		for (Map.Entry<String, List<Item>> e : itemsMap.entrySet()) {
			File f = new File(e.getKey());
			if (!f.exists())
				continue;
			if (f.isDirectory())
				continue;

			JarFile jf = new JarFile(f);

			List<Item> items = e.getValue();

			System.out.println("Extracting " + items.size() + " classes form "
					+ f.getAbsolutePath());
			for (Item item : items) {
				JarEntry je = jf.getJarEntry(item.classPath);
				if (je == null)
					throw new Exception("Can't find jar entry "
							+ item.classPath);

				File cf = new File(rdFile, je.getName());
				cf.getParentFile().mkdirs();

				System.out.println("\t" + je);
				// StreamPipe.inputToFile(is, cf, true);

				// 去除重复项
				if (entriesMap.get(item.classPath) != null)
					continue;
				jos.putNextEntry(je);
				// 记录加入的
				entriesMap.put(je.getName(), je.getName());
				// 写入数据
				InputStream is = jf.getInputStream(je);
				StreamPipe.inputToOutput(is, true, jos, false);
			}
			System.out.println();
		}

		jos.close();
		fos.close();
	}

	public static void main(String... args) throws Exception {
		ClassFinder finder = new ClassFinder("C:\\gf\\tomcat\\webapps\\TUT\\src\\class.txt");
		finder.run();
		// JarFile jf = new
		// JarFile("C:\\Program Files\\Java\\jre6\\lib\\ext\\localedata.jar");
		// Enumeration<JarEntry> em = jf.entries();
		// while(em.hasMoreElements()) {
		// JarEntry je = em.nextElement();
		// System.out.println(je.getName());
		// }
		//	
	}

}