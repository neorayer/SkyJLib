/* ExcelCSVLexer.java is a generated file.	You probably want to
 * edit ExcelCSVLexer.lex to make changes.	Use JFlex to generate it.
 * JFlex may be obtained from
 * <a href="http://jflex.de">the JFlex website</a>.
 * This file was tested to work with jflex 1.4 (and may not 
 * work with more recent version because it needs a skeleton file)
 * Run: <br>
 * jflex --skel csv.jflex.skel ExcelCSVLexer.lex<br>
 * You will then have a file called ExcelCSVLexer.java
 */

/*
 * Read files in comma separated value format.
 * Copyright (C) 2001-2004 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */

package com.skymiracle.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Read files in comma separated value format as outputted by the Microsoft
 * Excel Spreadsheet program. More information about this class is available
 * from <a href= "http://ostermiller.org/utils/ExcelCSV.html">ostermiller.org</a>.
 * <P>
 * Excel CSV is a file format used as a portable representation of a database.
 * Each line is one entry or record and the fields in a record are separated by
 * commas.
 * <P>
 * If field includes a comma or a new line, the whole field must be surrounded
 * with double quotes. When the field is in quotes, any quote literals must be
 * escaped by two quotes (""). Text that comes after quotes that have been
 * closed but come before the next comma will be ignored.
 * <P>
 * Empty fields are returned as as String of length zero: "". The following line
 * has three empty fields and three non-empty fields in it. There is an empty
 * field on each end, and one in the middle. One token is returned as a space.<br>
 * 
 * <pre>
 * ,second,, ,fifth,
 * </pre>
 * 
 * <P>
 * Blank lines are always ignored. Other lines will be ignored if they start
 * with a comment character as set by the setCommentStart() method.
 * <P>
 * An example of how CVSLexer might be used:
 * 
 * <pre>
 * ExcelCSVLexer shredder = new ExcelCSVLexer(System.in);
 * String t;
 * while ((t = shredder.getNextToken()) != null) {
 * 	System.out.println(&quot;&quot; + shredder.getLineNumber() + &quot; &quot; + t);
 * }
 * </pre>
 * 
 * <P>
 * The CSV that Excel outputs differs from the genrally accepted standard CSV
 * standard in several respects:
 * <ul>
 * <li>Leading and trailing whitespace is significant.</li>
 * <li>A backslash is not a special character and is not used to escape
 * anything.</li>
 * <li>Quotes inside quoted strings are escaped with a double quote rather than
 * a backslash.</li>
 * <li>Excel may convert data before putting it in CSV format:
 * <ul>
 * <li>Tabs are converted to a single space.</li>
 * <li>New lines in the data are always represented as the unix new line.
 * ("\n")</li>
 * <li>Numbers that are greater than 12 digits may be represented in trunkated
 * scientific notation form.</li>
 * </ul>
 * This parser does not attempt to fix these excel conversions, but users should
 * be aware of them.</li>
 * </ul>
 * 
 * @author Stephen Ostermiller
 *         http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */

class ExcelCSVLexer {

	/** This character denotes the end of file */
	private static final int YYEOF = -1;

	/** initial size of the lookahead buffer */
	private static final int ZZ_BUFFERSIZE = 16384;

	/** lexical states */
	public static final int BEFORE = 1;

	public static final int YYINITIAL = 0;

	public static final int COMMENT = 3;

	public static final int AFTER = 2;

	/**
	 * Translates characters to character classes
	 */
	private static final String ZZ_CMAP_PACKED = "\12\0\1\2\2\0\1\1\24\0\1\4\11\0\1\3\uffd3\0";

	/**
	 * Translates characters to character classes
	 */
	private static final char[] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

	private static char[] zzcmap_instance;

	/**
	 * Translates DFA states to action switch labels.
	 */
	private static final int[] ZZ_ACTION = zzUnpackAction();

	private static final String ZZ_ACTION_PACKED_0 = "\2\0\2\1\1\2\2\3\1\4\1\5\1\6\2\7"
			+ "\1\10\1\11\1\1\1\12\1\1\1\13\1\14";

