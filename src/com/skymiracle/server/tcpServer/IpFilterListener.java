package com.skymiracle.server.tcpServer;

import java.util.Map;

import com.skymiracle.server.listener.Listener;

public abstract class IpFilterListener implements Listener {

	private TcpServer tcpServer;

	public IpFilterListener() {
		super();
	}

	public TcpServer getTcpServer() {
		return tcpServer;
	}

	public void setTcpServer(TcpServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	public void dealWithEvent(Event event) {
		if (event instanceof ChangedEvent)
			dealWithEvent((ChangedEvent) event);
	}

	public void dealWithEvent(ChangedEvent changedEvent) {
		switch(changedEvent.type) {
		case ChangedEvent.CHANGE_TYPE_ALL:
			for (Map.Entry<String, String> entry : changedEvent.getAllowMap()
					.entrySet())
				this.tcpServer.addAllowIP(entry.getKey());
			for (Map.Entry<String, String> entry : changedEvent.getRejectMap()
					.entrySet())
				this.tcpServer.addRejectIP(entry.getKey(), entry.getValue());
			return;
		case ChangedEvent.CHANGE_TYPE_ALLOW:
			for (Map.Entry<String, String> entry : changedEvent.getAllowMap()
					.entrySet())
				this.tcpServer.addAllowIP(entry.getKey());
			break;
		case ChangedEvent.CHANGE_TYPE_REJECT:
			for (Map.Entry<String, String> entry : changedEvent.getRejectMap()
					.entrySet())
				this.tcpServer.addRejectIP(entry.getKey(), entry.getValue());
			break;
		default:
			return;
		}
	}

	public class ChangedEvent implements Event {
		public static final int CHANGE_TYPE_NONE = -1;

		public static final int CHANGE_TYPE_ALLOW = 0;

		public static final int CHANGE_TYPE_REJECT = 10;

		public static final int CHANGE_TYPE_ALL = 100;

		private Map<String, String> allowMap;

		private Map<String, String> rejectMap;

		private int type = CHANGE_TYPE_NONE;

		public Map<String, String> getAllowMap() {
			return allowMap;
		}

		public void setAllowMap(Map<String, String> allowMap) {
			this.allowMap = allowMap;
		}

		public Map<String, String> getRejectMap() {
			return rejectMap;
		}

		public void setRejectMap(Map<String, String> rejectMap) {
			this.rejectMap = rejectMap;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}


	}
}
