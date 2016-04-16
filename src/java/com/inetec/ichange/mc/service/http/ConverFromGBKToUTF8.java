package com.inetec.ichange.mc.service.http;


public class ConverFromGBKToUTF8 {
 public static void main(String[] args){
  
  try {
   
         ConverFromGBKToUTF8 convert = new ConverFromGBKToUTF8();
         byte [] fullByte = convert.gbk2utf8("Ф");
         String fullStr = new String(fullByte, "UTF-8");
         System.out.println("string from GBK to UTF-8 byte:  " + fullStr);


     } catch (Exception e) {
      e.printStackTrace();
     }
 }
 
 public byte[] gbk2utf8(String chenese){
  char c[] = chenese.toCharArray();
        byte [] fullByte =new byte[3*c.length];
        for(int i=0; i<c.length; i++){
         int m = (int)c[i];
         String word = Integer.toBinaryString(m);
//         System.out.println(word);
         
         StringBuffer sb = new StringBuffer();
         int len = 16 - word.length();

         for(int j=0; j<len; j++){
          sb.append("0");
         }
         sb.append(word);
         sb.insert(0, "1110");
         sb.insert(8, "10");
         sb.insert(16, "10");
         
//         System.out.println(sb.toString());
         
         String s1 = sb.substring(0,8);          
         String s2 = sb.substring(8,16);          
         String s3 = sb.substring(16);
         
         byte b0 = Integer.valueOf(s1, 2).byteValue();
         byte b1 = Integer.valueOf(s2, 2).byteValue();
         byte b2 = Integer.valueOf(s3, 2).byteValue();
         byte[] bf = new byte[3];
         bf[0] = b0;
         fullByte[i*3] = bf[0];
         bf[1] = b1;
         fullByte[i*3+1] = bf[1];
         bf[2] = b2;
         fullByte[i*3+2] = bf[2];
         
        }
        return fullByte;
 }
}