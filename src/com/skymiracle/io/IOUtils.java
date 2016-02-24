package com.skymiracle.io;

import java.io.*;

import com.skymiracle.util.ByteUtils;

public class IOUtils {
	public static void blockRead(InputStream is, byte[] buf, int off, int len) throws IOException{
		int c = 0;
		int rlen = -1;
		for (; c < len;) {
			rlen = is.read(buf, c + off, len - c);
			if (rlen == -1)
				throw new IOException("blockRead error");
			c += rlen;
		}
	}

	
}
