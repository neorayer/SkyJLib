package com.skymiracle.validate;

public class Vf_IsFixPhone extends Validate {

	public Vf_IsFixPhone() {
		super("电话/传真格式不对");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
//		String regex = "^+?(\\d){1,3}\\s*([-]?((\\d)|[ ]){1,12})+$";
//		if(!Pattern.matches(regex, value.toString()))
//			throwTip();
	}

}
