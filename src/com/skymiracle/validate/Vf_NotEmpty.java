package com.skymiracle.validate;

public class Vf_NotEmpty extends Validate{


	public Vf_NotEmpty() {
		super("此项为必填项");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		if (value.toString().trim().length() == 0)
			throwTip();
	}
	
}
