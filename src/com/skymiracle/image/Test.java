package com.skymiracle.image;

import java.io.IOException;

public class Test {

	
	public static void main(String[] args) throws IOException {
		SkyImage si = new SkyImageImpl("c:\\temp\\3.jpg");
		
		si.cut(50,50,50,50);
		
		si.saveAs("c:\\temp\\4.jpg", SkyImage.FORMAT_JPG);
	}
}
