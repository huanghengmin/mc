package com.inetec.ichange.mc.action;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.pojo.DeviceBean;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ListDeviceAction extends IAction {
	static Logger logger = Logger.getLogger(ListDeviceAction.class);
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String start = request.getParameter(IPlatConstant.PAGINATION_START);
		String limit = request.getParameter(IPlatConstant.PAGINATION_LIMIT);
		logger.info("start: "+start);
		logger.info("limit: "+limit);
		//DetachedCriteria dCriteria = DetachedCriteria.forClass(DeviceBean.class);
		//dCriteria.addOrder(Order.desc(IPlatConstant.PK_ID));
		Pagination<DeviceBean> paginatin =  deviceDAO.listDevice(Integer.parseInt(limit), Integer.parseInt(start));
	
		List<DeviceBean> resources = paginatin.getItems();

		JSONArray json = JSONArray.fromObject(resources);
		
		logger.info("{\"totalCount\":" + paginatin.getTotalCount() + ",\"devices\": " + json.toString() + "}");
		try {
			response.getWriter().write("{\"totalCount\":" + paginatin.getTotalCount() + ",\"devices\": " + json.toString() + "}");
		} catch (IOException e) {
			throw new Ex().set(E.E_IOException, e);
		}

	}

}
