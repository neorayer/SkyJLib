package com.skymiracle.server.udpServer;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

public class UdpCmdServer extends UdpServer {
	private String charset = "UTF-8";

	private Map<String, Class> commanderMap = new HashMap<String, Class>();

	public UdpCmdServer(String name, int port) {
		super(name, port, UdpCmdHandler.class);
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public static String getCommanderName(Class commanderClass)
			throws ErrorCommanderClassException {
		String cmdClassName = commanderClass.getSimpleName();
		int i = cmdClassName.indexOf("Commander");
		if (i < 0)
			throw new ErrorCommanderClassException(commanderClass);
		return cmdClassName.substring(0, i).toUpperCase();
	}

	public void addCommander(Class commanderClass)
			throws InstantiationException, IllegalAccessException,
			ErrorCommanderClassException {
		String commanderName = getCommanderName(commanderClass);
		this.commanderMap.put(commanderName, commanderClass);
		info(commanderName + " add => server(" + this.name + ")");
	}

	@SuppressWarnings("unchecked")
	public byte[] doCmd(UdpCmdHandler udpCmdHandler, String cmd, String[] args,
			DatagramPacket dPacket) throws Exception{
		Class<Commander> commanderClass = this.commanderMap.get(cmd
				.toUpperCase());
		if (commanderClass == null)
			return null;
		Commander commander = commanderClass.getConstructor(
				new Class[] { UdpCmdHandler.class }).newInstance(
				new Object[] { udpCmdHandler });
		// TODO Auto-generated method stub
		try {
			return commander.doCmd(args, dPacket);
		} catch (Exception e) {
			error("", e);
			return ("550 " + e.getMessage() + "\r\n").getBytes("UTF-8");
		}
	}

}
