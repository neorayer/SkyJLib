package com.skymiracle.mdo5;

import com.skymiracle.sor.exception.AppException;

public class NotExistException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3580376185509197481L;

	public NotExistException(Mdo<?> mdo) {
		super(mdo.keyTip() +" 不存在，可能已经删除！");
	}

}
