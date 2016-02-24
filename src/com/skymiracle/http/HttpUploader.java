package com.skymiracle.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.skymiracle.logger.Logger;
import com.skymiracle.util.UUID;

public class HttpUploader {

	private int maxMemorySize = 102400000;

	private long maxRequestSize = 102400000;

	private String charset = "UTF-8";

	private String tmpDirPath = "/tmp/";

	public HttpUploader() {
	}

	public HttpUploader(String charset) {
		this.charset = charset;
	}

	public int getMaxMemorySize() {
		return maxMemorySize;
	}

	public void setMaxMemorySize(int maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}

	public long getMaxRequestSize() {
		return maxRequestSize;
	}

	public void setMaxRequestSize(long maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTmpDirPath() {
		return tmpDirPath;
	}

	public void setTmpDirPath(String tmpDirPath) {
		this.tmpDirPath = tmpDirPath;
	}

	public UploadResultSet doUpload(HttpServletRequest request)
			throws Exception {
		return doUpload(request, null);
	}

	@SuppressWarnings("unchecked")
	public UploadResultSet doUpload(HttpServletRequest request,
			String progressId) throws Exception {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// Make sure that the data is written to file
		factory.setSizeThreshold(0);

		factory.setSizeThreshold(maxMemorySize);
		factory.setRepository(new File(tmpDirPath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding(charset);
		upload.setSizeMax(maxRequestSize);
		if (progressId != null) {
			upload.setProgressListener(getListener(progressId, true));
		}
		try {
			List<FileItem> items = upload.parseRequest(request);
			List<TempUpFile> tempUpFiles = new LinkedList<TempUpFile>();

			ParamsMap paraMap = new ParamsMap();
			for (FileItem item : items) {
				

				if (item.isFormField()) { // parameters
					String name = item.getFieldName();
					try {
						String value = item.getString(charset);
						paraMap.put(name, value);
					} catch (UnsupportedEncodingException e) {
						Logger.error("", e);
					}
				} else { // files
					if (item.getSize() == 0)
						continue;
					String oFilePath = item.getName();

					String attPath = tmpDirPath + "/"
							+ new UUID().toShortString();
					item.write(new File(attPath));
					TempUpFile tempUpFile = new TempUpFile();

					oFilePath = oFilePath.replace('\\', '/');
					tempUpFile.setFieldName(item.getFieldName());
					tempUpFile.setOrginalName(new File(oFilePath).getName());
					tempUpFile.setTmpUpPath(attPath);
					tempUpFiles.add(tempUpFile);
				}
			}
			return new UploadResultSet(tempUpFiles, paraMap);
		} catch (SizeLimitExceededException e) {
			throw new Exception("上传文件过大");
		} finally {
			if (progressId != null) {
				listenersMap.remove(progressId);
			}
		}

	}

	public static Map<String, ProcListener> listenersMap = new ConcurrentHashMap<String, ProcListener>();

	public static ProcListener getListener(String id) {
		return getListener(id, false);
	}

	private static ProcListener getListener(String id,
			boolean createWhileNoExist) {
		ProcListener l = listenersMap.get(id);
		if (createWhileNoExist) {
			if (l == null) {
				l = new ProcListener(id);
				listenersMap.put(id, l);
			}
		}
		return l;
	}

	// 上传进度
	public static class ProcListener implements ProgressListener {
		private long megaBytes = -1;
		private long allLength;
		private long curLength;
		private String id;

		public ProcListener(String id) {
			this.id = id;
		}

		public void update(long curLength, long allLength, int pItems) {
			long mBytes = curLength / 10000;
			if (megaBytes == mBytes) {
				return;
			}
			megaBytes = mBytes;
			this.curLength = curLength;
			this.allLength = allLength;
			Logger.debug("upload id:" + id + " " + curLength + "/" + allLength);
		}

		public long getAllLength() {
			return allLength;
		}

		public long getCurLength() {
			return curLength;
		}

	};

	// 上传结果集
	public class UploadResultSet {

		private List<TempUpFile> tempUpFiles;

		private ParamsMap paramsMap;

		public UploadResultSet(List<TempUpFile> tempUpFiles, ParamsMap paramsMap) {
			super();
			this.tempUpFiles = tempUpFiles;
			this.paramsMap = paramsMap;
		}

		public List<TempUpFile> getTempUpFiles() {
			return tempUpFiles;
		}

		public ParamsMap getParamsMap() {
			return paramsMap;
		}
	}

	public class TempUpFile {
		private String tmpUpPath;

		private String orginalName;

		private String fieldName;

		public String getTmpUpPath() {
			return tmpUpPath;
		}

		public void setTmpUpPath(String tmpUpPath) {
			this.tmpUpPath = tmpUpPath;
		}

		public String getOrginalName() {
			return orginalName;
		}

		public void setOrginalName(String orginalName) {
			this.orginalName = orginalName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

	}
}
