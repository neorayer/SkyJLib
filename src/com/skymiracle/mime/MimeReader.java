package com.skymiracle.mime;

import java.io.File;

import com.skymiracle.mime.Mime;

public interface MimeReader {

	public Mime loadMime(File file, String uuid) throws Exception;
}
