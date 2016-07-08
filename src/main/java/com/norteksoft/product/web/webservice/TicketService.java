package com.norteksoft.product.web.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface TicketService {
	/**
	 * 验证用户是否登录成功,登录成功则返回用户登录名，否则返回“false”
	 */
	@WebMethod
	public String validateUserByTicket(@WebParam(name="ticket")String ticket) throws Exception ;
	/**
	 * 通过用户名和密码验证用户，通过各子系统的登录页面进入相应系统时调用该方法
	 * @param loginName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	public String validateUserByUserInfo(@WebParam(name="loginName")String loginName,
			@WebParam(name="password")String password) throws Exception ;
	/**
	 * 通过用户登录、密码、系统编码获得能自动登录到系统默认访问的路径
	 * @param loginName 用户登录名
	 * @param password 密码
	 * @param systemCode 系统编码
	 * @return 获得能自动登录到系统默认访问的路径
	 * @throws Exception
	 */
	@WebMethod
	public String getRequestUrlBySystemCode(@WebParam(name="loginName")String loginName,
			@WebParam(name="password")String password,@WebParam(name="systemCode")String systemCode) throws Exception ;
	/**
	 * 通过用户登录、密码、系统编码、需要访问的路径获得能自动登录系统的路径
	 * @param loginName 用户登录名
	 * @param password 密码
	 * @param systemCode 系统编码
	 * @param url 需要访问的路径
	 * @return 获得能自动登录系统的路径
	 * @throws Exception
	 */
	@WebMethod
	public String getRequestUrl(@WebParam(name="loginName")String loginName,
			@WebParam(name="password")String password,@WebParam(name="systemCode")String systemCode,@WebParam(name="url")String url) throws Exception ;
	/**
	 * 获得ticket信息
	 * @param loginName 用户登录名
	 * @param password 密码
	 * @return String,格式为{'ticket':'ticketValue','tgt':'tgtValue'}
	 * @throws Exception
	 */
	@WebMethod
	public String getTicket(@WebParam(name="loginName")String loginName,
			@WebParam(name="password")String password) throws Exception ;
}
