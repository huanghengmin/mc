package com.inetec.ichange.mc.action;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.pojo.DeviceBean;
import com.inetec.ichange.mc.pojo.SnmpOIDBean;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ListSnmpOIDAction extends IAction {
	static Logger logger = Logger.getLogger(ListSnmpOIDAction.class);
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String start = request.getParameter(IPlatConstant.PAGINATION_START);
		String limit = request.getParameter(IPlatConstant.PAGINATION_LIMIT);
		logger.info("start: "+start);
		logger.info("limit: "+limit);
		//DetachedCriteria dCriteria = DetachedCriteria.forClass(DeviceBean.class);
		//dCriteria.addOrder(Order.desc(IPlatConstant.PK_ID));
		if(start==null&&limit==null){
			start="0";
			limit="3000";
		}
		Pagination<SnmpOIDBean> paginatin =  snmpoidDAO.listSnmpOID(Integer.parseInt(limit), Integer.parseInt(start));
	
		List<SnmpOIDBean> resources = paginatin.getItems();

		JSONArray json = JSONArray.fromObject(resources);
		
		logger.info("{\"totalCount\":" + paginatin.getTotalCount() + ",\"snmpoids\": " + json.toString() + "}");
		try {
			response.getWriter().write("{\"totalCount\":" + paginatin.getTotalCount() + ",\"snmpoids\": " + json.toString() + "}");
		} catch (IOException e) {
			throw new Ex().set(E.E_IOException, e);
		}

	}

}
