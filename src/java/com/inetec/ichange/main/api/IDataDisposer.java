package com.inetec.ichange.main.api;


import com.inetec.common.exception.Ex;

import java.io.IOException;
import java.io.InputStream;


public interface IDataDisposer {
    public static final String Str_FilePrix = "@ChangeData@";

    public DataAttributes disposeFile(InputStream is, DataAttributes props, java.lang.String url)
            throws Ex, IOException;

    public DataAttributes disposeFile(String filename, DataAttributes props, java.lang.String url)
            throws Ex, IOException;

    public DataAttributes disposeControl(String command, DataAttributes fp, String url) throws Ex;

    public String writeFile(InputStream is, DataAttributes props, boolean bFinal) throws Ex;
}
