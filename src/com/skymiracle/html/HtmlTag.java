package com.skymiracle.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HtmlTag<T >{

	private String tag;
	
	private Map<String, String> attrsMap = new HashMap<String, String>();

	private List<HtmlTag<?>> subTags = new LinkedList<HtmlTag<?>>();
	
	private String content = null;
	
	private boolean hasInner = false;
	
	private HtmlTag<?> parent = null;
	
	public HtmlTag(String tag, boolean hasInner) {
		this.tag = tag;
		this.hasInner = hasInner;
	}
	
	public T setId(String id) {
		put("id", id);
		return (T) this;
	}

	public T setCss(String cssClass) {
		put("class", cssClass);
		return (T) this;
	}

	public T put(String k, Object v) {
		if (v != null)
		attrsMap.put(k, String.valueOf(v));
		return (T) this;
	}
	
	public <C extends HtmlTag<?>> C add(C subTag) {
		subTags.add(subTag);
		subTag.parent = this;
		return subTag;
	}
	
	public  T setContent(String content) {
		this.content = content;
		return (T) this;
	}
	
	public String get(String k) {
		return attrsMap.get(k);
	}
	
	public void addBr() {
		add(new Br());
	}
	

	private Map<String, String> styleMap = new HashMap<String, String>();
	public T putStyle(String name, String value) {
		styleMap.put(name, value);
		return (T)this;
	}
	
	private int getDeep() {
		return parent == null ? 0 : parent.getDeep() + 1;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int deep = getDeep();
		StringBuffer deepSb = new StringBuffer();
		for(int i=0; i<deep; i++) {
			deepSb.append("\t");
		}
		
		sb.append("\r\n");
		sb.append(deepSb);
		String s = String.format("<" + tag );
		sb.append(s);
		for(Map.Entry<String, String> e:attrsMap.entrySet()) {
			sb.append(" " + e.getKey() + "='" + e.getValue() + "' ");
		}
		if (styleMap.size() > 0) {
			sb.append(" style='");
			for(Map.Entry<String, String> e:styleMap.entrySet()) {
				sb.append(e.getKey() + ":" + e.getValue() + ";");
			}
			sb.append("' ");
			
		}
		if (hasInner) {
			sb.append(">");
			if (content != null)
				sb.append(content);
			for(HtmlTag<?> subTag: subTags) {
				sb.append(subTag.toString());
			}
			sb.append("\r\n");
			sb.append(deepSb);
			sb.append("</" + tag + ">");
		}else
		sb.append(" />");
		return sb.toString();
	}

	
}
