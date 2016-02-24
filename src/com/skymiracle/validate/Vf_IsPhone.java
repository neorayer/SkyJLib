package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsPhone extends Validate {

	public Vf_IsPhone() {
		//021-56120000 or 0791-3893755 or 0755- 84756784 or 84756784
		super("电话格式为：021-56120000");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		String regex = "^0\\d{2}\\s*-?\\s*\\d{7,8}|0\\d{3}\\s*-\\s*\\d{7}|\\d{7,8}$";
		if(!Pattern.matches(regex, value.toString()))
			throwTip();
	}
	


}
