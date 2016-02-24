package com.skymiracle.image.metadata.jpeg;

import java.util.HashMap;

import com.skymiracle.image.metadata.Directory;

/**
 * Created by IntelliJ IDEA. User: Drew Noakes Date: Oct 10, 2003
 * 
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegCommentDirectory extends Directory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957278627541393839L;

	/**
	 * This is in bits/sample, usually 8 (12 and 16 not supported by most
	 * software).
	 */
	public static final int TAG_JPEG_COMMENT = 0;

	protected static final HashMap tagNameMap = new HashMap();

	static {
		tagNameMap.put(new Integer(TAG_JPEG_COMMENT), "Jpeg Comment");
	}

	public JpegCommentDirectory() {
		this.setDescriptor(new JpegCommentDescriptor(this));
	}

	@Override
	public String getName() {
		return "JpegComment";
	}

	@Override
	protected HashMap getTagNameMap() {
		return tagNameMap;
	}
}
