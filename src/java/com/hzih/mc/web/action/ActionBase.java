package com.hzih.mc.web.action;

import com.hzih.mc.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ActionBase {

    private static final long serialVersionUID = -3517772370262338399L;

    public String actionBegin(HttpServletRequest request){
        request.getCharacterEncoding();
//		String result = new CheckTimeResult().getResult(request);
        String result = null;
        return result;
    }

    public void actionEnd(HttpServletResponse response,String json,String result) throws IOException {
        response.setCharacterEncoding("utf-8");
        StringUtils st = new StringUtils();
        response.setContentType("text/html");//上传文件回调函数处理时需要
//		response.getWriter().print(result);

        response.getWriter().write(st.trim(json));
        response.getWriter().close();
    }
}
