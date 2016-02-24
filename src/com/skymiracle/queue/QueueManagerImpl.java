package com.skymiracle.queue;

import com.skymiracle.server.ServerManagerImpl;

public class QueueManagerImpl extends ServerManagerImpl implements QueueManager {

	public QueueManagerImpl(int mgrSrvPort, String mgrSrvUsername,
			String mgrSrvPassword) throws Exception {
		super(mgrSrvPort, mgrSrvUsername, mgrSrvPassword);
	}
}
