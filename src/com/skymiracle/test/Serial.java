package com.skymiracle.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Serial implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6048300099273377157L;
	ArrayList arrayList = new ArrayList();

	public Serial(int id) {

	}

	public void putStr(String s) {
		this.arrayList.add(s);
	}

	public int getCount() {
		return this.arrayList.size();
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		{
			HashMap hMap = new HashMap();
			hMap.put("1", "a");
			hMap.put("2", "b");
			hMap.put("3", "c");
			FileOutputStream fos = new FileOutputStream("/tmp/1.obj");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			Serial s = new Serial(123);
			s.putStr("1");
			s.putStr("1");
			s.putStr("1");
			s.putStr("1");
			oos.writeObject(hMap);
			oos.close();
		}
		{
			FileInputStream fis = new FileInputStream("/tmp/1.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			// Serial s = (Serial)ois.readObject();
			HashMap hMap = (HashMap) ois.readObject();
			System.out.println(hMap.get("1"));
		}

	}
}
