/*
 * Created by dnoakes on 21-Nov-2002 17:58:19 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.iptc;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.TagDescriptor;

/**
 * 
 */
public class IptcDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2726057367131575913L;

	public IptcDescriptor(Directory directory) {
		super(directory);
	}

	@Override
	public String getDescription(int tagType) {
		return this._directory.getString(tagType);
	}
}
