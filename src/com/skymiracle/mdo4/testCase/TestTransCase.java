package com.skymiracle.mdo4.testCase;

import junit.framework.TestCase;

import com.skymiracle.logger.Logger;
import com.skymiracle.mdo4.trans.TransServiceProxyCGLIB;
import com.skymiracle.mdo4.trans.TransServiceProxyJP;
import com.skymiracle.mdo4.RdbmsDaoStorage;

public class TestTransCase extends TestCase {

	static {
		try {
			Logger.setLevel(Logger.LEVEL_DEBUG);
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TestTransCase() throws Exception {

	}

	private void createInitTable() throws Exception {
		RdbmsDaoStorage rds = (RdbmsDaoStorage) IocFactory.getBeanFactory()
				.getBean("RdbmsDaoStorage");
		rds.createTableForce(Domain.class, true);
		rds.createTableForce(User.class, true);
	}

	@Override
	public void setUp() throws Exception {
		createInitTable();
	}

	public void testTransNONE() throws Exception {
		ICaseService caseService = ServiceFactory.getCaseService();
		doTest(caseService, false);
		
	}

	public void testTransJP() throws Exception {
		ServiceFactory.transServiceProxyClass = TransServiceProxyJP.class;
		ICaseService caseService = ServiceFactory.getCaseService();
		doTest(caseService, true);
	}

	public void testTransCGLIB() throws Exception {
		ServiceFactory.transServiceProxyClass = TransServiceProxyCGLIB.class;
		ICaseService caseService = ServiceFactory.getCaseService();
		doTest(caseService, true);
	}

	private void doTest(ICaseService caseService, boolean isTrans)
			throws Exception {
		Domain domain = new Domain();
		domain.setDc("skymiracle.com");
		caseService.addDomain(domain);

		// Test for Good!
		{
			User user = new User();
			user.setUid("test");

			caseService.addUser(domain, user);
			assertEquals(1, caseService.getUserCount(domain));
			assertEquals(1, caseService.getUserCountFromDomain(domain));
		}
		// Test for Good!
		{
			User user = new User();
			user.setUid("test2");

			caseService.addUser(domain, user);
			assertEquals(2, caseService.getUserCount(domain));
			assertEquals(2, caseService.getUserCountFromDomain(domain));
		}

		// Test for Exception!
		{
			User user = new User();
			user.setUid("test_error");
			caseService.addUser(domain, user);
			assertEquals(3, caseService.getUserCount(domain));
			assertEquals(3, caseService.getUserCountFromDomain(domain));
		}

		// Test for Exception!
		{
			User user = new User();
			user.setUid("test_error");
			try {
				caseService.addUserFailed(domain, user);
			} catch (Exception e) {
				boolean isWantedException = "Test Exception".equals(e
						.getMessage());
				if (!isWantedException)
					Logger.info("", e);
				assertEquals(true, isWantedException);
			}
			assertEquals(3, caseService.getUserCount(domain));
			if (isTrans)
				assertEquals(3, caseService.getUserCountFromDomain(domain));
			else
				assertEquals(4, caseService.getUserCountFromDomain(domain));
		}

		// Test for Exception!
		{
			User user = new User();
			user.setUid("test2");
			try {
				caseService.addUser(domain, user);
			} catch (Exception e) {
				System.out.println(e.getMessage());

				boolean isWantedException = "java.sql.SQLException: Unique constraint violation:"
						.equals(e.getMessage().trim());
				if (!isWantedException)
					Logger.info("", e);
				// if (!isWantedException)
				// Logger.info("",e);
				// assertEquals(true,isWantedException);
			}
			assertEquals(3, caseService.getUserCount(domain));
			if (isTrans)
				assertEquals(3, caseService.getUserCountFromDomain(domain));
			else
				assertEquals(5, caseService.getUserCountFromDomain(domain));
		}
		
		// test Cache
		{
			for(int i=0; i<10; i++)
				caseService.getDomain("skymiracle.com");
		}
		
		
	}

}
