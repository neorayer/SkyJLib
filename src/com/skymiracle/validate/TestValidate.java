package com.skymiracle.validate;

import com.skymiracle.logger.Logger;

import junit.framework.TestCase;

public class TestValidate extends TestCase {

	public void testFailed() {
		testFailed(new Vf_NotEmpty(), null);
		testFailed(new Vf_NotEmpty(), "");

		testFailed(new Vf_NotNull(), null);

		testFailed(new Vf_IsInt(), 1.2);
		testFailed(new Vf_IsInt(), "asdf");

		testFailed(new Vf_IsUid(), null);
		testFailed(new Vf_IsUid(), "");
		testFailed(new Vf_IsUid(), " ");
		testFailed(new Vf_IsUid(), " It have inner space ");
		testFailed(new Vf_IsUid(), "a"); //Too Short
		
		testFailed(new Vf_IsPassword(), null); //必填
		testFailed(new Vf_IsPassword(), ""); // (6-16) 太短
		testFailed(new Vf_IsPassword(), "12345"); // (6-16) 太短
		testFailed(new Vf_IsPassword(), "1234567890abcdef12323"); // (6-16) 太长
		
		testFailed(new Vf_IsDate(), null); // 必填
		testFailed(new Vf_IsDate(), "2008/01/01"); // 格式不对
		testFailed(new Vf_IsDate(), "2008-13-01"); // 格式不对
		testFailed(new Vf_IsDate(), "2008-01-32"); // 格式不对
		
		testFailed(new Vf_IsTime(), null); // 必填
		testFailed(new Vf_IsTime(), "09/01/01"); // 格式不对
		testFailed(new Vf_IsTime(), "9:01:01"); // 格式不对
		
		testFailed(new Vf_IsDateTime(), null); // 必填
		testFailed(new Vf_IsDateTime(), "2008/01/01 12:59:59"); // 格式不对
		testFailed(new Vf_IsDateTime(), "2008-13-01 12:59:59"); // 格式不对
		testFailed(new Vf_IsDateTime(), "2008/01/01 09/01/01"); // 格式不对
		testFailed(new Vf_IsDateTime(), "2008/01/01 9:01:01"); // 格式不对
		
		testFailed(new Vf_IsEmail(), null); // 必填
		testFailed(new Vf_IsEmail(), "_tianliang@skymiracle.com"); // 不能以_开头
		testFailed(new Vf_IsEmail(), "tia@skymiracle.com"); // 用户名太短
		testFailed(new Vf_IsEmail(), "tianliangtianliango@skymiracle.com"); // 用户名太长
		testFailed(new Vf_IsEmail(), "tianliang@skymiracle.abc"); // 后缀不对
		
		testFailed(new Vf_IsMobilePhone(), null); // 必填
		testFailed(new Vf_IsMobilePhone(), "136666888"); // 短了
		testFailed(new Vf_IsMobilePhone(), "913666668888"); // 在十一位的基础上, 至多以零开头
		
		testFailed(new Vf_IsPhone(), null); // 必填
		testFailed(new Vf_IsPhone(), "021/56120000"); // 格式不对
		
		testFailed(new Vf_IsIdcard(), null); // 必填
		testFailed(new Vf_IsIdcard(), "360481832710041"); // 格式不对
		testFailed(new Vf_IsIdcard(), "368888198317100400"); // 格式不对
		
		testFailed(new Vf_IsZipcode(), null);  // 必填
		testFailed(new Vf_IsZipcode(), "032220");  // 格式不对
		testFailed(new Vf_IsZipcode(), "33aa20");  // 格式不对
}

	public void testSuccess() throws ValidateException {
		testSuccess(new Vf_NotEmpty(), "jerry");
		testSuccess(new Vf_NotEmpty(), "tom");
//
		testSuccess(new Vf_NotNull(), "");
//
		testSuccess(new Vf_IsInt(), 123);

		testSuccess(new Vf_IsUid(), "t.o+m-end");
//
		testSuccess(new Vf_IsPassword(), "123456");
		
		testSuccess(new Vf_IsDate(), "2008-12-31");
		
		testSuccess(new Vf_IsTime(), "12:59:59");
		
		testSuccess(new Vf_IsDateTime(), "2008-12-31 12:59:59");
		
		testSuccess(new Vf_IsEmail(), "tianliang@skymiracle.com");
		
		testSuccess(new Vf_IsMobilePhone(), "13666668888");
		testSuccess(new Vf_IsMobilePhone(), "013666668888");
		
		testSuccess(new Vf_IsPhone(), "3893755");
		testSuccess(new Vf_IsPhone(), "0791 - 3893755");
		
		testSuccess(new Vf_IsIdcard(), "360481830710041"); // 15
		testSuccess(new Vf_IsIdcard(), "368888198307100401"); // 18
		testSuccess(new Vf_IsIdcard(), "36888819830710040X"); // 18
		
		testSuccess(new Vf_IsZipcode(), "332200"); 
		
//		testSuccess(new Vf_IsFixPhone(), "332200"); 
}
	
	private void testFailed(Validate validate, Object errValue) {
		try {
			validate.validate(errValue);
			assertTrue(false);
		} catch (ValidateException e) {
			Logger.info( e.getMessage());
			assertTrue(true);
		}
	}
	
	private void testSuccess(Validate validate, Object goodValue)
			throws ValidateException {
			validate.validate(goodValue);
	}
}
