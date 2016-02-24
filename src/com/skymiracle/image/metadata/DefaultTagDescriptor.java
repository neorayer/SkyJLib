/*
 * Created by dnoakes on 22-Nov-2002 16:45:19 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata;

/**
 * 
 */
public class DefaultTagDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8949712152053625340L;

	public DefaultTagDescriptor(Directory directory) {
		super(directory);
	}

	public String getTagName(int tagType) {
		String hex = Integer.toHexString(tagType).toUpperCase();
		while (hex.length() < 4)
			hex = "0" + hex;
		return "Unknown tag 0x" + hex;
	}

	@Override
	public String getDescription(int tagType) {
		return this._directory.getString(tagType);
	}
}
