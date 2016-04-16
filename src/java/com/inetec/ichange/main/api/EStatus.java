package com.inetec.ichange.main.api;

import com.inetec.common.i18n.Key;


public class EStatus {
    public static final int I_OK = 0;
    public static final Key K_OK = new Key("OKEY.");
    public static final EStatus E_OK = new EStatus(K_OK, I_OK);

    /**
     * 不知道的错误编码
     */
    public static final int I_UNKNOWN = -1;
    public static final Key K_UNKNOWN = new Key("UNKNOWN");
    public static final EStatus E_UNKNOWN = new EStatus(K_UNKNOWN, I_UNKNOWN);

    /**
     * 不知道的错误编码
     */
    public static final int I_CONFIGOK = 2;
    public static final Key K_CONFIGOK = new Key("CONFIGOK");
    public static final EStatus E_CONFIGOK = new EStatus(K_CONFIGOK, I_CONFIGOK);
    /**
     * 数据库错误编码
     */
    public static final int I_DATABASEERROR = -2;
    public static final Key K_DATABASEERROR = new Key("Database Error.");
    public static final EStatus E_DATABASEERROR = new EStatus(K_DATABASEERROR, I_DATABASEERROR);
    /**
     * 数据库连接错误编码
     */
    public static final int I_DATABASECONNECTIONERROR = -3;
    public static final Key K_DATABASECONNECTIONERROR = new Key("Database Connector Error.");
    public static final EStatus E_DATABASECONNECTIONERROR = new EStatus(K_DATABASECONNECTIONERROR, I_DATABASECONNECTIONERROR);

    // General errors: 10010
    /**
     * 字符串为空的错误编码。
     */
    public static final int I_Data_FormatError = -10011;
    public static final Key K_Data_FormatError = new Key("Data Format Error.");
    public static final EStatus E_Data_FormatError = new EStatus(K_Data_FormatError, I_Data_FormatError);
    /**
     * 索引超界的错误编码。
     */
    public static final int I_NetWorkError = -10012;
    public static final Key K_NetWorkError = new Key("Network Error.");
    public static final EStatus E_NetWorkError = new EStatus(K_NetWorkError, I_NetWorkError);

    /**
     * 目标输出处理失败.
     */
    public static final int I_DbChangeTargetProcessFaild = -10013;
    public static final Key K_DbChangeTargetProcessFaild = new Key("DbChangeTarget Process Faild.");
    public static final EStatus E_DbChangeTargetProcessFaild = new EStatus(K_DbChangeTargetProcessFaild, I_DbChangeTargetProcessFaild);

    /**
     * 源端读取数据失败.
     */
    public static final int I_DbChangeSourceProcessFaild = -10014;
    public static final Key K_DbChangeSourceProcessFaild = new Key("DbChangeSource Process Faild.");
    public static final EStatus E_DbChangeSourceProcessFaild = new EStatus(K_DbChangeSourceProcessFaild, I_DbChangeSourceProcessFaild);
    /**
     * 临时表中有无效数据.
     */
    public static final int I_TempDataInvalid = -10015;
    public static final Key K_TempDataInvalid = new Key("Temp table record is Invalid.");
    public static final EStatus E_TempDataInvalid = new EStatus(K_TempDataInvalid, I_TempDataInvalid);


    /**
     * 组装数据失败
     */
    public static final int I_PackDataFaild = -10017;
    public static final Key K_PackDataFaild = new Key("Pack data faild.");
    public static final EStatus E_PackDataFaild = new EStatus(K_PackDataFaild, I_PackDataFaild);

    /**
     * 后处理失败.
     */
    public static final int I_RecordProcessFaild = -10018;
    public static final Key K_RecordProcessFaild = new Key("Record process faild.");
    public static final EStatus E_RecordProcessFaild = new EStatus(K_RecordProcessFaild, I_RecordProcessFaild);

    /**
     * 导出数据失败
     */
    public static final int I_ExportDataFaild = -10016;
    public static final Key K_ExportDataFaild = new Key("Export data faild.");
    public static final EStatus E_ExportDataFaild = new EStatus(K_ExportDataFaild, I_ExportDataFaild);
    // NodeInfo: 10020


    /**
     * 无效数据
     */
    public static final int I_InvalidData = 10016;
    public static final Key K_InvalidData = new Key("Invalid data.");
    public static final EStatus E_InvalidData = new EStatus(K_InvalidData, I_InvalidData);


    /**
     * 无效的目标字段
     */
    public static final int I_InvalidTargetField = -10019;
    public static final Key K_InvalidTargetField = new Key("Invalid target field.");
    public static final EStatus E_InvalidTargetField = new EStatus(K_InvalidTargetField, I_InvalidTargetField);


    /**
     * 已经配置过的错误编码。
     */
    public static final int I_AlreadyConfigured = -10035;
    public static final Key K_AlreadyConfigured = new Key("Already Configured.");
    public static final EStatus E_AlreadyConfigured = new EStatus(K_AlreadyConfigured, I_AlreadyConfigured);
    /**
     * 配置不成功的错误编码。
     */
    public static final int I_CF_Failed = -10036;
    public static final Key K_CF_Failed = new Key("Config faild.");
    public static final EStatus E_CF_Faild = new EStatus(K_CF_Failed, I_CF_Failed);

    /**
     * 配置变量无效的错误编码。
     */
    public static final int I_CF_VariableNotFound = -10037;
    public static final Key K_CF_VariableNotFound = new Key("Config Variable Not Found");
    public static final EStatus E_CF_VariableNotFound = new EStatus(K_CF_VariableNotFound, I_CF_VariableNotFound);
    /**
     * 配置数据为空的错误编码。
     */
    public static final int I_CF_NullConfigData = -10038;
    public static final Key K_CF_NullConfigData = new Key("Config data is null.");
    public static final EStatus E_CF_NullConfigData = new EStatus(K_CF_NullConfigData, I_CF_NullConfigData);

    /*
    * 没有配置过的错误编码
    */
    public static final int I_CF_NotConfigured = -10040;
    public static final Key K_CF_NotConfigured = new Key("Not Configured.");
    public static final EStatus E_CF_NotConfigured = new EStatus(K_CF_NotConfigured, I_CF_NotConfigured);

    /**
     * 没有运行。
     */
    public static final int I_NotRun = -10041;
    public static final Key K_NotRun = new Key("Not Run.");
    public static final EStatus E_NotRun = new EStatus(K_NotRun, I_NotRun);

    private int m_code;

    private Key m_key;
    private String m_dbName;
    private String m_desc;

    private EStatus(Key key, int code) {
        m_key = key;
        m_code = code;
    }

    public int getCode() {
        return m_code;
    }

    public Key getKey() {
        return m_key;
    }

    public String getDbName() {
        return m_dbName;

    }

    public String getDbDesc() {
        return m_desc;
    }

    public void setDbInfo(String dbName, String desc) {
        m_dbName = dbName;
        m_desc = desc;
    }
}
