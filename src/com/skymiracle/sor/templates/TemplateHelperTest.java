package com.skymiracle.sor.templates;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateHelperTest {

	private Configuration cfg;

	public void init() throws Exception {

		// 初始化FreeMarker配置

		// 创建一个Configuration实例

		cfg = new Configuration();

		// 设置FreeMarker的模版文件位置
		
		cfg.setDirectoryForTemplateLoading(new File("D:/applications/workspace/SkyJLib4/src/com/skymiracle/sor/templates"));
	}

	public void process() throws Exception {

		Map<String, String> root = new HashMap<String, String>();

		root.put("name", "FreeMarker!");

		root.put("msg", "您已经完成了第一个FreeMarker的示例");

		Template t = cfg.getTemplate("test.ftl");

		t.process(root, new OutputStreamWriter(System.out));

	}

	public static void main(String[] args) throws Exception {

		TemplateHelperTest hf = new TemplateHelperTest();

		hf.init();

		hf.process();

	}
}
