package com.skymiracle.validate;

public class Vf_IsInt extends Validate{


	public Vf_IsInt() {
		super("此项必须为整数");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		if (value == null)
			return;
		if (!(value  instanceof Integer))
			throwType();

	}
	
}
