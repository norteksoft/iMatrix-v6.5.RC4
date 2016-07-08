package com.norteksoft.security.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.norteksoft.security.service.CheckLicenseManager;


@Component
@Path("/license")
public class CheckLicenseTimer {
	@Autowired
	private CheckLicenseManager checkLicenseManager;
	
	@POST
	@Path("/checkLicense")
	@Produces("text/html;charset=UTF-8")
	@Consumes("text/html;charset=UTF-8")
	public Response checkLicense() {
		try {
			checkLicenseManager.checkLicense();
		} catch (Exception e) {
			return Response.status(201).entity(e.getMessage()).build();
		}
		return Response.status(201).entity(" acs finish ok ").build();
	}

}
