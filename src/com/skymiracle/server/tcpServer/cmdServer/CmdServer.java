package com.skymiracle.server.tcpServer.cmdServer;

import java.util.HashMap;
import java.util.Map;

import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.tcpServer.TcpServer;

public class CmdServer extends TcpServer {

	private String welcome = "220 Welcome";

	private String unknown = "500 Syntax error, command unrecognized";

	// 命令列表
	private Map<String, Class<? extends Commander>> commanderMap = new HashMap<String, Class<? extends Commander>>();

	// 退出命令列表
	private Map<String, String> quitCmdMap = new HashMap<String, String>();

	// 是否是短连接
	private boolean isShortConn = true;

	// 异常处理器
	private WhenExceptionDo whenExceptionDo = new WhenExceptionDoNothing();
	
	
	public CmdServer(String name, int port) throws Exception {
		super(name, port, CmdConnHandler.class);
	}
	
	public WhenExceptionDo getWhenExceptionDo() {
		return this.whenExceptionDo;
	}

	public void setWhenExceptionDo(WhenExceptionDo whenExceptionDo) {
		this.whenExceptionDo = whenExceptionDo;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}

	public String getWelcome() {
		return this.welcome;
	}

	public String getUnknown() {
		return this.unknown;
	}

	public void setUnknown(String unknown) {
		this.unknown = unknown;
	}

	public String getDefaultCharset() {
		return this.defaultCharset;
	}

	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	public boolean isShortConn() {
		return this.isShortConn;
	}

	public void setShortConn(boolean isShortConn) {
		this.isShortConn = isShortConn;
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 命令名
	 * @param commanderClass 命令类型
	 * @return
	 * @throws ErrorCommanderClassException
	 */
	public static String getCommanderName(Class<? extends Commander> commanderClass)
			throws ErrorCommanderClassException {
		String cmdClassName = commanderClass.getSimpleName();
		int i = cmdClassName.indexOf("Commander");
		if (i < 0)
			throw new ErrorCommanderClassException(commanderClass);
		return cmdClassName.substring(0, i).toUpperCase();
	}

	public void addCommander(Class<? extends Commander> commanderClass)
			throws InstantiationException, IllegalAccessException,
			ErrorCommanderClassException {
		String commanderName = getCommanderName(commanderClass);
		this.commanderMap.put(commanderName, commanderClass);
		info(this.name + ' '+ commanderName + " add => server(" + this.name + ")");
	}

	public byte[] doCmd(CmdConnHandler connHander, String head, String tail) throws Exception {
		Class<? extends Commander> commanderClass = this.commanderMap.get(head.toUpperCase());
		if (commanderClass == null)
			return null;

		Commander commander = commanderClass.getConstructor(
				new Class[] { CmdConnHandler.class }).newInstance(
				new Object[] { connHander });
		try {
			return commander.doCmd(head, tail);
		} catch (Exception e) {
			this.whenExceptionDo.dealWithException(e);
			error("", e);
			return ("550 " + e.getMessage() + "\r\n").getBytes("UTF-8");
		}
	}

	public void addQuitCmd(String cmd) {
		this.quitCmdMap.put(cmd.toUpperCase(), cmd);
	}

	public boolean isQuitCmd(String head) {
		return this.quitCmdMap.get(head.toUpperCase()) != null;
	}
	
}