	private static int[] zzUnpackAction() {
		int[] result = new int[19];
		int offset = 0;
		offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAction(String packed, int offset, int[] result) {
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	/**
	 * Translates a state to a row index in the transition table
	 */
	private static final int[] ZZ_ROWMAP = zzUnpackRowMap();

	private static final String ZZ_ROWMAP_PACKED_0 = "\0\0\0\5\0\12\0\17\0\24\0\31\0\36\0\36"
			+ "\0\43\0\50\0\55\0\36\0\36\0\62\0\67\0\36" + "\0\74\0\101\0\106";

	private static int[] zzUnpackRowMap() {
		int[] result = new int[19];
		int offset = 0;
		offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackRowMap(String packed, int offset, int[] result) {
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l) {
			int high = packed.charAt(i++) << 16;
			result[j++] = high | packed.charAt(i++);
		}
		return j;
	}

	/**
	 * The transition table of the DFA
	 */
	private static final int[] ZZ_TRANS = zzUnpackTrans();

	private static final String ZZ_TRANS_PACKED_0 = "\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"
			+ "\1\15\1\16\1\17\1\6\1\7\1\20\1\17\1\21"
			+ "\1\6\1\7\2\21\1\5\3\0\1\5\2\0\1\7"
			+ "\7\0\4\11\1\22\1\12\3\0\1\12\2\0\1\14"
			+ "\2\0\4\16\1\23\1\17\3\0\1\17\1\21\2\0"
			+ "\2\21\4\0\1\11\4\0\1\16";

	private static int[] zzUnpackTrans() {
		int[] result = new int[75];
		int offset = 0;
		offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackTrans(String packed, int offset, int[] result) {
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			value--;
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	/* error codes */
	private static final int ZZ_UNKNOWN_ERROR = 0;

	private static final int ZZ_NO_MATCH = 1;

	private static final int ZZ_PUSHBACK_2BIG = 2;

	/* error messages for the codes above */
	private static final String ZZ_ERROR_MSG[] = {
			"Unkown internal scanner error", "Error: could not match input",
			"Error: pushback value was too large" };

	/**
	 * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
	 */
	private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();

	private static final String ZZ_ATTRIBUTE_PACKED_0 = "\2\0\4\1\2\11\3\1\2\11\2\1\1\11\3\1";

	private static int[] zzUnpackAttribute() {
		int[] result = new int[19];
		int offset = 0;
		offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
		return result;
	}

	private static int zzUnpackAttribute(String packed, int offset, int[] result) {
		int i = 0; /* index in packed string */
		int j = offset; /* index in unpacked array */
		int l = packed.length();
		while (i < l) {
			int count = packed.charAt(i++);
			int value = packed.charAt(i++);
			do
				result[j++] = value;
			while (--count > 0);
		}
		return j;
	}

	/** the input device */
	private java.io.Reader zzReader;

	/** the current state of the DFA */
	private int zzState;

	/** the current lexical state */
	private int zzLexicalState = YYINITIAL;

	/**
	 * this buffer contains the current text to be matched and is the source of
	 * the yytext() string
	 */
	private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

	/** the textposition at the last accepting state */
	private int zzMarkedPos;

	/** the textposition at the last state to be included in yytext */
	private int zzPushbackPos;

	/** the current text position in the buffer */
	private int zzCurrentPos;

	/** startRead marks the beginning of the yytext() string in the buffer */
	private int zzStartRead;

	/**
	 * endRead marks the last character in the buffer, that has been read from
	 * input
	 */
	private int zzEndRead;

	// /** number of newlines encountered up to the start of the matched text */
	// private int yyline;
	//
	// /** the number of characters up to the start of the matched text */
	// private int yychar;
	//
	// /**
	// * the number of characters from the last newline up to the start of the
	// * matched text
	// */
	// private int yycolumn;
	//
	// /**
	// * zzAtBOL == true <=> the scanner is currently at the beginning of a line
	// */
	// private boolean zzAtBOL = true;

	/** zzAtEOF == true <=> the scanner is at the EOF */
	private boolean zzAtEOF;

	/* user code: */
	/**
	 * Prints out tokens and line numbers from a file or System.in. If no
	 * arguments are given, System.in will be used for input. If more arguments
	 * are given, the first argument will be used as the name of the file to use
	 * as input
	 * 
	 * @param args
	 *            program arguments, of which the first is a filename
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public static void main(String[] args) {
		InputStream in;
		try {
			if (args.length > 0) {
				File f = new File(args[0]);
				if (f.exists()) {
					if (f.canRead()) {
						in = new FileInputStream(f);
					} else {
						throw new IOException("Could not open " + args[0]);
					}
				} else {
					throw new IOException("Could not find " + args[0]);
				}
			} else {
				in = System.in;
			}
			ExcelCSVLexer shredder = new ExcelCSVLexer(in, "ISO-8859-1");
			String t;
			while ((t = shredder.getNextToken()) != null) {
				System.out.print(new StringBuffer(shredder.getLineNumber())
						.append(" ").append(t).append("\r\n").toString());
			}
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
	}

	private char delimiter = ',';

	private char quote = '\"';

	/**
	 * Checks that zzcmap_instance is an instance variable (not just a pointer
	 * to a static variable). If it is a pointer to a static variable, it will
	 * be cloned.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private void ensureCharacterMapIsInstance() {
		if (ZZ_CMAP == zzcmap_instance) {
			zzcmap_instance = new char[ZZ_CMAP.length];
			System.arraycopy(ZZ_CMAP, 0, zzcmap_instance, 0, ZZ_CMAP.length);
		}
	}

	/**
	 * Ensures that the given character is not used for some special purpose in
	 * parsing. This method should be called before setting some character to be
	 * a delimiter so that the parsing doesn't break. Examples of bad characters
	 * are quotes, commas, and whitespace.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private boolean charIsSafe(char c) {
		// There are two character classes that one could use as a delimiter.
		// The first would be the class that most characters are in. These
		// are normally data. The second is the class that the tab is usually
		// in.
		return (zzcmap_instance[c] == ZZ_CMAP['a'] || zzcmap_instance[c] == ZZ_CMAP['\t']);
	}

	/**
	 * Change the character classes of the two given characters. This will make
	 * the state machine behave as if the characters were switched when they are
	 * encountered in the input.
	 * 
	 * @param old
	 *            the old character, its value will be returned to initial
	 * @param two
	 *            second character
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private void updateCharacterClasses(char oldChar, char newChar) {
		// before modifying the character map, make sure it isn't static.
		ensureCharacterMapIsInstance();
		// make the newChar behave like the oldChar
		zzcmap_instance[newChar] = zzcmap_instance[oldChar];
		// make the oldChar behave like it isn't special.
		switch (oldChar) {
		case ',':
		case '"': {
			// These should act as normal character,
			// not delimiters or quotes right now.
			zzcmap_instance[oldChar] = ZZ_CMAP['a'];
		}
			break;
		default: {
			// Set the it back to the way it would act
			// if not used as a delimiter or quote.
			zzcmap_instance[oldChar] = ZZ_CMAP[oldChar];
		}
			break;
		}
	}

	/**
	 * Change this Lexer so that it uses a new delimiter.
	 * <p>
	 * The initial character is a comma, the delimiter cannot be changed to a
	 * quote or other character that has special meaning in CSV.
	 * 
	 * @param newDelim
	 *            delimiter to which to switch.
	 * @throws BadDelimiterException
	 *             if the character cannot be used as a delimiter.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public void changeDelimiter(char newDelim) throws BadDelimiterException {
		if (newDelim == this.delimiter)
			return; // no need to do anything.
		if (!charIsSafe(newDelim)) {
			throw new BadDelimiterException(newDelim
					+ " is not a safe delimiter.");
		}
		updateCharacterClasses(this.delimiter, newDelim);
		// keep a record of the current delimiter.
		this.delimiter = newDelim;
	}

	/**
	 * Change this Lexer so that it uses a new character for quoting.
	 * <p>
	 * The initial character is a double quote ("), the delimiter cannot be
	 * changed to a comma or other character that has special meaning in CSV.
	 * 
	 * @param newQuote
	 *            character to use for quoting.
	 * @throws BadQuoteException
	 *             if the character cannot be used as a quote.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public void changeQuote(char newQuote) throws BadQuoteException {
		if (newQuote == this.quote)
			return; // no need to do anything.
		if (!charIsSafe(newQuote)) {
			throw new BadQuoteException(newQuote + " is not a safe quote.");
		}
		updateCharacterClasses(this.quote, newQuote);
		// keep a record of the current quote.
		this.quote = newQuote;
	}

	private String unescape(String s) {
		if (s.indexOf('\"', 1) == s.length() - 1) {
			return s.substring(1, s.length() - 1);
		}
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = 1; i < s.length() - 1; i++) {
			char c = s.charAt(i);
			char c1 = s.charAt(i + 1);
			if (c == '\"' && c1 == '\"') {
				i++;
				sb.append("\"");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private String commentDelims = "";

	/**
	 * Set the characters that indicate a comment at the beginning of the line.
	 * For example if the string "#;!" were passed in, all of the following
	 * lines would be comments:<br>
	 * 
	 * <pre>
	 *  # Comment
	 *  ; Another Comment
	 *  ! Yet another comment
	 * </pre>
	 * 
	 * By default there are no comments in Excel CVS files. Commas and quotes
	 * may not be used to indicate comment lines.
	 * 
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public void setCommentStart(String commentDelims) {
		this.commentDelims = commentDelims;
	}

	private int addLine = 1;

	private int lines = 0;

	/**
	 * Get the line number that the last token came from.
	 * <p>
	 * New line breaks that occur in the middle of a token are not counted in
	 * the line number count.
	 * <p>
	 * If no tokens have been returned, the line number is undefined.
	 * 
	 * @return line number of the last token.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public int getLineNumber() {
		return this.lines;
	}

	/**
	 * Creates a new scanner There is also a java.io.InputStream version of this
	 * constructor.
	 * 
	 * @param in
	 *            the java.io.Reader to read input from.
	 */
	ExcelCSVLexer(java.io.Reader in) {
		this.zzReader = in;
	}

	/**
	 * Creates a new scanner. There is also java.io.Reader version of this
	 * constructor.
	 * 
	 * @param in
	 *            the java.io.Inputstream to read input from.
	 * @throws UnsupportedEncodingException 
	 */
	ExcelCSVLexer(java.io.InputStream in, String charset) throws UnsupportedEncodingException {
		this(new java.io.InputStreamReader(in, charset));
	}

	/**
	 * Unpacks the compressed character translation table.
	 * 
	 * @param packed
	 *            the packed character translation table
	 * @return the unpacked character translation table
	 */
	private static char[] zzUnpackCMap(String packed) {
		char[] map = new char[0x10000];
		int i = 0; /* index in packed string */
		int j = 0; /* index in unpacked array */
		while (i < 18) {
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);
			do
				map[j++] = value;
			while (--count > 0);
		}
		return map;
	}

	/**
	 * Refills the input buffer.
	 * 
	 * @return <code>false</code>, iff there was new input.
	 * 
	 * @exception java.io.IOException
	 *                if any I/O-Error occurs
	 */
	private boolean zzRefill() throws java.io.IOException {

		/* first: make room (if you can) */
		if (this.zzStartRead > 0) {
			System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0,
					this.zzEndRead - this.zzStartRead);

			/* translate stored positions */
			this.zzEndRead -= this.zzStartRead;
			this.zzCurrentPos -= this.zzStartRead;
			this.zzMarkedPos -= this.zzStartRead;
			this.zzPushbackPos -= this.zzStartRead;
			this.zzStartRead = 0;
		}

		/* is the buffer big enough? */
		if (this.zzCurrentPos >= this.zzBuffer.length) {
			/* if not: blow it up */
			char newBuffer[] = new char[this.zzCurrentPos * 2];
			System.arraycopy(this.zzBuffer, 0, newBuffer, 0,
					this.zzBuffer.length);
			this.zzBuffer = newBuffer;
		}

		/* finally: fill the buffer with new input */
		int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead,
				this.zzBuffer.length - this.zzEndRead);

		if (numRead < 0) {
			return true;
		} else {
			this.zzEndRead += numRead;
			return false;
		}
	}

	// /**
	// * Closes the input stream.
	// */
	// private final void yyclose() throws java.io.IOException {
	// zzAtEOF = true; /* indicate end of file */
	// zzEndRead = zzStartRead; /* invalidate buffer */
	//
	// if (zzReader != null)
	// zzReader.close();
	// }

	/**
	 * Resets the scanner to read from a new input stream. Does not close the
	 * old reader.
	 * 
	 * All internal variables are reset, the old input stream <b>cannot</b> be
	 * reused (internal buffer is discarded and lost). Lexical state is set to
	 * <tt>ZZ_INITIAL</tt>.
	 * 
	 * @param reader
	 *            the new input stream
	 */
	// private final void yyreset(java.io.Reader reader) {
	// zzReader = reader;
	// // zzAtBOL = true;
	// zzAtEOF = false;
	// zzEndRead = zzStartRead = 0;
	// zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
	// // yyline = yychar = yycolumn = 0;
	// zzLexicalState = YYINITIAL;
	// }
	// /**
	// * Returns the current lexical state.
	// */
	// private final int yystate() {
	// return zzLexicalState;
	// }
	/**
	 * Enters a new lexical state
	 * 
	 * @param newState
	 *            the new lexical state
	 */
	private final void yybegin(int newState) {
		this.zzLexicalState = newState;
	}

	/**
	 * Returns the text matched by the current regular expression.
	 */
	private final String yytext() {
		return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos
				- this.zzStartRead);
	}

