package com.norteksoft.product.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.product.util.zip.ZipEntry;
import com.norteksoft.product.util.zip.ZipFile;
import com.norteksoft.product.util.zip.ZipOutputStream;

  
public class ZipUtils {  
      /**
       * 压缩文件入口
       * @param zipFileName
       * @param inputFileName
       * @throws Exception
       */
    public static void zipFile(String zipFileName, String inputFileName)  
            throws Exception {  
        ZipOutputStream out = new ZipOutputStream(  
                new FileOutputStream(zipFileName));  
        out.setEncoding("utf-8");  
        File inputFile = new File(inputFileName);  
        zipIt(out, inputFile, "", true);  
        out.close();  
    }  
      
    /* 
     * 能支持中文的压缩 参数base 开始为"" first 开始为true 
     */  
    public static void zipIt(ZipOutputStream out, File file,  
            String base, boolean first) throws Exception {  
        if (file.isDirectory()) {  
            File[] fiels = file.listFiles();  
            if (first) {  
                first = false;  
            } else {  
                base = base + "/";  
            }  
            for (int i = 0; i < fiels.length; i++) {  
                zipIt(out, fiels[i], base + fiels[i].getName(), first);  
            }  
        } else {  
            if (first) {  
                base = file.getName();  
            }  
            out.putNextEntry(new ZipEntry(base));  
            FileInputStream in = new FileInputStream(file);  
            int b;  
            while ((b = in.read()) != -1) {  
                out.write(b);  
            }  
            in.close();  
        }  
    }  
  
    public static String unZipFile(String unZipFileName, String unZipPath)  
            throws Exception {  
        ZipFile zipFile = new ZipFile(  
                unZipFileName,prexEncoding(unZipFileName));  
        unZipFileByOpache(zipFile, unZipPath); 
        return unZipPath;
    }  
      
    /* 
     * 解压文件 unZip为解压路径 
     */  
    @SuppressWarnings("unchecked")
	public static void unZipFileByOpache(ZipFile zipFile,  
            String unZipRoot) throws Exception, IOException {  
       Enumeration e = zipFile.getEntries();  
        ZipEntry zipEntry;  
        while (e.hasMoreElements()) {  
            zipEntry = (ZipEntry) e.nextElement();  
            if (zipEntry.isDirectory()) {  
            } else {  
            	 InputStream fis = zipFile.getInputStream(zipEntry);  
                File file = new File(unZipRoot + File.separator  
                        + zipEntry.getName());  
                File parentFile = file.getParentFile();  
                parentFile.mkdirs();  
                FileOutputStream fos = new FileOutputStream(file);  
                byte[] b = new byte[1024];  
                int len;  
                while ((len = fis.read(b, 0, b.length)) != -1) {  
                    fos.write(b, 0, len);  
                }  
                fos.close();  
                fis.close();  
            }  
        } 
        zipFile.close();
    }  
    
//    /**
//     * 解析文件编码格试
//     * @param fileName
//     * @return
//     */
//    public static String prexEncoding(String fileName){//d:/1.txt
//    	java.io.File f=new java.io.File(fileName);  
//    	try{  
//    	  java.io.InputStream ios=new java.io.FileInputStream(f);  
//    	  byte[] b=new byte[3];  
//    	  ios.read(b);  
//    	  ios.close();  
//    	  if(b[0]==-17&&b[1]==-69&&b[2]==-65)  
//    		  return "utf-8"; 
//    	  else 
//    		  return "GBK";
//    	}catch(Exception e){  
//    	   e.printStackTrace();  
//    	}
//    	return "utf-8";
//    }
    
