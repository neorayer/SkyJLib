package com.skymiracle.dns;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.skymiracle.logger.Logger;
import com.skymiracle.util.Checker;
import com.skymiracle.util.IpErrorException;
import com.skymiracle.util.IpFilter;

/**
 * A Dns Class can be used for resolving the Dns name to IP. MX or A record can
 * be resolved.
 * 
 * @author zhourui
 * 
 */

public class Dns {

	public static DnsMxRecord[] resolveMxRecord(String mxDomain)
			throws NamingException {
		return resolveMxRecord("", mxDomain);
	}

	
	public static String[] resolveMxOrARecord(String mxDomain)
			throws NamingException {
		try {
			
			DnsMxRecord[] mxRecords = resolveMxRecord("", mxDomain);
			if (mxRecords.length == 0)
				return resolveARecord(mxDomain);
			String[] ss = new String[mxRecords.length];
			int i = 0;
			for (DnsMxRecord record : mxRecords){
				if(IpFilter.ipValidateFalg(record.getName()))
					ss[i++] = record.getName().substring(0,record.getName().length()-1);
				else
					ss[i++] = record.getName();
			}
			return ss;
		} catch (NamingException e) {
			return resolveARecord(mxDomain);
		}
	}

	/**
	 * Resolve DNS recorder typed A with system default DNS server.
	 * 
	 * @param aDomain
	 * @return
	 * @throws NamingException
	 */
	public static String[] resolveARecord(String aDomain)
			throws NamingException {
		return resolveARecord("", aDomain);
	}

	/**
	 * Resolve DNS recorder typed A with a special DNS server (dnsHost).
	 * 
	 * @param dnsHost
	 * @param aDomain
	 * @return
	 * @throws NamingException
	 */
	public static String[] resolveARecord(String dnsHost, String aDomain)
			throws NamingException {
		try {
			ArrayList<String> addrList = new ArrayList<String>();
			InetAddress[] addrs = Inet4Address.getAllByName(aDomain);
			for(InetAddress addr: addrs) {
				addrList.add(addr.getHostAddress());
			}
			return addrList.toArray(new String[addrList.size()]);
		} catch (UnknownHostException e) {
			// nothing todo
		}
		
		DirContext ictx = new InitialDirContext();
		
		Attributes attrs = ictx.getAttributes(new StringBuffer("dns://")
				.append(dnsHost).append("/").append(aDomain).toString(),
				new String[] { "A" });
		Attribute attr = attrs.get("A");
		if (attr == null)
			return new String[0];
		List<String> recordList = new ArrayList<String>();
		for (int i = 0; i < attr.size(); i++) {
			String line = (String) attr.get(i);
			String name = null;
			int pos = line.indexOf(' ');
			if (pos < 0)
				name = line;
			else {
				name = line.substring(pos + 1);
			}
			recordList.add(name);
		}
		return recordList.toArray(new String[recordList.size()]);
	}

	public static String[] resolveTextRecord(String dnsHost, String aDomain)
			throws NamingException {
		DirContext ictx = new InitialDirContext();

		Attributes attrs = ictx.getAttributes(new StringBuffer("dns://")
				.append(dnsHost).append("/").append(aDomain).toString(),
				new String[] { "TXT" });
		Attribute attr = attrs.get("TXT");
		if (attr == null)
			return new String[0];
		List<String> recordList = new ArrayList<String> ();
		for (int i = 0; i < attr.size(); i++) {
			String line = (String) attr.get(i);
			String name = null;
			int pos = line.indexOf(' ');
			if (pos < 0)
				name = line;
			else {
				name = line.substring(pos + 1);
			}
			recordList.add(name);
		}
		return recordList.toArray(new String[recordList.size()]);
	}

	/**
	 * get the mxrecords as name address on the mxDomain
	 * 
	 * @param mxDomain
	 * @return
	 * @throws NamingException
	 */
	public static DnsMxRecord[] resolveMxRecord(String dnsHost, String mxDomain)
			throws NamingException {
		Logger.debug(new StringBuffer("Dns.resolveMxRecord(").append(dnsHost)
				.append(mxDomain).append(")"));
		List<DnsMxRecord> recordList = new ArrayList<DnsMxRecord>();

		DirContext ictx = new InitialDirContext();
		
		Attributes attrs = ictx.getAttributes(new StringBuffer("dns://")
				.append(dnsHost).append("/").append(mxDomain).toString(),
				new String[] { "MX" });

		Attribute attr = attrs.get("MX");
		if (attr != null)
			for (int i = 0; i < attr.size(); i++) {
				String line = (String) attr.get(i);
				int pri = 0;
				String name = null;
				int pos = line.indexOf(' ');
				if (pos < 0)
					name = line;
				else {
					pri = Integer.parseInt(line.substring(0, pos));
					name = line.substring(pos + 1);
				}
				recordList.add(new DnsMxRecord(pri, name));
			}

		// recordList.
		DnsMxRecord[] mxRecords = recordList
				.toArray(new DnsMxRecord[recordList.size()]);
		Arrays.sort(mxRecords, new Comparator<DnsMxRecord> (){

			public int compare(DnsMxRecord arg0, DnsMxRecord arg1) {
				return (arg0).getPri() - ( arg1).getPri();
			}
		});
		return mxRecords;
	}

