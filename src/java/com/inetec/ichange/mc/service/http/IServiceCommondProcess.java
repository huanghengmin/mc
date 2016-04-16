package com.inetec.ichange.mc.service.http;

import com.inetec.common.exception.Ex;
import com.inetec.ichange.main.api.DataAttributes;

import java.io.InputStream;

/**
 * ���ø澯����
 * Created by IntelliJ IDEA.
 * User: wxh
 * Date: 2005-8-16
 * Time: 21:22:54
 * To change this template use File | Settings | File Templates.
 */
public interface IServiceCommondProcess {
    /**
     * @param input
     * @param dataAttributes
     * @throws Ex
     */
    public DataAttributes process(InputStream input, DataAttributes dataAttributes) throws Ex;

    /**
     * @param input
     * @throws Ex
     */
    public DataAttributes process(InputStream input) throws Ex;

    /**
     * @param fileName
     * @param dataAttributes
     * @throws Ex
     */
    public DataAttributes process(String fileName, DataAttributes dataAttributes) throws Ex;

    /**
     * @param fileName
     * @throws Ex
     */
    public DataAttributes process(String fileName) throws Ex;

    public static int I_FileProcess = 0;
    public static int I_StreamProcess = 1;

    public int getProcessgetCapabilitie();
}
