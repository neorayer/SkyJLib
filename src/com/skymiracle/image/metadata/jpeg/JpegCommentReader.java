package com.skymiracle.image.metadata.jpeg;

import java.io.File;
import java.io.FileNotFoundException;

import com.skymiracle.image.metadata.JpegProcessingException;
import com.skymiracle.image.metadata.JpegSegmentReader;
import com.skymiracle.image.metadata.Metadata;
import com.skymiracle.image.metadata.MetadataReader;

/**
 * Created by IntelliJ IDEA. User: Drew Noakes Date: Oct 10, 2003
 * 
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegCommentReader implements MetadataReader {
	/**
	 * The COM data segment.
	 */
	private final byte[] _data;

	/**
	 * Creates a new JpegReader for the specified Jpeg jpegFile.
	 */
	public JpegCommentReader(File jpegFile) throws JpegProcessingException,
			FileNotFoundException {
		this(new JpegSegmentReader(jpegFile)
				.readSegment(JpegSegmentReader.SEGMENT_COM));
	}

	public JpegCommentReader(byte[] data) {
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

		JpegCommentDirectory directory = (JpegCommentDirectory) metadata
				.getDirectory(JpegCommentDirectory.class);

		directory.setString(JpegCommentDirectory.TAG_JPEG_COMMENT, new String(
				this._data));

		return metadata;
	}
}
