/*
 * Created by dnoakes on 12-Nov-2002 19:00:03 using IntelliJ IDEA.
 */
package com.skymiracle.image.metadata.iptc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import com.skymiracle.image.metadata.Directory;
import com.skymiracle.image.metadata.JpegProcessingException;
import com.skymiracle.image.metadata.JpegSegmentReader;
import com.skymiracle.image.metadata.Metadata;
import com.skymiracle.image.metadata.MetadataException;
import com.skymiracle.image.metadata.MetadataReader;

/**
 * 
 */
public class IptcReader implements MetadataReader {
	/*
	 * public static final int DIRECTORY_IPTC = 2;
	 * 
	 * public static final int ENVELOPE_RECORD = 1; public static final int
	 * APPLICATION_RECORD_2 = 2; public static final int APPLICATION_RECORD_3 =
	 * 3; public static final int APPLICATION_RECORD_4 = 4; public static final
	 * int APPLICATION_RECORD_5 = 5; public static final int
	 * APPLICATION_RECORD_6 = 6; public static final int PRE_DATA_RECORD = 7;
	 * public static final int DATA_RECORD = 8; public static final int
	 * POST_DATA_RECORD = 9;
	 */
	/**
	 * The Iptc data segment.
	 */
	private final byte[] _data;

	/**
	 * Creates a new IptcReader for the specified Jpeg jpegFile.
	 */
	public IptcReader(File jpegFile) throws JpegProcessingException,
			FileNotFoundException {
		this(new JpegSegmentReader(jpegFile)
				.readSegment(JpegSegmentReader.SEGMENT_APPD));
	}

	public IptcReader(byte[] data) {
		this._data = data;
	}

	/**
	 * Performs the Exif data extraction, returning a new instance of
	 * <code>Metadata</code>.
	 */
	public Metadata extract() {
		return extract(new Metadata());
	}

	/**
	 * Performs the Exif data extraction, adding found values to the specified
	 * instance of <code>Metadata</code>.
	 */
	public Metadata extract(Metadata metadata) {
		if (this._data == null) {
			return metadata;
		}

		Directory directory = metadata.getDirectory(IptcDirectory.class);

		// find start of data
		int offset = 0;
		try {
			while (offset < this._data.length - 1
					&& get32Bits(offset) != 0x1c02) {
				offset++;
			}
		} catch (MetadataException e) {
			directory
					.addError("Couldn't find start of Iptc data (invalid segment)");
			return metadata;
		}

		// for each tag
		while (offset < this._data.length) {
			// identifies start of a tag
			if (this._data[offset] != 0x1c) {
				break;
			}
			// we need at least five bytes left to read a tag
			if ((offset + 5) >= this._data.length) {
				break;
			}

			offset++;

			int directoryType;
			int tagType;
			int tagByteCount;
			try {
				directoryType = this._data[offset++];
				tagType = this._data[offset++];
				tagByteCount = get32Bits(offset);
			} catch (MetadataException e) {
				directory
						.addError("Iptc data segment ended mid-way through tag descriptor");
				return metadata;
			}
			offset += 2;
			if ((offset + tagByteCount) > this._data.length) {
				directory
						.addError("data for tag extends beyond end of iptc segment");
				break;
			}

			processTag(directory, directoryType, tagType, offset, tagByteCount);
			offset += tagByteCount;
		}

		return metadata;
	}

	/**
	 * Returns an int calculated from two bytes of data at the specified offset
	 * (MSB, LSB).
	 * 
	 * @param offset
	 *            position within the data buffer to read first byte
	 * @return the 32 bit int value, between 0x0000 and 0xFFFF
	 */
	private int get32Bits(int offset) throws MetadataException {
		if (offset >= this._data.length) {
			throw new MetadataException(
					"Attempt to read bytes from outside Iptc data buffer");
		}
		return ((this._data[offset] & 255) << 8)
				| (this._data[offset + 1] & 255);
	}

	/**
	 * This method serves as marsheller of objects for dataset. It converts from
	 * IPTC octets to relevant java object.
	 */
	private void processTag(Directory directory, int directoryType,
			int tagType, int offset, int tagByteCount) {
		int tagIdentifier = tagType | (directoryType << 8);

		switch (tagIdentifier) {
		case IptcDirectory.TAG_RECORD_VERSION:
			// short
			short shortValue = (short) ((this._data[offset] << 8) | this._data[offset + 1]);
			directory.setInt(tagIdentifier, shortValue);
			return;
		case IptcDirectory.TAG_URGENCY:
			// byte
			directory.setInt(tagIdentifier, this._data[offset]);
			return;
		case IptcDirectory.TAG_RELEASE_DATE:
		case IptcDirectory.TAG_DATE_CREATED:
			// Date object
			if (tagByteCount >= 8) {
				String dateStr = new String(this._data, offset, tagByteCount);
				try {
					int year = Integer.parseInt(dateStr.substring(0, 4));
					int month = Integer.parseInt(dateStr.substring(4, 6)) - 1;
					int day = Integer.parseInt(dateStr.substring(6, 8));
					Date date = (new java.util.GregorianCalendar(year, month,
							day)).getTime();
					directory.setDate(tagIdentifier, date);
					return;
				} catch (NumberFormatException e) {
					// fall through and we'll store whatever was there as a
					// String
				}
			}
		case IptcDirectory.TAG_RELEASE_TIME:
		case IptcDirectory.TAG_TIME_CREATED:
			// time...
		default:
			// fall through
		}
		// If no special handling by now, treat it as a string
		String str;
		if (tagByteCount < 1) {
			str = "";
		} else {
			str = new String(this._data, offset, tagByteCount);
		}
		if (directory.containsTag(tagIdentifier)) {
			String[] oldStrings;
			String[] newStrings;
			try {
				oldStrings = directory.getStringArray(tagIdentifier);
			} catch (MetadataException e) {
				oldStrings = null;
			}
			if (oldStrings == null) {
				newStrings = new String[1];
			} else {
				newStrings = new String[oldStrings.length + 1];
				for (int i = 0; i < oldStrings.length; i++) {
					newStrings[i] = oldStrings[i];
				}
			}
			newStrings[newStrings.length - 1] = str;
			directory.setStringArray(tagIdentifier, newStrings);
		} else {
			directory.setString(tagIdentifier, str);
		}
	}
}
