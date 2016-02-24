package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsZipcode extends Validate {

	public Vf_IsZipcode() {
		//TODO 332200
		super("邮编格式为：332200");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^[1-9]\\d{5}$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}

}
