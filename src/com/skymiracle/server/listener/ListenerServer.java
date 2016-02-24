package com.skymiracle.server.listener;

import java.util.LinkedList;
import java.util.List;

import com.skymiracle.logger.Logger;
import com.skymiracle.server.*;
import com.skymiracle.server.listener.Listener.Event;

public class ListenerServer extends ServerImpl implements Server {

	private List<Listener> listeners = new LinkedList<Listener>();

	private Thread listenThread;

	private boolean stopFlag = false;

	private boolean isRunning = false;

	private long checkInterval = 1000;

	public long getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public void start() {
		stopFlag = false;

		// init all listeners
		for (Listener listener : listeners) {
			listener.initStart();
		}
		
		listenThread = new Thread() {
			@Override
			public void run() {
				Logger.info("ListenerServer started.");
				isRunning = true;
				while (!stopFlag) {
					for (Listener listener : listeners) {
						Event event = listener.checkEvent();
						if (event != null)
							listener.dealWithEvent(event);
					}
					try {
						Thread.sleep(checkInterval);
					} catch (InterruptedException e) {
						Logger.error("", e);
					}
				}
				isRunning = false;
				Logger.info("ListenerServer stoped.");
			}
		};
		listenThread.start();
	}

	public void stop() {
		while (isRunning) {
			stopFlag = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Logger.error("", e);
			}
		}
	}

	public void run() {
		// TODO Auto-generated method stub
	}

	public List<Listener> getListeners() {
		return listeners;
	}

	public void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
	}

}
