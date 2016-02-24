package com.skymiracle.mdo4.testCase;

import java.io.File;
import java.util.List;
import junit.framework.TestCase;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.jdbcConnPool.JdbcConnPool;
import com.skymiracle.mdo4.DaoAttrSet;
import com.skymiracle.mdo4.DaoStorage;
import com.skymiracle.mdo4.DaoStorageException;
import com.skymiracle.mdo4.LdapDaoStorage;
import com.skymiracle.mdo4.jdbcConnPool.NoPoolJdbcConnPool;
import com.skymiracle.mdo4.RdbmsDaoStorage;
import com.skymiracle.mdo4.XmlDaoStorage;
import com.skymiracle.mdo4.confDao.RdbmsConf;

public class Mdo4TestCase extends TestCase {

	private RdbmsDaoStorage rdbmsDaoStorage = null;

	static {
		try {
			Logger.setLevel(Logger.LEVEL_DEBUG);
			Class.forName("org.hsqldb.jdbcDriver");
//			org.apache.log4j.xml.DOMConfigurator.configure("classpath:com/skymiracle/mdo4/testCase/log4j.xml"); 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private DaoStorage getXmlStorage() throws DaoStorageException {
		new File("/tmp/test.xml").delete();
		return new XmlDaoStorage("/tmp/test.xml", "//wpx/user");
	}

	private DaoStorage getRdbmsStorage() throws DaoStorageException {
		if (this.rdbmsDaoStorage == null) {
			RdbmsConf rdbmsConf = new RdbmsConf();
			rdbmsConf.setJdbcDriver("org.hsqldb.jdbcDriver");
			rdbmsConf.setUsername("sa");
			rdbmsConf.setPassword("");
			rdbmsConf.setUrl("jdbc:hsqldb:mem:aname");
//			this.rdbmsDaoStorage = new RdbmsDaoStorage();
//			this.rdbmsDaoStorage.setRdbmsConf(rdbmsConf);
			
			this.rdbmsDaoStorage = new RdbmsDaoStorage();
			JdbcConnPool connPool = new NoPoolJdbcConnPool();
			connPool.setRdbmsConf(rdbmsConf);
			(this.rdbmsDaoStorage).setJdbcConnPool(connPool);

			// public void createTable(Class daoClass)
			( this.rdbmsDaoStorage).createTable(Domain.class);
			// public void dropTable(Class daoClass)
			(this.rdbmsDaoStorage).dropTable(Domain.class);
			// public void createTable(Class daoClass)
			(this.rdbmsDaoStorage).createTable(Domain.class);

			// public void createTable(Class daoClass)
			(this.rdbmsDaoStorage).createTable(User.class);
			// public void dropTable(Class daoClass)
			( this.rdbmsDaoStorage).dropTable(User.class);
			// public void createTable(Class daoClass)
			( this.rdbmsDaoStorage).createTable(User.class);

		}
		return this.rdbmsDaoStorage;
	}

	private DaoStorage getLdapStorage(String baseDN) throws DaoStorageException {
		return new LdapDaoStorage("127.0.0.1", 389, "cn=Manager,o=wpx",
				"1qw2azxs", baseDN);

	}

	@Override
	protected void setUp() {
	}

	public void testRdbmsStorage() throws Exception {
		testDomainStorage(getRdbmsStorage());
		testUserStorage(getRdbmsStorage());
	}

	public void testXmlStorage() throws DaoStorageException, Exception {
		testUserStorage(getXmlStorage());
	}

	public void testLdapStorage() throws Exception {
		if (true)
			return;
		DaoStorage domainStorage = getLdapStorage("o=wpx");
		DaoStorage userStorage = getLdapStorage("dc=skymiracle.com,o=wpx");
		DaoStorage adminStorage = getLdapStorage("ou=admin,o=wpx");
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com");
			if (domainStorage.existDao(domain)) {
				userStorage.empty(User.class);
				List<User> list = userStorage.getDaos(User.class, null);

				assertEquals(list.size(), 0);
			}
		}
		{
			domainStorage.empty(Domain.class);
			List<Domain> list = domainStorage.getDaos(Domain.class, null);
			assertEquals(list.size(), 0);
		}
		{
			adminStorage.empty(GlobalAdmin.class);
			List<GlobalAdmin> list = adminStorage.getDaos(GlobalAdmin.class, null);
			assertEquals(list.size(), 0);
		}
		// testDomainStorage(getLdapStorage("o=wpx"));
		// testUserStorage(getLdapStorage("dc=skymiracle.com,o=wpx"));

		testAdminStorage(adminStorage);
	}

