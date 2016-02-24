package com.skymiracle.sor.templates;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;

import com.skymiracle.io.StreamPipe;
import com.skymiracle.sor.SorFilter;
import com.skymiracle.sor.SorRequest;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateHelper {

	private static Configuration cfg = new Configuration();

	// 模板根目录文件夹名
	private static String Templates_Root_File_Name = "_templates";

	private static final String RouteNotFindException_File_Name = "RouteNotFindException.ftl";

	private static final String RenderFailException_File_Name = "RenderFailException.ftl";
	
	private static final String ControllerBuildException_File_Name = "ControllerBuildException.ftl";
	
	private static final String NoSuchControllerMethodException_File_Name = "NoSuchControllerMethodException.ftl";
	
	public static void newInstatnce()
			throws ServletException {

		// 模板文件夹根目录
		File templates = new File(SorRequest.Project_Real_Path, Templates_Root_File_Name);
		if (!templates.exists() || !templates.isDirectory()) {
			if (!templates.mkdirs()) {
				throw new ServletException("项目模板文件夹创建失败; Template DIR = "
						+ templates.getPath());
			}
		}

		Templates_Root_File_Name = templates.getPath();
		try {
			cfg.setDirectoryForTemplateLoading(templates);
		} catch (IOException e) {
			throw new ServletException("项目模板文件夹创建失败; Template DIR = "
					+ templates.getPath());
		}

		createTemplateFileIfNot(RouteNotFindException_File_Name);
		createTemplateFileIfNot(RenderFailException_File_Name);
		createTemplateFileIfNot(ControllerBuildException_File_Name);
	}

	public static Template getTemplateOfRouteNotFindException()
			throws ServletException {
		return getTemplate(RouteNotFindException_File_Name);
	}

	public static Template getTemplateOfRenderFailException()
			throws ServletException {
		return getTemplate(RenderFailException_File_Name);
	}
	
	public static Template getTemplateOfControllerBuildException() throws ServletException {
		return getTemplate(ControllerBuildException_File_Name);
	}
	
	public static Template getTemplateOfNoSuchControllerMethodException() throws ServletException {
		return getTemplate(NoSuchControllerMethodException_File_Name);
	}
	
	
	//--------------------------

	public synchronized static void createTemplateFileIfNot(String fileName)
			throws ServletException {
		File templateFile = new File(Templates_Root_File_Name, fileName);
		if (!templateFile.exists() || !templateFile.isFile()) {
			InputStream is = TemplateHelper.class.getResourceAsStream(fileName);
			try {
				StreamPipe.inputToFile(is, templateFile, true);
			} catch (IOException e) {
				throw new ServletException("模板文件创建失败;  Template File Path = "
						+ templateFile.getPath());
			}
		}
	}

	public static Template getTemplate(String fileName) throws ServletException {
		try {
			createTemplateFileIfNot(fileName);
			return cfg.getTemplate(fileName, SorFilter.encoding);
		} catch (IOException e) {
			throw new ServletException("模板文件获取失败;  Template File Path = "
					+ Templates_Root_File_Name + File.separator + fileName);
		}
	}

	public static boolean isStaticFile(String url) {
		File file = new File(SorRequest.Project_Real_Path, url);
		return file.exists() && file.isFile();
	}

}
