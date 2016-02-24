package com.skymiracle.mdo4;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class Daos {

	private Daos() {
	}

	public static <T extends Dao> long sumL(Collection<T> daos,
			String fieldName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		long l = 0;
		for (T dao : daos)
			l += dao.fieldValueL(fieldName);
		return l;
	}

}
