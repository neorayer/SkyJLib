package com.skymiracle.client.tcpClient.pop3Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.skymiracle.auth.PopAuthImpl;
import com.skymiracle.client.tcpClient.TcpClientAbs;
import com.skymiracle.logger.Logger;
import com.skymiracle.util.UUID;
import com.skymiracle.util.UsernameWithDomain;

/**
 * A POP3Client implement. We can get emails with POP3 by it.
 * 
 */
public class Pop3Client extends TcpClientAbs {

	public Pop3ClientListener listener = null;

	public void setListener(Pop3ClientListener listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param isDelete
	 * @return mails path
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String[] getMail(String host, int port, String username,
			String password, String saveDirPath, boolean isDelete)
			throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;
		if (this.listener != null)
			this.listener.connect();
		connection();

		List<String> mailPathList = new ArrayList<String>();
		try {
			// read welcome
			String s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}

			// send cmd user
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("user ").append(username).append("\r\n");
				if (this.listener != null)
					this.listener.sendUser();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}

			// send cmd password
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("pass ").append(password).append("\r\n");
				if (this.listener != null)
					this.listener.sendPass();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}

			// send cmd list
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("list\r\n");
				if (this.listener != null)
					this.listener.sendList();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}
			// read list result, and save in idList
			if (!s.startsWith("+OK"))
				throw new Exception(s);
			List<String> idList = new LinkedList<String>();
			s = this.lnReader.readLine();
			Logger.detail(s);
			String[] ss = null;
			long allMailSize = 0;
			HashMap<String,Integer> mailSizeMap = new HashMap<String,Integer>();
			while (!s.equals(".")) {
				ss = s.split(" ");
				idList.add(ss[0]);
				int mailSize = Integer.parseInt(ss[1]);
				mailSizeMap.put(ss[0], new Integer(mailSize));
				allMailSize += mailSize;
				s = this.lnReader.readLine();
				Logger.detail(s);
			}
			if (this.listener != null) {
				this.listener.receivedMailCount(idList.size());
				this.listener.receivedAllMailSize(allMailSize);
			}

			// retr all mail
			for (String id: idList) {
				StringBuffer sb = new StringBuffer();
				sb.append("retr ").append(id).append("\r\n");
				int mailSize = ((Integer) mailSizeMap.get(id)).intValue();
				int stepSize = mailSize / 50;
				int size = 0;
				int curStepSize = 0;
				if (this.listener != null)
					this.listener.retrMail(id, mailSize);
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
				s = this.lnReader.readLine();
				Logger.detail(s);
				if (s == null) {
					throw new Exception("Socket read nothing.");
				}
				if (s.startsWith("+OK")) {
					UUID uuid = new UUID();
					String filePath = saveDirPath + "/" + uuid.toShortString();
					File file = new File(filePath);
					file.createNewFile();
					FileWriter fw = new FileWriter(file);
					s = this.lnReader.readLine();
					Logger.detail(s);
					while (!s.equals(".")) {
						if (this.listener != null) {
							size += s.length();
							if (curStepSize < stepSize)
								curStepSize += s.length();
							else {
								curStepSize = 0;
								this.listener.mailRetring(id, size);
							}
						}
						fw.write(s);
						fw.write("\r\n");
						s = this.lnReader.readLine();
						Logger.detail(s);
					}
					fw.close();
					mailPathList.add(filePath);
				}

				// send cmd delete
				if (isDelete) {
					sb = new StringBuffer().append("dele ").append(id).append(
							"\r\n");
					this.lnWriter.print(sb.toString());
					Logger.detail(sb);
					this.lnWriter.flush();
					this.lnReader.readLine();
				}
			}
			this.lnWriter.print("quit\r\n");
			this.lnWriter.flush();
		} catch (Exception e) {
			Logger.debug(e.toString());
			disConnection();
		}
		if (this.listener != null)
			this.listener.quit();
		return mailPathList.toArray(new String[0]);
	}
	
	public boolean getMailB(String host, int port, String username,
			String password, String saveDirPath, boolean isDelete)
			throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;
		if (this.listener != null)
			this.listener.connect();
		connection();

		List<String> mailPathList = new ArrayList<String>();
		try {
			// read welcome
			String s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}

			// send cmd user
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("user ").append(username).append("\r\n");
				if (this.listener != null)
					this.listener.sendUser();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}

			// send cmd password
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("pass ").append(password).append("\r\n");
				if (this.listener != null)
					this.listener.sendPass();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}

			// send cmd list
			if (s.startsWith("+OK")) {
				StringBuffer sb = new StringBuffer();
				sb.append("list\r\n");
				if (this.listener != null)
					this.listener.sendList();
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
			} else {
				throw new Exception(s);
			}
			s = this.lnReader.readLine();
			Logger.detail(s);
			if (s == null) {
				throw new Exception("Socket read nothing.");
			}
			// read list result, and save in idList
			if (!s.startsWith("+OK"))
				throw new Exception(s);
			List<String> idList = new LinkedList<String>();
			s = this.lnReader.readLine();
			Logger.detail(s);
			String[] ss = null;
			long allMailSize = 0;
			HashMap<String,Integer> mailSizeMap = new HashMap<String,Integer>();
			while (!s.equals(".")) {
				ss = s.split(" ");
				idList.add(ss[0]);
				int mailSize = Integer.parseInt(ss[1]);
				mailSizeMap.put(ss[0], new Integer(mailSize));
				allMailSize += mailSize;
				s = this.lnReader.readLine();
				Logger.detail(s);
			}
			if (this.listener != null) {
				this.listener.receivedMailCount(idList.size());
				this.listener.receivedAllMailSize(allMailSize);
			}

			// retr all mail
			for (String id: idList) {
				StringBuffer sb = new StringBuffer();
				sb.append("retr ").append(id).append("\r\n");
				int mailSize = ((Integer) mailSizeMap.get(id)).intValue();
				int stepSize = mailSize / 50;
				int size = 0;
				int curStepSize = 0;
				if (this.listener != null)
					this.listener.retrMail(id, mailSize);
				this.lnWriter.print(sb.toString());
				Logger.detail(sb);
				this.lnWriter.flush();
				s = this.lnReader.readLine();
				Logger.detail(s);
				if (s == null) {
					throw new Exception("Socket read nothing.");
				}
				if (s.startsWith("+OK")) {
					UUID uuid = new UUID();
					String filePath = saveDirPath + "/" + uuid.toShortString();
					File file = new File(filePath);
					file.createNewFile();
					FileWriter fw = new FileWriter(file);
					s = this.lnReader.readLine();
					Logger.detail(s);
					while (!s.equals(".")) {
						if (this.listener != null) {
							size += s.length();
							if (curStepSize < stepSize)
								curStepSize += s.length();
							else {
								curStepSize = 0;
								this.listener.mailRetring(id, size);
							}
						}
						fw.write(s);
						fw.write("\r\n");
						s = this.lnReader.readLine();
						Logger.detail(s);
					}
					fw.close();
					mailPathList.add(filePath);
					System.out.println(sb.toString());
				}

				// send cmd delete
				if (isDelete) {
					sb = new StringBuffer().append("dele ").append(id).append(
							"\r\n");
					this.lnWriter.print(sb.toString());
					Logger.detail(sb);
					this.lnWriter.flush();
					this.lnReader.readLine();
				}
			}
			this.lnWriter.print("quit\r\n");
			this.lnWriter.flush();
		} catch (Exception e) {
			Logger.debug(e.toString());
			disConnection();
			return false;
		}
		if (this.listener != null)
			this.listener.quit();
//		return mailPathList.toArray(new String[0]);
		return true;
	}
	
	public static void main(String[] args) throws UnknownHostException,
			IOException {}


}
