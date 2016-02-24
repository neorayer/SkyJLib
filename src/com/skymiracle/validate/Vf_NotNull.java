package com.skymiracle.validate;

public class Vf_NotNull extends Validate{


	public Vf_NotNull() {
		super("此项为必填项");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		if(value == null)
			throwTip();
	}
	
}
