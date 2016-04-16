package com.inetec.ichange.mc.action;

import com.inetec.common.exception.Ex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-11-14
 * Time: 15:33:17
 */
public class LogoffAction extends IAction{
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Ex {
        HttpSession session = request.getSession(true);
        Enumeration attrs = session.getAttributeNames();
        while (attrs.hasMoreElements()) {
            session.removeAttribute((String) attrs.nextElement());
        }
        session.invalidate();
        PrintWriter out=null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        out.print("<html>");
        out.print("<body onload=\"javascript:top.location='index.html'\"></body>");
        out.print("</html>");
        out.flush();
        out.close();
    }
}
