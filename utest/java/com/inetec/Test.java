package com.inetec;

import com.inetec.common.exception.Ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-12
 * Time: ????12:12
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws Ex {

        String strDate = "2012-10-31 17:52:10.111";
        String pat1 = "yyyy-MM-dd HH:mm:ss";
        String pat2 = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒";
        SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);
        SimpleDateFormat sdf2 = new SimpleDateFormat(pat2);
        Date d = null;
        try {
            d = sdf1.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(sdf1.format(d));

    }

}
