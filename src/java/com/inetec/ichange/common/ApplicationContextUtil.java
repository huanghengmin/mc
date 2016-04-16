/**
 * ������ 2007-1-26
 */
package com.inetec.ichange.common;

import org.springframework.context.ApplicationContext;

/**
 * Spring Ӧ�������Ĺ����ࡣ�ṩ Spring Ӧ�������ĵ���
 *
 */
public abstract class ApplicationContextUtil {

    /** Spring Ӧ�������ĵ��� */
    private static ApplicationContext ctx;

    /**
     * ���� Spring Ӧ�������ĵ���
     * @param context Spring Ӧ��������
     */
    public static void setContext(ApplicationContext context) {
        ctx = context;
    }

    /**
     * ��ȡ Spring Ӧ�������ĵ���
     * @return Spring Ӧ��������
     */
    public static ApplicationContext getContext() {
        return ctx;
    }

    /**
     * ��Ӧ����������ȡ�� Bean ʵ��
     * @param beanName Bean ʵ����
     * @return Bean ʵ��
     */
    public static Object getBean(String beanName) {
        if (ctx == null) {
            return null;
        }

        return ctx.getBean(beanName);
    }
}
