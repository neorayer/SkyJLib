package com.skymiracle.skyUpload;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.jspsmart.upload.SmartUpload;

public class ServletUpload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1767643259639327996L;
	private ServletConfig config;

	/**
	 * Init the servlet
	 */
	@Override
	final public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}

	@Override
	final public ServletConfig getServletConfig() {
		return this.config;
	}

	/**
	 * Handles GET requests
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<BODY BGCOLOR='white'>");
		out.println("<H1>jspSmartUpload : Servlet Sample</H1>");
		out.println("<HR><BR>");
		out.println("The method of the HTML form must be POST.");
		out.println("</BODY>");
		out.println("</HTML>");
	}

	/**
	 * Handles POST requests
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<BODY BGCOLOR='white'>");
		out.println("<H1>jspSmartUpload : Servlet Sample</H1>");
		out.println("<HR>");

		// Variables
		int count = 0;
		SkyUpload mySmartUpload = new SkyUpload();

		try {
			// Initialization
			mySmartUpload.initialize(this.config, request, response);

			// Upload
			mySmartUpload.upload();

			// Save the file with the original name
			// in a virtual path of the web server
			count = mySmartUpload.save(mySmartUpload.getRequest().getParameter(
					"PATH"));

			// Display the result
			out.println(count + " file uploaded.");

		} catch (Exception e) {
			out.println("Unable to upload the file.<br>");
			out.println("Error : " + e.toString());
		}

		out.println("</BODY>");
		out.println("</HTML>");
	}

	/**
	 * Destroy the servlet
	 */
	@Override
	public void destroy() {
	}

}
