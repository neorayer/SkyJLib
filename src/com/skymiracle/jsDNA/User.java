package com.skymiracle.jsDNA;

import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.Mdo.Title;
import com.skymiracle.validate.Vf_IsUid;

@Title("用户")
public class User extends Mdo {


	@Title("用户名")
	@Valid(Vf_IsUid.class)
	String uid;

	private enum STATUS {open, pause, delete};
	
	private STATUS status = STATUS.open;
	
	long sizeLocate;

	String index;

	String storageLocation;

	int spacealert;

	int notifystatus;

	int mnpp;

	String forward;


	String UUID;

	String userPassword;

	String weathercity;

	int savenew;

	int autoreplyswitch;

	String ou;

	String notifynum;

	int isProxy;

	int ispop3;


		
	@Title("描述")
	String description;
	private String dc;
	

	@Override
	public String[] keyNames() {
		return new String[] { "dc", "uid" };
	}


	@Override
	public String table() {
		return "tb_user";
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public long getSizeLocate() {
		return sizeLocate;
	}


	public void setSizeLocate(long sizeLocate) {
		this.sizeLocate = sizeLocate;
	}


	public String getIndex() {
		return index;
	}


	public void setIndex(String index) {
		this.index = index;
	}


	public String getStorageLocation() {
		return storageLocation;
	}


	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}


	public int getSpacealert() {
		return spacealert;
	}


	public void setSpacealert(int spacealert) {
		this.spacealert = spacealert;
	}


	public int getNotifystatus() {
		return notifystatus;
	}


	public void setNotifystatus(int notifystatus) {
		this.notifystatus = notifystatus;
	}


	public int getMnpp() {
		return mnpp;
	}


	public void setMnpp(int mnpp) {
		this.mnpp = mnpp;
	}


	public String getForward() {
		return forward;
	}


	public void setForward(String forward) {
		this.forward = forward;
	}



	public STATUS getStatus() {
		return status;
	}


	public void setStatus(STATUS status) {
		this.status = status;
	}


	public String getUUID() {
		return UUID;
	}


	public void setUUID(String uuid) {
		UUID = uuid;
	}


	public String getUserPassword() {
		return userPassword;
	}


	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}


	public String getWeathercity() {
		return weathercity;
	}


	public void setWeathercity(String weathercity) {
		this.weathercity = weathercity;
	}


	public int getSavenew() {
		return savenew;
	}


	public void setSavenew(int savenew) {
		this.savenew = savenew;
	}


	public int getAutoreplyswitch() {
		return autoreplyswitch;
	}


	public void setAutoreplyswitch(int autoreplyswitch) {
		this.autoreplyswitch = autoreplyswitch;
	}


	public String getOu() {
		return ou;
	}


	public void setOu(String ou) {
		this.ou = ou;
	}


	public String getNotifynum() {
		return notifynum;
	}


	public void setNotifynum(String notifynum) {
		this.notifynum = notifynum;
	}


	public int getIsProxy() {
		return isProxy;
	}


	public void setIsProxy(int isProxy) {
		this.isProxy = isProxy;
	}


	public int getIspop3() {
		return ispop3;
	}


	public void setIspop3(int ispop3) {
		this.ispop3 = ispop3;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDc() {
		return dc;
	}


	public void setDc(String dc) {
		this.dc = dc;
	}



}
