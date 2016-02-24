package com.skymiracle.image;

import java.awt.AWTException;
import java.io.IOException;

import com.skymiracle.util.*;

public class ScreenShotShell {

	/**
	 * @param args
	 * @throws AWTException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws AWTException, IOException, InterruptedException {
		String shotPath = "/tmp/" + new UUID().toShortString();
		SkyImage simg = new SkyImageImpl(SkyImage.FORMAT_PNG);
		Thread.sleep(5000);
		simg.snapShot();
		simg.saveAs(shotPath, SkyImage.FORMAT_PNG);
	}

}
