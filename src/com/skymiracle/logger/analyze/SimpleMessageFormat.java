package com.skymiracle.logger.analyze;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleMessageFormat implements MessageFormat {

	private String type = "";
	private String threadName = "";
	private String from = "";
	private String to = "";
	private long size = 0;
	private String result = "";
	private String message = "";
	private String datetime = "";
	private String messageId = "";

	public SimpleMessageFormat(String input) throws UnSupportFormatMessage {
		parse(input);
	}

	public SimpleMessageFormat(String type, String threadName, String from,
			String to, long size, String result, String message,
			String datetime, String messageId) {
		this.type = type;
		this.threadName = threadName;
		this.from = from;
		this.to = to;
		this.size = size;
		this.result = result;
		this.message = message;
		this.datetime = datetime;
		this.messageId = messageId;
	}

	public SimpleMessageFormat(String type, String threadName, String from,
			String to, long size, String result, String message,
			String messageId) {
		this.type = type;
		this.threadName = threadName;
		this.from = from;
		this.to = to;
		this.size = size;
		this.result = result;
		this.message = message;
		this.datetime = formatDateTime(new Date());
		this.messageId = messageId;
	}

	private static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private String formatDateTime(Date time) {
		return fmt.format(time);
	}

	public String format() {
		StringBuffer sb = new StringBuffer().append(this.datetime).append(" ")
				.append(this.type).append(": ").append(this.threadName).append(
						"[").append(this.messageId).append("]")
				.append(" from=").append(this.from).append(",to=").append(
						this.to).append(",").append(this.size).append(",")
				.append(this.result).append(",").append(this.message);
		return sb.toString();
	}

	public void parse(String input) throws UnSupportFormatMessage {
		try {
			this.datetime = input.substring(0, 19);
			String tmp = input.substring(20);
			int pos = tmp.indexOf(": ");
			this.type = tmp.substring(0, pos);
			tmp = tmp.substring(pos + 2);
			pos = tmp.indexOf(" ");
			int x = tmp.indexOf("[");
			this.threadName = tmp.substring(0, x);
			this.messageId = tmp.substring(x + 1, pos - 1);
			tmp = tmp.substring(pos + 1);
			pos = tmp.indexOf(",");
			this.from = tmp.substring(5, pos);
			tmp = tmp.substring(pos + 1);
			pos = tmp.indexOf(",");
			this.to = tmp.substring(3, pos);
			tmp = tmp.substring(pos + 1);
			pos = tmp.indexOf(",");
			this.size = Long.parseLong(tmp.substring(0, pos));
			tmp = tmp.substring(pos + 1);
			pos = tmp.indexOf(",");
			this.result = tmp.substring(0, 1);
			this.message = tmp.substring(2);
		} catch (Exception e) {
			throw new UnSupportFormatMessage("message form error");
		}

	}

	public static void main(String[] args) throws UnSupportFormatMessage {
		SimpleMessageFormat smf = new SimpleMessageFormat("DELIVER",
				"NativeRemoteQueue", "test@test.com", "test@test.com", 1173,
				"F", "Reject Setting Filter", "2006-07-04 18:22:16",
				"10c390f3dfa50990743b7b6535ef30436d4c780b7fb");
		System.out
				.println("2006-07-04 18:22:16 DELIVER: NativeRemoteQueue[10c390f3dfa50990743b7b6535ef30436d4c780b7fb] from=test@test.com,to=test@test.com,1173,F,Reject Setting Filter");
		System.out.println(smf.format());
		SimpleMessageFormat smf2 = new SimpleMessageFormat("DELIVER",
				"TIMEQUEUE", "gates@dislle@com",
				"dislle@test.com|xiaomei@tom.com", 1111, "S", "OK",
				"1111111111111");
		System.out.println(smf2.format());
	}
}
