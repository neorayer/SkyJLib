/*
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.exif;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.MetadataException;
import com.skymiracle.image.metadata.Rational;
import com.skymiracle.image.metadata.TagDescriptor;

/**
 * 
 */
public class NikonType2MakernoteDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6435962950385667931L;

	public NikonType2MakernoteDescriptor(Directory directory) {
		super(directory);
	}

	@Override
	public String getDescription(int tagType) throws MetadataException {
		switch (tagType) {
		case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_SETTING:
			return getIsoSettingDescription();
		case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM:
			return getDigitalZoomDescription();
		case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION:
			return getAutoFocusPositionDescription();
		default:
			return this._directory.getString(tagType);
		}
	}

	private String getAutoFocusPositionDescription() throws MetadataException {
		if (!this._directory
				.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION))
			return null;
		int[] values = this._directory
				.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION);
		if (values.length != 4 || values[0] != 0 || values[2] != 0
				|| values[3] != 0) {
			return "Unknown ("
					+ this._directory
							.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION)
					+ ")";
		}
		switch (values[1]) {
		case 0:
			return "Centre";
		case 1:
			return "Top";
		case 2:
			return "Bottom";
		case 3:
			return "Left";
		case 4:
			return "Right";
		default:
			return "Unknown (" + values[1] + ")";
		}
	}

	private String getDigitalZoomDescription() throws MetadataException {
		if (!this._directory
				.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM))
			return null;
		Rational rational = this._directory
				.getRational(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM);
		if (rational.intValue() == 1) {
			return "No digital zoom";
		}
		return rational.toSimpleString(true) + "x digital zoom";
	}

	private String getIsoSettingDescription() throws MetadataException {
		if (!this._directory
				.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_SETTING))
			return null;
		int[] values = this._directory
				.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_SETTING);
		if (values[0] != 0 || values[1] == 0) {
			return "Unknown ("
					+ this._directory
							.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_SETTING)
					+ ")";
		}
		return "ISO " + values[1];
	}
}
