package com.norteksoft.bs.sms.base.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.bs.sms.service.SmsSendMessageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Component
@Path("/sms")
public class SmsDataTimer {
	@Autowired
	private SmsSendMessageManager smsSendMessageManager;
	private AcsUtils acsUtils;
	@Autowired
	public void setAcsUtils(AcsUtils acsUtils) {
		this.acsUtils = acsUtils;
	}
	
	@POST
	@Path("/sendMessage")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response sendMessage(@FormParam("runAsUser")String identity) {
		try {
			
			List<Company> companys=acsUtils.getAllCompanys();
			for(Company company:companys){
				ThreadParameters parameters=new ThreadParameters(company.getId());
				ParameterUtils.setParameters(parameters);
				com.norteksoft.product.api.entity.User systemAdmin = ApiFactory.getAcsService().getSystemAdmin();
				parameters=new ThreadParameters(company.getId());
				parameters.setUserName("系统");
				parameters.setLoginName(systemAdmin.getLoginName());
				parameters.setUserId(systemAdmin.getId());
				ParameterUtils.setParameters(parameters);
				
				ParameterUtils.setParameters(parameters);
				smsSendMessageManager.send();
			}
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
		return Response.status(201).entity(" acs finish ok ").build();
	}
 public static void main(String[] args) {
	while(true){
//		try {
			System.out.println("send a message!");
			//Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
}
