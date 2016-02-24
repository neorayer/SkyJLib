package com.skymiracle.util;

import java.io.UnsupportedEncodingException;

import com.skymiracle.logger.Logger;

public class BytesLineImpl implements BytesLine {

	private byte[] bytes;

	private int begin;

	private int end;

	public BytesLineImpl(byte[] bytes, int begin, int end) {
		this.bytes = bytes;
		this.begin = begin;
		this.end = end;
	}

	public int getBegin() {
		return this.begin;
	}

	public int getEnd() {
		return this.end;
	}

	public byte[] getBytes() {
		return this.bytes;
	}

	@Override
	public String toString() {
		return new String(this.bytes, this.begin, this.end - this.begin);
	}

	public String toString(String charsetName) {
		try {
			return new String(this.bytes, this.begin, this.end - this.begin,
					charsetName);
		} catch (UnsupportedEncodingException e) {
			Logger.warn("BytesLineImpl.toString(charsetName) ", e);
			return toString();
		}
	}
}
