package com.inetec.ichange.mc.service.monitor.databean;

import net.sf.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 终端用户信息对象
 *
 * @author wxh
 */

public class SysterminalinfDataBean extends BaseDataBean {
    private static final long IonlineTime = 5 * 60 * 1000;

    private String idTerminal;

    private String cardType;

    private String cardName;
    private String cardModel;

    private String card_version;

    private String userId;

    private String userName;

    private String userDepart;

    private String userZone;
    private String userOrg;
    private String cn;

    private String policeNumber;

    private long in_flux;
    private long out_flux;


    public long getIn_flux() {
        return in_flux;
    }

    public void setIn_flux(long inFlux) {
        in_flux = inFlux;
    }

    public long getOut_flux() {
        return out_flux;
    }

    public void setOut_flux(long outFlux) {
        out_flux = outFlux;
    }

    public String getUserOrg() {
        return userOrg;
    }

    public void setUserOrg(String userOrg) {
        this.userOrg = userOrg;
    }

    public String getPolicecate() {
        return policecate;
    }

    public void setPolicecate(String policecate) {
        this.policecate = policecate;
    }

    private String policecate;

    private String regTime;

    private boolean ifcancel = false;

    private String flag;
    private long lastDate;
    private String ip = "0.0.0.0";
    private String onlineTime;

