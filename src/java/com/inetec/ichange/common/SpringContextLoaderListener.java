/**
 * ?????? 2007-1-26
 */
package com.inetec.ichange.common;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;


/**
 * @author zwl
 */
public class SpringContextLoaderListener extends ContextLoaderListener {



    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ApplicationContextUtil.setContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));


    }


    @Override
    public void contextDestroyed(ServletContextEvent event) {
        super.contextDestroyed(event);
        ApplicationContextUtil.setContext(null);
    }

}
