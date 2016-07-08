package com.norteksoft.tags.workflow.web;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.norteksoft.acs.base.web.struts2.CRUDActionSupport;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.TaskPermission;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.web.struts2.Struts2Utils;

@Namespace("/workflow")
@ParentPackage("default")
public class CommonOpinionAction extends CRUDActionSupport{
	private static final long serialVersionUID = 1L;
	
	private String controlId;//任务id
	private String callbackFun;//回掉方法名
	private Long taskId;
	private String opinion;//意见内容
	private String opinionRequired;//意见是否必填
	private String opinionSign;//签名
	private String opinionDate;//日期
	private String conclusion;//结论
	private String position;//保存意见(opinion)还是保存结论(conclusion)

	@Override
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	//通用意见
	@Override
	public String list() throws Exception {
		String resourceCtx=PropUtils.getProp("host.resources");
//		String webapp = SystemUrls.getSystemPageUrl(ContextUtils.getSystemCode());
		TaskPermission tp=ApiFactory.getPermissionService().getActivityPermission(taskId);
		if(tp!=null&&tp.getOpinionRequired()){
			opinionRequired="true";
		}else{
			opinionRequired="false";
		}
		HttpServletRequest request=Struts2Utils.getRequest();
		request.setAttribute("resourcesCtx",resourceCtx);
		String language = ContextUtils.getCurrentLanguage();
		request.setAttribute("inputOpinion",Struts2Utils.getText("inputOpinion",language));
		request.setAttribute("opinionControl",Struts2Utils.getText("opinionControl",language));
		request.setAttribute("opinion",Struts2Utils.getText("opinion",language));
		request.setAttribute("ftlSubmit",Struts2Utils.getText("ftlSubmit",language));
		return "success";
	}
	
	//意见标签
	public String opinionControl() throws Exception {
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		int year =cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		opinionDate = year+"年"+month+"月"+day+"日";
		this.renderText(opinionDate==null?"":opinionDate);
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

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public String getCallbackFun() {
		return callbackFun;
	}

	public void setCallbackFun(String callbackFun) {
		this.callbackFun = callbackFun;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getOpinionRequired() {
		return opinionRequired;
	}

	public void setOpinionRequired(String opinionRequired) {
		this.opinionRequired = opinionRequired;
	}

	public String getOpinionSign() {
		return opinionSign;
	}

	public void setOpinionSign(String opinionSign) {
		this.opinionSign = opinionSign;
	}

	public String getOpinionDate() {
		return opinionDate;
	}

	public void setOpinionDate(String opinionDate) {
		this.opinionDate = opinionDate;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
