package com.skymiracle.sysinfo;

import java.io.File;
 
 public class Test {
     public static void main(String[] args) {
         File[] roots = File.listRoots();
         for (File _file : roots) {
             System.out.println(_file.getPath());
//             System.out.println("Free space = " + _file.getFreeSpace());
//             System.out.println("Usable space = " + _file.getUsableSpace());
//             System.out.println("Total space = " + _file.getTotalSpace());
//             System.out.println();
         }
     }
 }
