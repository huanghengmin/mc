package com.inetec.ichange.mc.service;

import org.apache.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: ?????? Date: 2005-9-7 Time: 23:39:14
 */
public class SetCharsetFilter implements Filter {
    static Logger logger = Logger.getLogger(SetCharsetFilter.class);

    // ???????????web.xml??????
    protected String encoding = null;

    protected FilterConfig filterConfig = null;

    public void destroy() {
        this.encoding = null;
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (request.getCharacterEncoding() == null) {
            logger.info("????HttpRequest.");
            // ????request?????
            request.setCharacterEncoding(encoding);
            // ????response?????
            //response.setCharacterEncoding(encoding);
            logger.info("????????--" + request.getCharacterEncoding());
        }
        // ?л??????????filter
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        // ?????web.xml??????????
        this.encoding = filterConfig.getInitParameter("encoding");
        // ???????????????????
        if (this.encoding == null)
            encoding = "utf-8";
    }

    protected String getEncoding() {
        // ???????????
        return (this.encoding);
    }

}
