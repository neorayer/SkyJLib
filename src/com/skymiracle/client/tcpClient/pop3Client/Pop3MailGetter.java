package com.skymiracle.client.tcpClient.pop3Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import com.skymiracle.client.tcpClient.AbsSocketClient;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.exception.AppException;

public class Pop3MailGetter extends AbsSocketClient{

	private String username;
	
	private String password;
	
	private boolean isDeleteAfterReceive = false;
	
	private String mailStoreDir = "/tmp";
	
	private HashMap<String, Boolean> notReceiveMailIDs = new HashMap<String, Boolean>();
	
	private HashMap<String, File> mailsMap = new HashMap<String, File>();
	
	public Pop3MailGetter(String host, int port, String username, String password, boolean isDeleteAfterReceive) {
		setHost(host);
		setPort(port);
		this.username = username;
		this.password = password;
		this.isDeleteAfterReceive = isDeleteAfterReceive;
		
		setTimeoutSeconds(60);
	}
	
	public String getMailStoreDir() {
		return mailStoreDir;
	}

	public void setMailStoreDir(String mailStoreDir) {
		this.mailStoreDir = mailStoreDir;
	}

	/**
	 * Getter。是否在收取邮件后，从服务器上删除
	 */
	public boolean isDeleteAfterReceive() {
		return isDeleteAfterReceive;
	}

	/**
	 * Setter。是否在收取邮件后，从服务器上删除
	 */
	public void setDeleteAfterReceive(boolean isDeleteAfterReceive) {
		this.isDeleteAfterReceive = isDeleteAfterReceive;
	}

	/**
	 * 增加一个在收信时排除的邮件ID。
	 */
	public void addNotReceiveMailID(String mailID) {
		notReceiveMailIDs.put(mailID, true);
	}
	
	public void run() throws AppException, Exception {
		mailsMap.clear();
		
		String cmd, rsp;
		
		rsp = openSocketAndReadln();
		checkOK(rsp);
		
		rsp = oneCmd("user " + username);
		checkOK(rsp);
		
		rsp = oneCmd("pass " + password);
		checkOK(rsp, "Pop3验证失败,用户名或密码错误。");
		
		rsp = oneCmd("uidl");
		checkOK(rsp);
		
		// <nid, uuid>
		HashMap<String,String> uidlMap = new HashMap<String,String>();
		for(;;) {
			rsp = readln();
			if (rsp.trim().equals(".")) 
				break;
	
			String[] ss = rsp.split(" ");
			String nid = ss[0]; // number id
			String uuid = ss[1]; // uuid
			uidlMap.put(nid, uuid);
		}
		
		//用于保存接收(retr)了的mail的nid.注意是nid
		LinkedList<String> receivedNids = new LinkedList<String>(); 
		
		for(Map.Entry<String, String> entry : uidlMap.entrySet()) {
			String nid = entry.getKey();
			String uuid = entry.getValue();

			//跳过需要排除（不接收）的uuid
			if (notReceiveMailIDs.containsKey(uuid))
				continue;
			
			rsp = oneCmd("retr " + nid);
			checkOK(rsp);
			
			File file = new File(mailStoreDir, UUID.randomUUID().toString());
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			try {
				for(;;) {
					rsp = readln();
					if (rsp.trim().equals(".")) 
						break;
					bos.write(rsp.getBytes("UTF-8"));
					bos.write("\r\n".getBytes());
				}
			}finally {
				bos.close();
				fos.close();
			}
			
			receivedNids.add(nid); 
			mailsMap.put(uuid, file);
		}
		
		if (isDeleteAfterReceive) {
			for(String nid:receivedNids) {
				oneCmd("dele " + nid);
			}
		}
		
		sendCmd("quit");
		
		closeSocket();
	}
	
	/**
	 * 检查响应是否是+OK
	 * @param rsp 需要检查的响应字符串
	 * @param msg 特定的异常信息，如果未null。则异常输出为缺省。否则为msg。
	 * @throws AppException 
	 */
	private void checkOK(String rsp, String msg) throws AppException {
		if (!rsp.startsWith("+OK")) {
			if (msg == null)
				msg = "与远程Pop3通讯出错。" + rsp;
			throw new AppException(msg);
		}
	}
	private void checkOK(String rsp) throws AppException {
		 checkOK(rsp, null);
	}
	
	public HashMap<String, File> getMails() {
		return mailsMap;
	}
	
	public static void main(String[] args) throws AppException, Exception {
		//Logger.setLevel(Logger.LEVEL_DETAIL);
		
		//第1步：创建 Pop3MailGetter对象。参数分别是host,port,username,password,isDeleteAfterReceive(是否接收后从服务器上删除)
		Pop3MailGetter mailGetter = new Pop3MailGetter("mail.skymiracle.com", 110, "neorayer@skymiracle.com", "111111", false);
		
		//第2步：增加被排除的邮件ID。
		// 	注意，这个ID不是WPX本地生成的UUID，而是上次从Pop3服务器接收邮件的时候，自己存下来的ID。
		mailGetter.addNotReceiveMailID("9a12d3dd-9242-463f-92fa-548c31a558a7");
		mailGetter.addNotReceiveMailID("3ff31ccf-bb83-4f56-8a47-36cba8b3061f");
		
		//第3步：执行! 从远程POP3服务器接收邮件
		System.out.println("正在从Pop3服务器接收邮件，请稍候...");
		mailGetter.run();
		System.out.println("邮件接收完毕！\r\n");
		
		//第4步：从对象中取回邮件的信息
		//	注意：返回的Map中，key是邮件在Pop3服务器中的ID。程序要自己保留，在下次避免重复收信的时候使用。value邮件保存到本地后的临时文件。
		HashMap<String, File> mailsMap = mailGetter.getMails();
		
		//最后：程序员自己使用收到的邮件吧。注意注意：使用完一定要删除产生的邮件临时文件。
		for(Map.Entry<String, File> entry: mailsMap.entrySet()) {
			String mailID = entry.getKey();
			File mailFile = entry.getValue();

			System.out.println("邮件ID: " + mailID);
			System.out.println("保存路径: " + mailFile.getAbsolutePath());
			System.out.println("");
			
			// 注意注意：使用完一定要删除产生的邮件临时文件。
			mailFile.delete(); 
		}
		
		
	}
	
	
}
