package com.skymiracle.image.metadata.jpeg;

import java.io.File;
import java.io.FileNotFoundException;

import com.skymiracle.image.metadata.JpegProcessingException;
import com.skymiracle.image.metadata.JpegSegmentReader;
import com.skymiracle.image.metadata.Metadata;
import com.skymiracle.image.metadata.MetadataException;
import com.skymiracle.image.metadata.MetadataReader;

/**
 * Created by IntelliJ IDEA. User: Darrell Silver www.darrellsilver.com Date:
 * Aug 2, 2003
 * 
 * @author Darrell Silver http://www.darrellsilver.com
 */
public class JpegReader implements MetadataReader {
	/**
	 * The SOF0 data segment.
	 */
	private final byte[] _data;

	/**
	 * Creates a new JpegReader for the specified Jpeg jpegFile.
	 */
	public JpegReader(File jpegFile) throws JpegProcessingException,
			FileNotFoundException {
		this(new JpegSegmentReader(jpegFile)
				.readSegment(JpegSegmentReader.SEGMENT_SOF0));
	}

	public JpegReader(byte[] data) {
		this._data = data;
	}

	/**
	 * Performs the Jpeg data extraction, returning a new instance of
	 * <code>Metadata</code>.
	 */
	public Metadata extract() {
		return extract(new Metadata());
	}

	/**
	 * Performs the Jpeg data extraction, adding found values to the specified
	 * instance of <code>Metadata</code>.
	 */
	public Metadata extract(Metadata metadata) {
		if (this._data == null) {
			return metadata;
		}

		JpegDirectory directory = (JpegDirectory) metadata
				.getDirectory(JpegDirectory.class);

		try {
			// data precision
			int dataPrecision = get16Bits(JpegDirectory.TAG_JPEG_DATA_PRECISION);
			directory.setInt(JpegDirectory.TAG_JPEG_DATA_PRECISION,
					dataPrecision);

			// process height
			int height = get32Bits(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT);
			directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, height);

			// process width
			int width = get32Bits(JpegDirectory.TAG_JPEG_IMAGE_WIDTH);
			directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, width);

			// number of components
			int numberOfComponents = get16Bits(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS);
			directory.setInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS,
					numberOfComponents);

			// for each component, there are three bytes of data:
			// 1 - Component ID: 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
			// 2 - Sampling factors: bit 0-3 vertical, 4-7 horizontal
			// 3 - Quantization table number
			int offset = 6;
			for (int i = 0; i < numberOfComponents; i++) {
				int componentId = get16Bits(offset++);
				int samplingFactorByte = get16Bits(offset++);
				int quantizationTableNumber = get16Bits(offset++);
				JpegComponent component = new JpegComponent(componentId,
						samplingFactorByte, quantizationTableNumber);
				directory.setObject(
						JpegDirectory.TAG_JPEG_COMPONENT_DATA_1 + i, component);
			}

		} catch (MetadataException me) {
			directory.addError("MetadataException: " + me);
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
		if (offset + 1 >= this._data.length) {
			throw new MetadataException(
					"Attempt to read bytes from outside Jpeg segment data buffer");
		}

		return ((this._data[offset] & 255) << 8)
				| (this._data[offset + 1] & 255);
	}

	/**
	 * Returns an int calculated from one byte of data at the specified offset.
	 * 
	 * @param offset
	 *            position within the data buffer to read byte
	 * @return the 16 bit int value, between 0x00 and 0xFF
	 */
	private int get16Bits(int offset) throws MetadataException {
		if (offset >= this._data.length) {
			throw new MetadataException(
					"Attempt to read bytes from outside Jpeg segment data buffer");
		}

		return (this._data[offset] & 255);
	}
}
