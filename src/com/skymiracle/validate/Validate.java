package com.skymiracle.validate;

public abstract class Validate {

	protected String tip;

	public Validate(String tip) {
		this.tip = tip;
	}

	public abstract <T> void validate(T value) throws ValidateException;

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	protected void throwTip() throws ValidateException {
		throw new ValidateException(tip);
	}

	protected void throwTip(String newTip) throws ValidateException {
		throw new ValidateException(newTip);
	}

	protected void throwType() throws ValidateException {
		throw new ValidateException("数据类型错误");
	}

	public String toJS() {
		return "function() {return null}";
	}
	
	public String toJS(String regex) {
		StringBuffer sb = new StringBuffer();
		sb.append("function(v) {");
		sb.append("var ptn = /" + regex +"/;" );
		sb.append("var flg = ptn.test(v);" );
		sb.append("if(!flg) return '" + this.tip+ "';" );
		sb.append("return null;");
		sb.append("}");
		return sb.toString();
	}
	
	public static <T extends Validate> T instance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
