package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsMobilePhone extends Validate {

	public Vf_IsMobilePhone() {
		super("移动手机格式为：13688888888");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^0?(13[456789]\\d{8}|15[89]\\d{8})$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

	@Override
	public String toJS() {
		return toJS("^0?(13[456789]\\d{8}|15[89]\\d{8})$");
	}
}
