/*
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.exif;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.MetadataException;
import com.skymiracle.image.metadata.TagDescriptor;

/**
 * 
 */
public class OlympusMakernoteDescriptor extends TagDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2657937778866837275L;

	public OlympusMakernoteDescriptor(Directory directory) {
		super(directory);
	}

	@Override
	public String getDescription(int tagType) throws MetadataException {
		switch (tagType) {
		case OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE:
			return getSpecialModeDescription();
		case OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY:
			return getJpegQualityDescription();
		case OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE:
			return getMacroModeDescription();
		case OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO:
			return getDigiZoomRatioDescription();
		default:
			return this._directory.getString(tagType);
		}
	}

	private String getDigiZoomRatioDescription() throws MetadataException {
		if (!this._directory
				.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO))
			return null;
		int value = this._directory
				.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO);
		switch (value) {
		case 0:
			return "Normal";
		case 2:
			return "Digital 2x Zoom";
		default:
			return "Unknown (" + value + ")";
		}
	}

	private String getMacroModeDescription() throws MetadataException {
		if (!this._directory
				.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE))
			return null;
		int value = this._directory
				.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE);
		switch (value) {
		case 0:
			return "Normal (no macro)";
		case 1:
			return "Macro";
		default:
			return "Unknown (" + value + ")";
		}
	}

	private String getJpegQualityDescription() throws MetadataException {
		if (!this._directory
				.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY))
			return null;
		int value = this._directory
				.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY);
		switch (value) {
		case 1:
			return "SQ";
		case 2:
			return "HQ";
		case 3:
			return "SHQ";
		default:
			return "Unknown (" + value + ")";
		}
	}

	private String getSpecialModeDescription() throws MetadataException {
		if (!this._directory
				.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE))
			return null;
		int[] values = this._directory
				.getIntArray(OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE);
		StringBuffer desc = new StringBuffer();
		switch (values[0]) {
		case 0:
			desc.append("Normal picture taking mode");
			break;
		case 1:
			desc.append("Unknown picture taking mode");
			break;
		case 2:
			desc.append("Fast picture taking mode");
			break;
		case 3:
			desc.append("Panorama picture taking mode");
			break;
		default:
			desc.append("Unknown picture taking mode");
			break;
		}
		desc.append(" - ");
		switch (values[1]) {
		case 0:
			desc.append("Unknown sequence number");
			break;
		case 1:
			desc.append("1st in a sequnce");
			break;
		case 2:
			desc.append("2nd in a sequence");
			break;
		case 3:
			desc.append("3rd in a sequence");
			break;
		default:
			desc.append(values[1]);
			desc.append("th in a sequence");
			break;
		}
		switch (values[2]) {
		case 1:
			desc.append("Left to right panorama direction");
			break;
		case 2:
			desc.append("Right to left panorama direction");
			break;
		case 3:
			desc.append("Bottom to top panorama direction");
			break;
		case 4:
			desc.append("Top to bottom panorama direction");
			break;
		}
		return desc.toString();
	}
}
