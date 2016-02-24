package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsUnicomPhone extends Validate {
	public Vf_IsUnicomPhone() {
		super("联通手机格式为：13088888888");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "/^13[012]\\d{8}$/";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

	@Override
	public String toJS() {
		return toJS("^13[012]\\d{8}$");
	}
}
