package com.skymiracle.fax;

import java.io.File;

public class FilenameException extends Exception{

	private static final long serialVersionUID = 8262286815063298757L;

	public FilenameException(File file) {
		super(file.getName() + " is NOT good format!");
	}
}
