package gnu.getopt;


/*
 * This sample code was written by Aaron M. Renn and is a demonstration
 * of how to utilize some of the features of the GNU getopt package.  This
 * sample code is hereby placed into the public domain by the author and
 * may be used without restriction.
 */

public class GetoptDemo {

	public static void main(String[] argv) {
		int c;
		String arg;
		LongOpt[] longopts = new LongOpt[3];
		// 
		StringBuffer sb = new StringBuffer();
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("outputdir", LongOpt.REQUIRED_ARGUMENT, sb,
				'o');
		longopts[2] = new LongOpt("maximum", LongOpt.OPTIONAL_ARGUMENT, null, 2);
		// 
		Getopt g = new Getopt("testprog", argv, "-:bc::d:hW;", longopts);
		g.setOpterr(false); // We'll do our own error handling
		//
		while ((c = g.getopt()) != -1)
			switch (c) {
			case 0:
				arg = g.getOptarg();
				System.out.print("Got long option with value '"
						+ (char) (new Integer(sb.toString())).intValue()
						+ "' with argument " + ((arg != null) ? arg : "null")
						+ "\r\n");
				break;
			//
			case 1:
				System.out.print("I see you have return in order set and that "
						+ "a non-option argv element was just found "
						+ "with the value '" + g.getOptarg() + "'\r\n");
				break;
			//
			case 2:
				arg = g.getOptarg();
				System.out.print("I know this, but pretend I didn't\r\n");
				System.out.print("We picked option "
						+ longopts[g.getLongind()].getName() + " with value "
						+ ((arg != null) ? arg : "null") + "\r\n");
				break;
			//
			case 'b':
				System.out.print("You picked plain old option " + (char) c
						+ "\r\n");
				break;
			//
			case 'c':
			case 'd':
				arg = g.getOptarg();
				System.out.print("You picked option '" + (char) c
						+ "' with argument " + ((arg != null) ? arg : "null")
						+ "\r\n");
				break;
			//
			case 'h':
				System.out.print("I see you asked for help\r\n");
				break;
			//
			case 'W':
				System.out.print("Hmmm. You tried a -W with an incorrect long "
						+ "option name\r\n");
				break;
			//
			case ':':
				System.out.print("Doh! You need an argument for option "
						+ (char) g.getOptopt() + "\r\n");
				break;
			//
			case '?':
				System.out.print("The option '" + (char) g.getOptopt()
						+ "' is not valid\r\n");
				break;
			//
			default:
				System.out.print("getopt() returned " + c + "\r\n");
				break;
			}
		//
		for (int i = g.getOptind(); i < argv.length; i++)
			System.out.print("Non option argv element: " + argv[i] + "\r\n");
	}

} // Class GetoptDemo

