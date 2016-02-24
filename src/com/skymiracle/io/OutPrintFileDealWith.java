package com.skymiracle.io;

import java.io.File;
import java.io.IOException;

import com.skymiracle.util.CalendarUtil;

public class OutPrintFileDealWith implements FileDealWith {

	public void dealWith(File file) throws IOException {
		System.out.print(file.getAbsolutePath());
		System.out
				.print("\t" + CalendarUtil.getLongFormat(file.lastModified()));
		System.out.println();
	}
}
