package com.fartec.ichange.plugin.filechange.utils;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-7-31
 * Time: 上午9:19
 * To change this template use File | Settings | File Templates.
 */
public class FileType {
    public static final HashMap<String, String> FILE_TYPE_MAP = new HashMap<String, String>();
    public static final HashMap<String, String> REAL_NAME_MAP = new HashMap<String, String>();
    static {
        //images
        FILE_TYPE_MAP.put("FFD8FFE000104A464946", "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("89504E470D0A1A0A0000", "png"); // PNG (png)
        FILE_TYPE_MAP.put("474946383961A900A900", "gif"); // GIF (gif)
        FILE_TYPE_MAP.put("4D4D002A000000080018", "tif"); // TIFF (tif)
        FILE_TYPE_MAP.put("424D228C010000000000", "bmp"); // 16色位图(bmp)
        FILE_TYPE_MAP.put("424D26A4240000000000", "bmp") ;
        FILE_TYPE_MAP.put("424D8240090000000000", "bmp"); // 24位位图(bmp)
        FILE_TYPE_MAP.put("424D8E1b030000000000", "bmp"); // 256色位图(bmp)
        FILE_TYPE_MAP.put("41433130313500000000", "dwg"); // CAD (dwg)
        FILE_TYPE_MAP.put("EFBBBF3C21444F435459", "html"); // HTML (html)
        FILE_TYPE_MAP.put("3C68746D6C3E0D0A3C68", "htm"); // HTM (htm)
        FILE_TYPE_MAP.put("40636861727365742022", "css"); // css
        FILE_TYPE_MAP.put("2F2FE59586E59381E588", "js"); // js
        FILE_TYPE_MAP.put("7B5C727466205C616E73", "rtf"); // Rich Text Format
        FILE_TYPE_MAP.put("7B5C727466315C616E73" ,"rtf");
        FILE_TYPE_MAP.put("4D4D002A000000080022", "dng");
        // (rtf)
        FILE_TYPE_MAP.put("38425053000100000000", "psd"); // Photoshop (psd)
        FILE_TYPE_MAP.put("3C3F786D6C2076657273", "eml"); // Email [Outlook
        // Express 6] (eml)
        FILE_TYPE_MAP.put("D0CF11E0A1B11AE10000", "doc"); // MS Excel
        // 注意：word、msi 和
        // excel的文件头一样
//        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", "vsd"); // Visio 绘图
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb"); // MS Access (mdb)
        FILE_TYPE_MAP.put("252150532D41646F6265",  "ps");
        FILE_TYPE_MAP.put("255044462D312E340D25", "pdf"); // Adobe Acrobat
        // (pdf)
        FILE_TYPE_MAP.put("2e524d46000000120001","rmvb"); // rmvb/rm相同
        FILE_TYPE_MAP.put("464c5601050000000900", "flv"); // flv与f4v相同
        FILE_TYPE_MAP.put("00000020667479706973", "mp4");
        FILE_TYPE_MAP.put("49443303000000023E06", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg"); //
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv"); // wmv与asf相同
        FILE_TYPE_MAP.put("52494646320000005741", "wav"); // Wave (wav)
        FILE_TYPE_MAP.put("524946468EA823004156", "avi");
        FILE_TYPE_MAP.put("4d546864000000060001", "mid"); // MIDI (mid)
        FILE_TYPE_MAP.put("504B0304140000000800", "zip");
        FILE_TYPE_MAP.put("526172211A0700CF9073", "rar");
        FILE_TYPE_MAP.put("7461726765743D616E64", "ini");
        FILE_TYPE_MAP.put("504B03040A0000000000", "jar");
        FILE_TYPE_MAP.put("4D5A9000030000000400", "exe");// 可执行文件
        FILE_TYPE_MAP.put("3C252D2D0D0A20204C69", "jsp");// jsp文件
        FILE_TYPE_MAP.put("4D616E69666573742D56", "MF");// MF文件
        FILE_TYPE_MAP.put("3C3F786D6C2076657273", "xml");// xml文件
        FILE_TYPE_MAP.put("23204D7953514C2D4672", "sql");// xml文件
        FILE_TYPE_MAP.put("7061636b616765207765", "java");// java文件
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat");// bat文件
        FILE_TYPE_MAP.put("1F8B080840EB1D430003", "gz");// gz文件
        FILE_TYPE_MAP.put("2320546869732066696C","properties");// bat文件
        FILE_TYPE_MAP.put("49545346030000006000", "chm");// bat文件
        FILE_TYPE_MAP.put("04000000010000001300", "mxp");// bat文件
        FILE_TYPE_MAP.put("504B0304140006000800", "docx");// docx文件
        FILE_TYPE_MAP.put("64383A616E6E6F756E63","torrent");
        FILE_TYPE_MAP.put("A1B6BCABC6B7BCD2B6A1", "txt");
        FILE_TYPE_MAP.put("B2E2CAD420B7B4B9B220", "txt");
        FILE_TYPE_MAP.put("B7B4B9B220C3F0B9B220", "txt");
        FILE_TYPE_MAP.put("B7B4B9B220B7A8C2D6B9", "txt");
        FILE_TYPE_MAP.put("B7A8C2D6B9A600000000", "txt");
        FILE_TYPE_MAP.put("FF5750431E000000010A", "wpd"); // WordPerfect (wpd)
        FILE_TYPE_MAP.put("CFAD12FE309DFE268F1A", "dbx"); // Outlook Express (dbx)
//        FILE_TYPE_MAP.put("2142444E", "pst"); // Outlook (pst)
//        FILE_TYPE_MAP.put("AC9EBD8F", "qdf"); // Quicken (qdf)
//        FILE_TYPE_MAP.put("E3828596", "pwl"); // Windows Password (pwl)
//        FILE_TYPE_MAP.put("2E7261FD", "ram"); // Real Audio (ram)
        FILE_TYPE_MAP.put("504B0304140008080800", "apk");
        FILE_TYPE_MAP.put("7061636B61676520636F","java");
        FILE_TYPE_MAP.put("736574202D650A736574",  "sh");
        FILE_TYPE_MAP.put("4C4F43414C5F50415448",  "mk");
        FILE_TYPE_MAP.put("23696E636C7564652022",   "c");
        FILE_TYPE_MAP.put("2F2A20444F204E4F5420",   "h");
        FILE_TYPE_MAP.put("CAFEBABE000000320036","class");
        FILE_TYPE_MAP.put("2320636163686520666F","cache");
        FILE_TYPE_MAP.put("3C212D2D0D0A204C6963", "tag");
        FILE_TYPE_MAP.put("4357530AEC9A020078DA", "swf");
        FILE_TYPE_MAP.put("EFBBBF3C3F786D6C2076", "jsb");
        FILE_TYPE_MAP.put("6F72672E65636C697073","container");
        FILE_TYPE_MAP.put("3C3F786D6C2076657273","component");
        FILE_TYPE_MAP.put("57696E646F7700000000","name");
        FILE_TYPE_MAP.put("23576564204D61722032","prefs");
        FILE_TYPE_MAP.put("2F2F20436F7079726967", "jsx");
        FILE_TYPE_MAP.put("2720436F707972696768", "vbs");
        FILE_TYPE_MAP.put("00000020667479707174", "mov");
        FILE_TYPE_MAP.put("FFFE2200240024002400","zdct");
        FILE_TYPE_MAP.put("FFFE7600650072007300", "inf");
        FILE_TYPE_MAP.put("384B5343000000040000", "kys");
        FILE_TYPE_MAP.put("384D4E55000000020000", "mnu");
        FILE_TYPE_MAP.put("66756E6374696F6E205B", "m");
        FILE_TYPE_MAP.put("000000100000003D0024", "atn");
        FILE_TYPE_MAP.put("00000010000000010000", "blw");
        FILE_TYPE_MAP.put("000600013842494D7361", "abr");
        FILE_TYPE_MAP.put("00010001000000000064", "cha");
        FILE_TYPE_MAP.put("0002082C0002FFFF1919", "aco");
        FILE_TYPE_MAP.put("3842465300010000001E", "shc");
        FILE_TYPE_MAP.put("00040005000200000000", "acv");
        FILE_TYPE_MAP.put("63757368000000020000", "csh");
        FILE_TYPE_MAP.put("000100020002FFFFFFFF", "ADO");
        FILE_TYPE_MAP.put("00014000000000000000", "eap");
        FILE_TYPE_MAP.put("38424752000500000010", "grd");
        FILE_TYPE_MAP.put("68647274000000033F5C", "hdt");
        FILE_TYPE_MAP.put("00020100FF6F00190000", "ahu");
        FILE_TYPE_MAP.put("0002000F00FF000000FF", "alv");
        FILE_TYPE_MAP.put("3842334D000200000012", "p3m");
        FILE_TYPE_MAP.put("00000001010102020203", "act");
        FILE_TYPE_MAP.put("38423345000200000012", "p3e");
        FILE_TYPE_MAP.put("38425054000100000018", "pat");
        FILE_TYPE_MAP.put("00023842534C00030017", "asl");
        FILE_TYPE_MAP.put("38425450000000020000", "tpl");
        FILE_TYPE_MAP.put("5A6F6F6D696679206578", "zvt");
        FILE_TYPE_MAP.put("7368656574206170705F", "adm");
        FILE_TYPE_MAP.put("6C61796F757420766965", "eve");
        FILE_TYPE_MAP.put("5B30303A30302E37305D", "lrc");
        FILE_TYPE_MAP.put("3026B2758E66CF11A6D9", "wma");
        FILE_TYPE_MAP.put("FEEF010475873F4F0101", "GHO");
        FILE_TYPE_MAP.put("0000001C667479706D70", "tdl");
        FILE_TYPE_MAP.put("54494900BB0000000000", "tii");
        FILE_TYPE_MAP.put("5450540400D965A001D3", "tpt");
        FILE_TYPE_MAP.put("30821BEE06092A864886", "CAT");
        FILE_TYPE_MAP.put("323031322D30342D3238", "log");
        FILE_TYPE_MAP.put("480C0000000000009307", "vir");
        FILE_TYPE_MAP.put("7F454C46010101000000",  "so");
        FILE_TYPE_MAP.put("234D6F6E204A756E2031", "epf");
        FILE_TYPE_MAP.put("308203533082023BA003", "crt");
        FILE_TYPE_MAP.put("FEEDFEED000000020000", "jks");
        FILE_TYPE_MAP.put("3080020103308006092A", "p12");
        FILE_TYPE_MAP.put("3842434200010BD80000", "acb");
        FILE_TYPE_MAP.put("E9342800000000000000", "com");
        FILE_TYPE_MAP.put("406563686F206F66660D", "bat");
    }
    public static String getFileType(String fileName,byte[] src){
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        if(builder.toString().startsWith("FFD8FFE")){
            REAL_NAME_MAP.put(fileName,getName(fileName)+".jpg");
            return getName(fileName)+".jpg";
        }
        if(FILE_TYPE_MAP.get(builder.toString()) == null){
            if(!fileName.endsWith("txt")){
                return getName(fileName)+".null";
            }
            FILE_TYPE_MAP.put(fileName,fileName);
            return fileName;
        }
        REAL_NAME_MAP.put(fileName,getName(fileName)+"."+FILE_TYPE_MAP.get(builder.toString())) ;
        return getName(fileName)+"."+FILE_TYPE_MAP.get(builder.toString());
    }
    private static String getName(String fileName){
        String name = "";
        int point = fileName.lastIndexOf(".");
        if(point != -1){
            name = fileName.substring(0,point);
        }
        return  name;
    }
}

