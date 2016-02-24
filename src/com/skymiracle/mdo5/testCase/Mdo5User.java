package com.skymiracle.mdo5.testCase;

import static com.skymiracle.mdo5.testCase.Singletons.*;

import com.skymiracle.mdo5.Mdo;
import com.skymiracle.validate.*;

public class Mdo5User extends Mdo<Mdo5User> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2456015969069340459L;

	@Title("用户名")
	@Valid(Vf_IsUid.class)
	@Desc("4-20个字符，由字母、数字、下划线“_”组成。")
	@Required
	public String uid;

	@Title("密码")
	@IsPwd
	@Valid(Vf_IsPassword.class)
	@Desc("6-16个字符，密码中不能有空格或中文字符")
	@Required
	public String password;
	
	@Title("确认密码")
	@IsPwd2(equal="password")
	@Valid(Vf_PassConf.class)
	@Desc("重新输入一次密码")
	@Required
	public String password2;

	@Title("域名")
	public String dc;

	@Title("密码问题")
	@Required
	public String question;

	@Title("密码回答")
	@Required
	public String answer;

	@Title("身份证")
	@Valid(Vf_IsIdcard.class)
	@Desc("15-18位的身份证号码，这将作为注册的验证凭据！")
	public String idcard;

	@Title("真实姓名")
	@Desc("中文字符,请填写正确的本人真实姓名")
	public String realname;

	@Title("手机号码")
	@Valid(Vf_IsMobilePhone.class)
	@Desc("请输入输入你的手机号码，点击发送，获得你的验证码")
	public String mobile;

	@EnumNames({"开通","暂停","删除"})
	public enum STATUS {
		open, 
		pause, 
		delete
	};

	@Title("状态")
	public STATUS status = STATUS.open;

	@Title("简历")
	public StringBuffer resume;
	
	public boolean isPop3;

	public Mdo5User() {
		super(Mdo5UserX);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public boolean getIsPop3() {
		return isPop3;
	}

	public void setIsPop3(boolean isPop3) {
		this.isPop3 = isPop3;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public StringBuffer getResume() {
		return resume;
	}

	public void setResume(StringBuffer resume) {
		this.resume = resume;
	}

	@Override
	public String[] keyNames() {
		return new String[] { "uid", "dc" };
	}

	@Override
	public String table() {
		return "tb_user";
	}

}
