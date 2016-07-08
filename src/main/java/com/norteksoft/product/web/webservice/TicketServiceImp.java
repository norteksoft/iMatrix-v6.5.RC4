package com.norteksoft.product.web.webservice;


import javax.jws.WebService;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.util.TicketUtil;



@WebService
public class TicketServiceImp implements TicketService {

	/**
	 * 验证用户是否登录成功,登录成功则返回用户登录名，否则返回“false”
	 */
	public String validateUserByTicket(String ticket)
			throws Exception {
		String result = TicketUtil.validateTicket(PropUtils.getProp("host.sso")+"/proxyValidate",
				ticket, PropUtils.getProp("host.imatrix")+"/j_spring_cas_security_check");
		//成功的返回值
		if(result.contains("cas:authenticationSuccess")){//验证ticket成功，返回用户登录名
			return result.substring(result.indexOf("<cas:user>")+10,result.indexOf("</cas:user>"));
		}else{//验证ticket失败
			return "failure";
		}
	}
	
	/*public static void main(String[] args) {
		try {
			String ticket1 = Util.getTicket(PropUtils.getProp("host.sso"), 
					"nortek.systemAdmin1", "b1de53c1c9665b5a7b7b8a34855d642e");//密码加密的例子
			System.out.println("ticket1="+ticket1);
			String ticket2 = Util.getTicket(PropUtils.getProp("host.sso"),
					"zzx", "123");//密码不加密的例子
			System.out.println("ticket2="+ticket2);
			Service service = new Service();
	        Call call = (Call) service.createCall();
	        call.setTargetEndpointAddress("http://192.168.1.51:8088/cim/services/cim");
	        call.setOperationName("validateUserByTicket");
	        call.addParameter("ticket", XMLType.XSD_STRING, ParameterMode.IN);
	        call.setReturnType(XMLType.XSD_STRING);
	        String jsonStr  = (String) call.invoke(new Object[]
	        { ticket2 });
			System.out.println("result="+jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


	public String validateUserByUserInfo(String loginName, String password)
			throws Exception {
		String ticket = TicketUtil.getTicket(PropUtils.getProp("host.sso"), loginName, password);
		String result = validateUserByTicket(ticket);
		return result;
	}
	
	public static void main(String[] args) {
		try {
			//获得ticket和tgt
//			String ticket = TicketUtil.getServiceTicket(PropUtils.getProp("host.sso"), "test.systemAdmin", "systemAdmin");
//			String tgt=TicketUtil.getTicketGrantingTicket(PropUtils.getProp("host.sso"), "test.systemAdmin", "systemAdmin");
//			System.out.println(ticket+"--"+tgt);
			//验证ticket是否有效
//			JaxWsDynamicClientFactory dynamicClient = JaxWsDynamicClientFactory.newInstance();
//			Client client = dynamicClient.createClient("http://192.168.1.51:8087/imatrix/services/ticket?wsdl");//http://192.168.1.51:6665/bqmdm/services/bqmdm为bqmdm的webservice访问路径,该路径只是例子，不是真正的路径
			
//			Object[] result= client.invoke("validateUserByTicket", "ST-5-NztPGV3IxnDHU6yDKN1m-cas");
//String widgetContent = result[0].toString();//结果为各系统传回的json字符串
//			System.out.println(widgetContent);
			//获得需要访问的url
//			JaxWsDynamicClientFactory dynamicClient = JaxWsDynamicClientFactory.newInstance();
//			Client client = dynamicClient.createClient("http://192.168.1.51:8088/imatrix/services/ticket?wsdl");//http://192.168.1.51:6665/bqmdm/services/bqmdm为bqmdm的webservice访问路径,该路径只是例子，不是真正的路径
//			
//			Object[] result= client.invoke("getRequestUrl",  "test.systemAdmin", "systemAdmin","imatrix");
//			System.out.println(result[0].toString());
			JaxWsDynamicClientFactory dynamicClient = JaxWsDynamicClientFactory.newInstance();
			Client client = dynamicClient.createClient("http://192.168.1.51:8088/imatrix/services/ticket?wsdl");//http://192.168.1.51:6665/bqmdm/services/bqmdm为bqmdm的webservice访问路径,该路径只是例子，不是真正的路径
			
			Object[] result= client.invoke("getTicket",  "test.systemAdmin", "systemAdmin");
			System.out.println(result[0].toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
}

	public String getRequestUrlBySystemCode(String loginName, String password,
			String systemCode) throws Exception {
		return TicketUtil.getRequestUrl(loginName, password, systemCode, null);
	}

	public String getRequestUrl(String loginName, String password,
			String systemCode, String url) throws Exception {
		return TicketUtil.getRequestUrl(loginName, password, systemCode, url);
	}

	public String getTicket(String loginName, String password)
			throws Exception {
		String ticketInfo = "";
		String server = PropUtils.getProp("host.sso")+"/v1/tickets";
		String tgt=TicketUtil.getTicketGrantingTicket(server, loginName,password);
		String rootUrl = SystemUrls.getSystemUrl("imatrix");
		String service = rootUrl+"/j_spring_cas_security_check";
		if(rootUrl.length()>0&&rootUrl.lastIndexOf("/")==rootUrl.length()-1){//rootUrl的地址最后一个字符是“/”
			service = rootUrl+"j_spring_cas_security_check";
		}
		String ticket = TicketUtil.getServiceTicket(server, tgt, service);
		ticketInfo="'ticket':'"+ ticket+"',";
		ticketInfo=ticketInfo+"'tgt':'"+ tgt+"'";
		return ticketInfo;
	}
}
