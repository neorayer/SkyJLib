package com.skymiracle.mdo4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.BeanAttrPool.BeanAttr;
import com.skymiracle.sor.exception.AppException;
import com.skymiracle.xml.XmlTools;

/**
 * 以XML文件为实际存储介质的DaoStorage实现。 注意：可能有些方法没有完全实现。
 * 
 * @author skymiracle
 * 
 */
public class XmlDaoStorage implements DaoStorage {

	private String filePath = null;

	private String xpath = null;

	private Element rootElement;

	private Document document;

	public XmlDaoStorage() {
		super();
	}

	public XmlDaoStorage(String filePath, String xpath)
			throws DaoStorageException {
		this();
		this.filePath = filePath;
		this.xpath = xpath;
		reload();
	}

	public XmlDaoStorage(File file, String xpath) throws DaoStorageException {
		this(file.getAbsolutePath(), xpath);
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) throws DaoStorageException {
		this.filePath = filePath;
		File dir = new File(this.filePath).getParentFile();
		if (!dir.exists()) {
			Logger.info("Force create dir " + dir.getAbsolutePath());
			dir.mkdirs();
		}
		if (this.xpath != null)
			reload();
	}

	public String getXpath() {
		return this.xpath;
	}

	public void setXpath(String xpath) throws DaoStorageException {
		this.xpath = xpath;
		if (this.filePath != null)
			reload();
	}

	private void reload() throws DaoStorageException {
		this.document = getDocument();
		this.rootElement = (Element) this.document.selectSingleNode(this.xpath);

		if (this.rootElement == null) {
			createNode();
			this.rootElement = (Element) this.document
					.selectSingleNode(this.xpath);
		}
	}

	private void createNode() {
		String[] ss = this.xpath.split("/");
		if (ss.length < 3)
			return;
		String curPath = "//" + ss[2];
		Element el = (Element) this.document.selectSingleNode(curPath);
		if (el == null)
			el = this.document.addElement(ss[2]);

		for (int i = 3; i < ss.length; i++) {
			Element subEl = el.element(ss[i]);
			if (subEl == null) {
				el = el.addElement(ss[i]);
			} else {
				el = subEl;
			}
		}
	}