	public void testDomainStorage(DaoStorage storage) throws Exception {
		// public void empty(Class daoClass);

		// public void insertDao(Dao dao)
		// public void insertDao(Dao[] daos)
		for (int i = 0; i < 10; i++) {
			Domain domain = new Domain();
			domain.setDc("skymiracle.com" + i);
			domain.setSize(100000 * i);
			domain.setAlias(new String[] { "mail2008.cn", "hifiboy.cn",
					"homepay.cn" });
			storage.addDao(domain);
		}
		assertEquals(storage.count(Domain.class, null), 10);

		// public List getDaos(Class daoClass, Map attrMap);
		// public List getDaos(Class daoClass, Map attrMap, String filter);
		// public List getDaos(Class daoClass, Map attrMap, String
		// orderBy,boolean isASC);
		// public List getDaos(Class daoClass, Map attrMap, String filter,String
		// orderBy, boolean isASC);
		{
			List<Domain> list = storage.getDaos(Domain.class, null);
			Domain domain = list.get(1);
			assertEquals(domain.getSize(), 100000);
		}
		{
			DaoAttrSet attrSet = new DaoAttrSet();
			attrSet.put("size", 600000);
			List<Domain> list = storage.getDaos(Domain.class, attrSet);
			Domain domain = list.get(0);
			assertEquals(domain.getDc(), "skymiracle.com6");
		}

		// public void delDao(Dao[] daos)
		// public void delDao(Dao[] daos)
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com9");
			storage.delDao(domain);
			List<Domain> list = storage.getDaos(Domain.class, null);
			assertEquals(list.size(), 9);
		}

