package com.skymiracle.mdo5.cache;

import com.skymiracle.mdo5.*;

public interface MdoCache {

	public long getHitCount();

	public long getGetCount();

	public float getHitRate();

	public void clear();

	public void rset();
	
	public boolean load(Mdo<?> mdo);

	public boolean put(Mdo<?> mdo);

	public void remove(Mdo<?> mdo);

	public void remove(MList<?> mdos);

	public void initialize();
	
	public boolean exists(Mdo<?> mdo);

}