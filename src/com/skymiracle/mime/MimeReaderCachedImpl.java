package com.skymiracle.mime;

import java.io.File;
import com.skymiracle.io.Dir;
import com.skymiracle.mime.Mime;
import com.skymiracle.mime.MimeImpl;
import com.skymiracle.util.FsHashPath;
import com.skymiracle.util.HashPath;

public class MimeReaderCachedImpl implements MimeReader {

	private String cacheRootPath = "/tmp/mime-cache";

	public MimeReaderCachedImpl() {
		super();
	}

	public MimeReaderCachedImpl(String cacheRootPath) {
		this();
		this.cacheRootPath = cacheRootPath;
	}

	public String getCacheRootPath() {
		return cacheRootPath;
	}

	public void setCacheRootPath(String cacheRootPath) {
		this.cacheRootPath = cacheRootPath;
	}

	public Mime loadMime(File file, String uuid) throws Exception {
		HashPath hashPath = new FsHashPath(this.cacheRootPath, uuid);
		String cachePath = hashPath.getPath(2);
		
		if (new File(cachePath).exists()) {
			return new MimeImpl(cachePath);
		} else {
			Dir dir = new Dir(cachePath);
			if (dir.mkdirs()) {
				Mime mime = new MimeImpl(file.getAbsolutePath(), true);
				mime.save(cachePath);
				return new MimeImpl(cachePath);
			} else
				throw new Exception("can't create dirs:" + cachePath);
		}
	}
	
	public static void main(String[] args) {
		//
		HashPath hashPath = new FsHashPath("/wpx/", "c00def36-2a32-4bdf-9279-6f04bac13470");
		String cachePath = hashPath.getPath(2);
		System.out.println(cachePath);
	}

}
