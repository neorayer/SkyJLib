 package com.skymiracle.util;

 import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpFilter {
	
	public static boolean ipValidate(String ip){
		String regex0="(2[0-4]\\d)" + "|(25[0-5])";
		String regex1="1\\d{2}";
		String regex2="[1-9]\\d";
		String regex3="\\d";
		String regex="("+regex0+")|("+regex1+")|("+regex2+")|("+regex3+")";
		regex = "("+regex+").("+regex+").("+regex+").("+regex+")";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(ip);
		return m.matches();
	}
	
	public static boolean ipFilter(String ip,String ipStart,String ipEnd){
		
		long ipV = IpFilter.getIpLongV(ip);
		long ipStartV = IpFilter.getIpLongV(ipStart);
		long ipEndV = IpFilter.getIpLongV(ipEnd);
		if(ipV >= ipStartV && ipV <= ipEndV){        
            return true;
        }
		return false;
	}
	
	public static boolean ipValidateFalg(String ip){
		String regex0="(2[0-4]\\d)" + "|(25[0-5])";
		String regex1="1\\d{2}";
		String regex2="[1-9]\\d";
		String regex3="\\d";
		String regex="("+regex0+")|("+regex1+")|("+regex2+")|("+regex3+")";
		regex = "("+regex+").("+regex+").("+regex+").("+regex+")"+".";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(ip);
		return m.matches();
	}
	
	public static boolean ipFilterPart(String ip,String ipPart){
		
		if(ipPart.indexOf("/") == -1) {
			if(ip.equals(ipPart))
				return true;
	       else	return false;
		}else{
			String ipStartS = ipPart.substring(0, ipPart.indexOf("/"));
			String ipEndS = ipPart.substring(ipPart.indexOf("/")+1);
			return IpFilter.ipFilter(ip, ipStartS, ipEndS);
		}
		
	}
	
	public static boolean ipBigSmall(String ipStart,String ipEnd){
		
		long ipStartV = IpFilter.getIpLongV(ipStart);
		long ipEndV = IpFilter.getIpLongV(ipEnd);
		if(ipStartV <= ipEndV){        
            return true;
        }
		return false;
	}
	
	public static long getIpLongV(String ip){
		String[] ip1 = ip.trim().split("[.]");
		Integer[] ipInt1 = new Integer[ip1.length];
		for(int i = 0 ; i < ip1.length ; i++){
        	ipInt1[i] = new Integer(ip1[i]);
        }
		return ipInt1[0]*100*100*100+ipInt1[1]*100*100+ipInt1[2]*100+ipInt1[3];
	}
	
	public static void main(String[] args) {
		System.out.println(IpFilter.ipBigSmall( "255.255.255.254", "255.255.255.255"));
		System.out.println(IpFilter.getIpLongV("255.255.255.255"));
		System.out.println(IpFilter.ipValidate("0.1.1.1"));
	}
}
