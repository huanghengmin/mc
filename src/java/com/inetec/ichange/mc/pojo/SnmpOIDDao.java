package com.inetec.ichange.mc.pojo;

import java.util.List;

import org.apache.log4j.Logger;

import com.avdheshyadav.p4j.jdbc.PersistenceConfig;
import com.avdheshyadav.p4j.jdbc.dbms.DataProviderFactoryImpl;
import com.avdheshyadav.p4j.jdbc.service.DataProvider;
import com.avdheshyadav.p4j.jdbc.service.DataProviderFactory;
import com.avdheshyadav.p4j.jdbc.service.GenericDAO;
import com.avdheshyadav.p4j.jdbc.service.GenericDaoImpl;
import com.avdheshyadav.p4j.jdbc.service.TransactionService;
import com.avdheshyadav.p4j.jdbc.transfer.TransferUtil;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.utils.DaoService;

public class SnmpOIDDao {
	private static Logger logger=Logger.getLogger(SnmpOIDDao.class);
	public SnmpOIDDao() {

	}

	public SnmpOIDBean getSnmpOID(String name) {
		SnmpOIDBean result=null;
		try {
			TransferUtil.registerClass(SnmpOIDBean.class);
			
			GenericDAO<SnmpOIDBean> genericDAO = new GenericDaoImpl<SnmpOIDBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			Object[] names=new Object[1];
			names[0]=name;
            result=genericDAO.findByPrimaryKey(SnmpOIDBean.class,names);
			
		} catch (Exception e) {
			logger.error(e);
		}
		return result;

	}
	public Pagination<SnmpOIDBean> listSnmpOID(int limit, int start) {
		Pagination<SnmpOIDBean> result=null;
		try {
			TransferUtil.registerClass(SnmpOIDBean.class);
			
			GenericDAO<SnmpOIDBean> genericDAO = new GenericDaoImpl<SnmpOIDBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			int countrows=genericDAO.countRows(SnmpOIDBean.class, "1=1", "1=1");
			result=new Pagination(genericDAO.findAll(SnmpOIDBean.class, start, limit),countrows);
			
		} catch (Exception e) {
			logger.error(e);
		}
		return result;

	}
	public void saveDevice(SnmpOIDBean bean) {
		//TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
		try {
			TransferUtil.registerClass(SnmpOIDBean .class);
			
			GenericDAO<SnmpOIDBean> genericDAO = new GenericDaoImpl<SnmpOIDBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			//ts.beginTransaction();
			if (genericDAO.isEntityExists(bean)) {
				
				genericDAO.deleteEntity(bean);
			}
				genericDAO.createEntity(bean); 
			
		   // ts.commitTransaction();	
		} catch (Exception e) {
			logger.error(e);
			//ts.rollbackTransaction();
		}

	}
	public void delSnmpOID(SnmpOIDBean bean) {
		//TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
		try {
			TransferUtil.registerClass(SnmpOIDBean .class);
			
			GenericDAO<SnmpOIDBean> genericDAO = new GenericDaoImpl<SnmpOIDBean>(
					DaoService.getDaoService().getDataProvider()
							.getDataFetcher());
			//ts.beginTransaction();
			if(genericDAO.isEntityExists(bean))
				genericDAO.deleteEntity(bean);
			//ts.commitTransaction();
				
			
				
		} catch (Exception e) {
			logger.error(e);
			//ts.rollbackTransaction();
		}

	}
}
