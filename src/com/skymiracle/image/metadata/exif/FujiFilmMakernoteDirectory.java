/*
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.exif;

import java.util.HashMap;

import com.skymiracle.image.metadata.Directory;

/**
 * 
 */
public class FujiFilmMakernoteDirectory extends Directory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8556895616735053885L;
	public static final int TAG_FUJIFILM_MAKERNOTE_VERSION = 0x0000;
	public static final int TAG_FUJIFILM_QUALITY = 0x1000;
	public static final int TAG_FUJIFILM_SHARPNESS = 0x1001;
	public static final int TAG_FUJIFILM_WHITE_BALANCE = 0x1002;
	public static final int TAG_FUJIFILM_COLOR = 0x1003;
	public static final int TAG_FUJIFILM_TONE = 0x1004;
	public static final int TAG_FUJIFILM_FLASH_MODE = 0x1010;
	public static final int TAG_FUJIFILM_FLASH_STRENGTH = 0x1011;
	public static final int TAG_FUJIFILM_MACRO = 0x1020;
	public static final int TAG_FUJIFILM_FOCUS_MODE = 0x1021;
	public static final int TAG_FUJIFILM_SLOW_SYNCHRO = 0x1030;
	public static final int TAG_FUJIFILM_PICTURE_MODE = 0x1031;
	public static final int TAG_FUJIFILM_UNKNOWN_1 = 0x1032;
	public static final int TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING = 0x1100;
	public static final int TAG_FUJIFILM_UNKNOWN_2 = 0x1200;
	public static final int TAG_FUJIFILM_BLUR_WARNING = 0x1300;
	public static final int TAG_FUJIFILM_FOCUS_WARNING = 0x1301;
	public static final int TAG_FUJIFILM_AE_WARNING = 0x1302;

	protected static final HashMap tagNameMap = new HashMap();

	static {
		tagNameMap.put(new Integer(TAG_FUJIFILM_AE_WARNING), "AE Warning");
		tagNameMap.put(new Integer(TAG_FUJIFILM_BLUR_WARNING), "Blur Warning");
		tagNameMap.put(new Integer(TAG_FUJIFILM_COLOR), "Color");
		tagNameMap.put(new Integer(
				TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING),
				"Continuous Taking Or Auto Bracketting");
		tagNameMap.put(new Integer(TAG_FUJIFILM_FLASH_MODE), "Flash Mode");
		tagNameMap.put(new Integer(TAG_FUJIFILM_FLASH_STRENGTH),
				"Flash Strength");
		tagNameMap.put(new Integer(TAG_FUJIFILM_FOCUS_MODE), "Focus Mode");
		tagNameMap
				.put(new Integer(TAG_FUJIFILM_FOCUS_WARNING), "Focus Warning");
		tagNameMap.put(new Integer(TAG_FUJIFILM_MACRO), "Macro");
		tagNameMap.put(new Integer(TAG_FUJIFILM_MAKERNOTE_VERSION),
				"Makernote Version");
		tagNameMap.put(new Integer(TAG_FUJIFILM_PICTURE_MODE), "Picture Mode");
		tagNameMap.put(new Integer(TAG_FUJIFILM_QUALITY), "Quality");
		tagNameMap.put(new Integer(TAG_FUJIFILM_SHARPNESS), "Sharpness");
		tagNameMap.put(new Integer(TAG_FUJIFILM_SLOW_SYNCHRO), "Slow Synchro");
		tagNameMap.put(new Integer(TAG_FUJIFILM_TONE), "Tone");
		tagNameMap.put(new Integer(TAG_FUJIFILM_UNKNOWN_1),
				"Makernote Unknown 1");
		tagNameMap.put(new Integer(TAG_FUJIFILM_UNKNOWN_2),
				"Makernote Unknown 2");
		tagNameMap
				.put(new Integer(TAG_FUJIFILM_WHITE_BALANCE), "White Balance");
	}

	public FujiFilmMakernoteDirectory() {
		this.setDescriptor(new FujifilmMakernoteDescriptor(this));
	}

	@Override
	public String getName() {
		return "FujiFilm Makernote";
	}

	@Override
	protected HashMap getTagNameMap() {
		return tagNameMap;
	}
}
