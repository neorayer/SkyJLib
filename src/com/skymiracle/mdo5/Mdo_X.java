package com.skymiracle.mdo5;

import java.io.File;
import java.util.List;

import com.skymiracle.mdo5.MdoReflector.MdoField;
import com.skymiracle.sor.exception.AppException;

public class Mdo_X<T extends Mdo<T>> {

	private Store store;

	private MdoFullTextSearcher<T> fullTextSearcher;

	protected Class<T> mdoClass;

	protected MdoField[] mFields;

	public Mdo_X() {

	}

	protected Mdo_X(Class<T> mdoClass, Store store) {
		this();
		mFields = MdoReflector.getMdoFields(mdoClass);
		setMdoClass(mdoClass);
		setStore(store);
	}

	public void addToIndex(T mdo) throws AppException, Exception {
		checkFullTextSearcher();
		fullTextSearcher.addToIndex(mdo);
	}

	protected boolean auth(T mdo, String passwordFieldname, String password)
			throws AppException, Exception {
		return this.store.auth(mdo, passwordFieldname, password);
	}

	private void checkFullTextSearcher() {
		if (fullTextSearcher == null)
			throw new RuntimeException("You have not initFullTextSearcher!");
	}

	public long count() throws AppException, Exception {
		return this.store.count(mdoClass);
	}

	public long count(MdoMap mdoMap) throws AppException, Exception {
		return this.store.count(mdoClass, mdoMap);
	}

	// public void inc(MdoMap condition, String fieldName, int value)
	// throws AppException, Exception {
	// this.store.inc(mdoClass, condition, fieldName, value);
	// }

	// //////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////

	public long count(MdoMap mdoMap, String filter) throws AppException,
			Exception {
		return this.store.count(mdoClass, mdoMap, filter);
	}

	public long count(String fieldsFormat, Object... values)
			throws AppException, Exception {
		return this.store.count(mdoClass, fieldsFormat, values);
	}

	protected T create(T mdo) throws AppException, Exception {
		if (this.fullTextSearcher != null)
			this.fullTextSearcher.addToIndex(mdo);
		return this.store.create(mdo);
	}

	protected T createOrUpdate(T mdo) throws AppException, Exception {
		return this.store.createOrUpdate(mdo);
	}

	public void delete(MdoMap mdoMap) throws AppException, Exception {
		this.store.delete(mdoClass, mdoMap);
	}

	protected void delete(T mdo) throws AppException, Exception {
		this.store.delete(mdo);
	}

	public void deleteAll() throws AppException, Exception {
		this.store.deleteAll(mdoClass);
	}

	protected boolean exists(T mdo) throws AppException, Exception {
		return this.store.exists(mdo);
	}

	// //////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////

	public String fieldTitle(String name) {
		return MdoReflector.getMdoField(mdoClass, name).title;
	}

	public MList<T> find(MdoMap mdoMap, String filter) throws AppException,
			Exception {
		return this.store.find(mdoClass, mdoMap, filter).setMdoX(this);
	};

	public MList<T> find(MdoMap mdoMap, String orderby, boolean isASC)
			throws AppException, Exception {
		return this.store.find(mdoClass, mdoMap, orderby, isASC).setMdoX(this);
	}

	public MList<T> find(MdoMap mdoMap, String filter, String orderby,
			boolean isASC) throws AppException, Exception {
		return this.store.find(mdoClass, mdoMap, filter, orderby, isASC)
				.setMdoX(this);
	}

	public MList<T> find(MdoMap mdoMap, String filter, String orderby,
			boolean isASC, long limitBegin, long limitCount)
			throws AppException, Exception {
		return this.store.find(mdoClass, mdoMap, filter, orderby, isASC,
				limitBegin, limitCount).setMdoX(this);
	}

	public MList<T> find(String fieldsFormat, Object... values)
			throws AppException, Exception {
		return this.store.find(mdoClass, fieldsFormat, values).setMdoX(this);
	}

	public MList<T> findAll() throws AppException, Exception {
		return this.store.findAll(mdoClass).setMdoX(this);
	}

	public List<T> findBySQL(String sql) throws AppException, Exception {
		if (this.store instanceof RdbmsStore)
			return ((RdbmsStore) this.store).findBySQL(mdoClass, sql);
		throw new AppException("mdoStorage is not RdbmsMdoStorage");
	}

	public PagedList<T> findPaged(MdoMap mdoMap, String filter, String orderby,
			boolean isASC, int pageNum, int countPerPage) throws AppException,
			Exception {
		PagedList<T> mdos = new PagedList<T>();
		mdos.setAllCount((int) count(mdoMap, filter));
		mdos.setPageNum(pageNum);
		mdos.setCountPerPage(countPerPage);
		mdos.addAll(find(mdoMap, filter, orderby, isASC, (pageNum - 1)
				* countPerPage, countPerPage));
		return mdos;
	}

	public PagedList<T> findPaged(int pageNum, int countPerPage,
			String fieldsFormat, Object... values) throws AppException,
			Exception {
		MdoMap mdoMap = new MdoMap();
		mdoMap.filledBy(fieldsFormat, values);

		return findPaged(mdoMap, null, null, true, pageNum, countPerPage);
	}

	public MList<T> getMdos(Class<T> mdoClass, MdoMap mdoMap)
			throws AppException, Exception {
		return this.store.find(mdoClass, mdoMap).setMdoX(this);
	}

	public void initFullTextSearcher(File indexDir) {
		fullTextSearcher = new MdoFullTextSearcher<T>(mdoClass, indexDir);
		if (!indexDir.exists()) {
			fullTextSearcher.create();
		}
	}

	protected T load(T mdo) throws AppException, Exception {
		mdo.mdoX = this;
		return this.store.load(mdo);
	}

	public void rebuildFullTextSearcherIndex() throws AppException, Exception {
		fullTextSearcher.clearIndex();
		fullTextSearcher.addToIndex(findAll());
	}

	// public boolean exists(String fieldsFormat, Object... values)
	// throws AppException, Exception {
	// return count(fieldsFormat, values) > 0;
	// }
	//	

	public MList<T> search(int pageNum, int countPerPage, String keyword,
			String field) throws AppException, Exception {
		checkFullTextSearcher();
		return fullTextSearcher.search(pageNum, countPerPage, keyword, field);
	}

	public void setMdoClass(Class<T> mdoClass) {
		this.mdoClass = mdoClass;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	// public void delete(T[] mdos) throws AppException, Exception {
	// this.store.delete(mdos);
	// }
	//
	// public void delete(List<T> mdos) throws AppException, Exception{
	// this.store.delete(mdos);
	// }

	public long sum(String fieldName, MdoMap mdoMap, String filter)
			throws AppException, Exception {
		return this.store.sum(mdoClass, fieldName, mdoMap, filter);
	}

	public long sumAll(String fieldName) throws AppException, Exception {
		return this.store.sum(mdoClass, fieldName, null, null);
	}

	public void update(MdoMap mdoMap, MdoMap condition) throws AppException,
			Exception {
		this.store.update(mdoClass, mdoMap, condition);
	}

	public void update(MdoMap mdoMap, StringBuffer conditionSQL) throws AppException,
			Exception {
		this.store.update(mdoClass, mdoMap, conditionSQL);
	}

	protected T update(T mdo, MdoMap mdoMap) throws AppException, Exception {
		return this.store.update(mdo, mdoMap);
	}

	protected T update(T mdo, String fieldFormat, Object... values)
			throws AppException, Exception {
		return this.store.update(mdo, fieldFormat, values);
	}
}
