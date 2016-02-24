// Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 2008-9-1 13:56:39
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CD.java

package com.skymiracle.util;

import com.skymiracle.util.FsHashPath;

public class CD2
{

    public CD2()
    {
    }

    public static String cd(String user)
    {
        String username = user.substring(0, user.indexOf("@"));
        String domain = user.substring(user.indexOf("@") + 1);
        StringBuffer sb = new StringBuffer();
        sb.append("/wpx/storage").append("/").append(domain);
        String userRootPath = (new StringBuilder(String.valueOf((new FsHashPath(sb.toString(), username)).getDir(2)))).append(username).toString();

        if(userRootPath == null)
            return ".";
        else
            return userRootPath;
    }

    public static void main(String args[])
    {
        if(args.length == 0)
            System.out.println(".");
        else
            System.out.println(cd(args[0]));
    }
}
