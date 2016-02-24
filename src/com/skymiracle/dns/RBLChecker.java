package com.skymiracle.dns;

/**
 * 
 * RBL 全称是 Real-time Blackhole Lists, 是国外的反垃圾邮件组织提供的检查垃圾邮件发送者地址的服务, RBL
 * 功能对中国用户而言，几乎不可用。 因为大部分中国的IP地址都在 RBL 数据库里。请不要启用RBL
 * 功能，否则可能造成邮件接收不正常，常用的RBL服务器地址有： 
 * 		relays.ordb.org 
 *		dnsbl.njabl.org 
 *		bl.spamcop.net
 * 		sbl.spamhaus.org 
 * 		dun.dnsrbl.net 
 * 		dnsbl.sorbs.net
 * 查询和删除RBL中的IP地址请到http://openrbl.org , http://ordb.org 或
 * http://anti-spam.org.cn(中国反垃圾邮件联盟)
 * 
 */

public class RBLChecker {

	/**
	 * 
	 * @param ip
	 * @return null if check pass ( not in RBL), or Block reason
	 */
	public String check(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

}
