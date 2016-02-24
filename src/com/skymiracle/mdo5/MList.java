package com.skymiracle.mdo5;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.skymiracle.csv.Csv;
import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.sor.exception.AppException;

public class MList<E extends Mdo<E>> extends LinkedList<E> {

	private static final long serialVersionUID = 2207023084028553431L;

	private Map<String, Object> attrsMap = new HashMap<String, Object>();

	public void create() throws AppException, Exception {
		for (E e : this) {
			e.create();
		}
	}

	public void delete() throws AppException, Exception {
		for (E e : this) {
			e.delete();
		}
	}

	public void createIfNotExist() throws AppException, Exception {
		for (E e : this) {
			e.createIfNotExist();
		}
	}

	public File exportCSV(File file, String charset) throws AppException,
			Exception {
		Csv csv = new Csv(file.getAbsolutePath(), charset);
		for (E e : this)
			csv.insert(e.toStrings());
		return file;
	}

	public File exportCSV(File file, Class<E> mdoClass, String charset)
			throws AppException, Exception {
		MdoReflector reflector = MdoReflector.get(mdoClass);
		MdoField[] mfs = reflector.getMdoFields();
		String[] labels = new String[mfs.length];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = mfs[i].name;
		}
		Csv csv = new Csv(file.getAbsolutePath(), labels, charset);
		for (E e : this)
			csv.insert(e.toStrings());
		return file;
	}

	public MList<E> load() throws AppException, Exception {
		for (E e : this) {
			e.load();
		}
		return this;
	}

	public MList<E> loadExists() throws AppException, Exception {
		MList<E> es = new MList<E>();
		for (E e : this) {
			try {
				e.load();
				es.add(e);
			} catch (NotExistException ex) {

			}
		}
		return es;
	}

	protected MList<E> setMdoX(Mdo_X<E> mdoX) {
		for (E e : this)
			e.mdoX = mdoX;
		return this;
	}

	public void setValue(String fieldName, Object value) throws AppException,
			Exception {
		for (E e : this)
			e.fieldValue(fieldName, value);
	}

	public void attr(String name, Object value) {
		this.attrsMap.put(name, value);
	}

	public Object attr(String name) {
		return this.attrsMap.get(name);
	}

	public Map<String, Object> getAttrsMap() {
		return attrsMap;
	}

	public MList<E> sub(int begin, int count) {
		MList<E> subList = new MList<E>();
		
		int end = begin + count - 1;
		int len = this.size() - 1;
		if (end > len)
			end = len;
		
		int i = -1;
		for (E e : this) {
			i++;
			if (i < begin)
				continue;
			if (i > end)
				break;
			subList.add(e);
		}
		return subList;
	}

}
