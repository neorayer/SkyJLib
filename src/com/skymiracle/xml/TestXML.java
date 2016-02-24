package com.skymiracle.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class TestXML {
	
	
	public static String buildLoginXml(String user, String password) {
		StringBuffer login_xml = new StringBuffer();
		login_xml.append("<?xml version='1.0' encoding='GBK'?>");
		login_xml.append("<request>");
		login_xml.append("<function>login</function>");
		login_xml.append("<user>%s</user>");
		login_xml.append("<password>%s</password>");
		login_xml.append("</request>");
		
		return String.format(login_xml.toString(), user, password);
	}
	
//	public static URLConnection getURLConnection() throws IOException {
//		URL url = new URL("http://211.138.155.5:9733/function.jsp");
//		URLConnection conn = url.openConnection();
//		conn.setUseCaches(false);
//		conn.setDoOutput(true);
//		PrintWriter out = new PrintWriter(conn.getOutputStream());
//
//		out.print(buildLoginXml("xxzx", "c67990717b9fc09b6eaedc57849c39a6"));
//		out.flush();
//		out.close();
//		
//		return conn;
//	}
	
	public static void main(String[] args) throws DocumentException,
			IOException {
		
		////////////////////////////////////////////
		//登录移动平台, 获取验证码
		////////////////////////////////////////////
		String verifyCode = "";
		{
			URL url1 = new URL("http://172.10.128.230:8080/eiss/j_spring_security_check");
			URL url = new URL("http://172.10.128.230:8080/eiss/j_spring_security_check");
			URLConnection conn = url.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream());

			out.print(buildLoginXml("admin", "admin"));
			out.flush();
			out.close();

			StringBuffer result = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			in.close();
			System.out.println(result);

			SAXReader saxReader = new SAXReader();
			InputStream inputStream = new ByteArrayInputStream(result
					.toString().getBytes());
			Document doc = saxReader.read(inputStream);
			Node node = doc.selectSingleNode("response/verifyCode");
			verifyCode = node.getText();
			System.out.println(verifyCode);
		}
		/*
		// ////////////////////////////////
		System.out.println("// ////////////////////////////////");
		{
			String xml_sendRandPwd = "<?xml version='1.0' encoding='GBK'?><request>" +
					"<function>sendRandPwd</function>" +
					"<verifyCode>%s</verifyCode>" +
					"<msisdn>%s</msisdn>" +
					"</request>";
			xml_sendRandPwd = String.format(xml_sendRandPwd, verifyCode, "13860196944");
			URL mapServer = new URL("http://211.138.155.5:9733/function.jsp");
			URLConnection conn = mapServer.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream());

			out.print(xml_sendRandPwd);
			out.flush();
			out.close();
			
			StringBuffer result = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			in.close();
			System.out.println(result);
//			"584825"
//			"829944"
		}
		
		// ////////////////////////////////
		System.out.println("// ////////////////////////////////");
		String randPwd = "829944";
		{
			StringBuffer xml_queryMonthBill_buf = new StringBuffer();
			xml_queryMonthBill_buf.append("<?xml version='1.0' encoding='GBK'?>");
			xml_queryMonthBill_buf.append("<request>");
			xml_queryMonthBill_buf.append("<function>queryMonthBill</function>");
			xml_queryMonthBill_buf.append("<msisdn>%s</msisdn>");
			xml_queryMonthBill_buf.append("<month>%s</month>");
			xml_queryMonthBill_buf.append("<randPwd>%s</randPwd>");
			xml_queryMonthBill_buf.append("<verifyCode>%s</verifyCode>");
			xml_queryMonthBill_buf.append("</request>");
			String xml_queryMonthBill = String.format(xml_queryMonthBill_buf.toString(), "13860196944", "200811", randPwd, verifyCode);
			
			URL mapServer = new URL("http://211.138.155.5:9733/function.jsp");
			URLConnection conn = mapServer.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream());

			out.print(xml_queryMonthBill);
			out.flush();
			out.close();
			
			StringBuffer result = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			in.close();
			System.out.println(result);
		}
		*/
	}
}
