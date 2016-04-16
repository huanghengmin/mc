/**
 * 创建于 2007-1-26
 */
package com.inetec.ichange.common;

import org.springframework.context.ApplicationContext;

/**
 * Spring 应用上下文工具类。提供 Spring 应用上下文单例
 *
 */
public abstract class ApplicationContextUtil {

    /** Spring 应用上下文单例 */
    private static ApplicationContext ctx;

    /**
     * 设置 Spring 应用上下文单例
     * @param context Spring 应用上下文
     */
    public static void setContext(ApplicationContext context) {
        ctx = context;
    }

    /**
     * 获取 Spring 应用上下文单例
     * @return Spring 应用上下文
     */
    public static ApplicationContext getContext() {
        return ctx;
    }

    /**
     * 从应用上下文中取得 Bean 实例
     * @param beanName Bean 实例名
     * @return Bean 实例
     */
    public static Object getBean(String beanName) {
        if (ctx == null) {
            return null;
        }

        return ctx.getBean(beanName);
    }
}
