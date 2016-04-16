package com.fartec.ichange.plugin.filechange.utils;

import com.inetec.common.config.nodes.SourceFile;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-8-9
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
public interface KeywordsFilterUtil {

    public byte[] filter(byte[] data,String keywords,SourceFile sourceFile) throws Exception;

    public InputStream filter(InputStream inputStream,String keywords,SourceFile sourceFile) throws Exception;

}
