package com.skymiracle.mdo4;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import com.novell.ldap.LDAPException;
import com.skymiracle.ldap.LDAPTools2;
import com.skymiracle.mdo4.confDao.ConfDao;
import com.skymiracle.mdo4.confDao.HasConfDao;
import com.skymiracle.mdo4.confDao.LdapConf;
import com.skymiracle.sor.exception.AppException;

/**
 * 以LDAP目录服务器为实际存储介质的DaoStorage实现。 注意：可能有些方法没有完全实现。
 * 
 * @author skymiracle
 * 
 */
public class LdapDaoStorage implements DaoStorage, HasConfDao {

	private LdapConf ldapConf;

	public LdapDaoStorage() {
	}

	public LdapConf getLdapConf() {
		return ldapConf;
	}

	public void setLdapConf(LdapConf ldapConf) {
		this.ldapConf = ldapConf;
	}

	public LdapDaoStorage(String host, int port, String bindDN,
			String bindPassword, String baseDN) {
		this.ldapConf = new LdapConf();
		this.ldapConf.setHost(host);
		this.ldapConf.setBindDN(bindDN);
		this.ldapConf.setBindPassword(bindPassword);
		this.ldapConf.setBaseDN(baseDN);
		this.ldapConf.setPort(port);
	}

	public void delDao(Dao dao) throws DaoStorageException, DaoBuildException,
			NullKeyException, NotEmptyException {
		String dn = dao.dn(this.ldapConf.getBaseDN());
		try {
			LDAPTools2.delNode(this.ldapConf.getHost(),
					this.ldapConf.getPort(), this.ldapConf.getBindDN(),
					this.ldapConf.getBindPassword(), new String[] { dn });
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> void delDao(T[] daos) throws DaoStorageException,
			DaoBuildException, NullKeyException, NotEmptyException {
		String[] dns = new String[daos.length];
		for (int i = 0; i < daos.length; i++)
			dns[i] = daos[i].dn(this.ldapConf.getBaseDN());
		try {
			LDAPTools2.delNode(this.ldapConf.getHost(),
					this.ldapConf.getPort(), this.ldapConf.getBindDN(),
					this.ldapConf.getBindPassword(), dns);
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		}
	}

	public void empty(Class<? extends Dao> daoClass) throws DaoStorageException {
		try {
			LDAPTools2.empty(this.ldapConf.getHost(), this.ldapConf.getPort(),
					this.ldapConf.getBindDN(), this.ldapConf.getBindPassword(),
					this.ldapConf.getBaseDN(), daoClass);
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass)
			throws DaoStorageException {
		return getDaos(daoClass, null);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet) throws DaoStorageException {
		return getDaos(daoClass, daoAttrSet, null);
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter) throws DaoStorageException {
		try {
			return LDAPTools2.getDaos(daoClass, this.ldapConf.getHost(),
					this.ldapConf.getPort(), this.ldapConf.getBindDN(),
					this.ldapConf.getBindPassword(), this.ldapConf.getBaseDN(),
					daoAttrSet, filter);
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		} catch (InstantiationException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String orderBy, boolean isASC)
			throws DaoStorageException {
		DList<T> list = getDaos(daoClass, daoAttrSet);
		DaoComparator<T> comparator = new DaoComparator<T>(orderBy, isASC);
		Collections.sort(list, comparator);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends Dao> DList<T> getDaos(Class<T> daoClass,
			DaoAttrSet daoAttrSet, String filter, String orderBy, boolean isASC)
			throws DaoStorageException {
		DList<T> list = getDaos(daoClass, daoAttrSet, filter);
		DaoComparator comparator = new DaoComparator(orderBy, isASC);
		Collections.sort(list, comparator);
		return list;
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

	public void addDao(Dao[] daos) throws DaoStorageException, NullKeyException {
		try {
			LDAPTools2.addDaos(this.ldapConf.getHost(),
					this.ldapConf.getPort(), this.ldapConf.getBindDN(),
					this.ldapConf.getBindPassword(), daos, this.ldapConf
							.getBaseDN());
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException();
		} catch (InvocationTargetException e) {
			throw new DaoStorageException();
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		}
	}

	public void loadDao(Dao dao) throws DaoStorageException, NullKeyException,
			DaoBuildException, KeyNotExistException {
		try {
			LDAPTools2.loadDao(dao, this.ldapConf.getHost(), this.ldapConf
					.getPort(), this.ldapConf.getBindDN(), this.ldapConf
					.getBindPassword(), this.ldapConf.getBaseDN());
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT)
				throw new KeyNotExistException();
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public boolean existDao(Dao dao) throws DaoStorageException {
		try {
			return LDAPTools2.dnExists(this.ldapConf.getHost(), this.ldapConf
					.getPort(), this.ldapConf.getBindDN(), this.ldapConf
					.getBindPassword(), dao.dn(this.ldapConf.getBaseDN()));
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		} catch (NullKeyException e) {
			throw new DaoStorageException(e);
		}
	}

	public <T extends Dao> T modDao(T dao, DaoAttrSet daoAttrSet)
			throws DaoStorageException, NullKeyException, DaoBuildException {
		modDao(new Dao[] { dao }, new DaoAttrSet[] { daoAttrSet });
		return dao;
	}

	public void modDao(Dao[] daos, DaoAttrSet[] daoAttrSets)
			throws DaoStorageException, DaoBuildException, NullKeyException {
		try {
			LDAPTools2.modDao(daos, this.ldapConf.getHost(), this.ldapConf
					.getPort(), this.ldapConf.getBindDN(), this.ldapConf
					.getBindPassword(), this.ldapConf.getBaseDN(), daoAttrSets);
		} catch (UnsupportedEncodingException e) {
			throw new DaoStorageException(e);
		} catch (LDAPException e) {
			throw new DaoStorageException(e);
		} catch (InterruptedException e) {
			throw new DaoStorageException(e);
		}
	}

	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet)
			throws DaoStorageException {
		return count(daoClass, daoAttrSet, null);
	}

	@SuppressWarnings("unchecked")
	public long count(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet,
			String filter) throws DaoStorageException {
		List DList = this.getDaos(daoClass, daoAttrSet, filter);
		return DList.size();
	}

	public ConfDao getConfDao() {
		return this.ldapConf;
	}

	public void setConfDao(ConfDao confDao) {
		this.ldapConf = (LdapConf) confDao;
	}

	public boolean auth(Dao dao, String passwordFieldname, String password)
			throws DaoStorageException, NullKeyException, AppException {
		if (password == null)
			throw new AppException("密码不能为空");
		try {
			return LDAPTools2.auth(this.ldapConf.getHost(), this.ldapConf
					.getPort(), this.ldapConf.getBaseDN(), dao, password);
		} catch (IllegalArgumentException e) {
			throw new DaoStorageException(e);
		} catch (IllegalAccessException e) {
			throw new DaoStorageException(e);
		} catch (InvocationTargetException e) {
			throw new DaoStorageException(e);
		}
	}

	public void delDao(Class<? extends Dao> daoClass, DaoAttrSet daoAttrSet) {
		// TODO Auto-generated method stub
		// TODO no implement
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
			return Daos.sumL(this.getDaos(daoClass, daoAttrSet, filter),
					fieldName);
		} catch (Exception e) {
			throw new DaoStorageException(e);
		}
	}

}
