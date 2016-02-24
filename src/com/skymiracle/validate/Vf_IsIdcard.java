package com.skymiracle.validate;

import java.util.regex.Pattern;

public class Vf_IsIdcard extends Validate {
	String regex15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
	String regex18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X)$";

	public Vf_IsIdcard() {
		super("身份证格式为15位或者18位");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
		if(!Pattern.matches(regex15, value.toString()))
			if(!Pattern.matches(regex18, value.toString()))
				throwTip();
	}

	@Override
	public String toJS() {
		StringBuffer sb = new StringBuffer();
		sb.append("function(v) {");
		sb.append("var flg = " + this.toJS(regex15) + "(v);");
		sb.append("var flg2 = " + this.toJS(regex18) + "(v);");
		sb.append("if (null == flg ) return null;");
		sb.append("if (null == flg2 ) return null;");
		sb.append("return flg || flg2 };");
		return sb.toString();
	}

}
