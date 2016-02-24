package com.skymiracle.mdo5;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.skymiracle.html.*;
import com.skymiracle.mdo5.Mdo.*;
import com.skymiracle.reflect.ReflectTools;
import com.skymiracle.validate.Validate;
import com.skymiracle.util.*;

public class MdoReflector {

	public class MdoField {

		public final String title;

		public final String name;

		public final Method getMethod;

		public final Method setMethod;

		public final Field field;

		public final Class<?> type;

		public final boolean isPrimary;

		public final Validate validate;

		public final String desc;

		public final boolean required;

		public final Class<? extends Mdo<?>> mdoClass;

		public Mdo.Auto.Type autoType = null;

		public int length = 0;

		private <T extends Mdo<?>> MdoField(Class<T> mdoClass,
				String fieldname, Method getMethod, Method setMethod,
				Field field, boolean isPrimary, Class<?> type, String title,
				int length, Validate validate, String desc, boolean required,
				Mdo.Auto.Type autoType) {
			this.mdoClass = mdoClass;
			this.name = fieldname;
			this.getMethod = getMethod;
			this.setMethod = setMethod;
			this.field = field;
			this.isPrimary = isPrimary;
			this.type = type;
			this.title = title;
			this.length = length;
			this.validate = validate;
			this.desc = desc == null ? "" : desc;
			this.required = required;
			this.autoType = autoType;
		}

		private HtmlTag<?> _getHtmlTag() {
			if (field.isAnnotationPresent(IsPwd.class)) {
				Password e = new Password(name);
				e.put("id", name);
				return e;
			}
			if (field.isAnnotationPresent(IsPwd2.class)) {
				Password e = new Password(name);
				e.put("id", name);
				return e;
			}
			if (type == boolean.class) {
				RadioGroup e = new RadioGroup(name);
				e.putRadio("是", true);
				e.putRadio("否", false);
				e.put("id", name);
				return e;
			}
			if (type.isEnum()) {
				Select e = new Select(name);
				EnumNames EnumNames = type.getAnnotation(EnumNames.class);
				String[] names = EnumNames != null ? EnumNames.value()
						: new String[0];
				int i = 0;
				for (Object value : type.getEnumConstants()) {
					String title = i < names.length ? names[i++] : value
							.toString();
					e.putOption(value, title);
				}
				e.put("id", name);
				return e;
			}
			Text e = new Text(name);
			e.put("id", name);
			return e;
		}

		public HtmlTag<?> getHtmlTag() {
			HtmlTag<?> e = _getHtmlTag();
			if (required)
				e.put("required", required);
			return e;
		}

		public Tr getTrTag() {
			Tr tr = new Tr();

			Th th = tr.addTh().setId(name + "TitleTD").setCss("TitleTD");
			Span reqiuredSpan = new Span().setCss("Required");
			if (required) {
				reqiuredSpan.setContent("*");
				reqiuredSpan.putStyle("color", "red");
			}
			th.add(reqiuredSpan);
			th.add(new Span(title + ":"));

			tr.addTd().setId(name + "InputTD").add(getHtmlTag());

			Td descTd = tr.addTd().setId(name + "DescTD");
			if (this.desc.length() > 0)
				descTd.add(new Div(this.desc).setId(name + "DescDiv").setCss(
						"DescDiv"));
			descTd.add(new Div().setId(name + "ValidDiv").setCss("ValidDiv"));

			return tr;
		}

