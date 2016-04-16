package com.hzih.mc.utils;
import com.hzih.mc.entity.NetInfo;
import com.inetec.common.util.OSInfo;
import com.inetec.common.util.Proc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class GetListNetInfo{


    /**
     * ��ȡ/etc/network/run/ifstate�ļ��е�������Ϣ ,
     * @return
     */
    public List<String> readIfState(){
        List<String> list = new ArrayList<String>();
        String result = getNetInfoResult(StringContext.IFSTATE);
        String[] ifStates = result.split("\\$net\\$");
        for (int i = 0; i < ifStates.length; i++) {
            list.add(ifStates[i].split("=")[1].trim());
        }
        return list;
    }
    /**
     * 	��ȡ/etc/network/interfaces�ļ��е�������Ϣ ,
     * @return
     */
    public List<NetInfo> readInterfaces(){
        List<NetInfo> netInfos = new ArrayList<NetInfo>();
        Map<String, String[]> map = new HashMap<String, String[]>();
        String result = getNetInfoResult(StringContext.INTERFACE);
        String[] lines = result.split("\\$net\\$");
        String[] netNames = getNetInfoNames(lines);
        String[] netNamesIsUp = getNetInfoNamesIsUp(lines);
        Map<String, String> macs = getInterfaceMac(netNames);
        for (int i = 0; i < netNames.length; i++) {
            String[] netInfoLines = result.split("iface "+netNames[i]+" inet static");
            if(netInfoLines.length>1){
                map.put(netNames[i], netInfoLines);
            }
        }
        for (int i = 0; i < netNames.length; i++) {
            NetInfo netInfo = null;
            String[] netInfoLines = map.get(netNames[i]);
            if(netInfoLines!=null&&netInfoLines.length>1){
                String[] nets = netInfoLines[1].split("\\$net\\$");
                netInfo = new NetInfo();
                netInfo.setInterfaceName(netNames[i]);
                String[] dns = readDns();
                setDNS(dns,netInfo);
                setEncap(netNames[i],netInfo);
                setIsUp(netNames[i],netNamesIsUp,netInfo);
                netInfo.setMac(macs.get(netNames[i]));
                for (int j = 0; j < nets.length; j++) {
                    if(nets[j].startsWith("address")){
                        String ip = nets[j].substring(7).trim();
                        netInfo.setIp(ip);
                    }else if(nets[j].startsWith("netmask")){
                        String netmask = nets[j].substring(7).trim();
                        netInfo.setSubnetMask(netmask);
                    }else if(nets[j].startsWith("broadcast")){
                        String broadCast = nets[j].substring(9).trim();
                        netInfo.setBroadCast(broadCast);
                    }else if(nets[j].startsWith("gateway")){
                        String gateway = nets[j].substring(7).trim();
                        netInfo.setGateway(gateway);
                    }else if(nets[j].startsWith("iface")){
                        break;
                    }
                }
            }
            netInfos.add(netInfo);
        }
        return netInfos;
    }

    /**
     * 	�ж���������ʱ�Ƿ�����
     * @param netName
     * @param netNamesIsUp
     * @param netInfo
     */
    private void setIsUp(String netName, String[] netNamesIsUp, NetInfo netInfo) {
        for (int j = 0; j < netNamesIsUp.length; j++) {
            if(netName.equals(netNamesIsUp[j])){
                netInfo.setIsUp(true);
                break;
            }else{
                netInfo.setIsUp(false);
            }
        }
    }

    /**
     * 	�ж��Ƿ�����������
     * @param name
     * @param netInfo
     */
    private void setEncap(String name, NetInfo netInfo) {
        if(name.split(":").length>1){
            netInfo.setEncap("Ethernet(����)");
        }else{
            netInfo.setEncap("Ethernet");
        }
    }

    /**
     * 	��netInfo���dns
     * @param dns
     * @param netInfo
     */
    private void setDNS(String[] dns, NetInfo netInfo) {
        for (int i = 0; i < dns.length; i++) {
            switch (i) {
                case 0:
                    netInfo.setDns_1(dns[0]);
                    break;
                case 1:
                    netInfo.setDns_2(dns[1]);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * ��ȡ/etc/resolv.conf�ļ��е�����nameserver
     * @return
     */
    private String[] readDns() {
        List<String> dns = new ArrayList<String>();
        Proc proc = new Proc();
        if(proc.exec("more /etc/resolv.conf")){
            String[] lines = proc.getOutput().split("\n");
            String dns_1 = null;
            String dns_2 = null;
            for (int i = 0; i < lines.length; i++) {
                if(lines[i].startsWith("nameserver")){
                    dns_1 = lines[i].split("nameserver")[1].trim();
                    dns.add(dns_1);
                    if(i < lines.length-1&&lines[i+1].startsWith("nameserver")){
                        dns_2 = lines[i+1].split("nameserver")[1].trim();
                        dns.add(dns_2);
                        break;
                    }else{
                        continue;
                    }
                }
            }
        }
        return dns.toArray(new String[dns.size()]);
    }

    /**
     * ��ȡ/etc/network/interfaces�ļ��е���������ʱ���õ�����ӿ�
     * @param lines ������
     * @return
     */
    private String[] getNetInfoNamesIsUp(String[] lines) {
        String[] netNames = null;
        for (int i = 0; i < lines.length; i++) {
            if(lines[i].startsWith("auto ")){
                netNames = lines[i].split("auto ")[1].split(" ");
            }
        }
        return netNames;
    }

    /**
     * ��ȡ/etc/network/interfaces�ļ��е���������ӿ�
     * @param lines
     * @return
     */
    private String[] getNetInfoNames(String[] lines) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < lines.length; i++) {
            if(lines[i].startsWith("iface")&&lines[i].endsWith("inet static")){
                String netNames = (lines[i].split("iface")[1]).split("inet static")[0];
                list.add(netNames.trim());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * ��ȡ�ļ��е���������
     * @return
     */
    private String getNetInfoResult(String file) {
        String str = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String r = in.readLine();
            while(r!=null){
                str += r.trim() + "$net$";
                r = in.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * ������������ȡ����mac��ַ
     * @param netNames
     * @return
     */
    private Map<String, String> getInterfaceMac(String[] netNames){
        Map<String,String> macs = new HashMap<String, String>();
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String command = "ifconfig -a";
            if(proc.exec(command)){
                String result = proc.getOutput();
                StringTokenizer tokenizer = new StringTokenizer(result, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    for (int i = 0; i < netNames.length; i++) {
                        if(line.startsWith(netNames[i])){
                            String mac = line.split("HWaddr")[1].trim();
                            macs.put(netNames[i], makeMac(mac));
                        }
                    }
                }
            }
        }
        return macs;
    }

    public List<NetInfo> readInterfaces(String command){
        List<NetInfo> interfaces = new ArrayList<NetInfo>();
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String str = "";
            if(proc.exec(command)){
                String result = proc.getOutput();
                StringTokenizer tokenizer = new StringTokenizer(result, "\n");
                while (tokenizer.hasMoreTokens()) {
                    String line = tokenizer.nextToken();
                    String ff = "";
                    if(line.trim().startsWith("Interrupt")){
                        ff = "this is a flag!";
                    }
                    str += line.trim()+"##"+ff;
                }
            }
            String[] netInfoStrs = str.split("this is a flag!");
            for(int i = 0 ; i < netInfoStrs.length; i ++){
                NetInfo netInfo = new NetInfo();
                String[] nets = netInfoStrs[i].split("##");
                for (int j = 0; j < nets.length; j++) {
                    if(j==0){
                        netInfo.setInterfaceName((nets[j].split("Link encap:"))[0].trim());
                        if(nets[j].split("HWaddr").length>1){
                            netInfo.setEncap(splitBetweenStrings(nets[j], "Link encap:", "HWaddr"));
                            netInfo.setMac(makeMac(nets[j].split("HWaddr")[1].trim()));
                        }else{
                            netInfo.setEncap(nets[j].split("Link encap:")[1].trim());
                        }
                    }else if(j==1){
                        String ip = "";
                        String broad = "";
                        String subnetMask = "";
                        String geteway = "";
                        if(nets[j].split("Bcast:").length>1){
                            ip = splitBetweenStrings(nets[j],"inet addr:","Bcast:");
                            broad = splitBetweenStrings(nets[j],"Bcast:","Mask:");
                            subnetMask = nets[j].split("Mask:")[1].trim();
                            geteway =  broad.substring(0,broad.lastIndexOf("."))+".1";
                        }else{
                            ip = splitBetweenStrings(nets[j],"inet addr:","Mask:");
                            subnetMask = nets[j].split("Mask:")[1].trim();
                        }
                        netInfo.setIp(ip);
                        netInfo.setGateway(geteway);
                        netInfo.setSubnetMask(subnetMask);
                        if(proc.exec("more /etc/resolv.conf |awk '/nameserver/{split($2,x,\" \");print x[1]}'")){
                            String[] dns = proc.getOutput().split("\n");
                            String dns_1 = null;
                            String dns_2 = null;
                            for (int k = 0; k < dns.length; k++) {
                                if(dns[k].startsWith("nameserver")){
                                    dns_1 = dns[k].split("nameserver")[1].trim();
                                    netInfo.setDns_1(dns_1);
                                    if(k < dns.length-1&&dns[k+1].startsWith("nameserver")){
                                        dns_2 = dns[k+1].split("nameserver")[1].trim();
                                        netInfo.setDns_2(dns_2);
                                        break;
                                    }else{
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                interfaces.add(netInfo);
            }

        }else if(osInfo.isWin()){

        }
        return interfaces;
    }

    /**
     * 	ת��:��Ϊ-
     * @param hWaddr 	00:1e:67:03:3d:69
     * @return			00-1e-67-03-3d-69
     */
    private String makeMac(String hWaddr) {
        String mac = "";
        if (hWaddr!=null){
            String[] macs = hWaddr.split(":");
            for(int i = 0 ; i < macs.length ; i ++){
                if(i < macs.length -1){
                    mac += macs[i] + "-";
                }else {
                    mac += macs[i];
                }
            }
        }
        return mac;
    }

    /**
     * ȥ�����ַ���֮����ַ���
     * @param str  "aaabbbccc"
     * @param first aaa
     * @param second ccc
     * @return	bbb
     */
    private String splitBetweenStrings(String str, String first, String second){
        return (str.split(first)[1]).split(second)[0].trim();
    }

    /**
     * ����"+StringContext.systemPath+"/console.sh/config_update.sh�޸�����ӿ���Ϣ
     * @param netInfo
     * @param isUp
     * @return
     */
    public String updateInterface(NetInfo netInfo, Boolean isUp) {
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String command = null;
            if(netInfo.getGateway()!=null&&"������IP".equals(netInfo.getGateway())){
                command = "sh "+StringContext.systemPath+"/others/console.sh/config_delete.sh "+netInfo.getInterfaceName();
            }else{
                command = "sh "+StringContext.systemPath+"/others/console.sh/config_delete.sh "+netInfo.getInterfaceName()+" "+netInfo.getGateway();
            }
            proc.exec(command);
            Proc _proc = new Proc();
            if("������IP".equals(netInfo.getGateway())){
                command = "sh "+StringContext.systemPath+"/others/console.sh/config_update.sh "+netInfo.getInterfaceName()+" "+netInfo.getIp()+" "+netInfo.getSubnetMask()+" "+netInfo.getBroadCast()+" "+setNetWork(netInfo.getIp())+" "+netInfo.getIsUp();
            }else{
                if(netInfo.getGateway()!=null){
                    command = "sh "+StringContext.systemPath+"/others/console.sh/config_update.sh "+netInfo.getInterfaceName()+" "+netInfo.getIp()+" "+netInfo.getSubnetMask()+" "+netInfo.getBroadCast()+" "+setNetWork(netInfo.getIp())+" "+netInfo.getIsUp()+" "+netInfo.getGateway();
                }else{
                    command = "sh "+StringContext.systemPath+"/others/console.sh/config_update.sh "+netInfo.getInterfaceName()+" "+netInfo.getIp()+" "+netInfo.getSubnetMask()+" "+netInfo.getBroadCast()+" "+setNetWork(netInfo.getIp())+" "+netInfo.getIsUp();
                }
            }
            boolean isUpdateIP = _proc.exec(command);
            return isUpdateIP?"�޸ĳɹ�����������б�ҳ��!":"�޸ĳ���!";
        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }

    /**
     *  ͨ��ip��ȡ�㲥��ַ
     * @param ip
     * @return
     */
//    private String setBroadCast(String ip) {
//
//		return null;
//	}

    /**
     *  ͨ��ip��ַ��ȡnetwork
     * @param ip
     * @return
     */
    private String setNetWork(String ip) {
        String[] ips = ip.split("\\.");
        return ips[0]+"."+ips[1]+"."+ips[2]+".0";
    }

    /**
     * ����"+StringContext.systemPath+"/console.sh/config_dns.sh����DNS
     * @param netInfo
     * @return
     */
    public String updateDNS(NetInfo netInfo) {
        Pattern dnsPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])");
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            if(dnsPattern.matcher(netInfo.getDns_1()).matches()){
                String dns_1 = null;
                if(dnsPattern.matcher(netInfo.getDns_1()).matches()){
                    dns_1 = netInfo.getDns_1();
                }
                String dns_2 = null;
                if(dnsPattern.matcher(netInfo.getDns_2()).matches()){
                    dns_2 = netInfo.getDns_2();
                }
                String command = null;
                if(netInfo.getDns_1()!=null&&netInfo.getDns_2()!=null){
                    command = "sh "+StringContext.systemPath+"/others/console.sh/config_dns.sh " +dns_1+" "+dns_2;
                    boolean isUpdateDNS = proc.exec(command);
                    if(isUpdateDNS){
                        return isUpdateDNS?"�޸ĳɹ�,��������б�ҳ��!":"DNS�޸ĳ���!";
                    }
                }else if(netInfo.getDns_1()!=null&&netInfo.getDns_2()==null){
                    command = "sh "+StringContext.systemPath+"/others/console.sh/config_dns.sh " +dns_1;
                    boolean isUpdateDNS = proc.exec(command);
                    if(isUpdateDNS){
                        return isUpdateDNS?"�޸ĳɹ�,��������б�ҳ��!":"DNS�޸ĳ���!";
                    }
                }else{
                    command = "sh "+StringContext.systemPath+"/others/console.sh/config_dns.sh";
                    boolean isUpdateDNS = proc.exec(command);
                    if(isUpdateDNS){
                        return isUpdateDNS?"ɾ���ɹ�,��������б�ҳ��!":"DNS�޸ĳ���!";
                    }
                }

            }else {
                return "DNS�������!";
            }

        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }

    /**
     * ����"+StringContext.systemPath+"/console.sh/config_save.sh ������������
     * @param netInfo
     * @return
     */
    public String saveInterface(NetInfo netInfo) {
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String command = null;
            if(netInfo.getIsUp()){
                command = "sh "+StringContext.systemPath+"/others/console.sh/config_save.sh "+netInfo.getInterfaceName()+" "+netInfo.getIp()+" "+netInfo.getSubnetMask()+" "+netInfo.getBroadCast()+" "+setNetWork(netInfo.getIp())+" "+netInfo.getIsUp();
            }else{
                command = "sh "+StringContext.systemPath+"/others/console.sh/config_save.sh "+netInfo.getInterfaceName()+" "+netInfo.getIp()+" "+netInfo.getSubnetMask()+" "+netInfo.getBroadCast()+" "+setNetWork(netInfo.getIp());
            }
            boolean isSaved = proc.exec(command);
            return isSaved?"����ɹ�,��������б�!":"����ʧ��!";
        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }

    /**
     * ɾ������
     * @param interfaceName
     * @return
     */
    public String deleteInterface(String interfaceName ) {
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            boolean isDeleteInterface = false;
            String command = "sh "+StringContext.systemPath+"/others/console.sh/config_delete.sh "+interfaceName;
            isDeleteInterface = proc.exec(command);
            return isDeleteInterface?"����ӿڹرճɹ�,��������б�!":"����ӿڹر�ʧ��!";
        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }

    /**
     * ����ӿڼ���
     * @param interfaceName
     * @return
     */
    public String ifUp(String interfaceName) {
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            boolean isDeleteInterface = false;
            String command = "ifup "+interfaceName;
            isDeleteInterface = proc.exec(command);
            return isDeleteInterface?"����ӿڼ���ɹ�,��������б�!":"���缤��ر�ʧ��!";
        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }

    /**
     * ����ӿ�ע��
     * @param interfaceName
     * @return
     */
    public String ifDown(String interfaceName) {
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            boolean isDeleteInterface = false;
            String command = "ifdown "+interfaceName;
            isDeleteInterface = proc.exec(command);
            return isDeleteInterface?"����ӿ�ע���ɹ�,��������б�!":"����ӿ�ע��ʧ��!";
        }else if(osInfo.isWin()){

        }
        return "����ϵͳ����,�����ø�ϵͳ!";
    }
    /**
     * 	��ȡ·����Ϣ
     * @return
     */
    public List<NetInfo> readRouter() {
        OSInfo osInfo = OSInfo.getOSInfo();
        List<NetInfo> list = new ArrayList<NetInfo>();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String command = "route -v";
            proc.exec(command);
            String result = proc.getOutput();
            StringTokenizer tokenizer = new StringTokenizer(result, "\n");
            while (tokenizer.hasMoreTokens()) {
                String line = tokenizer.nextToken().trim();
                if(line.startsWith("Kernel")){
                    continue;
                }else if(line.startsWith("Destination")){
                    continue;
                }else{
                    String[] netInfos = line.split("\\s+");
                    NetInfo netInfo = new NetInfo();
                    netInfo.setDestination(netInfos[0].trim());
                    netInfo.setGateway(netInfos[1].trim());
                    netInfo.setSubnetMask(netInfos[2].trim());
                    netInfo.setInterfaceName(netInfos[7].trim());
                    list.add(netInfo);
                }
            }
            return list;
        }else if(osInfo.isWin()){
            return list;
        }
        return list;
    }
    /**
     * ɾ��·����Ϣ
     * @param netInfos
     * @return
     */
    public String deleteRouter(List<NetInfo> netInfos) {
        int length = readRouter().size();
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            for (NetInfo netInfo : netInfos) {
                String command = "route del -net "+netInfo.getDestination()+" netmask "+netInfo.getSubnetMask()+" gw "+netInfo.getGateway()+" -n "+netInfo.getInterfaceName();
                proc.exec(command);
            }
            if(readRouter().size()<length){
                return "ɾ���ɹ�,���ȷ�������б�!";
            }else{
                return "ɾ��ʧ��!";
            }
        }
        return "����ϵͳ����!";
    }
    /**
     * ����·����Ϣ
     * @param netInfo
     * @return
     */
    public String saveRouter(NetInfo netInfo) {
        int length = readRouter().size();
        OSInfo osInfo = OSInfo.getOSInfo();
        if(osInfo.isLinux()){
            Proc proc = new Proc();
            String command = "route add -net "+netInfo.getDestination()+" netmask "+netInfo.getSubnetMask()+" gw "+netInfo.getGateway()+" -n "+netInfo.getInterfaceName();
            proc.exec(command);
            if(readRouter().size()>length){
                return "����ɹ�,���ȷ�������б�!";
            }else{
                return "����ʧ��!";
            }

        }
        return "����ϵͳ����!";
    }

}