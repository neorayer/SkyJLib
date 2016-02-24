package com.skymiracle.fileBox;

import java.io.File;

/**
 * When you have to return the java.io.File objects from FileBox, you need the
 * pathInBox too.
 * 
 * @author neora
 * 
 */
public class FileInfo extends File {

	private static final long serialVersionUID = 6476534760754624605L;

	String pathInBox;

	String pathInFs;

	public FileInfo(String pathInFs, String pathInBox) {
		super(pathInFs);
		this.pathInBox = pathInBox;
	}

	public String getPathInBox() {
		return this.pathInBox;
	}
}
