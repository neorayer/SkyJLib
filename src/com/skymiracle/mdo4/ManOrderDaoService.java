package com.skymiracle.mdo4;

/**
 * 
 * 废弃！废弃！
 * 写出这个类的程序员，居然还敢偷偷摸摸加入SkyJLib标准包。他应该转行。
 * 
 * @Deprecated 
 * @author skymiracle
 *
 */
@Deprecated
public class ManOrderDaoService {

	DaoService daoService;

	public ManOrderDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	public void addDaoBetween(ManOrderDao dao1, ManOrderDao dao2,
			ManOrderDao dao) throws DaoStorageException, NullKeyException {
		String mo1 = dao1.getManOrder();
		String mo2 = dao2.getManOrder();
		Long i1 = 9L;
		Long i2 = 9L;
		try {
			i1 = Long.parseLong(mo1);
		} catch (Exception e) {
		}
		try {
			i2 = Long.parseLong(mo2);
		} catch (Exception e) {
		}
		while (true) {
			if (i1 == i2) {
				dao.setManOrder("" + i1);
				break;
			}
			if (i1 > i2) {
				ManOrderDao tmp = dao1;
				dao1 = dao2;
				dao2 = tmp;
				Long i = i1;
				i1 = i2;
				i2 = i;
				String mo = mo1;
				mo1 = mo2;
				mo2 = mo;
			}

			if (mo1.charAt(mo1.length() - 1) == '9') {
				dao.setManOrder(mo1 + '0');
				break;
			}

			if (i1 + 1 < i2) {
				dao.setManOrder((i1 + 1) + "");
				break;
			}

			dao.setManOrder((i1 + "0"));
			break;
		}

		this.daoService.addDao(dao);
	}
}
