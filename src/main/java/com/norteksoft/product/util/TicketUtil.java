package com.norteksoft.product.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;


public class TicketUtil {
	private static final Logger LOG = Logger.getLogger(TicketUtil.class.getName());
	public static String getTicket(final String server, final String username,
			final String password, final String service) {
		notNull(server, "server must not be null");
		notNull(username, "username must not be null");
		notNull(password, "password must not be null");
		notNull(service, "service must not be null");

		return getServiceTicket(server, getTicketGrantingTicket(server,
				username, password), service);
	}

	public static String getServiceTicket(final String server,
			final String ticketGrantingTicket, final String service) {
		if (ticketGrantingTicket == null)return null;
		final HttpClient client = new HttpClient();
		final PostMethod post = new PostMethod(server + "/"
				+ ticketGrantingTicket);
		post.setRequestBody(new NameValuePair[] { new NameValuePair("service",
				service) });
		try {
			client.executeMethod(post);
			final String response = post.getResponseBodyAsString();
			switch (post.getStatusCode()) {
			case 200:
				return response;
			default:
				LOG.warning("Invalid response code (" + post.getStatusCode()
						+ ") from CAS server!");
				LOG.info("Response (1k): "
						+ response.substring(0, Math.min(1024, response
								.length())));
				break;
			}
		}catch (final IOException e) {
			LOG.warning(e.getMessage());
		}finally { post.releaseConnection(); }
		return null;
	}

	public static String getTicketGrantingTicket(final String server,
			final String username, final String password) {
		final HttpClient client = new HttpClient();
		final PostMethod post = new PostMethod(server);
		try {
			post.setRequestBody(new NameValuePair[] {
				new NameValuePair("username", URLEncoder.encode(username, "utf-8")),
				new NameValuePair("password", password) });
		} catch (IllegalArgumentException e1) {
			LOG.warning(e1.getMessage());
		} catch (UnsupportedEncodingException e1) {
			LOG.warning(e1.getMessage());
		}
		try {
			client.executeMethod(post);
			return getResponse(post);
		}catch (final IOException e) {
			LOG.warning(e.getMessage());
		}finally {
			post.releaseConnection();
		}
		return null;
	}
	private static String getResponse(PostMethod post) throws IOException{
		final String response = post.getResponseBodyAsString();
		switch (post.getStatusCode()) {
		case 201: {
			final Matcher matcher = Pattern.compile(
					".*action=\".*/(.*?)\".*").matcher(response);
			if (matcher.matches()) return matcher.group(1);
			LOG.warning("Successful ticket granting request, but no ticket found!");
			LOG.info("Response (1k): "
					+ response.substring(0, Math.min(1024, response
							.length())));
			break;
		}
		default:
			LOG.warning("Invalid response code (" + post.getStatusCode()
					+ ") from CAS server!");
			LOG.info("Response (1k): "
					+ response.substring(0, Math.min(1024, response
							.length())));
			break;
		}
		return null;
	}

	private static void notNull(final Object object, final String message) {
		if (object == null)
			throw new IllegalArgumentException(message);
	}
	//验证ticket是否有效
	public static String validateTicket(String serverValidate,String serviceTicket,String service){
		final HttpClient client = new HttpClient();
		PostMethod post = null;
		try{
			 post = new PostMethod(serverValidate+"?"+"ticket="+serviceTicket+
					 "&service="+URLEncoder.encode(service,"UTF-8"));
			client.executeMethod(post);
			final String response = post.getResponseBodyAsString();
			switch (post.getStatusCode()) {
			case 200: {
				return response;
			}default:
				LOG.warning("Invalid response code (" + post.getStatusCode()
						+ ") from CAS server!");
				LOG.info("Response (1k): "
						+ response.substring(0, Math.min(1024, response
								.length())));
				break;
			}
		}catch (Exception e) {
			LOG.warning(e.getMessage());
		}finally{
			post.releaseConnection();
		}
		return null;
	}
	

	/**
	 * �õ���Ӧ��ticket
	 * @param cas  //cas�ĵ�ַ
	 * @param username //�û���
	 * @param password //����
	 * @param rootUrl  //Ҫ���ʵ�ϵͳ���ַ
	 * @param url//Ҫ���ʵľ����ַ
	 * @return
	 * @throws Exception
	 */
	public static String getTicket(String cas,String username,String password) throws Exception{
		String rootUrl = PropUtils.getProp("host.imatrix");
		String server = ""; 
		String service = "";
		server = cas+"/v1/tickets";
		service = rootUrl+"/j_spring_cas_security_check";
		if(cas.length()>0&&cas.lastIndexOf("/")==cas.length()-1){//cas
			server = cas+"v1/tickets";
		}
		if(rootUrl.length()>0&&rootUrl.lastIndexOf("/")==rootUrl.length()-1){//rootUrl
			service = rootUrl+"j_spring_cas_security_check";
		}
		String tgt=getTicketGrantingTicket(server,username, password);
		return getServiceTicket(server, tgt, service);
		
	}
	
	/**
	 * 得到对应的ticket
	 * @param cas  //cas的地址
	 * @param username //用户名
	 * @param password //密码
	 * @param rootUrl  //要访问的系统根地址
	 * @param url//要访问的具体地址
	 * @return
	 * @throws Exception
	 */
	public static String getTicket(String cas,String username,String password,String rootUrl,String url) throws Exception{
		String server = ""; 
		String service = "";
		server = cas+"/v1/tickets";
		service = rootUrl+"/j_spring_cas_security_check";
		if(cas.length()>0&&cas.lastIndexOf("/")==cas.length()-1){//cas的地址最后一个字符是“/”
			server = cas+"v1/tickets";
		}
		if(rootUrl.length()>0&&rootUrl.lastIndexOf("/")==rootUrl.length()-1){//rootUrl的地址最后一个字符是“/”
			service = rootUrl+"j_spring_cas_security_check";
		}
		String tgt=getTicketGrantingTicket(server,username, password);
		String ticket=getServiceTicket(server, tgt, service);
		if(StringUtils.isEmpty(url)){//访问系统首页
			return service+"?ticket="+ticket+"&tgt="+tgt;
		}else{//需要访问特定路径
			String realUrl= url;//url
			String urlPara ="";//url的参数
			if(url.contains("?")){//表示有参数
				realUrl = url.substring(0,url.indexOf("?"));
				urlPara = url.substring(url.indexOf("?")+1);
			}
			if(StringUtils.isNotEmpty(urlPara)){
				return service+"?ticket="+ticket+"&tgt="+tgt+"&"+urlPara+"&url="+realUrl;
			}else{
				return service+"?ticket="+ticket+"&tgt="+tgt+"&url="+realUrl;
			}
		}
		
	}
	
	public static String getRequestUrl(String loginName, String password,
			String systemCode,String url)  throws Exception{
		String cas = PropUtils.getProp("host.sso");
		String rootUrl = SystemUrls.getSystemUrl(systemCode);
		if(StringUtils.isNotEmpty(url)&&!url.startsWith("http")){//判断是否是全路径,不是全路径则进入判断将url拼接全
			if(rootUrl.length()>0&&rootUrl.lastIndexOf("/")==rootUrl.length()-1){//rootUrl的地址最后一个字符是“/”
				rootUrl = rootUrl.substring(0,rootUrl.length()-1);
			}
			url = rootUrl +  url;
		}
		return getTicket(cas,loginName,password,rootUrl,url);
	}
}
