package com.skymiracle.util;

import java.io.IOException;

public class Base64Shell {

	private static void outHelpAndExit() {
		System.out.println("Usage:");
		System.out
				.println("\tjava com.skymiracle.util.Base64Shell -e[-d] string[string encoded]");
		System.exit(0);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2)
			outHelpAndExit();

		if (args[0].equals("-e")) {
			System.out.println(new String(Base64.encode(args[1].getBytes())));
		} else if (args[0].equals("-d")) {
			System.out.println(args[1]);
			System.out.write(Base64.decode(args[1].getBytes()));
		} else
			outHelpAndExit();
	}

}
