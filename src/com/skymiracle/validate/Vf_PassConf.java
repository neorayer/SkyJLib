package com.skymiracle.validate;

public class Vf_PassConf extends Validate {

	private String passName = "password";
	public Vf_PassConf() {
		super("确认密码与密码必须相同！");
	}

	@Override
	public <T> void validate(T value) throws ValidateException {
		new Vf_NotNull().validate(value);
	}

	@Override
	public String toJS() {
		StringBuffer sb = new StringBuffer();
		sb.append("function(v) {");
		sb.append("var flg = $('" + passName + "').value == v; ");
		sb.append("if(!flg) return '" + this.tip + "';");
		sb.append("return null;");
		sb.append("}");
		return sb.toString();
	}

}
