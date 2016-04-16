package com.inetec.ichange.mc.service.monitor.utils;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import com.inetec.ichange.mc.service.http.IServiceCommondProcess;

import java.io.IOException;
import java.util.Properties;
public class ServiceMonitorFactory {
    public static Properties props = null;

    public static IServiceCommondProcess createServiceCommon(String common) throws Ex {
        IServiceCommondProcess object = null;
        if (common == null || common == "") {
            throw new Ex().set(E.E_InvalidArgument, new Message("Commond is null."));
        }
        if (props == null) {
            props = new Properties();
            try {
                props.load(ServiceMonitorFactory.class.getResourceAsStream("/monitor.properties"));
            } catch (IOException e) {
                throw new Ex().set(E.E_OperationFailed, e, new Message("{0} monitor.properties not load.", common));
            }
        }
        common = common.toLowerCase();
        String classname = null;
        classname = props.getProperty(common, "");
        if (classname != null && !classname.equalsIgnoreCase("")) {
            //object = (IServiceCommondProcess) m_map.get(STR_ServiceCommonClassName_Audit);
            if (object == null) {
                object = (IServiceCommondProcess) ServiceUtils.newObjectByClass(classname, IServiceCommondProcess.class);
                // m_map.put(STR_ServiceCommonClassName_Audit, object);
            }
        }
        if (object == null) {
            throw new Ex().set(E.E_OperationFailed, new Message("{0} Common is undefined.", common));
        }

        return object;

    }
}
