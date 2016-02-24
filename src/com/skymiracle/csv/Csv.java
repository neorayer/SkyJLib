package com.skymiracle.csv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.skymiracle.io.TextFile;
import com.skymiracle.logger.Logger;

/**
 * Csv is a wapper for LabeledCSVParser. It is more eazy to use comparing with
 * LabeledCSVParser.
 * 
 * @author Zhourui
 * 
 */
@SuppressWarnings("deprecation")
public class Csv {
	protected ArrayList<String[]> lineList = null;

	private String filePath;

	private boolean changed = true;

	private String[] labels = null;

	private String charset;
	

	
	private Csv(String filePath, boolean autoLoad, String charset) throws IOException {
		this.filePath = filePath;
		this.charset = charset;
		if (autoLoad)
			load();
	}

	/**
	 * Create a new Csv Object from a file named <v>filePath</v> that exist.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public Csv(String filePath, String charset) throws IOException {
		this(filePath, false, charset);
	}

//	public Csv(String filePath) throws IOException {
//		this(filePath, false, "UTF-8");
//	}

	public String[] getLabels() {
		return this.labels;
	}

	/**
	 * Create a new Csv Object with file named <v>filePath</v>. NOTE: It will
	 * force creating the file on disk.
	 * 
	 * @param filePath
	 * @param labels
	 * @throws IOException
	 */
	public Csv(String filePath, String[] labels,String charset) throws IOException {
		this(filePath, false, charset);
		File file = new File(filePath);
		if (file.exists())
			file.delete();
		file.createNewFile();
		insert(labels);
	}

	/**
	 * Get all the lines of the Csv file. The type of elements of ArrayList is
	 * Stirng[]. e.g., "1001", "subject", "date", "size".
	 * 
	 * @return
	 */
	public ArrayList<String[]> getLineList() {
		if (this.changed)
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return this.lineList;
	}


	public void load() throws IOException {
		this.lineList = new ArrayList<String[]>();
		FileInputStream fis = new FileInputStream(this.filePath);
		LabeledCSVParser csvParser = new LabeledCSVParser(new ExcelCSVParser(
				fis, charset));
		this.labels = csvParser.getLabels();
		String[] lineValues = null;
		while ((lineValues = csvParser.getLine()) != null)
			this.lineList.add(lineValues);
		csvParser.close();
		Comparator<String[]> comp = new MyComparator("time", "string", false, this.labels);
		Collections.sort(this.lineList, comp);
		this.changed = false;
	}

	// public void save() throws IOException {
	// saveAs(this.filePath);
	// }
	//
	// public void saveAs(String filePath) throws IOException {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos);
	// for (int i = 0; i < this.lineList.size(); i++) {
	// String[] lineValues = (String[]) this.lineList.get(i);
	// csvPrinter.println(lineValues);
	// }
	// Logger.info("clean up over");
	// TextFile.save(filePath, baos.toByteArray());
	// }

