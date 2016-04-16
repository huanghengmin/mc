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
 * Date: 12-7-30
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
public class KeyWordsFilter  implements  KeywordsFilterUtil{
    private  String CHARSET ;
    private static final Logger logger = Logger.getLogger(KeyWordsFilter.class);
    public byte[] filter(byte[] data,String keyWords,SourceFile sourceFile) throws Exception {
        CHARSET = sourceFile.getCharset();
        int size = data.length;
        if(keyWords == null || keyWords.equals("")){
            return data;
        }
        String dataStr = new String(data,CHARSET);
        if(keyWords.contains(",")){
            String[] keyWord = keyWords.split(",");
            for(int i = 0 ; i < keyWord.length ; i ++ ){
                String str = replace(keyWord[i]);
                dataStr = dataStr.replaceAll(keyWord[i],str);
            }
        }
        else {
            String str = replace(keyWords);
            dataStr = dataStr.replaceAll(keyWords,str);
        }
        int size1 = dataStr.getBytes(CHARSET).length;
        if(size != size1){
            dataStr = dataStr + " ";
        }
        int size2 = dataStr.getBytes(CHARSET).length;
        if(size == size2){
            return dataStr.getBytes(CHARSET);
        }
        else {
            Exception exception = new Exception("文件编码格式不匹配,该文件不是"+CHARSET+"请尝试换其他编码方式");
            throw exception;
        }
    }
    public InputStream filter(InputStream in,String keywords,SourceFile sourceFile) throws Exception{
        byte[] data = readInputStream(in);
        byte[] bytes = this.filter(data,keywords,sourceFile);
        return new ByteArrayInputStream(bytes);
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
            logger.error("过滤txt",e);  //To change body of catch statement use File | Settings | File Templates.
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String replace(String keyword){
        if(distinguishString(keyword)){
            String str = "*";
            for(int i = 1 ; i < keyword.length() ; i++){
                str = str+"*";
            }
            return  str ;

        }
        else {
            String str = "**";
            for(int i = 1 ; i < keyword.length() ; i++){
                str = str+"**";
            }
            return  str ;
        }
    }
    private boolean distinguishString(String string){
        byte[] bytes = string.getBytes();
        for(int i = 0 ; i < bytes.length ; i++){
            if(bytes[i]<0){
                return false;
            }
        }
        return true;
    }
}