	/**
	 * Returns the character at position <tt>pos</tt> from the matched text.
	 * 
	 * It is equivalent to yytext().charAt(pos), but faster
	 * 
	 * @param pos
	 *            the position of the character to fetch. A value from 0 to
	 *            yylength()-1.
	 * 
	 * @return the character at position pos
	 */
	// private final char yycharat(int pos) {
	// return zzBuffer[zzStartRead+pos];
	// }
	//
	/**
	 * Returns the length of the matched text region.
	 */
	private final int yylength() {
		return this.zzMarkedPos - this.zzStartRead;
	}

	/**
	 * Reports an error that occured while scanning.
	 * 
	 * In a wellformed scanner (no or only correct usage of yypushback(int) and
	 * a match-all fallback rule) this method will only be called with things
	 * that "Can't Possibly Happen". If this method is called, something is
	 * seriously wrong (e.g. a JFlex bug producing a faulty scanner etc.).
	 * 
	 * Usual syntax/scanner level error handling should be done in error
	 * fallback rules.
	 * 
	 * @param errorCode
	 *            the code of the errormessage to display
	 */
	private void zzScanError(int errorCode) {
		String message;
		try {
			message = ZZ_ERROR_MSG[errorCode];
		} catch (ArrayIndexOutOfBoundsException e) {
			message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
		}

		throw new Error(message);
	}

