package com.skymiracle.util;

import java.io.IOException;

public interface LinesSection {

	public int getBegin();

	public int getEnd();

	public LinesSection getFirstSection();

	public LinesSection getNoFirstSection();

	public String getStringLine(int lineIndex, String charset);

	public BytesLine getBytesLine(int lineIndex);

	public LinesSection[] getSections(String boundary);

	public void save(String filePath) throws IOException;

	int lineIndexOf(int lineIndex, char c);

	public void save(String filePath, String charset) throws IOException;

}