	/**
	 * Insert a set of values into Csv file.
	 */
	public void insert(String[] values) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
		csvPrinter.println(values);
		FileOutputStream fos = new FileOutputStream(this.filePath, true);
		fos.write(baos.toByteArray());
		fos.close();
		csvPrinter.close();
		baos.close();
		this.changed = true;
	}

	public void insert(List<String[]> valuesList) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
		for (String[] values : valuesList)
			csvPrinter.println(values);
		FileOutputStream fos = new FileOutputStream(this.filePath, true);
		fos.write(baos.toByteArray());
		fos.close();
		csvPrinter.close();
		baos.close();
		this.changed = true;
	}

	/**
	 * To delete a line item by id that is first row of Csv file.
	 * 
	 * @param id
	 * @throws IOException
	 */
	public void delete(String id) throws IOException {
		delete(new String[] { id });
	}

	/**
	 * To delete a group of line items by a array of id.
	 * 
	 * @param ids
	 * @throws IOException
	 */
	public void delete(String[] ids) throws IOException {

		String[] keys = new String[ids.length];
		for (int i = 0; i < ids.length; i++)
			keys[i] = encode(ids[i]);

		ArrayList<String> lineList = TextFile.loadLinesList(this.filePath);
		int c = 0;
		for (int i = 0; i < lineList.size(); i++) {
			String line = lineList.get(i);
			for (int j = 0; j < keys.length; j++) {
				if (keys[j] == null)
					continue;
				if (line.indexOf(keys[j]) != -1) {
					lineList.remove(i);
					keys[j] = null;
					c++;
				}
			}
			if (c == keys.length)
				break;
		}
		TextFile.save(this.filePath, lineList);
		this.changed = true;
	}

	public void empty() throws IOException {
		File file = new File(this.filePath);
		file.delete();
		file.createNewFile();
		this.changed = true;
	}

	/**
	 * Return a string representation of the Csv.
	 * 
	 * @see Csv.toString(ArrayList lineList).
	 */
	@Override
	public String toString() {
		if (this.changed)
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return toString(this.lineList);
	}

	/**
	 * Returns a string representation of the lineList that elements is
	 * String[]. The method will return a set of beautiful strings just like
	 * what the console of mySQL output.
	 * 
	 * @param lineList
	 * @return
	 */
	public String toString(ArrayList<String[]> lineList) {
		int[] labelWidth = new int[1024];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.labels.length; i++)
			labelWidth[i] = this.labels[i].length();

		for (int i = 0; i < lineList.size(); i++) {
			String[] lineValues = lineList.get(i);
			for (int j = 0; j < lineValues.length; j++) {
				int width = lineValues[j].length();
				labelWidth[j] = width > labelWidth[j] ? width : labelWidth[j];
			}
		}

		// ----------------------------------------------
		sb.append("+");
		for (int i = 0; i < this.labels.length; i++) {
			sb.append("-");
			for (int j = 0; j < labelWidth[i]; j++)
				sb.append("-");
			sb.append("-+");
		}
		sb.append("\r\n");

		// |asdfasdf|asdfasdf |asdfasdf|2134 |
		sb.append("| ");
		for (int i = 0; i < this.labels.length; i++) {
			sb.append(this.labels[i]);
			for (int j = this.labels[i].length(); j < labelWidth[i]; j++)
				sb.append(" ");
			sb.append(" | ");
		}
		sb.append("\r\n");

		// ----------------------------------------------
		sb.append("+");
		for (int i = 0; i < this.labels.length; i++) {
			sb.append("-");
			for (int j = 0; j < labelWidth[i]; j++)
				sb.append("-");
			sb.append("-+");
		}
		sb.append("\r\n");

		// |asdfasdf|asdfasdf |asdfasdf|2134 |
		for (int i = 0; i < lineList.size(); i++) {
			String[] lineValues = lineList.get(i);
			sb.append("| ");
			for (int j = 0; j < lineValues.length; j++) {
				sb.append(lineValues[j]);
				int owidth = lineValues[j].length();
				for (int k = owidth; k < labelWidth[j]; k++)
					sb.append(" ");
				sb.append(" | ");
			}
			sb.append("\r\n");
		}

		// ----------------------------------------------
		sb.append("+");
		for (int i = 0; i < this.labels.length; i++) {
			sb.append("-");
			for (int j = 0; j < labelWidth[i]; j++)
				sb.append("-");
			sb.append("-+");
		}
		sb.append("\r\n");

		return sb.toString();
	}

	private String encode(String original) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
		csvPrinter.print(original);
		String s = baos.toString();
		csvPrinter.close();
		baos.close();
		return s;
	}

	/**
	 * Modify a row set a new value and add the file last line
	 * 
	 * @param id
	 *            The row keyword
	 * @param labelName
	 *            The label name being modified
	 * @param value
	 *            The new value
	 * @return The count of rows effected.
	 * @throws IOException
	 */
	public int modify(String id, String labelName, String value)
			throws IOException {
		int labelIdx = getLabelIndex(labelName);
		if (labelIdx < 0)
			return 0;
		String key = encode(id);

		int effectRowCount = 0;
		ArrayList<String> lineList = TextFile.loadLinesList(this.filePath);
		for (int i = 0; i < lineList.size(); i++) {
			String line =lineList.get(i);
			if (line.startsWith(key)) {
				LabeledCSVParser csvParser = new LabeledCSVParser(
						new ExcelCSVParser(new ByteArrayInputStream(line
								.getBytes()), charset));
				String[] values = csvParser.getLabels();

				values[labelIdx] = value;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
				csvPrinter.print(values);
				lineList.remove(i);
				lineList.add(new String(baos.toByteArray()));
				csvPrinter.close();
				baos.close();
				effectRowCount++;
			}
		}

		TextFile.save(this.filePath, lineList);
		this.changed = true;
		return effectRowCount;
	}

	private int getLabelIndex(String label) {
		if (this.changed)
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		for (int i = 0; i < this.labels.length; i++)
			if (this.labels[i].trim().equalsIgnoreCase(label))
				return i;
		return -1;
	}

	public ArrayList<String[]> select(CsvQueryQualification[] qualifications)
			throws IOException {
		if (this.changed)
			load();
		ArrayList<String[]> resList = new ArrayList<String[]>();
		int[] labelIndex = new int[qualifications.length];
		int andNum = 0;
		int orNum = 0;

		for (int j = 0; j < qualifications.length; j++) {
			for (int i = 0; i < this.labels.length; i++)
				if (qualifications[j].getLabel().equalsIgnoreCase(
						this.labels[i].trim()))
					labelIndex[j] = i;

			switch (qualifications[j].getRelation()) {
			case CsvQueryQualification.RE_I_AND:
				andNum++;
				break;
			case CsvQueryQualification.RE_I_OR:
				orNum++;
				break;
			default:
				break;
			}
		}

		for (int i = 0; i < this.lineList.size(); i++) {
			int a = 0, o = 0;
			String[] values = this.lineList.get(i);
			boolean found = true;

			ForLabel: for (int j = 0; j < qualifications.length; j++) {
				switch (qualifications[j].getRelation()) {
				case CsvQueryQualification.RE_I_AND:
					if (quaCompare(values[labelIndex[j]], qualifications[j]))
						a++;
					break;
				case CsvQueryQualification.RE_I_OR:
					if (quaCompare(values[labelIndex[j]], qualifications[j])) {
						o++;
						break ForLabel;
					}
					break;
				default:
					break;
				}
			}

			if (a == andNum)
				found = true;
			else if (o > 0)
				found = true;
			else
				found = false;
			if (found) {
				resList.add(values);
			}
		}
		return resList;
	}

	public String[] select(CsvQueryQualification[] qualifications,
			String resultLable) throws IOException {
		ArrayList<String[]> resList = new ArrayList<String[]>();
		resList = this.select(qualifications);
		int index = this.getLabelIndex(resultLable);
		if (index < 0)
			return new String[0];
		String[] res = new String[resList.size()];
		for (int i = 0; i < resList.size(); i++)
			res[i] = (resList.get(i))[index];
		return res;
	}

	/**
	 * Query a set of results from the Csv.
	 * 
	 * @param qualifications
	 *            A serials objects of CsvQueryQualification Class.
	 * @param orderLabel
	 *            The name of label order by.
	 * @param orderLabelType
	 *            Sepcial the type of <v>orderLabel<v>,
	 *            CsvQueryQualification.LABEL_I_xxx
	 * @param ascend
	 *            Ascend ? you don't know ascend ????!
	 * @param begin
	 *            The fromIndex of result.
	 * @param size
	 *            The result size.
	 * @return the result ArrayList<String[]> selected out.
	 * @throws IOException
	 * 
	 * @see CsvQueryQualification
	 */
	public ArrayList<String[]> select(CsvQueryQualification[] qualifications,
			String orderLabel, String orderLabelType, boolean ascend,
			int begin, int size) throws IOException {
		ArrayList<String[]> resList = new ArrayList<String[]>();
		resList = this.select(qualifications);

		Comparator<String[]> comp = new MyComparator(orderLabel, orderLabelType, ascend,
				this.labels);
		Collections.sort(resList, comp);

		if (begin == 0 && size == 0)
			return resList;
		if (begin >= resList.size())
			return new ArrayList<String[]>();
		int toIndex = begin + size;
		toIndex = toIndex > resList.size() ? resList.size() : toIndex;
		return new ArrayList<String[]>(resList.subList(begin, toIndex));

	}

	private boolean quaCompare(String value, CsvQueryQualification quali) {
		int op = quali.getOps();
		String arg1 = quali.getValue();
		switch (quali.getLabelType()) {
		case CsvQueryQualification.LABEL_I_STRING:
			return strCompare(op, value, arg1);
		case CsvQueryQualification.LABEL_I_INT:
			return intCompare(op, value, arg1);
		case CsvQueryQualification.LABEL_I_DOUBLE:
			return doubleCompare(op, value, arg1);
		default:
			return false;
		}
	}

	private boolean doubleCompare(int op, String arg0, String arg1) {
		double a0 = Double.parseDouble(arg0);
		double a1 = Double.parseDouble(arg1);
		switch (op) {
		case CsvQueryQualification.OP_I_EQL:
			return (a0 == a1);
		case CsvQueryQualification.OP_I_GRT:
			return (a0 > a1);
		case CsvQueryQualification.OP_I_LST:
			return (a0 < a1);
		default:
			return false;
		}
	}

	private boolean intCompare(int op, String arg0, String arg1) {

		int a0 = Integer.parseInt(arg0);
		int a1 = Integer.parseInt(arg1);
		switch (op) {
		case CsvQueryQualification.OP_I_EQL:
			return (a0 == a1);
		case CsvQueryQualification.OP_I_GRT:
			return (a0 > a1);
		case CsvQueryQualification.OP_I_LST:
			return (a0 < a1);
		default:
			return false;
		}
	}

	private boolean strCompare(int op, String arg0, String arg1) {
		switch (op) {
		case CsvQueryQualification.OP_I_EQL:
			return (arg0.equals(arg1));
		case CsvQueryQualification.OP_I_GRT:
			return (this.isMax(arg0, arg1) == 1);
		case CsvQueryQualification.OP_I_LST:
			return (this.isMax(arg0, arg1) == -1);
		case CsvQueryQualification.OP_I_INC:
			return (arg0.indexOf(arg1) != -1);
		default:
			return false;
		}
	}

	private int isMax(String arg0, String arg1) {
		int arg0Len = arg0.length();
		int arg1Len = arg1.length();
		int comIndex = arg0Len >= arg1Len ? arg1Len : arg0Len;

		for (int i = 0; i < comIndex; i++) {
			if (arg0.charAt(i) > arg1.charAt(i))
				return 1;
			else if (arg0.charAt(i) < arg1.charAt(i))
				return -1;
			else {
			}
		}
		if (arg0Len == arg1Len)
			return 0;
		else if (arg0Len > arg1Len)
			return 1;
		else
			return -1;
	}

	/**
	 * Return a string representation with XML format of the Csv
	 * 
	 * @return
	 */
	public String toXmlString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<CSV>");
		for (int i = 0; i < this.lineList.size(); i++) {
			String[] values = this.lineList.get(i);
			for (int j = 0; j < values.length; j++) {
				sb.append("<").append(this.labels[j]).append(">");
				sb.append(values[j]);
				sb.append("</").append(this.labels[j]).append(">");
			}
		}
		sb.append("</CSV>");
		return sb.toString();
	}

	/**
	 * Testcase
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Logger.info("Begin");

		String[] labels = { "id", "subject", "time", "size", "read", "bk1",
				"bk2" };

		Csv csv = new Csv("/tmp/fileinfo.conf", labels, "UTF-8");

		ArrayList<String[]> valuesList = new ArrayList<String[]>();
		for (int i = 0; i < 10000; i++) {
			String[] values = { "id", "subject", "time", "size", "read", "bk1",
					"bk2" };
			valuesList.add(values);
		}
		csv.insert(valuesList);

		// Csv csv = new Csv("/root/tmp/fileinfo.conf");

		// int r = csv.modify("1113215408.14283:2", "sender", "a sender");
		// System.out.println(r);
		// String[] newValues;
		// newValues = new String[] { "11132asdf14379.10891:2!S",
		// "Webmaster,Notifcation", "77\"954", "223334", "", "3", "" };
		// csv.insert(newValues);
		// newValues = new String[] { "111321423379.10891:2!S",
		// "Webmaster,Notifcation", "77\"954", "34333", "", "3", "" };
		// csv.insert(newValues);
		// newValues = new String[] { "111321491:2!S", "Webmaster,Notifcation",
		// "77\"954", "33343", "", "3", "" };
		// csv.insert(newValues);
		// Logger.info("insert over");
		//
		// csv.delete("AASDFASDF");
		// Logger.info("delete id over");
		//
		// csv.delete(new String[] { "ABCDEFG.7288!2!S:2!S", "ASDFASDF",
		// "FDASDF",
		// "!@#$#WQWE$#" });
		// Logger.info("delete ids over");

		// System.out.println(csv.toString());

		// String[] labels = { "id", "subject", "time", "size", "read", "tasdf",
		// "tasdf" };
		// Csv csv = new Csv("c:/1", labels);
		//
		// // Csv csv = new Csv("/root/tmp/1", true);
		//
		// Logger.info("scan over.");
		// // csv.saveAs("/root/tmp/2");
		// Logger.info("save over.");
		//
		// String[] newValues;
		// newValues = new String[] { "1", "Webmaster,Notifcation",
		// "2005-01-01",
		// "1567", "1", " ", " " };
		// csv.insert(newValues);
		// newValues = new String[] { "2", "hello", "2005-01-02", "1679", "2",
		// " ",
		// " " };
		// csv.insert(newValues);
		// newValues = new String[] { "3", "Re:modify",
		// "2005-01-02",
		// "1879", "1", " ", " " };
		// csv.insert(newValues);
		// newValues = new String[] { "4", "RE:find", "2005-01-03",
		// "1879", "1", " ", " " };
		// csv.insert(newValues);
		// Logger.info("insert over");
		//
		// csv.delete("AASDFASDF");
		// Logger.info("delete id over");
		//
		// csv.delete(new String[] { "ABCDEFG.7288!2!S:2!S", "ASDFASDF",
		// "FDASDF",
		// "!@#$#WQWE$#" });
		// Logger.info("delete ids over");
		//
		// // csv.empty();
		// System.out.println(csv.toString());

		Logger.info("End");
	}

	public void save() throws IOException {
		new File(this.filePath).delete();
		if (this.labels != null)
			insert(this.labels);
		insert(this.lineList);
	}

	public byte[] toBytes() throws IOException {
		if (this.changed)
			load();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
		for (int i = 0; i < this.lineList.size(); i++) {
			String[] values = this.lineList.get(i);
			csvPrinter.println(values);
		}
		byte[] bytes = baos.toByteArray();
		csvPrinter.close();
		baos.close();
		return bytes;
	}

	public byte[] toBytes(int lineCount) throws IOException {
		if (this.changed)
			load();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter csvPrinter = new ExcelCSVPrinter(baos, charset);
		lineCount = this.lineList.size() > lineCount ? lineCount
				: this.lineList.size();
		for (int i = 0; i < lineCount; i++) {
			String[] values = this.lineList.get(i);
			csvPrinter.println(values);
		}
		byte[] bytes = baos.toByteArray();
		csvPrinter.close();
		baos.close();
		return bytes;
	}
}
