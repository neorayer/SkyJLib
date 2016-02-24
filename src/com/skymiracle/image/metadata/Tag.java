/*
 * Created by dnoakes on 26-Nov-2002 18:29:12 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata;

import java.io.Serializable;

/**
 * 
 */
public class Tag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8417011063890924387L;
	private final int _tagType;
	private final Directory _directory;

	public Tag(int tagType, Directory directory) {
		this._tagType = tagType;
		this._directory = directory;
	}

	/**
	 * Gets the tag type as an int
	 * 
	 * @return the tag type as an int
	 */
	public int getTagType() {
		return this._tagType;
	}

	/**
	 * Gets the tag type in hex notation as a String with padded leading zeroes
	 * if necessary (i.e. <code>0x100E</code>).
	 * 
	 * @return the tag type as a string in hexadecimal notation
	 */
	public String getTagTypeHex() {
		String hex = Integer.toHexString(this._tagType);
		while (hex.length() < 4)
			hex = "0" + hex;
		return "0x" + hex;
	}

	/**
	 * Get a description of the tag's value, considering enumerated values and
	 * units.
	 * 
	 * @return a description of the tag's value
	 */
	public String getDescription() throws MetadataException {
		return this._directory.getDescription(this._tagType);
	}

	/**
	 * Get the name of the tag, such as <code>Aperture</code>, or
	 * <code>InteropVersion</code>.
	 * 
	 * @return the tag's name
	 */
	public String getTagName() {
		return this._directory.getTagName(this._tagType);
	}

	/**
	 * Get the name of the directory in which the tag exists, such as
	 * <code>Exif</code>, <code>GPS</code> or <code>Interoperability</code>.
	 * 
	 * @return name of the directory in which this tag exists
	 */
	public String getDirectoryName() {
		return this._directory.getName();
	}

	/**
	 * A basic representation of the tag's type and value in format:
	 * <code>FNumber - F2.8</code>.
	 * 
	 * @return the tag's type and value
	 */
	@Override
	public String toString() {
		String description;
		try {
			description = getDescription();
		} catch (MetadataException e) {
			description = this._directory.getString(getTagType())
					+ " (unable to formulate description)";
		}
		return "[" + this._directory.getName() + "] " + getTagName() + " - "
				+ description;
	}
}
