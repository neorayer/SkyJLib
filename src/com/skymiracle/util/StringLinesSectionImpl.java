package com.skymiracle.util;

import java.io.IOException;
import java.util.ArrayList;

import com.skymiracle.io.TextFile;

public class StringLinesSectionImpl extends LinesSectionImpl implements
		LinesSection {

	public StringLinesSectionImpl(ArrayList linesList, int begin, int end) {
		super(linesList, begin, end);
	}

	public StringLinesSectionImpl(ArrayList linesList) {
		super(linesList);
	}

	public StringLinesSectionImpl(String filePath) throws Exception {
		super(TextFile.loadLinesList(filePath));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = this.begin; i < this.end; i++) {
			String line = (String) this.linesList.get(i);
			sb.append(line);
			sb.append("\r\n");
		}
		return sb.toString();
	}

	public void save(String filePath) throws IOException {
		TextFile.save(filePath, this.linesList, this.begin, this.end);
	}

	public void save(String filePath, String charset) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean lineEqualsStr(int lineIndex, String str) {
		return ((String) this.linesList.get(lineIndex)).equals(str);
	}

	@Override
	protected LinesSection getInstance(ArrayList linesList, int begin, int end) {
		return new StringLinesSectionImpl(linesList, begin, end);
	}

	public String getStringLine(int lineIndex) {
		return (String) this.linesList.get(lineIndex);
	}

	public BytesLine getBytesLine(int lineIndex) {
		byte[] bytes = getStringLine(lineIndex).getBytes();
		return new BytesLineImpl(bytes, 0, bytes.length);
	}

	public int lineIndexOf(int lineIndex, char c) {
		return ((String) this.linesList.get(lineIndex)).indexOf(c);
	}

	public String getStringLine(int lineIndex, String charset) {
		return (String) this.linesList.get(lineIndex);
	}

}
