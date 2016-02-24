package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsDateTime extends Validate {

	public Vf_IsDateTime() {
		//TODO: yy-MM-dd hh:mm:ss
		super("时间格式为：yy-MM-dd hh:mm:ss");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^2\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-9]|3[0-1])\\s+\\d{2}:\\d{2}:\\d{2}$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

}
