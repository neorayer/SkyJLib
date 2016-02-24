package com.skymiracle.server.tcpServer.mailServer.queue;

import java.util.List;
import javax.naming.NamingException;

import com.skymiracle.client.tcpClient.smtpClient.SmtpSendResult;
import com.skymiracle.client.tcpClient.smtpClient.SmtpSendResult.ResultItem;
import com.skymiracle.logger.Logger;
import com.skymiracle.queue.Queue;

/**
 *	外网投递队列
 */
public class ForeignQueue extends MailQueue implements Queue {

	// 网关smtp服务器IP
	private String smtpRouteHost = "127.0.0.1";

	// 网关smtp服务器port
	private int smtpRoutePort = 26;

	// 是否需要网关过滤
	private boolean isUseSmtpRoute = false;

	public ForeignQueue() throws Exception {
		super(MailQueue.NAME_FOREIGN);
	}

	public void setSmtpRouteHost(String smtpRouteHosts) {
		this.smtpRouteHost = smtpRouteHosts;
	}

	public boolean isUseSmtpRoute() {
		return this.isUseSmtpRoute;
	}

	public void setIsUseSmtpRoute(boolean isUseSmtpRoute) {
		this.isUseSmtpRoute = isUseSmtpRoute;
	}

	public String getSmtpRouteHost() {
		return this.smtpRouteHost;
	}

	public int getSmtpRoutePort() {
		return this.smtpRoutePort;
	}

	public void setSmtpRoutePort(int smtpRoutePort) {
		this.smtpRoutePort = smtpRoutePort;
	}

	/**
	 * 
	 * @param mm
	 * @return 发送失败的<toEmail, reason>
	 * @throws SmtpSendHardException
	 * @throws SmtpSendSoftException
	 * @throws NamingException
	 */
	private SmtpSendResult doSmtpSend(MailMessage mm) {
		smtpClient.setFromEmail(mm.getFromUsername() + '@' + mm.getFromDomain());
		if (this.isUseSmtpRoute) {
			return smtpClient.send(this.smtpRouteHost, this.smtpRoutePort, mm
					.getToStr(), mm.getDataLineList());
		} else {
			//return smtpClient.send(mm.getToStr(), mm.getDataLineList());
			return smtpClient.send(mm.getToStr(), mm.getDataLineList(), true);
		}
	}

	@Override
	public void deliver(String uuid, Object message) {
		MailMessage mm = (MailMessage) message;
		debug("begin send message to : " + mm.getToStr());
		SmtpSendResult sendRes = doSmtpSend(mm);
		List<ResultItem> succItems = sendRes
				.getResultItems(ResultItem.TYPE_SUCC);
		if (succItems.size() > 0) {
			saveLog(mm, "DELIVER", "S", "SEND ok.");
		}

		List<ResultItem> softErrItems = sendRes
				.getResultItems(ResultItem.TYPE_SOFT_ERR);
		if (softErrItems.size() > 0) {
			String msg = softErrItems.get(0).getMsg();
			delay(mm, msg);
		}

		List<ResultItem> hardErrItems = sendRes
				.getResultItems(ResultItem.TYPE_HARD_ERR);
		if (hardErrItems.size() > 0) {
			String msg = hardErrItems.get(0).getMsg();
			bounce(mm, msg);
		}
	}

	private void delay(MailMessage mm, String msg) {
		try {
			saveLog(mm, "DELIVER", "F", msg);
			boolean isDelayOK = this.mailQueueManager.addNextForeignDelayQueue(mm);
			if (!isDelayOK)
				bounce(mm, msg);

		} catch (Exception e1) {
			bounce(mm, e1.getMessage());
		}

	}
	
	private void bounce(MailMessage mm, String msg) {
		try {
			saveLog(mm, "DELIVER", "F", msg);
			this.mailQueueManager.putInBounceQueue(mm, msg);

		} catch (Exception e1) {
			Logger.info("Bounce failed.", e1);
		}
	}

}
