package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsTime extends Validate {

	public Vf_IsTime() {
		super("时间格式为：hh:mm:ss");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^\\d{2}:\\d{2}:\\d{2}$";
		if (!Pattern.matches(regex, value.toString()))
			throwTip();
	}

}
