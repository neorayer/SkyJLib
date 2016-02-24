package com.skymiracle.image.metadata.jpeg;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.TagDescriptor;

/**
 * Created by IntelliJ IDEA. User: Drew Noakes Date: Oct 10, 2003
 * 
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegCommentDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8128749038859636131L;

	public JpegCommentDescriptor(Directory directory) {
		super(directory);
	}

	@Override
	public String getDescription(int tagType) {
		return this._directory.getString(tagType);
	}
}
