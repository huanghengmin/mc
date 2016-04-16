package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-8-6
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class KeywordsFilterRTF implements KeywordsFilterUtil{
    private final static String CODE = "ISO-8859-1" ;
    private final static String CHARACTER_CODE = "GBK";
    private final static String CHARACTER = "0123456789abcdef" ;
    private static final Logger logger = Logger.getLogger(KeywordsFilterRTF.class);
   
    public String strToRtf(String content) throws Exception{
        char[] digital = this.CHARACTER.toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = content.getBytes(CHARACTER_CODE);
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append("\\'");
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }
    public byte[] filter(byte[] data,String keywords,SourceFile sourceFile) throws Exception{
        if(keywords == null || keywords.equals("")){
            return data ;
        }
        String dataStr = new String(data);
        if(! keywords.contains(",")){
            dataStr = dataStr.replace(strToRtf(keywords),getString(keywords));
        }
        else {
            String[] keyword = keywords.split(",");
            for(int i = 0 ; i < keyword.length ; i++){
                dataStr = dataStr.replace(strToRtf(keyword[i]),getString(keyword[i]));
            }
        }
        return dataStr.getBytes(CODE);
    }
    private String getString(String keyword){
        String data = "";
        for(int i = 0 ; i < keyword.length() ; i ++){
            try {
                data = data + strToRtf("**");
            } catch (Exception e) {
                logger.error("rtf替换关键词为*",e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return data ;
    }
    @Override
    public InputStream filter(InputStream inputStream, String keywords,SourceFile sourceFile) throws Exception {
        byte[] data = readInputStream(inputStream);
        byte[] bytes = this.filter(data,keywords,sourceFile);
        return new ByteArrayInputStream(bytes);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[] readInputStream (InputStream inputStream){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024*1024];
        int c = 0;
        try {
            while((c = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes,0,c);
            }
        } catch (IOException e) {
            logger.error("流转换成byte[]",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return byteArrayOutputStream.toByteArray();
    }
}
