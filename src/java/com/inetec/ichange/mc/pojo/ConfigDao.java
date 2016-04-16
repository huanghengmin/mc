package com.inetec.ichange.mc.pojo;

import java.util.List;

import org.apache.log4j.Logger;


import com.avdheshyadav.p4j.jdbc.service.GenericDAO;
import com.avdheshyadav.p4j.jdbc.service.GenericDaoImpl;
import com.avdheshyadav.p4j.jdbc.service.TransactionService;
import com.avdheshyadav.p4j.jdbc.transfer.TransferUtil;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.utils.DaoService;

public class ConfigDao {
	private static Logger logger=Logger.getLogger(DaoService.class);
	public ConfigDao() {

	}

	public Pagination<ConfigBean> getConfig() {
		Pagination<ConfigBean> result=null;
		try {
			TransferUtil.registerClass(ConfigBean.class);
			
			GenericDAO<ConfigBean> genericDAO = new GenericDaoImpl<ConfigBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			int countrows=genericDAO.countRows(ConfigBean.class, "1=1", "1=1");
			
			result=new Pagination(genericDAO.findAll(ConfigBean.class, 1, 1),countrows);
			
		} catch (Exception e) {
			logger.error(e);
		}
		return result;

	}

	public void saveDevice(ConfigBean bean) {
		//TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
		try {
			TransferUtil.registerClass(ConfigBean.class);
			
			GenericDAO<ConfigBean> genericDAO = new GenericDaoImpl<ConfigBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			//ts.beginTransaction();
			if (genericDAO.isEntityExists(bean)) {	
				genericDAO.deleteEntity(bean);
			}
		    genericDAO.createEntity(bean);
			//ts.commitTransaction();
		} catch (Exception e) {
			logger.error(e);
			//ts.rollbackTransaction();
		}

	}
}
