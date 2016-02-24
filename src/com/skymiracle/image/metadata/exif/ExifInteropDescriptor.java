/*
 * Created by dnoakes on 12-Nov-2002 22:27:34 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.exif;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.MetadataException;
import com.skymiracle.image.metadata.TagDescriptor;

/**
 * 
 */
public class ExifInteropDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1660178537722332382L;

	public ExifInteropDescriptor(Directory directory) {
		super(directory);
	}

	@Override
	public String getDescription(int tagType) throws MetadataException {
		switch (tagType) {
		case ExifInteropDirectory.TAG_INTEROP_INDEX:
			return getInteropIndexDescription();
		case ExifInteropDirectory.TAG_INTEROP_VERSION:
			return getInteropVersionDescription();
		default:
			return this._directory.getString(tagType);
		}
	}

	private String getInteropVersionDescription() throws MetadataException {
		if (!this._directory
				.containsTag(ExifInteropDirectory.TAG_INTEROP_VERSION))
			return null;
		int[] ints = this._directory
				.getIntArray(ExifInteropDirectory.TAG_INTEROP_VERSION);
		return ExifDescriptor.convertBytesToVersionString(ints);
	}

	private String getInteropIndexDescription() {
		if (!this._directory
				.containsTag(ExifInteropDirectory.TAG_INTEROP_INDEX))
			return null;
		String interopIndex = this._directory.getString(
				ExifInteropDirectory.TAG_INTEROP_INDEX).trim();
		if ("R98".equalsIgnoreCase(interopIndex)) {
			return "Recommended Exif Interoperability Rules (ExifR98)";
		} else {
			return "Unknown (" + interopIndex + ")";
		}
	}
}
