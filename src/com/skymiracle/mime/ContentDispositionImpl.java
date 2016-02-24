package com.skymiracle.mime;

import com.skymiracle.util.Rfc2047Codec;
import com.skymiracle.util.Rfc2047Codec.CodeRes;

public class ContentDispositionImpl extends RichAttributeImpl implements
		ContentDisposition {

	public ContentDispositionImpl(String src) {
		super(src);
	}

//	public String getFileName() {
//		return Rfc2047Codec.decode(getAttrValue("filename"));
//	}

	public CodeRes getFileNameCodeRes() {
		return new Rfc2047Codec().decodeRes(getAttrValue("filename"));
	}

}
