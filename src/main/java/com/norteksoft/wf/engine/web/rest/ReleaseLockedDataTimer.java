package com.norteksoft.wf.engine.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.acs.service.authority.RecordLockerManager;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

@Component
@Path("/acs")
public class ReleaseLockedDataTimer {
	@Autowired
	private RecordLockerManager recordLockerManager;
	
	@POST
	@Path("/releaseLockedData")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response userAuthentication(@FormParam("runAsUser")String loginName,@FormParam("runAsUserId")String userId,@FormParam("companyId")String companyId) {
		try {
			ThreadParameters parameters=new ThreadParameters();
			parameters.setCompanyId(Long.valueOf(companyId));
			ParameterUtils.setParameters(parameters);
			recordLockerManager.timingReleaseLockedData();
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
		return Response.status(201).entity(" acs finish ok ").build();
	}

}
