package com.skymiracle.tcp;

import java.io.IOException;
import java.net.Socket;

import com.skymiracle.logger.Logger;

public class CmdTimeoutChecker extends Thread {
	private Socket socket;

	private int cmdTimeoutSeconds;

	private boolean checkEnabled = false;

	private boolean checking = false;

	public CmdTimeoutChecker(Socket socket, int cmdTimeoutSeconds) {
		this.socket = socket;
		this.cmdTimeoutSeconds = cmdTimeoutSeconds;
		setName("CmdTimeoutChecker Thread");
	}

	public void setSeconds(int cmdTimeoutSeconds) {
		this.cmdTimeoutSeconds = cmdTimeoutSeconds;
	}

	public int getSeconds() {
		return this.cmdTimeoutSeconds;
	}

	@Override
	public void run() {
		int millSeconds = this.cmdTimeoutSeconds * 1000;
		while (true) {
			long curTime = System.currentTimeMillis();
			long startTime = System.currentTimeMillis();
			while (this.checkEnabled) {
				this.checking = true;
				if (curTime - startTime > millSeconds) {
					try {
						this.socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Logger.info(new StringBuffer("Connection timeout."));
					return;
				}
				try {
					sleep(1);
				} catch (InterruptedException e) {
					return;
				}
				curTime = System.currentTimeMillis();
			}
			this.checking = false;
			try {
				sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public void startCheck() {
		this.checkEnabled = true;
		while (true)
			if (!this.checking)
				try {
					sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				break;
	}

	public void stopCheck() {
		this.checkEnabled = false;
		while (true)
			if (this.checking)
				try {
					sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				break;
	}

}
