package com.skymiracle.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.skymiracle.io.TextFile;

public class BytesLinesSectionImpl extends LinesSectionImpl implements
		LinesSection {

	private byte[] bytes;

	public BytesLinesSectionImpl(ArrayList linesList, int begin, int end) {
		super(linesList, begin, end);
		this.bytes = ((BytesLine) this.linesList.get(0)).getBytes();
	}

	public BytesLinesSectionImpl(ArrayList<BytesLine> linesList) {
		super(linesList);
		this.bytes = ((BytesLine) this.linesList.get(0)).getBytes();
	}

	public BytesLinesSectionImpl(String filePath) throws Exception {
		super(TextFile.loadBytesLinesList(filePath));
		this.bytes = ((BytesLine) this.linesList.get(0)).getBytes();
	}

	@Override
	protected LinesSection getInstance(ArrayList linesList, int begin, int end) {
		return new BytesLinesSectionImpl(linesList, begin, end);
	}

	@Override
	protected boolean lineEqualsStr(int lineIndex, String str) {
		BytesLine bls = (BytesLine) this.linesList.get(lineIndex);
		int begin = bls.getBegin();
		int end = bls.getEnd();
		byte[] bytes = bls.getBytes();
		if ((end - begin) != str.length())
			return false;
		byte[] strBytes = str.getBytes();
		for (int i = begin; i < end; i++) {
			if (bytes[i] != strBytes[i - begin])
				return false;
		}
		return true;
	}

	public void save(String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		if (this.begin < this.end) {
			int begin = ((BytesLine) this.linesList.get(this.begin)).getBegin();
			int end = this.end == 0 ? 0 : ((BytesLine) this.linesList
					.get(this.end - 1)).getEnd();
			bos.write(this.bytes, begin, end - begin);
			// bos.write(new byte[] { 13, 10 });
		}
		bos.close();
	}

	public void save(String filePath, String charset) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		if (this.begin < this.end) {
			int begin = ((BytesLine) this.linesList.get(this.begin)).getBegin();
			int end = this.end == 0 ? 0 : ((BytesLine) this.linesList
					.get(this.end - 1)).getEnd();
			bos.write(new String(this.bytes, charset).getBytes(), begin, end
					- begin);
			// bos.write(new byte[] { 13, 10 });
		}
		bos.close();
	}

	public BytesLine getBytesLine(int lineIndex) {
		return (BytesLine) this.linesList.get(lineIndex);
	}

	public String getStringLine(int lineIndex, String charset) {
		BytesLine bl = getBytesLine(lineIndex);
		String s = bl.toString(charset);
		return s;
	}

	public int lineIndexOf(int lineIndex, char c) {
		BytesLine bl = (BytesLine) this.linesList.get(lineIndex);
		byte[] bytes = bl.getBytes();
		int begin = bl.getBegin();
		int end = bl.getEnd();
		for (int i = begin; i < end; i++)
			if (bytes[i] == c)
				return i - begin;
		return -1;
	}

	@Override
	public String toString() {
		if (this.begin < this.end) {
			int begin = ((BytesLine) this.linesList.get(this.begin)).getBegin();
			int end = ((BytesLine) this.linesList.get(this.end - 1)).getEnd();
			return new String(this.bytes, begin, end - begin);
		} else
			return "";
	}
}
