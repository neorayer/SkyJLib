package com.skymiracle.util;

public interface BytesLine {
	public int getBegin();

	public int getEnd();

	public byte[] getBytes();

	public String toString(String charsetName);
}