	public static boolean isARecord(String domain, String ip)
			throws NamingException {
		String[] aRecords = resolveARecord(domain);
		for (int i = 0; i < aRecords.length; i++)
			if (ip.equals(aRecords[i]))
				return true;
		return false;
	}

	/**
	 * To check if the ip like the domain 'A' dns resolv, with special level.
	 * 
	 * @param domain
	 * @param ip
	 * @param level
	 *            0 -255.255.255.255, 1 - 255.255.255.0, 2 - 255.255.0.0, 3 -
	 *            255.0.0.0
	 * @return
	 */
	public static boolean likeARecord(String domain, String ip, int level)
			throws NamingException {
		String[] aRecords = resolveARecord(domain);
		for (int i = 0; i < aRecords.length; i++) {
			if (ipEquals(aRecords[i], ip, level))
				return true;
		}
		return false;
	}

	/**
	 * To compare two ip with special level. Example: ipEquals("1.1.1.1",
	 * "1.1.1.2", 0) is false ipEquals("1.1.1.1", "1.1.1.2", 1) is true
	 * ipEquals("1.1.1.1", "1.2.3.4", 3) is true
	 * 
	 * @param ip1
	 * @param ip2
	 * @param level
	 * @return
	 */
	public static boolean ipEquals(String ip1, String ip2, int level) {
		String[] ip1as = ip1.split("\\.");
		String[] ip2as = ip2.split("\\.");
		for (int i = 0; i < 4 - level; i++) {
			if (!ip1as[i].equals(ip2as[i]))
				return false;
		}
		return true;
	}

	/**
	 * To check if the ip is the domain 'A' dns resolv, or 'MX' dns resolv.
	 * 
	 * @param domain
	 * @param ip
	 * @return
	 * @throws NamingException
	 */
	public static boolean isAOrMxRecord(String domain, String ip)
			throws NamingException {
		if (isARecord(domain, ip))
			return true;
		DnsMxRecord[] mxRecords = resolveMxRecord(domain);
		for (int i = 0; i < mxRecords.length; i++) {
			DnsMxRecord dmr = mxRecords[i];
			String name = dmr.getName();
			if (ip.equals(name))
				return true;
			if (isARecord(name, ip))
				return true;
		}

		return false;
	}

	/**
	 * To check if the ip like the domain 'MX' dns resolv, with special level.
	 * 
	 * @param domain
	 * @param ip
	 * @param level
	 * @return
	 * @throws NamingException
	 */
	public static boolean likeMxRecord(String domain, String ip, int level)
			throws NamingException {
		DnsMxRecord[] mxRecords = resolveMxRecord(domain);
		for (int i = 0; i < mxRecords.length; i++) {
			DnsMxRecord dmr = mxRecords[i];
			String name = dmr.getName();
			if (ip.equals(name))
				return true;
			if (likeARecord(name, ip, level))
				return true;
		}
		return false;
	}

	/**
	 * To check if the ip like the domain 'A' dns resolv, or 'MX' dns resolv,
	 * with special level.
	 * 
	 * @param domain
	 * @param ip
	 * @param level
	 * @return
	 * @throws NamingException
	 */
	public static boolean likeAOrMxRecord(String domain, String ip, int level)
			throws NamingException {
		if (likeARecord(domain, ip, level))
			return true;
		if (likeMxRecord(domain, ip, level))
			return true;
		return false;
	}

	/**
	 * 
	 * @param ip
	 * @param zone
	 * @return null while can not lookup, return code( 127.*.*.*) if do lookup.
	 * @throws IpErrorException
	 */
	public static String rblLookup(String ip, String zone)
			throws IpErrorException {
		if (!Checker.isGoodIPv4(ip))
			throw new IpErrorException(ip);
		String[] args = ip.split("\\.");
		StringBuffer sb = new StringBuffer();
		sb.append(args[3]).append('.').append(args[2]).append('.').append(
				args[1]).append('.').append(args[0]).append('.').append(zone);
		String[] reses;
		try {
			reses = Dns.resolveARecord(sb.toString());
			if (reses.length == 0)
				return null;
			return reses[0];
		} catch (NamingException e) {
			return null;
		}
	}
	
	public static void main(String[] args) throws NamingException, IpErrorException{
		//String[] ss =  Dns.resolveARecord("skymiracle.com");
		//for(int i=0;i<ss.length;i++)
			//System.out.println(ss[i]);
//		String ss = Dns.rblLookup("60.191.29.69", "cn");
//		System.out.println(ss);
//		String[] as = Dns.resolveARecord("192.168.1.1", "test.com");
//		for(String s : as)
//			System.out.println(s);
		
//		try {
//			{
//				
//				InetAddress addr = Inet4Address.getByName("www.javaeye.com");
//				System.out.println(addr.getHostAddress());
//				System.out.println(addr.getHostName());
//			}
//			System.out.println("---");
//			InetAddress[] addrs = Inet4Address.getAllByName("www.javaeye.com");
//			for(InetAddress addr: addrs) {
//				System.out.println(addr.getHostAddress());
//				System.out.println(addr.getHostName());
//			}
//				
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		
//		String[] ss = Dns.resolveTextRecord("192.168.1.1", "test.com");
//		for(String s: ss)
//			System.out.println(s);
		
//		System.out.println("end");
		
	}
}
