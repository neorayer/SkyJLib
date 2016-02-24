package com.skymiracle.client.tcpClient.smtpClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.skymiracle.io.PlainFile;

public class SmtpClientTest {
	public static void main(String[] args) throws IOException {
		SmtpClient client = new SmtpClient();
		client.setFromEmail("test@test.com");
		client.setPassword("111111");
		File mimeFile = new File("d://SOR_66c6a93b-52bf-4f80-9f3f-8a69e1f11b86");
		List<String> dataLines = PlainFile.readLines(mimeFile, "UTF-8");
		client.send("192.168.1.188", 25, "test", "111111", new String[]{"test@test.com"}, dataLines);
	}
}
