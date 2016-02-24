package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsEmail extends Validate {

	public Vf_IsEmail() {
		super("邮箱格式为：username@skymiracle.com");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String[] arr = value.toString().split("@");
		if(arr.length != 2)
			throwTip();
		new Vf_IsUid().validate(arr[0]);
		String regex = "^\\w+\\.(com|cn)$";
		if(!Pattern.matches(regex, arr[1]))
			throwTip();
	}

}
