package com.skymiracle.system;

import java.io.IOException;

public class OS {
	public final static int TYPE_WINDOWS = 10;

	public final static int TYPE_UNIX = 20;

	public static int getType() {
		String sep = System.getProperty("file.separator");
		if (sep.equals("/"))
			return TYPE_UNIX;
		else
			return TYPE_WINDOWS;
	}

	public static void chown(String filePath, String own) throws IOException {
		if (OS.getType() != OS.TYPE_UNIX)
			return;
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(new String[] { "chown", own, filePath });
	}
}
