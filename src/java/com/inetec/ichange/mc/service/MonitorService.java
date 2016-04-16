package com.inetec.ichange.mc.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import com.inetec.ichange.common.ApplicationContextUtil;
import com.inetec.ichange.main.api.DataAttributes;
import com.inetec.ichange.mc.Pagination;
import com.inetec.ichange.mc.pojo.ConfigBean;
import com.inetec.ichange.mc.pojo.ConfigDao;
import com.inetec.ichange.mc.service.http.IServiceCommondProcess;
import com.inetec.ichange.mc.service.monitor.utils.ServiceMonitorFactory;
import com.inetec.ichange.mc.service.monitor.utils.ServiceUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class MonitorService extends HttpServlet {
	public static Category s_log = null;
	private static String cmsip = "";
	private static final long serialVersionUID = -3161681620670465963L;

	/*
	 * Constructor of the object.
	 */
	public MonitorService() {
		super();

	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>Mc Monitor  Service Page</title>");
		writer.println("</head>");
		writer.println("<body bgcolor=white>");
		writer.println("<table border=\"0\">");
		writer.println("<tr>");
		writer.println("<td>");
		writer.println("<h1>Mc Monitor  Status Page</h1>");
		writer.println("<P>Monitor  service is running.<P><BR>");
		writer.println("</td>");
		writer.println("</tr>");
		writer.println("</table>");
		writer.println("</body>");
		writer.println("</html>");

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if (s_log == null)
				s_log = Category.getInstance(MonitorService.class);
			if (true) {
				String reqtype = request
						.getParameter(ServiceUtils.HDR_ServiceRequestType);
				if (reqtype == null) {
	                reqtype = request.getHeader(ServiceUtils.HDR_ServiceRequestType);
	            }
				if (reqtype == null)
					throw new Ex().set(E.E_Unknown, new Message(
							"Service Request  Command is null."));

				if (reqtype
						.equalsIgnoreCase(ServiceUtils.STR_REQTP_ServiceDataPost)) {

					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				} else if (reqtype
						.equalsIgnoreCase(ServiceUtils.STR_REQTP_ServiceControlPost)) {
                           
					String commandBody = request
							.getParameter(ServiceUtils.Str_MonitorCommond);
					if (commandBody == null) {
	                    commandBody = request.getHeader(ServiceUtils.Str_MonitorCommond);
	                }
					if (commandBody == null) {
						commandBody = "";
					}
					// ???????????????
					if (commandBody.equalsIgnoreCase("")) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}

					DataAttributes fp = reciveServiceControl(request);
					byte[] data = null;
					if (fp.getValue("Command") == null) {
						if (fp.getStatus().isSuccess()) {
							String responsecode = fp
									.getProperty(ServiceUtils.Str_ResponseProcessStatus);
							if (responsecode != null) {
								int status = Integer.parseInt(responsecode);
								response.setStatus(status);
								if (fp.getResultData() != null) {
									data = DataAttributes.readInputStream(fp
											.getResultData());
									response.setContentLength(data.length);
									response.getOutputStream().write(data);
								}
							}
						} else {
							response
									.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						}
					} else {
						if (fp.getResultData() != null)
							data = DataAttributes.readInputStream(fp
									.getResultData());
						else
							data = fp.getContent();
						response.setContentLength(data.length);
						response.getOutputStream().write(data);
					}
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);

				}
			} else {
				response.setContentType("text/html");
				PrintWriter writer = response.getWriter();
				writer.println("<html>");
				writer.println("<head>");
				writer.println("<title>Mc Monitor  Service Page</title>");
				writer.println("</head>");
				writer.println("<body bgcolor=white>");
				writer.println("<table border=\"0\">");
				writer.println("<tr>");
				writer.println("<td>");
				writer.println("<h1>Mc Monitor  Status Page</h1>");
				writer
						.println("<P>Http request ip address is not cms address, not access!"
								+ request.getRemoteHost() + "<P><BR>");
				writer.println("</td>");
				writer.println("</tr>");
				writer.println("</table>");
				writer.println("</body>");
				writer.println("</html>");
			}
		} catch (Ex Ex) { // Server error; report to client.

			s_log.info("Could not process the request from " + Ex.getMessage());

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					" - Could not process the request from " + ": "
							+ Ex.getMessage());

			s_log.error("" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR
					+ " - Could not process the request: ", Ex);

		} catch (RuntimeException Ex) {
			s_log.error("Run-time exception is caught:- ", Ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					" - Could not process the request from " + ": "
							+ Ex.getMessage());
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		ConfigDao config = (ConfigDao) ApplicationContextUtil
				.getBean("configDAO");
		Pagination<ConfigBean> paginatin = config.getConfig();
		if (paginatin.getItems() != null && paginatin.getTotalCount() > 0) {
			ConfigBean configBean = (ConfigBean) paginatin.getItems().get(0);
			cmsip = configBean.getCmsip();
		}

	}

	public DataAttributes reciveServiceControl(HttpServletRequest req)
			throws Ex {
		DataAttributes result = new DataAttributes();
		String common = req.getParameter(ServiceUtils.Str_MonitorCommond);
		 if(common==null||common == "")  {
	            common=req.getHeader(ServiceUtils.Str_MonitorCommond);
	        }
		if (common == null || common == "") {
			throw new Ex().set(E.E_ObjectNotFound, new Message(
					"Command is null or empty."));
		}
		IServiceCommondProcess serviceCommonProcess = ServiceMonitorFactory
				.createServiceCommon(common);
		 Enumeration enumeration = req.getParameterNames();
	        while (enumeration.hasMoreElements()) {
	            String hdr = (String) enumeration.nextElement();
	            hdr = hdr.toLowerCase();
	            result.putValue(hdr, req.getParameter(hdr));
	        }
	        Enumeration reqheader = req.getHeaderNames();
	        while (reqheader.hasMoreElements()) {
	            String hdr = (String) reqheader.nextElement();
	            hdr = hdr.toLowerCase();
	            result.putValue(hdr, req.getHeader(hdr));
	        }
		// try {
		result = serviceCommonProcess.process("", result);
		/*
		 * } catch (IOException e) { throw new Ex().set(E.E_IOException, new
		 * Message("Request get Stream error.")); }
		 */
		return result;

	}

}
