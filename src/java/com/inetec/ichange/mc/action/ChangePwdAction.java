package com.inetec.ichange.mc.action;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.mc.IPlatConstant;
import com.inetec.ichange.mc.utils.TomcatHelp;

import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-11-14
 * Time: 14:16:03
 */
public class ChangePwdAction extends IAction {
    static Logger logger = Logger.getLogger(ChangePwdAction.class);

    public void execute(HttpServletRequest request, HttpServletResponse response) throws Ex {
        String userName = request.getRemoteUser();
        String oldPwd = request.getParameter(IPlatConstant.OLDPWD);
        String newPwd = request.getParameter(IPlatConstant.NEWPWD);
        
        logger.info("userName: " + userName);
        logger.info("oldPwd: " + oldPwd);
        logger.info("newPwd: " + newPwd);
        try {
            TomcatHelp.changeUserPwd(userName, oldPwd, newPwd);
//            request.setAttribute(IAuditConstant.CHANGE_PWD_MSG, "<b>?????????.</b>");

//            //res.getWriter().write("<html><body onload='javascript:window.close()'><table width='100%' height='100%'><tr><td align='center' valign='middle'><table><tr><td>?????????</td><tr></tr></table></td></tr></table></body></html>");
//            RequestDispatcher dis = request.getRequestDispatcher("/manage/changepwd.jsp");
//            dis.forward(request, response);
            response.getWriter().write("?????????.");
            //res.sendRedirect("manage/changepwd.jsp");
//            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Ex x) {
//            request.setAttribute(IAuditConstant.CHANGE_PWD_MSG, "<b>" + x.getMsg().toString() + "</b>");
//            RequestDispatcher dis = request.getRequestDispatcher("/changepwd.jsp");
//            dis.forward(request, response);
            try {
				response.getWriter().write(x.getMsg().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
