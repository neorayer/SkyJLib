package com.skymiracle.server.tcpServer.mailServer.queue;

import com.skymiracle.queue.Queue;

/**
 * 本地远程投递队列
 *
 */
public class NativeRemoteQueue extends NativeLocalQueue implements Queue {

	public NativeRemoteQueue() throws Exception {
		super(MailQueue.NAME_NATIVE_REMOTE);
	}

}
