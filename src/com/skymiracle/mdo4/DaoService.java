package com.skymiracle.mdo4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.skymiracle.csv.Csv;
import com.skymiracle.http.HttpRequestTools;
import com.skymiracle.json.JSONTools;
import com.skymiracle.logger.Logger;

/**
 * DaoService是MDO4中非常方便的一个类。几乎是所有App应用的Service层的基类。
 * 注入daoStorage后，DaoService包含的方法可以非常方便的实现Dao的CRUB工作。
 * 
 * @author skymiracle
 * 
 */
public class DaoService {

	protected DaoStorage daoStorage;

	public DaoService() {

	}

	public void setDaoStorage(DaoStorage daoStorage) {
		this.daoStorage = daoStorage;
	}

	public DaoService(DaoStorage daoStorage) {
		this.daoStorage = daoStorage;
	}

	/**
	 * 插入一个Dao
	 * 
	 * @param dao
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 */
	public <T extends Dao> T addDao(T dao) throws DaoStorageException,
			NullKeyException {
		return this.daoStorage.addDao(dao);
	};

	public <T extends Dao> T addUniqueDao(T dao) throws DaoStorageException,
			NullKeyException, DaoBuildException, UniqueDaoExistException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		checkUnique(dao);
		return addDao(dao);
	}

	public void checkUnique(Dao dao) throws UniqueDaoExistException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, DaoStorageException, NullKeyException,
			DaoBuildException {
		if (this.daoStorage.existDao(dao))
			throw new UniqueDaoExistException(dao);
	}

	/**
	 * 插入或者修改Dao。 当dao存在时修改，不存在时插入。
	 * 
	 * @param dao
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
//	public void addOrModDao(Dao dao) throws DaoStorageException,
//			NullKeyException, DaoBuildException, IllegalArgumentException,
//			IllegalAccessException, InvocationTargetException {
//		try {
//			loadDao(dao);
//			modDao(dao, dao.toDaoAttrSet());
//		} catch (KeyNotExistException e) {
//			addDao(dao);
//		}
//	}

	/**
	 * 插入一批Dao
	 * 
	 * @param daos
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 */
	public void addDao(Dao[] daos) throws DaoStorageException, NullKeyException {
		this.daoStorage.addDao(daos);
	}

	/**
	 * 当dao不存在的时候插入
	 * 
	 * @param dao
	 * @return
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 */
	public boolean addDaoIfNoExist(Dao dao) throws DaoStorageException,
			NullKeyException, DaoBuildException {
		if (this.daoStorage.existDao(dao))
			return false;
		else {
			this.daoStorage.addDao(dao);
			return true;
		}

	}

	/**
	 * 删除Dao
	 * 
	 * @param dao
	 * @throws DaoStorageException
	 * @throws DaoBuildException
	 * @throws NullKeyException
	 * @throws NotEmptyException
	 */
	public void delDao(Dao dao) throws DaoStorageException, DaoBuildException,
			NullKeyException, NotEmptyException {
		this.daoStorage.delDao(dao);
	};

	/**
	 * 修改dao, 要修改的项由dao的关键属性指定，要修改的值由daoAttrSet指定。 注：在多数情况下，应该用{@link #modAndLoadDao(Dao, DaoAttrSet)}
	 * 
	 * @see #modAndLoadDao(Dao, DaoAttrSet)
	 * 
	 * @param dao
	 * @param daoAttrSet
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 * @throws KeyNotExistException
	 */
	public <T extends Dao> T modDao(T dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException,
			KeyNotExistException {
		return this.daoStorage.modDao(dao, daoAttrSet);
	};

	/**
	 * 修改dao，然后从新load, 要修改的项由dao的关键属性指定，要修改的值由daoAttrSet指定。 注：此方法应该比modDao()更加常用
	 * 
	 * @see #modDao(Dao, DaoAttrSet)
	 * 
	 * @param dao
	 * @param daoAttrSet
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 * @throws KeyNotExistException
	 */
	public void modAndLoadDao(Dao dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException,
			KeyNotExistException {
		modDao(dao, daoAttrSet);
		loadDao(dao);
	};

	/**
	 * 累加（减）fieldname定义的字段，以condition为条件集。
	 * 
	 * @param daoClass
	 * @param condition
	 * @param fieldName
	 * @param value
	 * @throws DaoStorageException
	 */
	public void incDao(Class<? extends Dao> daoClass, DaoAttrSet condition,
			String fieldName, int value) throws DaoStorageException {
		this.daoStorage.incDao(daoClass, condition, fieldName, value);
	}

	/**
	 * 清空某类DomainObject的所有数据
	 * 
	 * @param daoClass
	 * @throws DaoStorageException
	 */
	public void empty(Class<? extends Dao> daoClass) throws DaoStorageException {
		this.daoStorage.empty(daoClass);
	}

	/**
	 * 返回所有daoClass指定的DomainObject类的对象
	 * 
	 * @param daoClass
	 * @return
	 * @throws DaoStorageException
	 */
	@SuppressWarnings("unchecked")
	public Dao[] getDaos(Class<? extends Dao> daoClass)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, null).toArray(new Dao[0]);
	};

	/**
	 * 返回以指定uuids所包含的所有uuid指定的对象集合。注意只能用于UuidDao类的子类。
	 * 
	 * @see com.skymiracle.mdo4.UuidDao
	 * 
	 * @param daoClass
	 * @param uuids
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass, String[] uuids)
			throws DaoStorageException {
		String filter = "1=2 ";
		for (String uuid : uuids) {
			filter += " OR uuid='" + uuid + "'";
		}
		return this.daoStorage.getDaos(daoClass, null, filter);
	}

	/**
	 * 此方法的意图是一argName为属性名，查处所有匹配values的对象。
	 * 
	 * 但......这个实现有如此大的BUG! 希望看到此处的程序员提出修改意见。让这个@Deprecated的代码“活”过来。
	 * 
	 * @param daoClass
	 * @param values
	 * @param argName
	 * @return
	 * @throws DaoStorageException
	 * 
	 */

	@Deprecated
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			String[] values, String argName) throws DaoStorageException {
		String filter = "1=1  ";
		for (String value : values) {
			filter += " AND argName='" + value + "'";
		}
		return this.daoStorage.getDaos(daoClass, null, filter);
	}

	/**
	 * 返回所有daoClass指定的DomainObject类的对象
	 * 
	 * @param daoClass
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, null);
	};

	/**
	 * 返回所有符合daoAttrSet条件的，daoClass指定的DomainObject类的对象 当前daoAttrSet只能定义完全==的条件。
	 * 禁止使用.
	 * 
	 * @see getDaosList();
	 * @param daoClass
	 * @param daoAttrSet
	 * @return
	 * @throws DaoStorageException
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public Dao[] getDaos(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet)
				.toArray(new Dao[0]);
	};

	/**
	 * 返回所有符合daoAttrSet条件的，daoClass指定的DomainObject类的对象 当前daoAttrSet只能定义完全==的条件。
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			DaoAttrSet daoAttrSet) throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet);
	}

	/**
	 * 返回所有符合daoAttrSet条件 AND filter，daoClass指定的DomainObject类的对象
	 * 
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @param filter
	 *            附加的AND查询语句。根据不同的DaoStorage类型，可以是SQL的条件语句，也可能是LDAP的filter格式。
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet, filter);
	}

	/**
	 * 与所有的getDaosList意义类似，但可以用orderby 和isASC来指定排序了。
	 * 
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @param orderby
	 *            排序字段
	 * @param isASC
	 *            升序 true, 降序 false
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String orderby, boolean isASC)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet, orderby, isASC);
	}

	/**
	 * @see DaoService#getDaosList(Class, DaoAttrSet, String, boolean)
	 * 
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @param filter
	 * @param orderby
	 * @param isASC
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderby, boolean isASC)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet, filter, orderby,
				isASC);
	}

	/**
	 * @see DaoService#getDaosList(Class, DaoAttrSet, String, String, boolean)
	 *      多了个limitBegin, 和limitCount 来限制取出的数量
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @param filter
	 * @param orderby
	 * @param isASC
	 * @param limitBegin
	 * @param limitCount
	 * @return
	 * @throws DaoStorageException
	 */
	public <T extends Dao> List<T> getDaosList(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderby,
			boolean isASC, long limitBegin, long limitCount)
			throws DaoStorageException {
		return this.daoStorage.getDaos(daoClass, daoAttrSet, filter, orderby,
				isASC, limitBegin, limitCount);
	}

	public <T extends Dao> List<T> getDaosPaged(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderby,
			boolean isASC, int pageNum, int countPerPage)
			throws DaoStorageException {

		return this.getDaosList(daoClass, daoAttrSet, filter, orderby, isASC,
				(pageNum - 1) * countPerPage, countPerPage);
	}

	/**
	 * 根据dao的关键属性，从库中提取dao的所有属性值出来
	 * 
	 * @param dao
	 * @throws DaoStorageException
	 * @throws NullKeyException
	 * @throws DaoBuildException
	 * @throws KeyNotExistException
	 */
	public void loadDao(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException, KeyNotExistException {
		this.daoStorage.loadDao(dao);
	};

	@Deprecated
	public String addDaoJSON(Dao dao) {
		try {
			addDao(dao);
			return JSONTools.getResJSONString("1", dao);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (NullKeyException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}
	};

	@Deprecated
	public String delDaoJSON(Dao dao) {
		try {
			delDao(dao);
			return JSONTools.getResJSONString("1", dao);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (DaoBuildException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (NullKeyException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (NotEmptyException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}
	};

	@Deprecated
	public String modDaoJSON(Dao dao, DaoAttrSet daoAttrSet) {
		try {
			modDao(dao, daoAttrSet);
			loadDao(dao);
			return JSONTools.getResJSONString("1", dao);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (NullKeyException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (DaoBuildException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (KeyNotExistException e) {
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}
	};

	@Deprecated
	public <T extends Dao> String getDaosJSON(Class<T> daoClass) {
		try {
			List<T> list = this.daoStorage.getDaos(daoClass, null);
			return JSONTools.getResJSONString("1", list);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}

	};

	@Deprecated
	public <T extends Dao> String getDaosJSON(Class<T> daoClass,
			DaoAttrSet daoAttrSet) {
		try {
			List<T> list = getDaosList(daoClass, daoAttrSet);
			return JSONTools.getResJSONString("1", list);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}
	};

	@Deprecated
	public String loadDaoJSON(Dao dao) {
		try {
			loadDao(dao);
			return JSONTools.getResJSONString("1", dao);
		} catch (DaoStorageException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (NullKeyException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (DaoBuildException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		} catch (KeyNotExistException e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getLocalizedMessage());
		}
	};

	/**
	 * 返回符合daoAttrSet的对象数量
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @return
	 * @throws DaoStorageException
	 */
	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException {
		return this.daoStorage.count(daoClass, daoAttrSet);
	}

	/**
	 * 返回符合daoAttrSet AND filter的对象数量
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @param filter
	 * @return
	 * @throws DaoStorageException
	 */
	protected long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) throws DaoStorageException {
		return this.daoStorage.count(daoClass, daoAttrSet, filter);
	}

	protected long sum(Class<? extends Dao> daoClass, String fieldName,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		return this.daoStorage.sum(daoClass, fieldName, daoAttrSet, filter);
	}

	/**
	 * 尽管这个函数被@Deprecated 了，但这个方法本事是不错的，里面的思想是超级懒人思维的体现。 想不通的可以找Zhourui讨论
	 * 
	 * @param daoClass
	 * @param request
	 * @return
	 */
	@Deprecated
	public String dealWithJSON(Class<? extends Dao> daoClass,
			HttpServletRequest request) {
		String _m = request.getParameter("_m").toLowerCase();
		try {
			if ("list".equals(_m)) {
				return this.getDaosJSON(daoClass, HttpRequestTools
						.getDaoAttrSet(daoClass, request));
			} else if ("add".equals(_m)) {
				return this.addDaoJSON(HttpRequestTools.getDao(daoClass,
						request));
			} else if ("mod".equals(_m)) {
				return this.modDaoJSON(HttpRequestTools.getDao(daoClass,
						request), HttpRequestTools.getDaoAttrSet(daoClass,
						request));
			} else if ("del".equals(_m)) {
				return this.delDaoJSON(HttpRequestTools.getDao(daoClass,
						request));
			} else {
				return JSONTools.getResJSONString("0", "unknown _m = " + _m);
			}
		} catch (Exception e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getMessage());
		}
	}

	/**
	 * 根据daoAttrSet条件删除某类对象
	 * 
	 * @param daoClass
	 * @param daoAttrSet
	 * @throws DaoStorageException
	 * @throws DaoBuildException
	 * @throws NullKeyException
	 * @throws NotEmptyException
	 */
	public void delDao(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			NotEmptyException {
		this.daoStorage.delDao(daoClass, daoAttrSet);
	}

	@Deprecated
	public String delDaosJSON(Dao[] daos) {
		try {
			delDaos(daos);
			return JSONTools.getResJSONString("1", "");
		} catch (Exception e) {
			Logger.error("", e);
			return JSONTools.getResJSONString("0", e.getMessage());
		}
	}

	/**
	 * 根据daos里所有的关键属性，删除一组dao
	 * 
	 * @param daos
	 * @throws DaoStorageException
	 * @throws DaoBuildException
	 * @throws NullKeyException
	 * @throws NotEmptyException
	 */
	public <T extends Dao> void delDaos(T[] daos) throws DaoStorageException,
			DaoBuildException, NullKeyException, NotEmptyException {
		this.daoStorage.delDao(daos);
	}

	public <T extends Dao> void delDaos(List<T> daos)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			NotEmptyException {
		this.daoStorage.delDao(daos);
	}

	/**
	 * 以condition为条件，将符合条件的对象属属性都设置为dataDaoAttrSet里的值
	 * 
	 * @param daoClass
	 * @param dataDaoAttrSet
	 * @param condition
	 * @throws DaoStorageException
	 * @throws DaoBuildException
	 * @throws NullKeyException
	 * @throws KeyNotExistException
	 */
	public void modDao(Class<? extends Dao> daoClass,
			DaoAttrSet dataDaoAttrSet, DaoAttrSet condition)
			throws DaoStorageException, DaoBuildException, NullKeyException,
			KeyNotExistException {
		this.daoStorage.modDao(daoClass, dataDaoAttrSet, condition);
	}

	public DaoStorage getDaoStorage() {
		return this.daoStorage;
	}

	public <T extends Dao> List<T> getDaosBySQL(Class<T> daoClass, String sql)
			throws DaoStorageException {
		if (this.daoStorage instanceof RdbmsDaoStorage)
			return ((RdbmsDaoStorage) this.daoStorage).getDaosBySQL(daoClass,
					sql);
		throw new DaoStorageException("daoStorage is not RdbmsDaoStorage");
	}

	public boolean auth(Dao dao, String passwordFieldname, String password)
			throws DaoStorageException, NullKeyException, Exception {
		return this.daoStorage.auth(dao, passwordFieldname, password);
	}

	public boolean exist(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException {
		return this.daoStorage.existDao(dao);
	}

	public <T extends Dao> File exportCSV(File file, String charset,
			Class<T> daoClass) throws DaoStorageException, IOException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		List<T> daos = getDaosList(daoClass);
		Csv csv = new Csv(file.getAbsolutePath(), charset);
		for (T dao : daos)
			csv.insert(dao.toStrings());

		return file;
	}
}