		public void setValue(Mdo<?> mdo, Object v) {
			try {
				this.setMethod.invoke(mdo, new Object[] { v });
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public String toHTML() {
			return getHtmlTag().toString();
		}
	}

	/**
	 * mappings 是个静态Map，它将cache住所有已经反射过的类的方法，这样下次调用的时候将不需要再次反射了。
	 */
	private static Map<Class<?>, MdoReflector> mappings = new HashMap<Class<?>, MdoReflector>();

	public static <T extends Mdo<?>> MdoReflector get(Class<T> source) {
		MdoReflector reflector = mappings.get(source);
		if (reflector == null)
			mappings.put(source, reflector = new MdoReflector(source));
		return reflector;
	}

	public static MdoReflector get(Mdo<?> mdo) {
		return get(mdo.getClass());
	}

	public static <T extends Mdo<?>> Field getField(Class<T> type,
			String fieldname) {
		return get(type).fieldsMap.get(fieldname).field;
	}

	public static <T extends Mdo<?>> Object getFieldValue(T mdo,
			String fieldname) {
		try {
			return getGetterMethod(mdo.getClass(), fieldname).invoke(mdo,
					new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** ************************************************************** */
	/** ************************************************************** */
	/** ************************************************************** */
	/** ************************************************************** */

	// public static <T extends Mdo<?>> MdoField[] getMdoFields(Class<T> source)
	// {
	// return getInstance(source).getMdoFields();
	// }
	public static <T extends Mdo<?>> Method getGetterMethod(Class<T> type,
			String fieldname) {
		MdoField f = get(type).fieldsMap.get(fieldname);
		if (f == null)
			throw new RuntimeException("Can not find field [" + fieldname + ']');
		return f.getMethod;
	}

	public static MdoField getMdoField(Class<? extends Mdo<?>> mdoClass,
			String name) {
		return get(mdoClass).fieldsMap.get(name);
	}

	public static MdoField[] getMdoFields(Class<? extends Mdo<?>> mdoClass) {
		return get(mdoClass).getMdoFields();
	}

	public static MdoField[] getMdoKeyFields(Class<? extends Mdo<?>> mdoClass) {
		return get(mdoClass).getMdoKeyFields();
	}

	// private boolean isDaoPrimaryField(Mdo mdo, String fieldname) {
	// String[] keyNames = mdo.keyNames();
	// if (keyNames == null)
	// return false;
	// for (String keyName : keyNames) {
	// if (keyName.equalsIgnoreCase(fieldname)) {
	// return true;
	// }
	// }
	// return false;
	// }

	public static <T extends Mdo<?>> Method getSetterMethod(Class<T> type,
			String fieldname) {
		return get(type).fieldsMap.get(fieldname).setMethod;
	}

	public static <T extends Mdo<?>> String getTitle(Class<T> type) {
		return get(type).title;
	}

	public static <T extends Mdo<?>> Class<?> getType(Class<T> type,
			String fieldname) {
		return get(type).fieldsMap.get(fieldname).type;
	}

	public static void setFieldValue(Mdo<? extends Mdo<?>> mdo,
			String fieldname, Object value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		// TODO: null dealwith
		if (value == null)
			return;
		getSetterMethod(mdo.getClass(), fieldname).invoke(mdo,
				new Object[] { value });
	}

	public static void setFieldValue(Mdo<? extends Mdo<?>> mdo,
			String fieldname, String value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		MdoField mf = MdoReflector.get(mdo).fieldsMap.get(fieldname);
		setFieldValue(mdo, fieldname, sToO(value, mf.type));
	}

	/**
	 * Convert String to Object with type
	 * 
	 * @param s
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object sToO(String s, Class<?> type) {
		if (s == null)
			return s;
		if (type == String[].class)
			return s.split("::::");
		else if (type == StringBuffer.class)
			return new StringBuffer(s);
		else if (type == long.class)
			return Long.parseLong(s);
		else if (type == int.class)
			return Integer.parseInt(s);
		else if (type == short.class)
			return Short.parseShort(s);
		else if (type == byte.class)
			return Byte.parseByte(s);
		else if (type == double.class)
			return Double.parseDouble(s);
		else if (type == float.class)
			return Float.parseFloat(s);
		else if (type == boolean.class)
			return Boolean.parseBoolean(s);
		else if (type.isEnum())
			return Enum.valueOf((Class<Enum>) type, s);
		else
			return s;
	}

	public final String title;

	public final Class<? extends Mdo<?>> source;

	private static class FieldsMap extends LinkedHashMap<String, MdoField> {

		private static final long serialVersionUID = 35126975793329868L;

		@Override
		public MdoField get(Object key) {
			return super.get(key.toString().toLowerCase());
		}

		@Override
		public MdoField put(String key, MdoField mdoField) {
			return super.put(key.toLowerCase(), mdoField);
		}
	}

	private final FieldsMap fieldsMap = new FieldsMap();

	private final FieldsMap keyFieldsMap = new FieldsMap();

	private <T extends Mdo<?>> MdoReflector(Class<T> source) {
		this.fieldsMap.clear();
		this.keyFieldsMap.clear();

		this.source = source;
		Title cTitle = source.getAnnotation(Title.class);
		this.title = cTitle == null ? source.getSimpleName() : cTitle.value();
		List<Field> fields = ReflectTools.getAllFields(source, Mdo.class);
		Method getter = null;
		Method setter = null;
		for (Field field : fields) {
			try {
				String firstCapName = StringUtils.toFirstCap(field.getName());
				setter = source.getMethod("set" + firstCapName,
						new Class[] { field.getType() });
				getter = source.getMethod("get" + firstCapName, new Class[0]);
				if (setter == null || getter == null)
					continue;
			} catch (NoSuchMethodException e) {
				continue;
			}

			String fieldname = field.getName().toLowerCase();

			String fTitle = fieldname;
			int length = 0;
			Validate validate = null;
			String desc = null;
			boolean required = false;
			boolean isKey = StringUtils.isIncluded(Mdo.instance(source)
					.keyNames(), fieldname);
			Mdo.Auto.Type autoType = null;
			Mdo.Auto auto = field.getAnnotation(Auto.class);
			if (auto != null)
				autoType = auto.value();

			{
				Title ft = field.getAnnotation(Title.class);
				if (ft != null)
					fTitle = ft.value();
			}

			{
				Length fl = field.getAnnotation(Length.class);
				if (fl != null)
					length = fl.value();
			}

			Valid valid = field.getAnnotation(Valid.class);
			if (valid != null)
				validate = Validate.instance(valid.value());

			Desc AnDesc = field.getAnnotation(Desc.class);
			if (AnDesc != null)
				desc = AnDesc.value();

			required = field.isAnnotationPresent(Required.class);

			MdoField mdoField = new MdoField(source, fieldname.toLowerCase(),
					getter, setter, field, isKey, field.getType(), fTitle, length,
					validate, desc, required, autoType);
			this.fieldsMap.put(mdoField.name, mdoField);
			if (isKey)
				this.keyFieldsMap.put(mdoField.name, mdoField);
		}
	}

	public Field getField(String fieldname) {
		return fieldsMap.get(fieldname).field;
	}

	public HtmlTag<?> getFormTag() {
		Form form = new Form();
		form.setId(source.getSimpleName() + "Form");

		Table table = form.add(new Table());

		for (MdoField mf : getMdoFields()) {
			table.add(mf.getTrTag());
		}

		Td td = table.addTr().addTd().setCss("SubmitTD").put("colspan", 3);
		td.add(new Submit("submit").put("value", "提交"));

		return form;
	}

	public Method getGetterMethod(String fieldname) {
		return fieldsMap.get(fieldname).getMethod;
	}

	public String getJsValidates() {
		StringBuffer sb = new StringBuffer(
				"<script language='javascript' >\r\n");
		for (MdoField mf : getMdoFields()) {
			sb.append("\tvar Vf_" + mf.name + " = ");
			if (mf.validate == null)
				sb.append("function() {return null};\r\n");
			else {
				sb.append(mf.validate.toJS() + ";\r\n");
			}
		}
		sb.append("</script>\r\n");
		return sb.toString();
	}

	public MdoField getMdoField(String fieldname) {
		return fieldsMap.get(fieldname);
	}

	public MdoField[] getMdoFields() {
		return this.fieldsMap.values().toArray(new MdoField[0]);
	}

	private MdoField[] getMdoKeyFields() {
		return this.keyFieldsMap.values().toArray(new MdoField[0]);
	}

	public Method getSetterMethod(String fieldname) {
		return fieldsMap.get(fieldname).setMethod;
	}

	public Class<?> getType(String fieldname) {
		return fieldsMap.get(fieldname).type;
	}

}
