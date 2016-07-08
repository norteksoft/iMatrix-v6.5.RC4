package com.norteksoft.tags.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.Opinion;
import com.norteksoft.product.api.entity.Option;
import com.norteksoft.product.api.entity.WorkflowTask;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.product.util.freemarker.TagUtil;

public class WriteOpinionTag extends TagSupport{
	private static final long serialVersionUID = 3L;
	private Log log = LogFactory.getLog(OpinionTag.class); 
	
	private Long taskId;//任务id
	private String controlId;//控件id
	private Integer width;//宽度
	private Integer height;//高度
	private String optionGroupCode;//选项组编号
	private String controlType="radio";//结论的控件,默认为单选
	private String position="top";//结论的位置,默认为top意见框的上方
	private String signRename="签名";//签名重命名
	private String dateRename="日期";//日期重命名
	
	@Override
	public int doStartTag() throws JspException {
		JspWriter out=pageContext.getOut();
		try {
			out.println(readTemplet());
		} catch (Exception e) {
			log.error(e);
			throw new JspException(e);
		}
		return Tag.EVAL_PAGE;
	}
	
	private String readTemplet() throws Exception {
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("position", position);
		root.put("taskId", (taskId==null)?0:taskId);
		root.put("controlId", controlId);
		root.put("width", (width==null || width==0)?300:width);
		root.put("height", (height==null || height==0)?100:height);
		root.put("userName", ContextUtils.getUserName());
		root.put("signRename", signRename);
		root.put("dateRename", dateRename);
		if(StringUtils.isNotEmpty(optionGroupCode)){
			root.put("controlType", controlType);
		}else{
			root.put("controlType", "");
		}
		WorkflowTask task= null;
		Opinion opi = null;
		if(taskId!=null && taskId!=0){
			task= ApiFactory.getTaskService().getTask(taskId);
			opi = ApiFactory.getOpinionService().getOpinion(task.getProcessInstanceId(), controlId, task.getCompanyId());
		}
		if(task==null){
			root.put("opinion", "");
			root.put("opinionSign", "");
			root.put("opinionDate", "");
		}else{
			root.put("opinion", opi==null?"":opi.getOpinion());
			if(opi!=null){
				root.put("opinionSign", getOpinionPartMessage(opi,"opinionSign"));
				root.put("opinionDate", getOpinionPartMessage(opi,"opinionDate"));
			}else{
				root.put("opinionSign", "");
				root.put("opinionDate", "");
			}
		}
		if(StringUtils.isNotEmpty(optionGroupCode)){
			 List<Option> options=ApiFactory.getSettingService().getOptionsByGroupCode(optionGroupCode);
			 String tempOption="";
			 for(Option o:options){
				 if(StringUtils.isEmpty(o.getName())&&StringUtils.isEmpty(o.getValue()))break;
				 if("checkbox".equals(controlType)){
					 tempOption+="<input name='_iMatrix_opinionConclusion_"+controlId+"' ";
				 }else{
					 tempOption+="<input name='_iMatrix_opinionRadioConclusion_"+controlId+"' ";
				 }
				 tempOption+=" type='"+controlType+"' ";
				 tempOption+=" onclick=\"_iMatrix_promptlyOpinionSignAndDate('"+controlId+"',this,'"+ContextUtils.getUserName()+"');\" ";
				 if(opi!=null&&validateOpinionConclusion(opi,o.getValue()))tempOption+=" checked='checked'";
				 tempOption+=" value='"+o.getValue()+"'";
				 tempOption+="/>"+o.getName();
			 }
			 root.put("options", tempOption);
		 }else{
			 root.put("options", "");
		 }
		String result =TagUtil.getContent(root, "workflow/writeOpinion.ftl");
		return result;
	}
	
	private boolean validateOpinionConclusion(Opinion opinion,String optionValue){
		 boolean result=false;
		 if(StringUtils.isNotEmpty(opinion.getConclusion())){
			 for(String val:opinion.getConclusion().split(",")){
				 if(val.equals(optionValue)){
					 result=true;
					 break;
				 }
			 }
		 }
		 return result;
	 }
	
	private String getOpinionPartMessage(Opinion opinion,String type){
		 String result="";
		 if(opinion!=null){
			 if("opinionSign".equals(type)){
				 result=StringUtils.isNotEmpty(opinion.getOpinionSign())?opinion.getOpinionSign():"";
			 }else if("opinionDate".equals(type)){
				 result=StringUtils.isNotEmpty(opinion.getOpinionDate())?opinion.getOpinionDate():"";
			 }
		 }
		 return result;
	 }
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}
	
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getControlId() {
		return controlId;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getOptionGroupCode() {
		return optionGroupCode;
	}

	public void setOptionGroupCode(String optionGroupCode) {
		this.optionGroupCode = optionGroupCode;
	}
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getPosition() {
		return position;
	}

	public String getSignRename() {
		return signRename;
	}

	public void setSignRename(String signRename) {
		this.signRename = signRename;
	}

	public String getDateRename() {
		return dateRename;
	}

	public void setDateRename(String dateRename) {
		this.dateRename = dateRename;
	}
	

}
