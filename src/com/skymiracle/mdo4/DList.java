package com.skymiracle.mdo4;

import java.util.LinkedList;

public class DList<E extends Dao> extends LinkedList<E> {

	public void del() throws DaoStorageException, DaoBuildException,
			NullKeyException, NotEmptyException {
		for (E e : this) {
			e.del();
		}
	}
}
