package com.skymiracle.image.metadata;

import java.io.File;

import com.skymiracle.json.JSONTools;

public class Test {

	public static void main(String[] args) throws Exception {
		JpegMetaData jmd = JpegMetadataReader.getJpegMetaData(new File(
				"/tmp/113d70066193961aff3d332dc13821ff5fcfcc9ac17.jpg"));
		System.out.println(JSONTools.getJSONObject(jmd));
	}
}
