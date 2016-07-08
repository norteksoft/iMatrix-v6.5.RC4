package org.jwebap.cfg.persist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownServiceException;


/**
 * jwebap配置输入源
 * 
 * 用于统一jwebap配置的输入协议
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.6
 * @date  2009-1-29
 */
public class InputSource {

	private URL _url=null;
	
	private String _encode=null;
	
	public InputSource(String spec) throws MalformedURLException {
		_url=new URL(spec);
	}
	
	public InputSource(String spec,String encode) throws MalformedURLException {
		_url=new URL(spec);
		_encode=encode;
	}
	
	public InputSource(URL url,String encode){
		_url=url;
		_encode=encode;
	}
	
	public InputSource(URL url) {
		this(url,null);
	}
	
	public String getEncoding(){
		return _encode;
	}
	
	public String getPath() {
		return _url.getFile();
	}

	public String getProtocol() {
		return _url.getProtocol();
	}

	/**
	 * 返回标识此输入的URL
	 * @return
	 * @throws MalformedURLException 
	 */
	public URL getURL(){
		return _url;
	}
	
	/**
	 * 返回标识此输入的File
	 * @return
	 * @throws IOException 
	 */
	public File getFile() throws IOException {
		if("file".equals(getProtocol())){
			return new File(getPath());
		}else{
			throw new UnknownServiceException("protocol isn't 'file'");
		}
		
	}
	
	/**
	 * 返回此输入的输入流
	 * @return
	 */
	public InputStream getInputStream() throws IOException{
		InputStream in=null;
		URL url=getURL();
		in=url.openStream();
		return in;
	}
	
	/**
	 * 返回此输入的输出流
	 * @return
	 */
	public OutputStream getOutputStream() throws IOException{
		OutputStream out=null;
		if("file".equals(getProtocol())){
			out=new FileOutputStream(getFile());
			return out;
		}else{
			throw new UnknownServiceException("protocol doesn't support output");
		}
		
	}
	
	/**
	 * 返回此输入的URL标识
	 * @return
	 */
	public String getSpec() {
		return _url.toString();
	}
	
	public String toString(){
		return getSpec();
	}
	/**
	 * 返回标识此输入的Reader
	 * @return
	 */
	public Reader getReader() throws IOException{
		return new InputStreamReader(getInputStream(),_encode);
	}
	
	/**
	 * 返回标识此输入的Writer
	 * @return
	 */
	public Writer getWriter() throws IOException{
		return new OutputStreamWriter(getOutputStream(),_encode);
	}
}
