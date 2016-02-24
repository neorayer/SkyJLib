package com.skymiracle.jsDNA;

import java.lang.reflect.InvocationTargetException;

import com.skymiracle.mdo5.Mdo;
import com.skymiracle.mdo5.MdoReflector;
import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.mdo5.testCase.Mdo5User;
import com.skymiracle.util.StringUtils;

public class DnaBuilder {

	Class<? extends Mdo> mdoClass;
	MdoField[] mfs;

	public DnaBuilder(Class<? extends Mdo<?>> mdoClass) {
		super();
		this.mdoClass = mdoClass;
		this.mfs = MdoReflector.getMdoFields(mdoClass);
	}

	public String getDataStruct() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		StringBuffer sb = new StringBuffer("var dataStruct ={\r\n");
		int i = 0;
		for (MdoField mf : mfs) {
			sb.append(String.format("\t%-16s: {title: %s", mf.name, "'"
					+ mf.title + "'"));
			if (mf.isPrimary)
				sb.append(", isKey: true");
			if (mf.validate != null)
				sb.append(", validate: " + mf.validate.toJS());
			if (++i == mfs.length)
				sb.append("}\r\n");
			else
				sb.append("},\r\n");
		}
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getTableProps() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		StringBuffer sb = new StringBuffer("var tableProps ={\r\n");
		sb.append("\tsubjects: [");
		for (MdoField mf : mfs) {
			sb.append("'" + mf.name + "',");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("],\r\n");
		sb.append("\tisCheckbox: true\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getDsProps() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		StringBuffer sb = new StringBuffer("var dsProps ={\r\n");
		String name = mdoClass.getSimpleName();
		name = StringUtils.toFirstLow(name);
		sb.append("\tlistURL: '../" + name + "/" + name + "s.json.do',\r\n");
		sb.append("\taddURL: '../" + name + "/" + name + ".add.json.do',\r\n");
		sb.append("\tmodURL: '../" + name + "/" + name + ".mod.json.do',\r\n");
		sb.append("\tdelURL: '../" + name + "/" + name + "s.del.json.do'\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getAddWinProps() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		StringBuffer sb = new StringBuffer("var addWinProps ={\r\n");
		sb.append("\ttitle: '新增" + MdoReflector.getTitle(mdoClass) + "',\r\n");
		sb.append("\twidth: 560,\r\n");
		sb.append("\tsubjects: [");
		for (MdoField mf : mfs) {
			sb.append("'" + mf.name + "'");
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("],\r\n");
		sb.append("\thiddens: [],\r\n");
		sb.append("\tisForgetDataStructKey: true\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getModWinProps() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		StringBuffer sb = new StringBuffer("var modWinProps ={\r\n");
		sb.append("\ttitle: '修改" + MdoReflector.getTitle(mdoClass) + "',\r\n");
		sb.append("\twidth: 560,\r\n");
		sb.append("\tsubjects: [");
		for (MdoField mf : mfs) {
			if (mf.isPrimary)
				continue;
			sb.append("'" + mf.name + "'");
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("],\r\n");
		sb.append("\thiddens: []\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getMain() throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		String ctr = "_G('toolsBlock')";

		StringBuffer sb = new StringBuffer("function main(){\r\n");
		sb.append("\tvar ds = new SkyDNA.DataSource(dataStruct,dsProps);\r\n");
		sb.append("\tvar btnsBar = SkyDNA.Element.createButtonsBar(" + ctr
				+ ");\r\n");
		sb.append("\twith(btnsBar) {\r\n");
		sb.append("\t\tds.bindAdd(addButton('添加'), addWinProps);\r\n");
		sb.append("\t\tds.bindMod(addButton('修改'), modWinProps);\r\n");
		sb.append("\t\tds.bindRefresh(addButton('刷新'));\r\n");
		sb.append("\t\tds.bindDel(addButton('删除'));\r\n");
		sb.append("\t}\r\n");
		sb
				.append("\tds.createPageBar(btnsBar.addCell(), 'pagenum', 'countperpage', {countPerPage: 10});\r\n");
		sb.append("\tds.createDataTable(document.body, tableProps);\r\n");
		sb.append("\tds.remoteListData();\r\n");
		sb.append("}\r\n");
		return sb.toString();
	}

	public String getJsValidates() {
		StringBuffer sb = new StringBuffer(
				"<script language='javascript' >\r\n");
		for (MdoField mf : mfs) {
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

	public void printAll() throws Exception {
		 System.out.println(getDataStruct());
		 System.out.println(getDsProps());
		 System.out.println(getTableProps());
		 System.out.println(getAddWinProps());
		 System.out.println(getModWinProps());
		 System.out.println(getMain());
		System.out.println(getJsValidates());
	}
	public static void main(String[] args) throws Exception {
		DnaBuilder b = new DnaBuilder(Mdo5User.class);
		b.printAll();
	}
}