		// public void loadDao(Dao dao);
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com0");
			storage.loadDao(domain);
			assertEquals(domain.getSize(), 0);
		}

		// public void modDao(Dao dao)
		// public void modDao(Dao[] daos)
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com7");
			DaoAttrSet attrSet = new DaoAttrSet();
			attrSet.put("size", 7777);
			storage.modDao(domain, attrSet);
		}
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com7");
			storage.loadDao(domain);
			assertEquals(domain.getSize(), 7777);
		}

		{

			Domain domain = new Domain();
			domain.setDc("skymiracle.com");
			domain.setSize(100000);
			domain.setAlias(new String[] { "mail2008.cn", "hifiboy.cn",
					"homepay.cn" });
			storage.addDao(domain);
			
		}
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com");
			storage.loadDao(domain);
			assertEquals(domain.getSize(), 100000);
			
			DaoAttrSet das = new DaoAttrSet();
			das.put("dc", "skymiracle.com");
			storage.incDao(Domain.class, das, "size",2);
			storage.incDao(Domain.class, das, "size",-1);
		}
		{
			Domain domain = new Domain();
			domain.setDc("skymiracle.com");
			storage.loadDao(domain);
			assertEquals(domain.getSize(), 100001);
		}
		//

	}

	public void testUserStorage(DaoStorage storage) throws Exception {
		storage.empty(User.class);

		// public void insertDao(Dao dao)
		// public void insertDao(Dao[] daos)
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setUid("zhourui" + i);
			user.setSize(100000 * i);
			user.setDc("test.com");
			user.setRejectemail(new String[] { "a@mail2008.cn", "a@hifiboy.cn",
					"a@homepay.cn" });
			storage.addDao(user);
		}

		// public List getDaos(Class daoClass, Map attrMap);
		// public List getDaos(Class daoClass, Map attrMap, String filter);
		// public List getDaos(Class daoClass, Map attrMap, String
		// orderBy,boolean isASC);
		// public List getDaos(Class daoClass, Map attrMap, String filter,String
		// orderBy, boolean isASC);
		{
			List<User> list = storage.getDaos(User.class, null);
			User user = list.get(1);
			assertEquals(user.getSize(), 100000);
			assertEquals(user.getRejectemail()[0], "a@mail2008.cn");
		}
		{
			DaoAttrSet attrSet = new DaoAttrSet();
			attrSet.put("size", 600000);
			List<User> list = storage.getDaos(User.class, attrSet);
			User user = list.get(0);
			assertEquals(user.getUid(), "zhourui6");
		}

		// public void delDao(Dao[] daos)
		// public void delDao(Dao[] daos)
		{
			User user = new User();
			user.setUid("zhourui9");
			user.setDc("test.com");
			storage.delDao(user);
			List<User> list = storage.getDaos(User.class, null);
			assertEquals(list.size(), 9);
		}

		// public void loadDao(Dao dao);
		{
			User user = new User();
			user.setUid("zhourui0");
			user.setDc("test.com");
			storage.loadDao(user);
			assertEquals(user.getSize(), 0);
		}

		// public void modDao(Dao dao)
		// public void modDao(Dao[] daos)
		{
			User user = new User();
			user.setUid("zhourui7");
			user.setDc("test.com");
			DaoAttrSet attrSet = new DaoAttrSet();
			attrSet.put("forwardaddr", new String[] { "neorayer@gmail.com" });
			storage.modDao(user, attrSet);
		}
		{
			User user = new User();
			user.setUid("zhourui7");
			user.setDc("test.com");
			storage.loadDao(user);
			assertEquals(user.getForwardaddr()[0], "neorayer@gmail.com");
		}
	}

	public void testAdminStorage(DaoStorage storage) throws Exception {
		// public void empty(Class daoClass);

		// public void insertDao(Dao dao)
		// public void insertDao(Dao[] daos)
		for (int i = 0; i < 10; i++) {

			GlobalAdmin admin = new GlobalAdmin();
			admin.setUid("admin" + i);
			admin.setUserPassword("" + i);
			storage.addDao(admin);
		}

		// public List getDaos(Class daoClass, Map attrMap);
		// public List getDaos(Class daoClass, Map attrMap, String filter);
		// public List getDaos(Class daoClass, Map attrMap, String
		// orderBy,boolean isASC);
		// public List getDaos(Class daoClass, Map attrMap, String filter,String
		// orderBy, boolean isASC);
		{
			List<GlobalAdmin> list = storage.getDaos(GlobalAdmin.class, null);
			GlobalAdmin admin = list.get(1);
			assertEquals(admin.getUid(), "admin1");
		}

		// public void delDao(Dao[] daos)
		// public void delDao(Dao[] daos)
		{
			GlobalAdmin admin = new GlobalAdmin();
			admin.setUid("admin9");
			storage.delDao(admin);
			List<GlobalAdmin> list = storage.getDaos(GlobalAdmin.class, null);
			assertEquals(list.size(), 9);
		}

		// public void loadDao(Dao dao);
		{
			GlobalAdmin admin = new GlobalAdmin();
			admin.setUid("admin0");
			storage.loadDao(admin);
			assertEquals(admin.getUserPassword(), "0");
		}

		// public void modDao(Dao dao)
		// public void modDao(Dao[] daos)
		{
			GlobalAdmin admin = new GlobalAdmin();
			admin.setUid("admin7");
			DaoAttrSet attrSet = new DaoAttrSet();
			attrSet.put("userPassword", "7777");
			storage.modDao(admin, attrSet);
		}
		{
			GlobalAdmin admin = new GlobalAdmin();
			admin.setUid("admin7");
			storage.loadDao(admin);
			assertEquals(admin.getUserPassword(), "7777");
		}

		//

	}
}
