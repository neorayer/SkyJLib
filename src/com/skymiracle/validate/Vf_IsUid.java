package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsUid extends Validate{


	public Vf_IsUid() {
		super("由字母a～z(不区分大小写)、数字0～9、点、减号或下划线组成·只能以数字或字母开头和结尾，例如：beijing.2008·用户名长度为4～18个字符 ");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		//TODO 应该单独测长度
		String regex = "^[\\da-zA-Z][\\w\\.\\+\\-]{2,16}[\\da-zA-Z]$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}
	
	@Override
	public String toJS() {
		return toJS("^[\\da-zA-Z][\\w\\.\\+\\-]{2,16}[\\da-zA-Z]$");
	}

}
