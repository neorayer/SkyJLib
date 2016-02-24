package com.skymiracle.client.tcpClient.smtpClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SmtpSendResult {

	public static int RESTYPE_NORES = -1;
	public static int RESTYPE_ALLSUCC = 0;
	public static int RESTYPE_PARTSUCC = 2;
	public static int RESTYPE_ALLFAILED = 4;

	private Map<String, ResultItem> entryMap = new HashMap<String, ResultItem>();

	public SmtpSendResult(String[] toEmails) {
		for (String toEmail : toEmails) {
			put(toEmail);
		}
	}

	public void put(String toEmail) {
		ResultItem item = new ResultItem(toEmail, ResultItem.TYPE_SUCC, "");
		entryMap.put(toEmail, item);
	}

	public void put(String toEmail, SmtpSendException e) {
		ResultItem entry = new ResultItem(toEmail, e.getType(), e.getMessage());
		entryMap.put(toEmail, entry);
	}

	public int getResultType() {
		int resCount = entryMap.size();
		if (resCount == 0)
			return RESTYPE_NORES;

		int succCount = 0;
		for (Map.Entry<String, ResultItem> entry : entryMap.entrySet()) {
			switch (entry.getValue().getType()) {
			case ResultItem.TYPE_SUCC:
				succCount++;
			}
		}
		if (succCount == 0)
			return RESTYPE_ALLFAILED;
		else if (succCount == resCount)
			return RESTYPE_ALLSUCC;
		else if (resCount > succCount)
			return RESTYPE_PARTSUCC;
		else {
			return RESTYPE_ALLSUCC;
		}
	}

	public void setAllRes(int type, String msg) {
		for (Map.Entry<String, ResultItem> entry : entryMap.entrySet()) {
			ResultItem item = entry.getValue();
			item.setType(type);
			if (msg != null)
				item.setMsg(msg);
		}
	}

	public void setAllRes(SmtpSendException e) {
		setAllRes(e.getType(), e.getMessage());
	}

	public List<ResultItem> getResultItems(int type) {
		List<ResultItem> items = new LinkedList<ResultItem>();
		for (Map.Entry<String, ResultItem> entry : entryMap.entrySet()) {
			ResultItem item = entry.getValue();
			if (item.getType() == type)
				items.add(item);
		}
		return items;
	}

	public class ResultItem {
		public static final int TYPE_SUCC = 0;
		public static final int TYPE_SOFT_ERR = 2;
		public static final int TYPE_HARD_ERR = 4;
		private int type = TYPE_SUCC;

		private String msg = "";

		private String toEmail;

		public ResultItem(String toEmail) {
			this.toEmail = toEmail;
		}

		public ResultItem(String toEmail, int type, String msg) {
			this.toEmail = toEmail;
			this.type = type;
			this.msg = msg;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getToEmail() {
			return toEmail;
		}

		public void setToEmail(String toEmail) {
			this.toEmail = toEmail;
		}
	}

}