    public SysterminalinfDataBean() {
        status = 0;

    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getOnlineTime() {
        long temp = System.currentTimeMillis()
                - Timestamp.valueOf(regTime).getTime();
        Time time2 = new Time(temp);
        onlineTime = time2.getHours() + ":" + time2.getMinutes() + ":"
                + time2.getSeconds();
        return onlineTime;
    }

    /**
     * 是否阻断
     */
    private boolean isBlock = false;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIdTerminal() {
        return idTerminal;
    }

    public void setIdTerminal(String idTerminal) {
        this.idTerminal = idTerminal;
    }

    public String getCardType() {
        if (cardType.indexOf("TF") >= 0) {
            return "002";
        }
        if (cardType.indexOf("USB") >= 0) {
            return "001";
        }
        return "003";
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCard_version() {
        return card_version;
    }

    public void setCard_version(String card_version) {
        this.card_version = card_version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDepart() {
        return userDepart;
    }

    public void setUserDepart(String userDepart) {
        this.userDepart = userDepart;
    }

    public String getUserZone() {
        return userZone;
    }

    public void setUserZone(String userZone) {
        this.userZone = userZone;
    }

    public String getPoliceNumber() {
        return policeNumber;
    }

    public void setPoliceNumber(String policeNumber) {
        this.policeNumber = policeNumber;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public boolean getIfcancel() {
        return ifcancel;
    }

    public void setIfcancel(boolean ifcancel) {
        this.ifcancel = ifcancel;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public long getLastDate() {
        return lastDate;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }

    /**
     * 是否在线
     *
     * @return
     */

    public boolean isOnline() {
        long temp = System.currentTimeMillis() - getLastDate();
        if (temp <= SysterminalinfDataBean.IonlineTime)
            return true;
        else
            return false;
    }

    public String getCardModel() {
        return cardModel;
    }

    public void setCardModel(String cardModel) {
        this.cardModel = cardModel;
    }

    public String toJsonString() {
        // String
        // test="{status:'0',ifcancel:false,ip:'192.168.1.11',ifblock:false,cardtype:'001',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:'10101',policename:'test6',idno:'110108197903034913',org:'组织1',depart:'koal',region:'上海市',logindate:'2012-03-14 01:32:38',onlinetime:'01:32:38',createdate:'2012-03-27 10:15:21'}";
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("status:'" + status + "'");
        buffer.append(",ifcancel:" + ifcancel);
        buffer.append(",ip:'" + ip + "'");
        buffer.append(",ifblock:" + isBlock());
        if (getCardType().startsWith("'"))
            buffer.append(",cardtype:" + getCardType());
        else
            buffer.append(",cardtype:'" + getCardType() + "'");
        if (cardModel.startsWith("'"))
            buffer.append(",cardmodel:" + cardModel);
        else
            buffer.append(",cardmodel:'" + cardModel + "'");
        if (card_version.startsWith("'"))
            buffer.append(",cardver:" + card_version);
        else
            buffer.append(",cardver:'" + card_version + "'");
        if (policecate.startsWith("'"))
            buffer.append(",policecate:" + policecate);
        else
            buffer.append(",policecate:'" + policecate + "'");
        buffer.append(",policeno:'" + policeNumber + "'");
        buffer.append(",policename:'" + userName + "'");
        buffer.append(",idno:'" + userId + "'");
        buffer.append(",org:'" + userOrg + "'");
        buffer.append(",depart:'" + userDepart + "'");
        buffer.append(",region:'" + userZone + "'");
        buffer.append(",logindate:'" + regTime + "'");
        buffer.append(",onlinetime:'" + getOnlineTime() + "'");
        buffer.append(",influx:" + getIn_flux());
        buffer.append(",outflux:" + getOut_flux());
        buffer.append(",createdate:'" + regTime + "'}");
        return buffer.toString();
    }

    public static SysterminalinfDataBean jsonToObject(String json) {
        SysterminalinfDataBean bean = new SysterminalinfDataBean();
        JSONObject jsonbean = JSONObject.fromObject(json);
        if (jsonbean.containsKey("cardtype")
                && jsonbean.getString("cardtype") != null) {
            bean.setCardType(jsonbean.getString("cardtype"));
        }
        if (jsonbean.containsKey("cardmodel")
                && jsonbean.getString("cardmodel") != null) {
            bean.setCardModel(jsonbean.getString("cardmodel"));
        }
        if (jsonbean.containsKey("cardver")
                && jsonbean.getString("cardver") != null) {
            bean.setCard_version(jsonbean.getString("cardver"));
        }
        if (jsonbean.containsKey("policecate")
                && jsonbean.getString("policecate") != null) {
            bean.setPolicecate(jsonbean.getString("policecate"));
        }
        if (jsonbean.containsKey("policeno")
                && jsonbean.getString("policeno") != null) {
            bean.setPoliceNumber(jsonbean.getString("policeno"));
        }
        if (jsonbean.containsKey("policename")
                && jsonbean.getString("policename") != null) {
            String temp = jsonbean.getString("policename");
            if (temp.split(" ").length == 2) {
                bean.setUserName(temp.split(" ")[0]);
                bean.setUserId(temp.split(" ")[1]);
            }
            if (bean.getPoliceNumber() == null) {
                bean.setPoliceNumber(bean.getUserId());
            }
        }
        if (jsonbean.containsKey("idno") && jsonbean.getString("idno") != null) {
            String temp = jsonbean.getString("idno");
            if (!temp.equalsIgnoreCase("null")) {
                bean.setUserId(temp);
            }
        }
        if (jsonbean.containsKey("org") && jsonbean.getString("org") != null) {
            bean.setUserOrg(jsonbean.getString("org"));
        }
        if (jsonbean.containsKey("depart")
                && jsonbean.getString("depart") != null) {
            bean.setUserDepart(jsonbean.getString("depart"));
        }
        if (jsonbean.containsKey("region")
                && jsonbean.getString("region") != null) {
            bean.setUserZone(jsonbean.getString("region"));
        }
        if (jsonbean.containsKey("createdate")
                && jsonbean.getString("createdate") != null) {
            bean.setRegTime(jsonbean.getString("createdate"));
        }

        return bean;
    }

    public static SysterminalinfDataBean jsonToObjectKoarl(String json) {
        SysterminalinfDataBean bean = new SysterminalinfDataBean();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("},")) {
            json = json.substring(0, json.length() - 3);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 2);
        }

        String[] jsonbean = json.split(",");
        for (int i = 0; i < jsonbean.length; i++) {
            if (jsonbean[i].startsWith("cardtype")) {
                bean.setCardType(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("cardmodel")) {
                bean.setCardModel(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("cardver")) {
                bean.setCard_version(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("policecate")) {
                bean.setPolicecate(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("policeno")) {
                bean.setPoliceNumber(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("policename")) {
                String temp = jsonbean[i].split(":")[1];
                if (temp.split(" ").length == 2) {
                    bean.setUserName(temp.split(" ")[0]);
                    bean.setUserId(temp.split(" ")[1]);
                } else {
                    bean.setUserName(temp);
                    bean.setUserId(temp);
                }

                if (bean.getPoliceNumber() == null) {
                    bean.setPoliceNumber(bean.getUserId());
                }
                bean.setCn(temp);
            }
            if (jsonbean[i].startsWith("idno")) {
                String temp = jsonbean[i].split(":")[1];
                if (!temp.equalsIgnoreCase("null")) {
                    bean.setUserId(jsonbean[i].split(":")[1]);
                } else {
                    bean.setUserId(bean.getPoliceNumber());
                }
            }
            if (jsonbean[i].startsWith("org")) {
                bean.setUserOrg(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("depart")) {
                bean.setUserDepart(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("region")) {
                bean.setUserZone(jsonbean[i].split(":")[1]);
            }
            if (jsonbean[i].startsWith("createdate")) {
                String tem = jsonbean[i].substring("createdate:".length());
                bean.setRegTime(tem);
            }
        }

        return bean;
    }

    public static List<SysterminalinfDataBean> stringToBeans(String data) {
        List<SysterminalinfDataBean> beans = new ArrayList<SysterminalinfDataBean>();
        if (data.startsWith("[")) {
            data = data.substring(1);
        }
        if (data.endsWith("]")) {
            data = data.substring(0, data.length() - 2);
        }
        String[] obets = data.split("},");
        for (int i = 0; i < obets.length - 1; i++) {
            beans.add(SysterminalinfDataBean.jsonToObjectKoarl(obets[i]));
        }
        return beans;
    }

    public boolean equals(Object o) {
        boolean result = false;
        SysterminalinfDataBean t = (SysterminalinfDataBean) o;
        if (t.getUserId() == this.getUserId()
                && t.getUserName().equalsIgnoreCase(this.getUserName())) {
            result = true;
        }
        return result;
    }

    public static void main(String arg[]) throws Exception {
        String terminal = "[{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:123654,policename:sxl 123456789123456789,idno:123456789123465798,org:徐汇区公安局,depart:刑警大队,region:重庆市,createdate:2012-04-19 03:26:22},{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:null,policename:sxl 123456789012345678,idno:null,org:徐汇区公安局,depart:刑警大队,region:上海市,createdate:2012-04-13 09:10:54},{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:123654,policename:王五 333333333333333333,idno:333333333333333333,org:静安区公安局,depart:刑警大队,region:上海市,createdate:2012-04-12 08:01:38},{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:654321,policename:李四 222222222222222222,idno:222222222222222222,org:静安区公安局,depart:交警大队,region:上海市,createdate:2012-04-12 08:00:21},{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:123456,policename:张三 111111111111111111,idno:111111111111111111,org:徐汇区公安局,depart:刑警大队,region:上海市,createdate:2012-04-12 07:57:10},{cardtype:'tf',cardmodel:'200',cardver:'v1.0',policecate:'1',policeno:12345678,policename:测试1 123456789012345678,idno:123456789012345678,org:徐汇区公安局,depart:刑警大队,region:上海市,createdate:2012-04-11 08:43:16},{total:6,beginno:null,engno:null,pagesize:null}]";
        /*List<SysterminalinfDataBean> beanlist = SysterminalinfDataBean
                .stringToBeans(terminal);
        for (int i = 0; i < beanlist.size(); i++) {
            System.out.println(beanlist.get(i).toJsonString());
        }*/
        String temp = "{cardtype:'USBKEY',cardmodel:'200',cardver:'v3.0',policecate:'1',policeno:null,policename:sq,idno:null,org:缁勭粐1,depart:鏈烘瀯1,region:鏃犻敗甯?createdate:2012-07-26 06:58:53}";
        SysterminalinfDataBean bean = new SysterminalinfDataBean();
        bean = SysterminalinfDataBean.jsonToObjectKoarl(temp);

        // System.out.println(SysterminalinfDataBean.());
        /*
           *
           * SysterminalinfDataBean bean = SysterminalinfDataBean
           * .jsonToObjectKoarl(terminal); System.out.println("bean:" +
           * bean.toJsonString());
           */
    }

}
