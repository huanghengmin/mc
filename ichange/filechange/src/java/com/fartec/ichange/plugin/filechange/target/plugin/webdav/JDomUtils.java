package com.fartec.ichange.plugin.filechange.target.plugin.webdav;

import com.fartec.ichange.plugin.filechange.utils.FileBean;
import com.fartec.ichange.plugin.filechange.utils.FileList;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * ��jdom�����أ̡ͣ�������
 */
public class JDomUtils {
    private static Logger logger=Logger.getLogger(JDomUtils.class);
    private static final  String  SyncXmlFilePath=System.getProperty("ichange.home")+"/temp/webdav_Sync.xml";
    private static final  String fileList="fileList";
    private static final String fileBean="fileBean";
    private static final  String fileName="fileName";
    private static final String fileBeanFullName="fileBeanFullName";
    private static final String fileBeanSize="fileBeanSize";
    private static final String root="root";
    private static JDomUtils jDomUtils=null;

    private JDomUtils(){}

    public static JDomUtils getSingleJDomUtils(){
        if(jDomUtils==null){
            jDomUtils=new JDomUtils();
        }
        return jDomUtils;
    }

    /**
     * �õ�xml���Ƿ���ڴ��ļ�
     * @param fileBean    �ļ�����
     * @return             �Ƿ���ڴ��ļ�
     */
    public  boolean getSyncXMLFileBean(FileBean fileBean){
        boolean  flag=false;
        String path= JDomUtils.SyncXmlFilePath;

        Document document = buildFromFile(path);

        Element root=document.getRootElement();

        Element fileList= root.getChild(JDomUtils.fileList);
        List<Element> fileBeans=null;
        try{
            fileBeans = fileList.getChildren(JDomUtils.fileBean);
        }catch (Exception e){
            logger.info(e.getMessage());
        }


        if(!fileBeans.isEmpty()){
            Iterator<Element> its=fileBeans.iterator();
            while (its.hasNext()){
                Element file=its.next();
                if((file.getAttributeValue(JDomUtils.fileBeanFullName).equals(fileBean.getFullname()))&&
                        (Long.parseLong(file.getAttributeValue(JDomUtils.fileBeanSize))==(fileBean.getFilesize()))){
                    flag=true;
                }
            }

        }
        return flag;
    }

