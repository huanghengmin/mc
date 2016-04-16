package com.inetec.ichange.mc.pojo;

import com.avdheshyadav.p4j.jdbc.service.GenericDAO;
import com.avdheshyadav.p4j.jdbc.service.GenericDaoImpl;
import com.avdheshyadav.p4j.jdbc.transfer.TransferUtil;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.utils.DaoService;
import org.apache.log4j.Logger;

import java.util.List;

public class DeviceDao {
    private static Logger logger=Logger.getLogger(DaoService.class);
    public DeviceDao() {

    }

    public Pagination<DeviceBean> listDevice(int limit, int start) {
        Pagination<DeviceBean> result=null;
        try {
            TransferUtil.registerClass(DeviceBean.class);

            GenericDAO<DeviceBean> genericDAO = new GenericDaoImpl<DeviceBean>(
                    DaoService.getDaoService().getDataProvider()
                            .getDataFetcher());
            int countrows=genericDAO.countRows(DeviceBean.class, "1=1", "1=1");
            result=new Pagination(genericDAO.findAll(DeviceBean.class, start, limit),countrows);

        } catch (Exception e) {
            logger.error(e);
        }
        return result;

    }

    public void saveDevice(DeviceBean bean) {
        //TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
        try {
            //   ts.beginTransaction();
            TransferUtil.registerClass(DeviceBean.class);

            GenericDAO<DeviceBean> genericDAO = new GenericDaoImpl<DeviceBean>(
                    DaoService.getDaoService().getDataProvider()
                            .getDataFetcher());

            if (genericDAO.isEntityExists(bean)) {

                genericDAO.saveOrUpdate(bean);
            }else{
                genericDAO.createEntity(bean);
            }
            //ts.commitTransaction();
        } catch (Exception e) {
            logger.error(e);
            //.rollbackTransaction();
        }

    }
    public void delDevice(DeviceBean bean) {
        //TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
        try {
            TransferUtil.registerClass(DeviceBean.class);
            GenericDAO<DeviceBean> genericDAO = new GenericDaoImpl<DeviceBean>(
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

    public boolean findDeviceByIp(String ip) {
//        TransactionService ts = DaoService.getDaoService().getDataProvider().getTransactionService();
        try {
            TransferUtil.registerClass(DeviceBean.class);
            GenericDAO<DeviceBean> genericDAO = new GenericDaoImpl<DeviceBean>(
                    DaoService.getDaoService().getDataProvider()
                            .getDataFetcher());
//            ts.beginTransaction();
//            String sql = "select * from device where deviceip = '"+ip+"'";
//            List<DeviceBean> list = genericDAO.findByQuery(DeviceBean.class,sql,1,1);
//			if(list.size()>0){
//                return true;
//            }
            List<DeviceBean> list = genericDAO.findAll(DeviceBean.class,1,100000);
//            logger.info("---------------------------------------------------"+list.size());
            for (DeviceBean bean :list){
//                logger.info("____________________________________"+bean.getDeviceip());
                if(bean.getDeviceip().equals(ip)){
                    return true;
                }
            }
//            ts.commitTransaction();
        } catch (Exception e) {
            logger.error(e);
//            ts.rollbackTransaction();
        }
        return false;
    }

}
