package com.skymiracle.mime;

import com.skymiracle.util.Rfc2047Codec.CodeRes;

public interface ContentDisposition extends RichAttribute {
	public CodeRes getFileNameCodeRes();
}