	private Document getDocument() throws DaoStorageException {
		if (this.filePath == null)
			throw new DaoStorageException("FilePath is null.");

		if (!new File(this.filePath).exists()) {
			this.document = DocumentHelper.createDocument();
			String[] ss = this.xpath.split("/");
			Element e = null;
			for (int i = 2; i < ss.length; i++) {
				if (e == null)
					e = this.document.addElement(ss[i]);
				else {
					e.addElement(ss[i]);
				}
			}
			saveDocument();
		}
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(this.filePath);
			return document;
		} catch (DocumentException e) {
			throw new DaoStorageException(e);
		}
	}

	private void saveDocument() throws DaoStorageException {
		try {
			OutputFormat of = new OutputFormat("\t", true);
			of.setTrimText(true);
			XMLWriter writer;
			FileOutputStream fos = new FileOutputStream(new File(this.filePath));
			writer = new XMLWriter(fos, of);
			writer.write(this.document);
			writer.close();
			fos.close();
			reload();
		} catch (IOException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass)
			throws DaoStorageException {
		return getDaos(daoClass, null);
	}

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException {
		List list = this.getElements(daoClass, daoAttrSet);
		return list.size();
	}

	/**
	 * same count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet);
	 */
	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) throws DaoStorageException {
		return this.count(daoClass, daoAttrSet);
	}

	public void delDao(Dao dao) throws DaoStorageException, DaoBuildException,
			NullKeyException, NotEmptyException {
		delDao(new Dao[] { dao });
	}

	public <T extends Dao> void delDao(T[] daos) throws DaoStorageException,
			DaoBuildException, NullKeyException, NotEmptyException {
		if (daos.length == 0)
			return;
		List elList = this.rootElement.elements(daos[0].getClass()
				.getSimpleName());
		String[] keyNames = daos[0].keyNames();
		for (int di = 0; di < daos.length; di++) {
			Dao dao = daos[di];
			for (int i = 0; i < elList.size(); i++) {
				boolean isMatch = true;
				Element el = (Element) elList.get(i);
				for (int ki = 0; ki < keyNames.length; ki++) {
					String keyName = keyNames[ki].toLowerCase();
					Object keyValue = null;
					try {
						keyValue = BeanAttrPool.getFieldValue(dao, keyName);
					} catch (IllegalArgumentException e) {
						throw new DaoStorageException(e);
					} catch (IllegalAccessException e) {
						throw new DaoStorageException(e);
					} catch (InvocationTargetException e) {
						throw new DaoStorageException(e);
					}

					if (keyValue == null)
						throw new NullKeyException(keyName);
					Element subEl = el.element(keyName);
					if (subEl == null) {
						isMatch = false;
						break;
					}
					String text = subEl.getText();
					if (!keyValue.equals(text)) {
						isMatch = false;
						break;
					}
					isMatch = true;
				}
				if (isMatch)
					this.rootElement.remove(el);
			}
		}
		saveDocument();

	}

	public void empty(Class<? extends Dao> daoClass) throws DaoStorageException {
		List list = this.rootElement.elements(daoClass.getSimpleName());
		for (int i = 0; i < list.size(); i++) {
			this.rootElement.remove((Element) list.get(i));
		}
		saveDocument();
	}

	public boolean existDao(Dao dao) throws DaoStorageException,
			NullKeyException, DaoBuildException {
		try {
			loadDao(dao);
		} catch (KeyNotExistException e) {
			return false;
		}
		return true;
	}

	private List getElements(Class<? extends Dao> daoClass,
			DaoAttrSet daoAttrSet) {
		List elList = this.rootElement.elements(daoClass.getSimpleName());
		List<Element> resList = new LinkedList<Element>();
		BeanAttr[] attrs = BeanAttrPool.getAttrs(daoClass);
		for (int i = 0; i < elList.size(); i++) {
			Element el = (Element) elList.get(i);

			boolean isMatch = true;
			for (int ai = 0; ai < attrs.length; ai++) {
				String fieldname = attrs[ai].fieldname;
				Element subEl = el.element(fieldname);
				String text = subEl == null ? null : subEl.getText();

				if (daoAttrSet != null) {
					Object o = daoAttrSet.get(fieldname);
					if (o != null)
						isMatch = o.toString().equals(text);
				}

				if (!isMatch)
					break;
				if (text == null)
					continue;
			}
			if (isMatch)
				resList.add(el);
		}
		return resList;
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet) throws DaoStorageException {
		DList daoList = new DList();
		List elList = getElements(daoClass, daoAttrSet);
		try {
			for (int i = 0; i < elList.size(); i++) {
				Element el = (Element) elList.get(i);
				Dao dao = XmlTools.getDao(daoClass, el);
				daoList.add(dao);
			}
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		} catch (ClassNotFoundException e) {
			throw new DaoStorageException(e);
		}

		return daoList;
	}

	/**
	 * same to getDaos(daoClass, daoAttrSet);
	 */
	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		return getDaos(daoClass, daoAttrSet);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String orderBy, boolean isASC)
			throws DaoStorageException {
		DList<T> list = getDaos(daoClass, daoAttrSet);
		Comparator<T> comparator = new DaoComparator<T>(orderBy, isASC);
		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * same to getDaos(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
	 * String orderBy,boolean isASC);
	 */
	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy, boolean isASC)
			throws DaoStorageException {
		return this.getDaos(daoClass, daoAttrSet, orderBy, isASC);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy,
			boolean isASC, long limitBegin, long limitCount)
			throws DaoStorageException {
		DList<T> list = this.getDaos(daoClass, daoAttrSet, filter, orderBy,
				isASC);
		long max = limitBegin + limitCount;
		int i = -1;
		DList<T> resList = new DList<T>();
		for (T dao : list) {
			i++;
			if (i < limitBegin)
				continue;
			if (i >= max)
				break;
			resList.add(dao);
		}
		return resList;
	}

	public <T extends Dao> T addDao(T dao) throws DaoStorageException,
			NullKeyException {
		addDao(new Dao[] { dao });
		return dao;
	}

	public void insertDao(List daoList) throws DaoStorageException,
			NullKeyException {
		addDao((Dao[]) daoList.toArray(new Dao[0]));
	}

	public void addDao(Dao[] daos) throws DaoStorageException, NullKeyException {
		if (daos.length == 0)
			return;
		try {
			for (int di = 0; di < daos.length; di++) {
				this.rootElement.add(XmlTools.getElement(daos[di]));
				Logger.debug("XmlDaoStorage insertDao dao: " + daos[di]);
			}
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		}
		saveDocument();
	}

	public void loadDao(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException, KeyNotExistException {
		DaoAttrSet daoAttrSet;
		try {
			daoAttrSet = dao.toKeyDaoAttrSet();
			List list = getDaos(dao.getClass(), daoAttrSet);
			if (list.size() == 0)
				throw new KeyNotExistException(Dao.class.getName() + ". " + dao);
			Dao daoFind = (Dao) list.get(0);
			daoFind.copyTo(dao);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> T modDao(T dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException,
			KeyNotExistException {
		modDao(new Dao[] { dao }, new DaoAttrSet[] { daoAttrSet });
		return dao;
	}

	private String valueToText(Class<? extends Dao> daoClass, String fieldname,
			Object value) {
		Class type = BeanAttrPool.getType(daoClass, fieldname);
		if (type == String[].class)
			return Dao.stringArrayToString((String[]) value);
		else
			return value == null ? "0" : value.toString();
	}

	public void modDao(Dao[] daos, DaoAttrSet[] daoAttrSets)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException {
		if (daos.length == 0)
			return;

		for (int i = 0; i < daos.length; i++) {
			Dao dao = daos[i];
			DaoAttrSet daoAttrSet = daoAttrSets[i];
			try {
				List elList = this.getElements(dao.getClass(), dao
						.toKeyDaoAttrSet());
				if (elList.size() == 0)
					throw new KeyNotExistException();
				Element el = (Element) elList.get(0);
				Set keySet = daoAttrSet.keySet();
				Iterator iter = keySet.iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					Object val = daoAttrSet.get(key);
					Element subEl = el.element(key);
					if (subEl == null)
						el.addElement(key).setText(
								valueToText(dao.getClass(), key, val));
					else
						subEl.setText(valueToText(dao.getClass(), key, val));
				}
			} catch (IllegalArgumentException e) {
				throw new DaoStorageException(e);
			} catch (IllegalAccessException e) {
				throw new DaoStorageException(e);
			} catch (InvocationTargetException e) {
				throw new DaoStorageException(e);
			}
		}
		saveDocument();
	}

	public boolean auth(Dao dao, String passwordFieldname, String password)
			throws DaoStorageException, NullKeyException, AppException {
		if (password == null)
			throw new AppException("密码不能为空");

		// TODO Auto-generated method stub
		return false;
	}

	public void delDao(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			NotEmptyException {
		List list = getDaos(daoClass, daoAttrSet);
		for (int i = 0; i < list.size(); i++)
			delDao((Dao) list.get(i));
	}

	public void modDao(Class<? extends Dao> daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException {
		// TODO Auto-generated method stub

	}

	public void incDao(Class<? extends Dao> daoClass, DaoAttrSet condition,
			String fieldName, int value) throws DaoStorageException {
		// TODO Auto-generated method stub
	}

	public <T extends Dao> DList<T> getDaosByField(Class<T> daoClass,
			String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends Dao> T getDaoByField(Class<T> daoClass, String field,
			String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends Dao> void delDao(List<T> daos)
			throws DaoStorageException, DaoBuildException, NullKeyException {
		// TODO Auto-generated method stub

	}
	
	public long sum(Class<? extends Dao> daoClass, String fieldName,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		try {
		return Daos.sumL(this.getDaos(daoClass, daoAttrSet, filter), fieldName);
		}catch(Exception e) {
			throw new DaoStorageException(e);
		}
	}


}
