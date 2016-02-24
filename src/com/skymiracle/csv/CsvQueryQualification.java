package com.skymiracle.csv;

public class CsvQueryQualification {

	private static final String OP_S_EQL = "=";

	public static final int OP_I_EQL = 0;

	private static final String OP_S_GRT = ">";

	public static final int OP_I_GRT = 1;

	private static final String OP_S_LST = "<";

	public static final int OP_I_LST = -1;

	private static final String OP_S_INC = "include";

	public static final int OP_I_INC = 1000;

	private static final String RE_S_AND = "and";

	public static final int RE_I_AND = 1;

	private static final String RE_S_OR = "or";

	public static final int RE_I_OR = -1;

	private static final String LABEL_S_INT = "int";

	public static final int LABEL_I_INT = 0;

	private static final String LABEL_S_DOUBLE = "double";

	public static final int LABEL_I_DOUBLE = 10;

	private static final String LABEL_S_STRING = "string";

	public static final int LABEL_I_STRING = 20;

	private String label;

	private int labelType;

	private int ops;

	private int relation;

	private String value;

	public CsvQueryQualification(String label, String ops, String value) {
		this(label, ops, value, "and", "string");
	}

	public CsvQueryQualification(String label, String ops, String value,
			String relate) {
		this(label, ops, value, relate, "string");
	}

	public CsvQueryQualification(String label, String ops, String value,
			String relation, String labelType) {
		this.label = label;

		this.ops = getOpsFromStr(ops);
		this.value = value;
		this.relation = getRelationFromStr(relation);
		this.labelType = getLabelTypeFromStr(labelType);
	}

	private int getLabelTypeFromStr(String s) {
		s = s.toLowerCase();
		if (s.equals(CsvQueryQualification.LABEL_S_DOUBLE))
			return CsvQueryQualification.LABEL_I_DOUBLE;
		if (s.equals(CsvQueryQualification.LABEL_S_INT))
			return CsvQueryQualification.LABEL_I_INT;
		if (s.equals(CsvQueryQualification.LABEL_S_STRING))
			return CsvQueryQualification.LABEL_I_STRING;
		return CsvQueryQualification.LABEL_I_STRING;
	}

	private int getRelationFromStr(String s) {
		s = s.toLowerCase();
		if (s.equals(CsvQueryQualification.RE_S_AND))
			return CsvQueryQualification.RE_I_AND;
		if (s.equals(CsvQueryQualification.RE_S_OR))
			return CsvQueryQualification.RE_I_OR;
		return CsvQueryQualification.RE_I_AND;
	}

	private int getOpsFromStr(String s) {
		if (s.equals(CsvQueryQualification.OP_S_EQL))
			return CsvQueryQualification.OP_I_EQL;
		if (s.equals(CsvQueryQualification.OP_S_GRT))
			return CsvQueryQualification.OP_I_GRT;
		if (s.equals(CsvQueryQualification.OP_S_INC))
			return CsvQueryQualification.OP_I_INC;
		if (s.equals(CsvQueryQualification.OP_S_LST))
			return CsvQueryQualification.OP_I_LST;
		return CsvQueryQualification.OP_I_EQL;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLabelType() {
		return this.labelType;
	}

	public void setLabelType(int labelType) {
		this.labelType = labelType;
	}

	public int getOps() {
		return this.ops;
	}

	public void setOps(int ops) {
		this.ops = ops;
	}

	public int getRelation() {
		return this.relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}