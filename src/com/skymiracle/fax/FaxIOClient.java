package com.skymiracle.fax;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.skymiracle.client.tcpClient.TcpClientAbs;
import com.skymiracle.io.StreamPipe;

public class FaxIOClient extends TcpClientAbs {

	public FaxIOClient(String host, int port) {
		super(host, port);
	}

	public void doOutFax(File file, String ufaxUserID, String[] targetNumbers)
			throws IOException {
		SourceFileNameInfo sfni = new SourceFileNameInfo();
		sfni.setFilename(file.getName());
		sfni.setTargetNumbers(targetNumbers);
		sfni.setUfaxUserID(ufaxUserID);
		String filename = sfni.toString();

		try {
			connection();
			String s = this.readln();
			s = "outfax " + filename;
			this.writeln(s + "\r\n");
			s = this.readln();
			StreamPipe.fileToOutput(file.getPath(), this.socket
					.getOutputStream(), true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			disConnection();
		}
	}

	public void doStor_UserDB(File file)
			throws IOException {
		try {
			connection();
			String s = this.readln();
			s = "stor_userdb";
			this.writeln(s + "\r\n");
			s = this.readln();
			StreamPipe.fileToOutput(file.getPath(), this.socket
					.getOutputStream(), true);
		} catch (IOException e) {
			throw e;
		} finally {
			disConnection();
		}
	}
	
	public void doStor_UserDB(String[] datas) throws IOException {
		try {
			connection();
			String s = this.readln();
			s = "stor_userdb";
			this.writeln(s + "\r\n");
			s = this.readln();
			StreamPipe.stringsToOutput(datas, this.socket
					.getOutputStream(), true);
		} catch (IOException e) {
			throw e;
		} finally {
			disConnection();
		}
	}
	
	public void doPing() throws IOException {
		try {
			connection();
			String s = this.readln();
			s = "ping";
			this.writeln(s + "\r\n");
			s = this.readln();
			System.out.println(s);
		} catch (IOException e) {
			throw e;
		} finally {
			disConnection();
		}
	}

	public static void main(String[] args) throws IOException {
		FaxIOClient ofc = new FaxIOClient("192.168.1.111", 8799);
		ofc.doOutFax(new File("D:/example.doc"), "test@nc.cn", new String[] {"057982139298"});
		
//		ofc.doPing();
	}
}
