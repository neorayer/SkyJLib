package com.skymiracle.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.skymiracle.io.TextFile;

/**
 * A tool program to exchange the old format ldif of ChangHong mail, to WPX
 * ldif.
 * 
 * @author neora
 * 
 */
/*
 * # zhanghao, changhong.com, ChangHong
 * 
 * dn: uid=zhanghao,dc=changhong.com,o=ChangHong loginShell: /bin/sh mail:
 * zhanghao@changhong.com objectClass: person objectClass: dcObject objectClass:
 * country objectClass: inetorgperson uid: zhanghao
 * 
 * objectClass: posixAccount cn: zhanghao gidNumber: 99 uidNumber: 99
 * homeDirectory: /home/httpd/ftp
 * 
 * isProxy: 1 userPassword:: e1NIQX05RjQyZkNNVGZVRGJpZWk1Tm5KNTJPZEVXVlE9
 * 
 */

public class LdifParser {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Failed, arguments missing!");
			System.out.println("usage:");
			System.out.println("ldifChg source-ldif-path new-ldif-path");
			System.exit(0);
		}

		String srcPath = args[0];
		String[] lines = TextFile.loadLines(srcPath);

		ArrayList<ArrayList<String>> entryList = new ArrayList<ArrayList<String>>();
		ArrayList<String> entryLineList = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].length() == 0) {
				entryList.add(entryLineList);
				entryLineList = new ArrayList<String>();
				continue;
			}
			if (lines[i].charAt(0) == '#')
				continue;
			entryLineList.add(lines[i]);
		}

		ArrayList newLineList = new ArrayList();

		for (int i = 0; i < entryList.size(); i++) {
			HashMap vlMap = new HashMap();
			String dn = null;
			ArrayList eList = entryList.get(i);
			for (int j = 0; j < eList.size(); j++) {
				String line = (String) eList.get(j);
				int pos = line.indexOf(':');
				if (pos < 0)
					continue;
				String aName = line.substring(0, pos).trim();
				String aVal = line.substring(pos + 1).trim();
				if (aName.equalsIgnoreCase("dn")) {
					dn = aVal;
				} else
					vlMap.put(aName.toLowerCase(), aVal);
			}

			if (dn == null)
				continue;
			newLineList.add("dn:" + dn);

			Object obj;

			obj = vlMap.get("uid");
			if (obj != null) {
				newLineList.add("uid:" + (String) obj);
				newLineList.add("sn:" + (String) obj);
				newLineList.add("cn:" + (String) obj);
			}
			obj = vlMap.get("userpassword");
			if (obj != null)
				newLineList.add("userPassword:" + (String) obj);

			obj = vlMap.get("loginshell");
			if (obj != null) {
				newLineList.add("objectClass: posixAccount");
				newLineList.add("loginShell: "
						+ (String) vlMap.get("loginshell"));
				newLineList
						.add("uidNumber: " + (String) vlMap.get("uidnumber"));
				newLineList
						.add("gidNumber: " + (String) vlMap.get("gidnumber"));
				newLineList.add("homeDirectory: "
						+ (String) vlMap.get("homedirectory"));
			}

			obj = vlMap.get("isproxy");
			if (obj != null)
				newLineList.add("isProxy: " + (String) obj);

			newLineList.add("storageLocation: 192.168.0.41:6001");
			newLineList.add("Size: 51200000");

			newLineList.add("objectClass: person");
			newLineList.add("objectClass: wpxAccount");
			newLineList.add("objectClass: outlook");
			newLineList.add("objectClass: uidObject");
			newLineList.add("");
		}

		String newPath = args[1];
		TextFile.save(newPath, newLineList);
		System.out.println("ldifChg OK count=" + entryList.size());
	}
}
