package com.skymiracle.mdo4.testCase;

import com.skymiracle.mdo4.Dao;
import com.skymiracle.mdo4.DaoDefine;
import com.skymiracle.mdo4.NullKeyException;
import com.skymiracle.validate.ValidateException;
import com.skymiracle.validate.Vf_IsUid;

public class User extends Dao {

	long sizeLocate;

	String index;

	String zhourui;

	String storageLocation;

	int spacealert;

	int notifystatus;

	String[] forwardaddr;

	String[] rejectemail;

	int mnpp;

	String forward;

	String status;

	String UUID;

	String userPassword;

	String weathercity;

	int savenew;

	int autoreplyswitch;

	String ou;

	String notifynum;

	int isProxy;

	int ispop3;

	int issmtp;

	String[] rejectsubject;

	String style;

	String uid;

	String[] ink;

	int maxcc;

	String[] rejectdomain;

	long messagesize;

	long size;

	@DaoDefine(len=128)
	String description;

	String[] notifyfilter;

	private String dc;
	
	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	@Override
	public String selfDN() throws NullKeyException {
		if (this.uid == null)
			throw new NullKeyException("uid");
		return "uid=" + this.uid;
	}

	@Override
	public String fatherDN(String baseDN) {
		return baseDN;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "dc", "uid" };
	}

	@Override
	public String[] objectClasses() {
		return new String[] { "person", "inetOrgPerson", "dcObject", "country",
				"mailPerson", "systemConfig" };
	}

	@Override
	public String table() {
		return "tb_user";
	}

	public int getAutoreplyswitch() {
		return this.autoreplyswitch;
	}

	public void setAutoreplyswitch(int autoreplyswitch) {
		this.autoreplyswitch = autoreplyswitch;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getForward() {
		return this.forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String[] getForwardaddr() {
		return this.forwardaddr;
	}

	public void setForwardaddr(String[] forwardaddr) {
		this.forwardaddr = forwardaddr;
	}

	public String getIndex() {
		return this.index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String[] getInk() {
		return this.ink;
	}

	public void setInk(String[] ink) {
		this.ink = ink;
	}

	public int getIspop3() {
		return this.ispop3;
	}

	public void setIspop3(int ispop3) {
		this.ispop3 = ispop3;
	}

	public int getIsProxy() {
		return this.isProxy;
	}

	public void setIsProxy(int isProxy) {
		this.isProxy = isProxy;
	}

	public int getIssmtp() {
		return this.issmtp;
	}

	public void setIssmtp(int issmtp) {
		this.issmtp = issmtp;
	}

	public int getMaxcc() {
		return this.maxcc;
	}

	public void setMaxcc(int maxcc) {
		this.maxcc = maxcc;
	}

	public long getMessagesize() {
		return this.messagesize;
	}

	public void setMessagesize(long messagesize) {
		this.messagesize = messagesize;
	}

	public int getMnpp() {
		return this.mnpp;
	}

	public void setMnpp(int mnpp) {
		this.mnpp = mnpp;
	}

	public String[] getNotifyfilter() {
		return this.notifyfilter;
	}

	public void setNotifyfilter(String[] notifyfilter) {
		this.notifyfilter = notifyfilter;
	}

	public String getNotifynum() {
		return this.notifynum;
	}

	public void setNotifynum(String notifynum) {
		this.notifynum = notifynum;
	}

	public int getNotifystatus() {
		return this.notifystatus;
	}

	public void setNotifystatus(int notifystatus) {
		this.notifystatus = notifystatus;
	}

	public String getOu() {
		return this.ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public String[] getRejectdomain() {
		return this.rejectdomain;
	}

	public void setRejectdomain(String[] rejectdomain) {
		this.rejectdomain = rejectdomain;
	}

	public String[] getRejectemail() {
		return this.rejectemail;
	}

	public void setRejectemail(String[] rejectemail) {
		this.rejectemail = rejectemail;
	}

	public String[] getRejectsubject() {
		return this.rejectsubject;
	}

	public void setRejectsubject(String[] rejectsubject) {
		this.rejectsubject = rejectsubject;
	}

	public int getSavenew() {
		return this.savenew;
	}

	public void setSavenew(int savenew) {
		this.savenew = savenew;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getSizeLocate() {
		return this.sizeLocate;
	}

	public void setSizeLocate(long sizeLocate) {
		this.sizeLocate = sizeLocate;
	}

	public int getSpacealert() {
		return this.spacealert;
	}

	public void setSpacealert(int spacealert) {
		this.spacealert = spacealert;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStorageLocation() {
		return this.storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) throws ValidateException {
		new Vf_IsUid().validate(uid);
		this.uid = uid;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUUID() {
		return this.UUID;
	}

	public void setUUID(String uuid) {
		this.UUID = uuid;
	}

	public String getWeathercity() {
		return this.weathercity;
	}

	public void setWeathercity(String weathercity) {
		this.weathercity = weathercity;
	}

	public String getZhourui() {
		return this.zhourui;
	}

	public void setZhourui(String zhourui) {
		this.zhourui = zhourui;
	}

}