    /**
     * ɾ��ԭ�е�XML�ļ�
     * @return  ɾ���Ƿ�ɹ�
     */
    public boolean removeSyncXMLFile(){
        File file=new File(JDomUtils.SyncXmlFilePath);
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    /**
     * �ж�XMl�ļ��Ƿ����
     * @return
     */
    public  boolean existsSyncXMLFile(){
        String path= JDomUtils.SyncXmlFilePath;
        Document document = buildFromFile(path);
        if(document!=null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * �����ļ�·��
     */
    public void createDir(){
        String path=null;
        if(SyncXmlFilePath.contains("/")){
            path=SyncXmlFilePath.substring(0,SyncXmlFilePath.lastIndexOf("/"));
        }
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }
    /**
     * ����XML�ļ�
     */
    public void createSyncXMLFile(){
        Element root=new Element(JDomUtils.root);
        Element element=new Element(JDomUtils.fileList);
        root.addContent(element);
        Document doc=new Document(root);
        outputToFile(doc,JDomUtils.SyncXmlFilePath);
    }

    /**
     * �����������ļ��б��浽ͬ����XML�ļ���
     * @param fileList     Դ�˴��������ļ��б�
     */
    public  void saveToSyncXMLFile(FileList fileList){
        String path= JDomUtils.SyncXmlFilePath;
        Document document = buildFromFile(path);
        if(document!=null){
            Element root=document.getRootElement();
            Element element=root.getChild(JDomUtils.fileList);
            Iterator<FileBean> its=fileList.iterable();
            while (its.hasNext()){
                FileBean fileBean=its.next();
                Element file=new Element(JDomUtils.fileBean);
                Attribute fileFullName=new Attribute(JDomUtils.fileBeanFullName,fileBean.getFullname());
                Attribute fileName=new Attribute(JDomUtils.fileName,fileBean.getName());
                Attribute fileSize=new Attribute(JDomUtils.fileBeanSize,String.valueOf(fileBean.getFilesize()));
                file.setAttribute(fileFullName);
                file.setAttribute(fileName);
                file.setAttribute(fileSize);
                element.addContent(file);
            }
            outputToFile(document,path);
        }else{
            Element root=new Element(JDomUtils.root);
            Element element=new Element(JDomUtils.fileList);
            Iterator<FileBean> its=fileList.iterable();
            while (its.hasNext()){
                FileBean fileBean=its.next();
                Element file=new Element(JDomUtils.fileBean);
                Attribute fileFullName=new Attribute(JDomUtils.fileBeanFullName,fileBean.getFullname());
                Attribute fileName=new Attribute(JDomUtils.fileName,fileBean.getName());
                Attribute fileSize=new Attribute(JDomUtils.fileBeanSize,String.valueOf(fileBean.getFilesize()));
                file.setAttribute(fileFullName);
                file.setAttribute(fileName);
                file.setAttribute(fileSize);
                element.addContent(file);
            }
            root.addContent(element);
            Document doc=new Document(root);
            createDir();
            outputToFile(doc,path);
        }
    }

    /**
     * ����ָ��·����XML�ļ�����JDom����
     * @param filePath
     *            XML�ļ���·��
     * @return ���ؽ�����JDom���󣬽������ɹ�����null ��
     */
    public  Document buildFromFile(String filePath) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document anotherDocument = builder.build(new File(filePath));
            return anotherDocument;
        } catch (JDOMException e) {
            logger.error(e.getMessage());
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * ͨ�������󹹽��ĵ�
     * @param inputStream   ������
     * @return          �أͣ��ĵ�����
     */
    public  Document buildFromFile(InputStream inputStream) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document anotherDocument = builder.build(inputStream);
            return anotherDocument;
        } catch (JDOMException e) {
            logger.info(e.getMessage());
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * ����XML �ַ��� ����JDom����
     * @param xmlString
     *            XML��ʽ���ַ���
     * @return ���ؽ�����JDom���󣬽������ɹ�����null ��
     */
    public  Document buildFromXMLString(String xmlString) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document anotherDocument = builder
                    .build(new StringReader(xmlString));
            return anotherDocument;
        } catch (JDOMException e) {
            logger.info(e.getMessage());
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * ����Dom������JDom����
     * @param Dom
     *            org.w3c.dom.Document����
     * @return ���ؽ�����JDom���󣬽������ɹ�����null ��
     */
    public  Document buildFromDom(org.w3c.dom.Document Dom)throws JDOMException, IOException {
        DOMBuilder builder = new DOMBuilder();
        Document jdomDoc = builder.build(Dom);
        return jdomDoc;
    }

    /**
     * �������ʹ��XMLOutputer��һ��JDom�����������׼����豸��ʹ�� GBK ���� ��Ҫ�������JDom����
     */
    public  void outputToStdout(Document myDocument) {
        outputToStdout(myDocument, "GBK");
    }

    /**
     * �������ʹ��XMLOutputer��һ��JDom�����������׼����豸
     * @param myDocument
     *            ��Ҫ�������JDom����
     * @param encoding
     *            ���ʹ�õı���
     */
    public  void outputToStdout(Document myDocument, String encoding) {
        try {

            XMLOutputter outputter = new XMLOutputter();
            Format fm = Format.getRawFormat();
            fm.setEncoding(encoding);
            outputter.setFormat(fm);
            outputter.output(myDocument, System.out);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * ���������JDom����ת���ַ���.
     * @param document
     *            ��Ҫ��ת����JDom����
     */
    public  String outputToString(Document document) {
        return outputToString(document, "GBK");
    }

    /**
     * ���������JDom����ת���ַ���.
     * @param document
     *            ��Ҫ��ת����JDom����
     * @param encoding
     *            ����ַ���ʹ�õı���
     */
    public  String outputToString(Document document, String encoding) {
        ByteArrayOutputStream byteRep = new ByteArrayOutputStream();
        XMLOutputter outputter = new XMLOutputter();
        Format fm = Format.getRawFormat();
        fm.setEncoding(encoding);
        outputter.setFormat(fm);
        try {
            outputter.output(document, byteRep);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return byteRep.toString();
    }

    /**
     * ���������List����ת���ַ���.
     * @param //document
     *            ��Ҫ��ת����JDom����
     */
    public  String outputToString(List<Object> list) {
        return outputToString(list, "GBK");
    }

    /**
     * ���������List����ת���ַ���.
     * @param //document
     *            ��Ҫ��ת����JDom����
     * @param encoding
     *            ����ַ���ʹ�õı���
     */
    public  String outputToString(List<Object> list, String encoding) {
        ByteArrayOutputStream byteRep = new ByteArrayOutputStream();
        XMLOutputter outputter = new XMLOutputter();
        Format fm = Format.getRawFormat();
        fm.setEncoding(encoding);
        outputter.setFormat(fm);
        try {
            outputter.output(list, byteRep);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return byteRep.toString();
    }

    /**
     * ����JDom������Dom����
     * @param //Dom
     *            org.w3c.dom.Document����
     * @return ���ؽ�����Dom���󣬽������ɹ�����null ��
     */
    public  org.w3c.dom.Document outputToDom(Document jdomDoc)throws JDOMException {
        DOMOutputter outputter = new DOMOutputter();
        return outputter.output(jdomDoc);
    }

    /**
     * �������ʹ��XMLOutputter��JDom����������ļ�
     * @param myDocument
     *            ��Ҫ�����JDom����
     * @param filePath
     *            ��Ҫ������Ĵ���·��
     */
    public  void outputToFile(Document myDocument, String filePath) {
        outputToFile(myDocument, filePath, "GBK");
    }

    /**
     * �������ʹ��XMLOutputter��JDom����������ļ�
     * @param myDocument
     *            ��Ҫ�����JDom����
     * @param filePath
     *            ��Ҫ������Ĵ���·��
     * @param encoding
     *            ���뷽ʽ
     */
    public  void outputToFile(Document myDocument, String filePath,String encoding) {
        try {
            XMLOutputter outputter = new XMLOutputter();
            Format fm = Format.getRawFormat();
            fm.setExpandEmptyElements(true);
            fm.setEncoding(encoding);
            outputter.setFormat(fm);
            FileWriter writer = new FileWriter(filePath);
            outputter.output(myDocument, writer);
            writer.close();

        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * ���������JDom����ͨ����ʽ��ת��.
     * @param myDocument
     *            ��Ҫ��ת����JDom����
     * @param xslFilePath
     *            XSL�ļ��Ĵ���·��
     */
    public  void executeXSL(Document myDocument, String xslFilePath,StreamResult xmlResult) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            DOMOutputter outputter = new DOMOutputter();
            org.w3c.dom.Document domDocument = outputter.output(myDocument);
            Source xmlSource = new javax.xml.transform.dom.DOMSource(
                    domDocument);
            StreamSource xsltSource = new StreamSource(new FileInputStream(xslFilePath));
            Transformer transformer = tFactory.newTransformer(xsltSource);
            transformer.transform(xmlSource, xmlResult);
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage());
        } catch (TransformerConfigurationException e) {
            logger.info(e.getMessage());
        } catch (TransformerException e) {
            logger.info(e.getMessage());
        } catch (JDOMException e) {
            logger.info(e.getMessage());
        }
    }

}