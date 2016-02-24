package com.skymiracle.image.metadata;

import java.io.File;
import java.io.InputStream;

import com.skymiracle.image.metadata.exif.ExifDirectory;
import com.skymiracle.image.metadata.exif.ExifReader;
import com.skymiracle.image.metadata.iptc.IptcReader;
import com.skymiracle.image.metadata.jpeg.JpegCommentReader;
import com.skymiracle.image.metadata.jpeg.JpegReader;
import com.sun.image.codec.jpeg.JPEGDecodeParam;

public class JpegMetadataReader {
	public static Metadata readMetadata(InputStream in)
			throws JpegProcessingException {
		JpegSegmentReader segmentReader = new JpegSegmentReader(in);
		return extractJpegSegmentReaderMetadata(segmentReader);
	}

	public static Metadata readMetadata(File file)
			throws JpegProcessingException {
		JpegSegmentReader segmentReader = new JpegSegmentReader(file);
		return extractJpegSegmentReaderMetadata(segmentReader);
	}

	public static JpegMetaData getJpegMetaData(File file) throws Exception {
		JpegSegmentReader segmentReader = new JpegSegmentReader(file);
		Metadata metadata = extractJpegSegmentReaderMetadata(segmentReader);
		Directory d = metadata.getDirectory(ExifDirectory.class);

		JpegMetaData jmd = new JpegMetaData();
		jmd.setAperture(d.getString(ExifDirectory.TAG_APERTURE));
		jmd.setDateTimeDigitized(d
				.getString(ExifDirectory.TAG_DATETIME_DIGITIZED));
		jmd.setFocalLength(d.getString(ExifDirectory.TAG_FOCAL_LENGTH));
		jmd.setIso(d.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
		jmd.setMaker(d.getString(ExifDirectory.TAG_MAKE));
		jmd.setShutterSpeed(d.getString(ExifDirectory.TAG_SHUTTER_SPEED));
		jmd.setModel(d.getString(ExifDirectory.TAG_MODEL));
		return jmd;
	}

	private static Metadata extractJpegSegmentReaderMetadata(
			JpegSegmentReader segmentReader) {
		final Metadata metadata = new Metadata();
		try {
			byte[] exifSegment = segmentReader
					.readSegment(JpegSegmentReader.SEGMENT_APP1);
			new ExifReader(exifSegment).extract(metadata);
		} catch (JpegProcessingException e) {
			// in the interests of catching as much data as possible,
			// continue
			// TODO lodge error message within exif directory?
		}

		try {
			byte[] iptcSegment = segmentReader
					.readSegment(JpegSegmentReader.SEGMENT_APPD);
			new IptcReader(iptcSegment).extract(metadata);
		} catch (JpegProcessingException e) {
			// TODO lodge error message within iptc directory?
		}

		try {
			byte[] jpegSegment = segmentReader
					.readSegment(JpegSegmentReader.SEGMENT_SOF0);
			new JpegReader(jpegSegment).extract(metadata);
		} catch (JpegProcessingException e) {
			// TODO lodge error message within jpeg directory?
		}

		try {
			byte[] jpegCommentSegment = segmentReader
					.readSegment(JpegSegmentReader.SEGMENT_COM);
			new JpegCommentReader(jpegCommentSegment).extract(metadata);
		} catch (JpegProcessingException e) {
			// TODO lodge error message within jpegcomment directory?
		}

		return metadata;
	}

	public static Metadata readMetadata(JPEGDecodeParam decodeParam) {
		final Metadata metadata = new Metadata();

		/*
		 * We should only really be seeing Exif in _data[0]... the 2D array
		 * exists because markers can theoretically appear multiple times in the
		 * file.
		 */
		// TODO test this method
		byte[][] exifSegment = decodeParam
				.getMarkerData(JPEGDecodeParam.APP1_MARKER);
		if (exifSegment != null && exifSegment[0].length > 0) {
			new ExifReader(exifSegment[0]).extract(metadata);
		}

		// similarly, use only the first IPTC segment
		byte[][] iptcSegment = decodeParam
				.getMarkerData(JPEGDecodeParam.APPD_MARKER);
		if (iptcSegment != null && iptcSegment[0].length > 0) {
			new IptcReader(iptcSegment[0]).extract(metadata);
		}

		// NOTE: Unable to utilise JpegReader for the SOF0 frame here, as
		// the decodeParam doesn't contain the byte[]

		// similarly, use only the first Jpeg Comment segment
		byte[][] jpegCommentSegment = decodeParam
				.getMarkerData(JPEGDecodeParam.COMMENT_MARKER);
		if (jpegCommentSegment != null && jpegCommentSegment[0].length > 0) {
			new JpegCommentReader(jpegCommentSegment[0]).extract(metadata);
		}

		return metadata;
	}

	private JpegMetadataReader() {
	}
}
