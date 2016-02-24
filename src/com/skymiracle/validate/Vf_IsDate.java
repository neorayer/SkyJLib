package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsDate extends Validate {

	public Vf_IsDate() {
		super("日期格式为：yyyy-mm-dd");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^2\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-9]|3[0-1])$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

}