	/**
	 * Pushes the specified amount of characters back into the input stream.
	 * 
	 * They will be read again by then next call of the scanning method
	 * 
	 * @param number
	 *            the number of characters to be read again. This number must
	 *            not be greater than yylength()!
	 */
	public void yypushback(int number) {
		if (number > yylength())
			zzScanError(ZZ_PUSHBACK_2BIG);

		this.zzMarkedPos -= number;
	}

	/**
	 * Resumes scanning until the next regular expression is matched, the end of
	 * input is encountered or an I/O-Error occurs.
	 * 
	 * @return the next token
	 * @exception java.io.IOException
	 *                if any I/O-Error occurs
	 */
	public String getNextToken() throws java.io.IOException {
		int zzInput;
		int zzAction;

		// cached fields:
		int zzCurrentPosL;
		int zzMarkedPosL;
		int zzEndReadL = this.zzEndRead;
		char[] zzBufferL = this.zzBuffer;
		char[] zzCMapL = ZZ_CMAP;

		int[] zzTransL = ZZ_TRANS;
		int[] zzRowMapL = ZZ_ROWMAP;
		int[] zzAttrL = ZZ_ATTRIBUTE;

		while (true) {
			zzMarkedPosL = this.zzMarkedPos;

			zzAction = -1;

			zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;

			this.zzState = this.zzLexicalState;

			zzForAction: {
				while (true) {

					if (zzCurrentPosL < zzEndReadL)
						zzInput = zzBufferL[zzCurrentPosL++];
					else if (this.zzAtEOF) {
						zzInput = YYEOF;
						break zzForAction;
					} else {
						// store back cached positions
						this.zzCurrentPos = zzCurrentPosL;
						this.zzMarkedPos = zzMarkedPosL;
						boolean eof = zzRefill();
						// get translated positions and possibly new buffer
						zzCurrentPosL = this.zzCurrentPos;
						zzMarkedPosL = this.zzMarkedPos;
						zzBufferL = this.zzBuffer;
						zzEndReadL = this.zzEndRead;
						if (eof) {
							zzInput = YYEOF;
							break zzForAction;
						} else {
							zzInput = zzBufferL[zzCurrentPosL++];
						}
					}
					int zzNext = zzTransL[zzRowMapL[this.zzState]
							+ zzCMapL[zzInput]];
					if (zzNext == -1)
						break zzForAction;
					this.zzState = zzNext;

					int zzAttributes = zzAttrL[this.zzState];
					if ((zzAttributes & 1) == 1) {
						zzAction = this.zzState;
						zzMarkedPosL = zzCurrentPosL;
						if ((zzAttributes & 8) == 8)
							break zzForAction;
					}

				}
			}

			// store back cached position
			this.zzMarkedPos = zzMarkedPosL;

			switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
			case 2: {
				this.lines += this.addLine;
				this.addLine = 0;
				String text = yytext();
				if (this.commentDelims.indexOf(text.charAt(0)) == -1) {
					yybegin(AFTER);
					return (text);
				} else {
					yybegin(COMMENT);
				}
			}
			case 13:
				break;
			case 8: {
				yybegin(BEFORE);
				return ("");
			}
			case 14:
				break;
			case 3: {
				this.addLine++;
				yybegin(YYINITIAL);
			}
			case 15:
				break;
			case 4: {
				this.lines += this.addLine;
				this.addLine = 0;
				yybegin(BEFORE);
				return ("");
			}
			case 16:
				break;
			case 11: {
				this.lines += this.addLine;
				this.addLine = 0;
				yybegin(AFTER);
				return (unescape(yytext()));
			}
			case 17:
				break;
			case 6: {
				yybegin(AFTER);
				return (yytext());
			}
			case 18:
				break;
			case 7: {
				yybegin(YYINITIAL);
				this.addLine++;
				return ("");
			}
			case 19:
				break;
			case 5: {
				this.lines += this.addLine;
				this.addLine = 0;
				yybegin(YYINITIAL);
				return (yytext());
			}
			case 20:
				break;
			case 10: {
				yybegin(BEFORE);
			}
			case 21:
				break;
			case 12: {
				yybegin(AFTER);
				return (unescape(yytext()));
			}
			case 22:
				break;
			case 9: {
				yybegin(YYINITIAL);
				return (yytext());
			}
			case 23:
				break;
			case 1: {
			}
			case 24:
				break;
			default:
				if (zzInput == YYEOF && this.zzStartRead == this.zzCurrentPos) {
					this.zzAtEOF = true;
					switch (this.zzLexicalState) {
					case BEFORE: {
						yybegin(YYINITIAL);
						this.addLine++;
						return ("");
					}
					case 20:
						break;
					default:
						return null;
					}
				} else {
					zzScanError(ZZ_NO_MATCH);
				}
			}
		}
	}

}
