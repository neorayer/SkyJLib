package com.skymiracle.mdo4;

/**
 * 
 * 废弃！废弃！
 * 写出这个类的程序员，居然还敢偷偷摸摸加入SkyJLib标准包。需要批评自己。
 * 
 * @Deprecated 
 * @author skymiracle
 *
 */
@Deprecated
public abstract class ManOrderDao extends Dao {

	private String manOrder = "9";

	public String getManOrder() {
		return this.manOrder;
	}

	public void setManOrder(String manOrder) {
		this.manOrder = manOrder;
	}

}
