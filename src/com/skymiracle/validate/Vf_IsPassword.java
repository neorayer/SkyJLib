package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsPassword extends Validate {

	public Vf_IsPassword() {
		super("密码长度6～16位，字母区分大小写");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^\\w{6,16}$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

}
