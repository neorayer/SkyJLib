package com.skymiracle.auth;

import java.io.UnsupportedEncodingException;

import com.skymiracle.util.Base64Sky;
import com.skymiracle.util.Crypt;
import com.skymiracle.util.MD5Crypt;

/**
 * Be used for password checking, encrypting or decode.
 * 
 * @author Zhourui
 * 
 */
public class Password {
	private String password;

	/**
	 * 
	 * @param password
	 *            Always is the password string that user inputed in.
	 */
	public Password(String password) {
		this.password = password;
	}

	/**
	 * To check the password by comparing the password field and the password
	 * string encrypted. NOTE: Only {crypt} and plain text are supported now.
	 * 
	 * @param passwordEncrypted
	 *            It look like '{md5_hash}34Dl23kcx..... or {crypt}sZxmWEAQ or
	 *            {SHA}wMs2dkwsldkw$ and so on.
	 * @return true or false
	 */
	public boolean check(String passwordEncrypted) {
		passwordEncrypted = passwordEncrypted.trim();
		if (passwordEncrypted.length() == 0)
			return false;
		StringBuffer peBuf = new StringBuffer(passwordEncrypted);

		// if (('{' != peBuf.charAt(0)) || this.password.indexOf('{')>0) {
		// int pos = this.password.indexOf('}');
		// String cryptType = this.password.substring(1, pos);
		// String cryptStr = this.password.substring(pos + 1);
		// if(cryptType.equalsIgnoreCase("skyenc")){
		// try {
		// // return Base64Sky.encodeToPassword(this.password ,
		// "utf-8").equals(passwordEncrypted);
		// return new String(Base64Sky.decode(cryptStr),
		// "UTF-8").equals(passwordEncrypted);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

		if ('{' != peBuf.charAt(0))
			return this.password.equals(passwordEncrypted);

		int pos = passwordEncrypted.indexOf('}');
		if (pos < 0)
			return this.password.equals(passwordEncrypted);

		String cryptType = peBuf.substring(1, pos);
		String cryptStr = peBuf.substring(pos + 1);

		if (cryptType.equalsIgnoreCase("skyenc")) {

			if (cryptStr.length() < 2)
				return false;
			try {
				return new String(Base64Sky.encodeToPassword(this.password,
						"UTF-8")).equals(passwordEncrypted);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (cryptType.equalsIgnoreCase("crypt")) {
			if (cryptStr.length() < 2)
				return false;
			String salt = cryptStr.substring(0, 2);
			return Crypt.crypt(salt, this.password).equals(cryptStr);
		}
		if (cryptType.equalsIgnoreCase("md5")) {
			if (cryptStr.length() < 2)
				return false;
			String salt = cryptStr.substring(0, 2);
			return MD5Crypt.crypt(this.password, salt).equals(cryptStr);
		}
		if (cryptType.equalsIgnoreCase("MD5Crypt")) {
			if (cryptStr.length() < 2)
				return false;
			return MD5Crypt.crypt(this.password).equals(cryptStr);
		}

		return false;
	}

	public boolean isCrypt() {
		if (password.indexOf("{") != 0)
			return false;

		int pos = password.indexOf("}");
		if (pos <= 0)
			return false;

		String cryptType = password.substring(1, pos);
		if (cryptType.equals(""))
			return false;

		if (cryptType.equalsIgnoreCase("skyenc")
				|| cryptType.equalsIgnoreCase("crypt")
				|| cryptType.equalsIgnoreCase("md5")
				|| cryptType.equalsIgnoreCase("MD5Crypt")) {
			return true;
		}
		return false;
	}

	public String simpleCrypt(String salt) {
		return Crypt.crypt(salt, this.password);
	}

	public String md5Crypt(String salt) {
		return MD5Crypt.crypt(this.password, salt);
	}

	public static void main(String[] args) {
		Password Opassword = new Password("111111");
		try {
			String pass = Base64Sky.encodeToPassword("111111", "UTF-8");
			System.out.println(pass);
			System.out
					.println(new String(Base64Sky.decodePassword("xETMxETM")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Opassword.check("{md5}bMxe3y0Lsv6A."));
	}
}
