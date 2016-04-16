package com.inetec.ichange.mc.action;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.pojo.ConfigBean;
import com.inetec.ichange.mc.pojo.DeviceBean;

public class LoadConfigAction extends IAction {
	static Logger logger = Logger.getLogger(LoadConfigAction.class);

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
			throws Ex {
		String start = "1";
		String limit = "1";
		logger.info("start: " + start);
		logger.info("limit: " + limit);
		// DetachedCriteria dCriteria =
		// DetachedCriteria.forClass(DeviceBean.class);
		// dCriteria.addOrder(Order.desc(IPlatConstant.PK_ID));
		Pagination<ConfigBean> paginatin = configDAO.getConfig();
		if (paginatin.getItems() != null) {
			List<ConfigBean> resources = paginatin.getItems();

			JSONArray json = JSONArray.fromObject(resources);

			logger.info("{\"totalCount\":" + paginatin.getTotalCount()
					+ ",\"configs\": " + json.toString() + "}");
			try {
				response.getWriter().write(
						"{\"totalCount\":" + paginatin.getTotalCount()
								+ ",\"configs\": " + json.toString() + "}");
			} catch (IOException e) {
				throw new Ex().set(E.E_IOException, e);
			}
		}

	}

}
