package com.skymiracle.server.tcpServer.mailServer.wp8;

import java.io.IOException;

import com.skymiracle.auth.Authable;
import com.skymiracle.auth.MailUser;
import com.skymiracle.logger.Logger;
import com.skymiracle.server.tcpServer.cmdStorageServer.accessor.IMailAccessorFactory;
import com.skymiracle.server.tcpServer.mailServer.Smtp.SmtpServer;
import com.skymiracle.server.tcpServer.mailServer.queue.BounceToForeignQueue;
import com.skymiracle.server.tcpServer.mailServer.queue.MailQueueManager;
import com.skymiracle.server.tcpServer.mailServer.queue.NativeLocalQueue;
import com.skymiracle.server.tcpServer.mailServer.queue.NativeRemoteQueue;

public class Wp8SmtpServer extends SmtpServer{


	@Override
	public MailUser getMailUser(String username, String domain)
			throws Exception {
		Uindex uIndex = new Uindex(username, domain);
		try {
			// 再如uindex信息
			uIndex.load();
		} catch (IOException e) {
			Wp8MailUser user = new Wp8MailUser(null);
			user.setUid(username.toLowerCase());
			user.setDc(domain.toLowerCase());
			user.setStatus(MailUser.STATUS_OPEN);
			user.setSpaceAlert(0);
			user.setMessageSize(1024*1024*1024);
			user.setStorageLocation(Authable.LOCATION_FOREIGN);
			return user;
			
		}
		Wp8MailUser user = new Wp8MailUser(null);
		user.setUid(username.toLowerCase());
		user.setDc(domain.toLowerCase());
		user.setStatus(MailUser.STATUS_OPEN);
		user.setStorageLocation(uIndex.getHome());
		user.setMessageSize(1024*1024*1024);
		user.setSpaceAlert(0);
		return user;
	}

	public Wp8SmtpServer() throws Exception {
		
		super();
		
		
		AuthMailUindex authMail = new AuthMailUindex();
		this.setAuthMail(authMail);
//		
		IMailAccessorFactory smaFactory = new Wp8StorageMailAccessorFactory();
		this.setStorageMailAccessorFactory(smaFactory);
	}

	public static void main(String[] args) throws Exception {
		Wp8SmtpServer s = new Wp8SmtpServer();
		Logger.setLevel(Logger.LEVEL_DETAIL);
		
		
		final Wp8SmtpServer smtpServer =  new Wp8SmtpServer();
		
		
		MailQueueManager mailQueueManager = new MailQueueManager();
		NativeLocalQueue nativeLocalQueue = new NativeLocalQueue();
		mailQueueManager.setNativeLocalQueue(nativeLocalQueue);
		NativeRemoteQueue nativeRemoteQueue = new NativeRemoteQueue();
		mailQueueManager.setNativeRemoteQueue(nativeRemoteQueue);
		
		BounceToForeignQueue bounceToForeignQueue = new BounceToForeignQueue();
		mailQueueManager.setBounceToForeignQueue(bounceToForeignQueue);
		
		smtpServer.setMailQueueManager(mailQueueManager);
		smtpServer.setMaxMessageSize(1024*1024*1024);
		
		smtpServer.start();
	}

}
