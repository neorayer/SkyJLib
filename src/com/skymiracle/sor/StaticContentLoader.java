package com.skymiracle.sor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skymiracle.logger.Logger;
import com.skymiracle.sor.templates.TemplateHelper;

public class StaticContentLoader {

	/**
	 * Provide a formatted date for setting heading information when caching
	 * static content.
	 */
	protected static final Calendar lastModifiedCal = Calendar.getInstance();

	/**
	 * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
	 */

	protected static boolean serveStaticBrowserCache = true;

	public static void outputStaticResource(String path,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String name = cleanupPath(path);
		File file = new File(SorRequest.Project_Real_Path, buildPath(name));
		if (file.exists()) {
			InputStream is = new FileInputStream(file);
			if (is != null) {
				process(is, path, request, response);
				return;
			}
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	protected static void process(InputStream is, String path,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (is != null) {
			Calendar cal = Calendar.getInstance();

			// check for if-modified-since, prior to any other headers
			long ifModifiedSince = 0;
			try {
				ifModifiedSince = request.getDateHeader("If-Modified-Since");
			} catch (Exception e) {
				Logger.warn("Invalid If-Modified-Since header value: '"
						+ request.getHeader("If-Modified-Since")
						+ "', ignoring");
			}
			long lastModifiedMillis = lastModifiedCal.getTimeInMillis();
			long now = cal.getTimeInMillis();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			long expires = cal.getTimeInMillis();

			if (ifModifiedSince > 0 && ifModifiedSince <= lastModifiedMillis) {
				// not modified, content is not sent - only basic
				// headers and status SC_NOT_MODIFIED
				response.setDateHeader("Expires", expires);
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				is.close();
				return;
			}

			// set the content-type header
			String contentType = getContentType(path);
			if (contentType != null) {
				response.setContentType(contentType);
			}

			if (serveStaticBrowserCache) {
				// set heading information for caching static content
				response.setDateHeader("Date", now);
				response.setDateHeader("Expires", expires);
				response.setDateHeader("Retry-After", expires);
				response.setHeader("Cache-Control", "public");
				response.setDateHeader("Last-Modified", lastModifiedMillis);
			} else {
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "-1");
			}

			try {
				copy(is, response.getOutputStream());
			} finally {
				is.close();
			}
			return;
		}
	}

	/**
	 * Look for a static resource in the classpath.
	 * 
	 * @param path
	 *            The resource path
	 * @return The inputstream of the resource
	 * @throws IOException
	 *             If there is a problem locating the resource
	 */
	protected URL findResource(String path) throws IOException {
		return ClassLoaderUtil.getResource(path, getClass());
	}

	/**
	 * @param name
	 *            resource name
	 * @param packagePrefix
	 *            The package prefix to use to locate the resource
	 * @return full path
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	protected static String buildPath(String name)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(name, SorFilter.encoding);
	}

	/**
	 * Determine the content type for the resource name.
	 * 
	 * @param name
	 *            The resource name
	 * @return The mime type
	 */
	protected static String getContentType(String name) {
		// NOT using the code provided activation.jar to avoid adding yet
		// another dependency
		// this is generally OK, since these are the main files we server up
		if (name.endsWith(".js")) {
			return "text/javascript";
		} else if (name.endsWith(".css")) {
			return "text/css";
		} else if (name.endsWith(".html")) {
			return "text/html";
		} else if (name.endsWith(".txt")) {
			return "text/plain";
		} else if (name.endsWith(".gif")) {
			return "image/gif";
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (name.endsWith(".png")) {
			return "image/png";
		} else {
			return null;
		}
	}

	/**
	 * Copy bytes from the input stream to the output stream.
	 * 
	 * @param input
	 *            The input stream
	 * @param output
	 *            The output stream
	 * @throws IOException
	 *             If anything goes wrong
	 */
	protected static void copy(InputStream input, OutputStream output)
			throws IOException {
		final byte[] buffer = new byte[4096];
		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		output.flush();
	}

	public static boolean canHandle(String resourcePath) {
		return resourcePath.startsWith("/static");
	}

	/**
	 * @param path
	 *            requested path
	 * @return path without leading "/struts" or "/static"
	 */
	public static String cleanupPath(String path) {
		// path will start with "/struts" or "/static", remove them
		if (path.length() == 0)
			return path;
		return path.substring(7);
	}

}