package com.skymiracle.system;

import java.io.IOException;
import java.io.InputStream;
import com.skymiracle.io.StreamPipe;

public class ShellCommand {

	public static String exec(String cmd, String charset) throws IOException {
		if (cmd == null)
			return "";
		Process process = Runtime.getRuntime().exec(cmd);
		InputStream inputStream = process.getInputStream();
		return StreamPipe.inputToString(inputStream, charset, true);
	}
}
