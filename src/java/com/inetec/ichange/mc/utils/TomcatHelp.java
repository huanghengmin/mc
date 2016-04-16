package com.inetec.ichange.mc.utils;

import com.inetec.common.exception.E;
import com.inetec.common.exception.Ex;
import com.inetec.common.i18n.Message;
import org.apache.catalina.User;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.log4j.Logger;

import javax.management.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-11-1
 * Time: 22:50:21
 */
public class TomcatHelp {
    static Logger logger = Logger.getLogger(TomcatHelp.class);

    public static void changeUserPwd(String userName, String oldPwd, String newPwd) throws Ex {
        String jmxName = "Users:type=User,username=\"" + userName + "\",database=UserDatabase";

        MemoryUserDatabase userDb = new MemoryUserDatabase();
        try {
            userDb.open();
            User user = userDb.findUser(userName);
            if (user == null)
                throw new Ex().set(E.E_ObjectNotFound, new Message("??? {0} ??????.", userName));
            if (!oldPwd.equals(user.getPassword())) {
                throw new Ex().set(new Message("????????."));
            }
            user.setPassword(newPwd);
            userDb.save();
            userDb.close();

            MBeanServer mBeanServer;

            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                mBeanServer = (MBeanServer) MBeanServerFactory
                        .findMBeanServer(null).get(0);
            } else {
                throw new Ex().set(new Message("??????????????��?MBean."));
            }

            ObjectName userObject = getObjectName(jmxName, mBeanServer);

            if (userObject != null) {
                Attribute pwd = new Attribute("password", newPwd);
                mBeanServer.setAttribute(userObject, pwd);
            }

        } catch (Exception e) {
            throw new Ex().set(e);
        }
    }

    public static ObjectName getObjectName(String jmxName, MBeanServer mBeanServer) throws Ex {

        Set apps = new HashSet(0);
        try {
            apps = mBeanServer
                    .queryNames(
                            new ObjectName(jmxName),
                            null);
        } catch (MalformedObjectNameException e) {
            throw new Ex().set(e);
        } catch (NullPointerException e) {
            throw new Ex().set(e);
        }

        ObjectName appname = null;
        Iterator appit = apps.iterator();
        if (appit.hasNext()) {
            appname = (ObjectName) appit.next();
            logger.info("JNDI MBean ???.");
        }
        return appname;
    }
}
