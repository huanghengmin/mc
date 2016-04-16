package com.fartec.ichange.plugin.filechange.utils;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-5-2
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class FileFtp {
    private static final Logger logger=Logger.getLogger(FileFtp.class);
    private static Element list;
    private static Element subdirectory;
    private static Document dc;
    private static final String fileListPath = System.getProperty("ichange.home")+"/temp/ " ;
    private static final String path=fileListPath+"TargetFileList.xml" ;


    public Element getList() {
        return list;
    }

    public void setList(Element list) {
        this.list = list;
    }

    public Element getSubdirectory() {
        return subdirectory;
    }

    public void setSubdirectory(Element subdirectory) {
        this.subdirectory = subdirectory;
    }

    public Document getDc() {
        return dc;
    }

    public void setDc(Document dc) {
        this.dc = dc;
    }
    /**
     * 判断XMl文件是否存在
     * @return
     */
    public static boolean existsSyncXMLFile(){
        Document document = buildFromFile(path);
        if(document!=null){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 根据指定路径的XML文件建立JDom对象
     * @param filePath
     *            XML文件的路径
     * @return 返回建立的JDom对象，建立不成功返回null 。
     */
    public static  Document buildFromFile(String filePath) {
        try {
            SAXBuilder builder = new SAXBuilder();
            FileInputStream in = new FileInputStream(filePath);
            InputStreamReader isr=new InputStreamReader(in,"GBK");
            Document anotherDocument = builder.build(isr);
            return anotherDocument;
        } catch (JDOMException e) {
            logger.error(e);
        } catch (NullPointerException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }
//    public  static  void createJdomFileFtp(){
//        list =new Element("root");
//        //加入根节点
//        dc=new Document(list);
//        subdirectory =new Element("filelist").setAttribute("time", new Date().toString());
//        list.addContent(subdirectory);
//    }
    /**
     * 生成源端文件列表xml
     * @param fileList   源端文件列表
     * @throws IOException
     */
    public static  void  ftpJdomFile(FileList fileList) throws  IOException {
        Document document = buildFromFile(path);
        if(document!=null){
            Element root=document.getRootElement();
            Element element=root.getChild("filelist");
            for (Iterator i = fileList.iterable(); i.hasNext();) {
                FileBean s= (FileBean) i.next();
                boolean bool= getSyncXMLFileBean(s);
                if(bool==false){
                    //创立根节点

                    Element ss =new Element("file");
                    ss.setAttribute("Fullname",s.getFullname());
                    ss.setAttribute("Filesize", String.valueOf(s.getFilesize()));
                    ss.setAttribute("Md5",s.getMd5());
                    ss.setAttribute("Name",s.getName());
                    ss.setAttribute("Time", String.valueOf(s.getTime()));
                    element.addContent(ss);
                }
                Format format = Format.getPrettyFormat();
                //设置xml文件的缩进为4个空格
                format.setIndent("    ");
                XMLOutputter out = new XMLOutputter(format.setEncoding("GBK"));
                FileWriter writer = new FileWriter(path);
                out.output(document,writer);
            }
        }else {
            list =new Element("root");
            //加入根节点
            dc=new Document(list);
            subdirectory =new Element("filelist").setAttribute("time", new Date().toString());
            list.addContent(subdirectory);
            for (Iterator i = fileList.iterable(); i.hasNext();) {
                //创立根节点
                FileBean s= (FileBean) i.next();
                Element ss =new Element("file");
                ss.setAttribute("Fullname",s.getFullname());
                ss.setAttribute("Filesize", String.valueOf(s.getFilesize()));
                ss.setAttribute("Md5",s.getMd5());
                ss.setAttribute("Name",s.getName());
                ss.setAttribute("Time", String.valueOf(s.getTime()));
                subdirectory.addContent(ss);
            }
            Format format = Format.getPrettyFormat();
            //设置xml文件的缩进为4个空格
            format.setIndent("    ");
            XMLOutputter out = new XMLOutputter(format.setEncoding("GBK"));
            out.output(dc,new FileOutputStream(path));
        }
    }
    //解析xml文件  把xml节点转化成list
    public static FileList readXML() throws JDOMException, IOException{
        FileList fileList=new FileList();
        SAXBuilder build = new SAXBuilder();
        //从磁盘读取buildXML()方法生成的XML文件
        FileInputStream in = new FileInputStream(path);
        InputStreamReader isr=new InputStreamReader(in,"GBK");
        Document doc = build.build(isr);
        Format format = Format.getPrettyFormat();
        XMLOutputter out = new XMLOutputter(format.setEncoding("GBK"));

        //获取根目录
        Element root = doc.getRootElement();
        //获取第一个子节点fileBuildTime
        Element subdir = root.getChild("filelist");

        //通过XPath获取任意路径下的course元素
        XPath coursePath = XPath.newInstance("//file");
        //返回所有的course元素
        List courselist = coursePath.selectNodes(doc);
        logger.info("xml文件中含有*" + courselist.size() +  "*个节点");
        //循环处理所有的course元素列表
        Iterator course = courselist.iterator();
        int i=1;
        while (course.hasNext()) {
            Element courseElement = (Element) course.next();
            List list=courseElement.getAttributes();
            FileBean fileBean=new FileBean();
            for (Iterator iterator=list.iterator();iterator.hasNext();){
                String str=iterator.next().toString();
                if(FileFilter.existSubString(str,"Fullname=")){
                    int j = str.lastIndexOf("Fullname=");
                    String fullname=str.substring(j + "Fullname=".length()+1, str.length()-2);
                    fileBean.setFullname(fullname);
                }else if(FileFilter.existSubString(str,"Filesize=")){
                    int j = str.lastIndexOf("Filesize=");
                    String filesize=str.substring(j + "Filesize=".length()+1, str.length()-2);
                    fileBean.setFilesize(Long.parseLong(filesize));
                }else if(FileFilter.existSubString(str,"Md5=")){
                    int j = str.lastIndexOf("Md5=");
                    String md5=str.substring(j + "Md5=".length()+1, str.length()-2);
                    fileBean.setMd5(md5);
                }else if(FileFilter.existSubString(str,"Name=")){
                    int j = str.lastIndexOf("Name=");
                    String name=str.substring(j + "Name=".length()+1, str.length()-2);
                    fileBean.setName(name);
                }else {
                    int j = str.lastIndexOf("Time=");
                    String time=str.substring(j + "Time=".length()+1, str.length()-2);
                    fileBean.setTime(Long.parseLong(time));
                }
            }
            fileList.addFileBean(fileBean);
            //通过getParentElement()方法可以获得本节点的父节点的信息
        }
        return fileList;
    }
    /**
     * 得到xml中是否存在此文件
     * @param fileBean    文件对象
     * @return             是否存在此文件
     */
    public static  boolean getSyncXMLFileBean(FileBean fileBean){
        boolean  flag=false;
        Document document = buildFromFile(path);
        Element root=document.getRootElement();
        Element fileList= root.getChild("filelist");
        List<Element> fileBeans=null;
        try{
            fileBeans = fileList.getChildren("file");
        }catch (Exception e){
            logger.error(e);
        }
        if(!fileBeans.isEmpty()){
            Iterator<Element> its=fileBeans.iterator();
            while (its.hasNext()){
                Element file=its.next();
                if((file.getAttributeValue("Fullname").equals(fileBean.getFullname()))&&
                        (Long.parseLong(file.getAttributeValue("Filesize"))==(fileBean.getFilesize()))){
                    flag=true;
                }
            }
        }
        return flag;
    }
    /**
     * 删除原有的XML文件
     * @return  删除是否成功
     */
    public static boolean removeSyncXMLFile(){
        File file=new File(path);
        if(file.exists()){
            boolean bool=file.delete();
            return bool;
        }
        return false;
    }
}
