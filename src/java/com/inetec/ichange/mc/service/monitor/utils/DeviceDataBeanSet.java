package com.inetec.ichange.mc.service.monitor.utils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.service.monitor.databean.DeviceDataBean;



/**
 * Created by IntelliJ IDEA. User: bluesky Date: 2010-9-5 Time: 13:20:27 To
 * change this template use File | Settings | File Templates.
 */
public class DeviceDataBeanSet {
	public ConcurrentHashMap beanset = new ConcurrentHashMap();
	public ConcurrentHashMap ipbeanset = new ConcurrentHashMap();

	public void init(List<DeviceBean> list) {

		for (int i = 0; i < list.size(); i++) {
			beanset.put(list.get(i).getId(), initBeanByEquipment(list.get(i)));
			ipbeanset.put(list.get(i).getDeviceip(), list.get(i).getId());
		}
	}

	public DeviceDataBean getDeviceDataBeanByID(String id) {

		if (beanset.containsKey(id)) {
			DeviceDataBean bean1 = (DeviceDataBean) beanset.get(id);
			beanset.put(id, bean1);
			if (bean1.getStatus() == DeviceDataBean.I_Status_Error) {
				bean1 = initBeanByEquipment(bean1.getEqu_name());
			}
			return bean1;
		}
		return initBeanByEquipment(id);
	}

	public String getDeviceIDByIP(String ip) {

		if (ipbeanset.containsKey(ip)) {
			String bean1 = (String) ipbeanset.get(ip);

			return bean1;
		}
		return null;
	}

	public void returnDeviceDataBean(String key, DeviceDataBean bean) {
		beanset.put(key, bean);
		//ipbeanset.put(bean.getd)
	}

	protected DeviceDataBean initBeanByEquipment(DeviceBean equ) {
		DeviceDataBean bean = new DeviceDataBean();
		bean.setStatus(DeviceDataBean.I_Status_Error);
		// bean.setEqu_id(Integer.parseInt(getId()));
		bean.setEqu_name(equ.getName());
		bean.setCpu(0);
		bean.setCurrentcon(0);
		bean.setDisk(0);
		bean.setDisk_total(0);
		bean.setMaxcon(0);
		bean.setMem(0);
		bean.setMem_total(0);
		bean.setVpn(0);
		return bean;
	}

	protected DeviceDataBean initBeanByEquipment(String name) {
		DeviceDataBean bean = new DeviceDataBean();
		bean.setStatus(DeviceDataBean.I_Status_Error);
		// bean.setEqu_id(Integer.parseInt(getId()));
		bean.setEqu_name(name);
		bean.setCpu(0);
		bean.setCurrentcon(0);
		bean.setDisk(0);
		bean.setDisk_total(0);
		bean.setMaxcon(0);
		bean.setMem(0);
		bean.setMem_total(0);
		bean.setVpn(0);
		return bean;
	}
}
