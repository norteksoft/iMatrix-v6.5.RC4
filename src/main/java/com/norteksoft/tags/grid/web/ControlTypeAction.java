package com.norteksoft.tags.grid.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/grid")
@ParentPackage("default")
public class ControlTypeAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	private String currentInputId;
	/**
	 * 弹出选择编辑控件框
	 * @return
	 * @throws Exception
	 */
	public String controlType() throws Exception {
		String resourceCtx=PropUtils.getProp("host.resources");
		HttpServletRequest request=Struts2Utils.getRequest();
		request.setAttribute("resourceCtx",resourceCtx);
		String language = ContextUtils.getCurrentLanguage();
		request.setAttribute("confirmButton",Struts2Utils.getText("do.query",language));
		request.setAttribute("cancelButton",Struts2Utils.getText("portal.cancel",language));
		request.setAttribute("theTypeOfControl",Struts2Utils.getText("control.type.theTypeOfControl",language));
		request.setAttribute("controlTypeText",Struts2Utils.getText("control.type.text",language));
		request.setAttribute("checkbox",Struts2Utils.getText("control.type.checkbox",language));
		request.setAttribute("select",Struts2Utils.getText("control.type.select",language));
		request.setAttribute("multiselect",Struts2Utils.getText("control.type.multiselect",language));
		request.setAttribute("textarea",Struts2Utils.getText("control.type.textarea",language));
		request.setAttribute("custom",Struts2Utils.getText("format.custom",language));
		request.setAttribute("manDepartmentTree",Struts2Utils.getText("control.type.manDepartmentTree",language));
		request.setAttribute("theControlHiddenDomainAttributeName",Struts2Utils.getText("control.type.theControlHiddenDomainAttributeName",language));
		request.setAttribute("selectTheTypeOf",Struts2Utils.getText("control.type.selectTheTypeOf",language));
		request.setAttribute("companyTree",Struts2Utils.getText("control.type.companyTree",language));
		request.setAttribute("manDepartmentAndWorkgroupTree",Struts2Utils.getText("control.type.manDepartmentAndWorkgroupTree",language));
		request.setAttribute("manWorkgroupTree",Struts2Utils.getText("control.type.manWorkgroupTree",language));
		request.setAttribute("departmentTree",Struts2Utils.getText("control.type.departmentTree",language));
		request.setAttribute("departmentWorkgroupTree",Struts2Utils.getText("control.type.departmentWorkgroupTree",language));
		request.setAttribute("workgroupTree",Struts2Utils.getText("control.type.workgroupTree",language));
		request.setAttribute("formatType",Struts2Utils.getText("format.type",language));
		request.setAttribute("single",Struts2Utils.getText("control.type.single",language));
		request.setAttribute("multiple",Struts2Utils.getText("control.type.multiple",language));
		return "success";
	}
	
	public String getCurrentInputId() {
		return currentInputId;
	}

	public void setCurrentInputId(String currentInputId) {
		this.currentInputId = currentInputId;
	}

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void prepareModel() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String save() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
