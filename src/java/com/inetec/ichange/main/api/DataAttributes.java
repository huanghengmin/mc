package com.inetec.ichange.main.api;


import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Key;
import com.inetec.common.io.MergeInputStream;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * 属性封装类。 定义了一些常用的属性的关建字。
 *
 * @author Inetec System
 * @version 1.0
 */
public class DataAttributes extends Properties {
    /**
     * 采集类型关建字常量。
     */
    public static final String Str_ChangeType = "CHANGETYPE";
    /**
     * 会话ID关建字常量。
     */
    public static final String Str_SessionId = "SESSIONID";

    /**
     * 文件名关建字常量。
     */
    public static final String Str_FileName = "FILENAME";
    /**
     * 文件大小关建字常量。
     */
    public static final String Str_FileSize = "FILESIZE";
    /**
     * 控制类型关建字常量。
     */
    public static final String Str_ControlType = "CONTROLTYPE";
    /**
     * 压缩关建字常量
     */
    public static final String Str_Compression = "COMPRESSION";
    /**
     * 状态编码关建字常量
     */
    public static final String Str_Status = "status";
    /**
     * 状态信息关建字常量
     */
    public static final String Str_StatusMsg = "statusmsg";
    /**
     * 应用名关建字常量
     */
    public static final String Str_AppName = "typeName";
    /**
     * 错误信息关建字常量
     */
    public static final String Str_Error_Message = "ErrorMessage";
    /**
     * 错误编码关建字常量
     */
    public static final String Str_Error_Code = "ErrorCode";
    /**
     * 存放处理结果数据的成员。
     */
    private ByteArrayInputStream m_inputStream = null;
    private Object m_ob = null;

    /**
     * 类构造器。
     */
    public DataAttributes() {
    }

    /**
     * 类构造器。用户要传入一个Properties对象。
     *
     * @param tfp 属性封装对象。
     */
    public DataAttributes(Properties tfp) {
        super(tfp);
    }

    /**
     * 取得对应关键字的值。
     *
     * @param key 关建字（字符串对象）。
     * @return 对应关建字的值（字符串对象）
     */
    public String getValue(String key) {
        return super.getProperty(key);
    }

    /**
     * 设置相应的关建字与值。
     *
     * @param key   关建字（字符串对象）
     * @param value 对应关建字的值（字符串对象）
     */
    public void putValue(String key, String value) {
        if (value == null) {
            remove(key);
        } else {
            super.put(key, value);
        }
    }

    /**
     * 取得相应的状态（Status）对象
     *
     * @return Status 对象.
     */
    public Status getStatus() {
        String statuscode = (String) get(Str_Status);
        if (statuscode == null || statuscode.equals("")) {
            statuscode = "-1";
        }
        int stat = Integer.parseInt(statuscode);
        String msg = (String) get(Str_StatusMsg);
        return Status.query(stat, new Key(msg));
    }

    /**
     * 取得相应的出错的详细信息。
     *
     * @return
     */
    public String getErrorMessage() {
        return getValue(Str_Error_Message);
    }

    /**
     * 设置相应的错误信息。
     *
     * @param msg
     */
    public void setErrorMessage(String msg) {
        putValue(Str_Error_Message, msg);
    }

    /**
     * 设置相应的状态对象。
     *
     * @param status
     */
    public void setStatus(Status status) {
        put(Str_Status, status.getStatusCode() + "");
        put(Str_StatusMsg, status.getMessage());
    }

    /**
     * 设置对象
     *
     * @param obj 要设置的对象。（Object对象）
     */
    public void setObject(Object obj) {
        m_ob = obj;
    }

    /**
     * 取得设置过对象。
     *
     * @return 设置的对象。(Object对象)
     */
    public Object getObject() {
        return m_ob;
    }

    /**
     * 设置处理结果数据;ByteArrayInputStream
     *
     * @param is 要返回的结果数据     ByteArrayInputStream  的对象
     */
    public void setResultData(ByteArrayInputStream is) {
        if (is != null)
            m_inputStream = is;
    }

    public boolean isResultData() {
        if (m_inputStream != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置处理结果数据;
     *
     * @param data 要返回的结果数据:byte 数组
     */
    public void setResultData(byte[] data) {
        if (data != null)
            m_inputStream = new ByteArrayInputStream(data);
    }

    /**
     * 取得返回结果数据
     *
     * @return 处理结果数据(ByteArrayInputStream)
     * @throws Ex 如不能正确返回 ，则返回X异常。
     */
    public ByteArrayInputStream getResultData() throws Ex {
        return m_inputStream;
    }

    /**
     * 重载基类的load方法，完成从一个对象流中实例一个DataAttributes对象。
     *
     * @param in 对象数据流.
     * @throws IOException 如果读数据流不成功,返回IOException.
     */
    public synchronized void load(InputStream in) throws IOException {
        if (in == null)
            return;
        HeaderProcess headProcess = new HeaderProcess();
        clear();
        try {
            Properties temp = headProcess.parseHeader(in);
            Set keySet = temp.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                String value = (String) temp.get(name);
                putValue(name, value);
            }
        } catch (Ex ex) {
            throw new IOException(ex.getMessage());
        }
        m_inputStream = new ByteArrayInputStream(readInputStream(in));
    }

    /**
     * 重载基类的store方法. 完成从把当前对象序列到输出数据流中。
     *
     * @param os 输出数据流.
     * @param s  对象流的头部信息.
     * @throws IOException 如果写数据流不成功,返回IOException.
     */
    public synchronized void store(OutputStream os, String s) throws IOException {
        if (os == null)
            return;
        DataAttributes fp2 = new DataAttributes();
        Set keySet = keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String value = (String) get(name);
            fp2.setProperty(name, value);
        }
        HeaderProcess headProcess = new HeaderProcess(fp2);
        MergeInputStream in = new MergeInputStream();
        in.addInputStream(headProcess.getInputStream());
        if (s != null)
            os.write(s.getBytes());
        if (m_inputStream != null) {
            in.addInputStream(m_inputStream);
        }
        os.write(readInputStream(in));
        in.close();
        os.close();
    }

    /**
     * 取的当前对象的序列化后的byte数据,
     *
     * @return 序列化后的数据, (二进制数组)
     * @throws IOException 如果序列化不成功,返回IOException.
     */
    public byte[] getContent() throws IOException {
        DataAttributes fp2 = new DataAttributes();
        Set keySet = keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String value = (String) get(name);
            fp2.setProperty(name, value);
        }
        HeaderProcess headProcess = new HeaderProcess(fp2);
        MergeInputStream in = new MergeInputStream();
        in.addInputStream(headProcess.getInputStream());
        if (m_inputStream != null) {
            in.addInputStream(m_inputStream);
        }
        return readInputStream(in);
    }

    /**
     * 公共方法，完成的功能是读取一个数据流数据。返回二进制数组。
     *
     * @param isReceive 要读取的数据流。
     * @return 数据流对应的二进制数组。
     * @throws IOException 如果读取不 成功，返回IOException异常。
     */
    public static byte[] readInputStream(InputStream isReceive) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 1024];   //buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = isReceive.read(buff)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果
        return in_b;
    }
}