    /**
     * 解析文件编码格试
     * @param fileName
     * @return
     */
    public static String prexEncoding(String fileName){
    	String code="GBK";
 	   try {
 		   CharsetDetector charDect = new CharsetDetector();
 		   FileInputStream input = new FileInputStream(new File(fileName));
 		   String[] probableSet = charDect.detectChineseCharset(input);
 		   StringBuilder bu =new StringBuilder();
 		   for (String charset : probableSet)
 		   {
 			   bu.append(charset+",");
 		   }
 		   if(StringUtils.isNotEmpty(bu.toString())){
 			   String coding=bu.toString().toUpperCase();
 			   if(StringUtils.contains(coding, "GB2312")||StringUtils.contains(coding, "GBK")){
 				   return "GB2312";
 			   }else if(StringUtils.contains(coding, "GB18030")){
 				   return "GB2312";
 			   }else if(StringUtils.contains(coding, "UTF-8")){
 				   return "UTF-8";
 			   }
 		   }
 		} catch (Exception e) {
 			e.printStackTrace(); 
 		}
 	   return code;
    }
    
    /**
     * 解析文件夹
     */
    public static void prexFolder(File dir){//d:/1.txt
    	File[] files =dir.listFiles(); 
    	if(files.length != 0){
    		for (File file : files) {
				if(file.isDirectory()){ 
	                System.out.println(file.getName());
	                prexFolder(file);
	            }else{
	            	System.out.println(file.getName());
	            }
			}
    	}
    }
    public static void zipFolder(String exportRootPath,OutputStream fileOut){
    	//将生成的文件夹打成zip包且删除暂时文件夹
    	try {
    		File baseFile = new File(exportRootPath); 
    		ZipOutputStream out = new ZipOutputStream(fileOut);  
    		out.setEncoding("gbk");  
    		zipIt(out, baseFile, "", true);
    		out.close();
    	} catch (Exception e) {
    		e.printStackTrace(); 
    	}
    }
  
//    public static void main(String[] args) throws Exception {  
//        zipFile("d:/temp/folders.zip", "D:/temp/folders");//压缩入口  
//        unZipFile("d:/测试.zip","e:/a/");//解压入口 
//    	prexFolder(new File("e:/a"));
//    }  
    
    public static final String DEFAULT_CHARSET = "UTF-8";
	/**
	 * 使用gzip进行压缩
	 * 
	 * @param original
	 *            原始字符串
	 * @return 压缩后的字符串
	 */
	public static String gzip(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(original.getBytes(ZipUtils.DEFAULT_CHARSET));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return new sun.misc.BASE64Encoder().encode(out.toByteArray());
	}

	/**
	 * 
	 * <p>
	 * Description:使用gzip进行解压缩
	 * </p>
	 * 
	 * @param compressedString
	 *            压缩过的字符串
	 * @return 解压后的字符串
	 */
	public static String ungzip(String compressedString) {
		if (compressedString == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		byte[] compressed = null;
		String decompressed = null;
		try {
			compressed = new sun.misc.BASE64Decoder()
					.decodeBuffer(compressedString);
			in = new ByteArrayInputStream(compressed);
			ginzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString(ZipUtils.DEFAULT_CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}

	/**
	 * 使用zip进行压缩
	 * 
	 * @param original
	 *            原始字符串
	 * @return 返回压缩后的字符串
	 */
	public static final String zip(String original) {
		if (original == null)
			return null;
		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		String compressedString = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(original.getBytes(ZipUtils.DEFAULT_CHARSET));
			zout.closeEntry();
			compressed = out.toByteArray();
			compressedString = new sun.misc.BASE64Encoder()
					.encodeBuffer(compressed);
		} catch (IOException e) {
			compressed = null;
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return compressedString;
	}

	/**
	 * 使用zip进行解压缩
	 * 
	 * @param compressedString
	 *            压缩过的字符串
	 * @return 解压后的字符串
	 */
	public static final String unzip(String compressedString) {
		if (compressedString == null) {
			return null;
		}

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			byte[] compressed = new sun.misc.BASE64Decoder()
					.decodeBuffer(compressedString);
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString(ZipUtils.DEFAULT_CHARSET);
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return decompressed;
	}
} 
