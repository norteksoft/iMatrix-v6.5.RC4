package com.norteksoft.bs.options.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.bs.options.entity.InternationOption;
import com.norteksoft.bs.options.service.InternationManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "internation", type = "redirectAction")})
public class InternationAction extends CrudActionSupport<Internation>{
	private static final long serialVersionUID = 1L;
	@Autowired
	private InternationManager internationManager;
	private Page<Internation> pages=new Page<Internation>(0,true);
	private Page<InternationOption> interOptions=new Page<InternationOption>(0,true);
	private Internation internation;
	private Long id;
	private String ids;
	private String interCode;
	private String type;
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	/**
	 * 删除国际化设置
	 */
	@Override
	@Action("internation-delete")
	public String delete() throws Exception {
		internationManager.deleteInternations(ids);
		addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.deleteInternation"),ContextUtils.getSystemId("bs"));
		return "internation";
	}

	/**
	 * 国际化设置表单
	 */
	@Override
	@Action("internation-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationSetform"),ContextUtils.getSystemId("bs"));
		return "internation-input";
	}

	/**
	 * 国际化列表
	 */
	@Override
	public String list() throws Exception {
		if(StringUtils.isNotEmpty(type)){
			if(pages.getPageSize()>1){
				internationManager.getInternations(pages,internationManager.getInternationTypeByCode(type));
				this.renderText(PageUtils.pageToJson(pages));
				return null;
			}
		}
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.viewInternationList"),ContextUtils.getSystemId("bs"));
		return SUCCESS;
	}
	
	/**
	 * 子表内容(国际化选项表)
	 * @return
	 */
	@Action("internation-chiledList")
	public String chiledList() throws Exception {
		if(interOptions.getPageSize()>1){
			if(id!=null){
				interOptions = internationManager.getInternationOptions(interOptions,id);
				this.renderText(PageUtils.pageToJson(interOptions));
			}
		}
		return null;
	}
	

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			internation=new Internation();
		}else{
			internation=internationManager.getInternation(id);
		}
	}

	/**
	 * 保存国际化设置
	 */
	@Override
	@Action("internation-save")
	public String save() throws Exception {
		internation.setInternationType(internationManager.getInternationTypeByCode(type));
		internationManager.saveInternation(internation,interCode);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.saveInternationConfigure"),ContextUtils.getSystemId("bs"));
		return "internation-input";
	}
	/**
	 * 选择语言种类树页面
	 * @return
	 * @throws Exception
	 */
	@Action("internation-select-category")
	public String selectCategory() throws Exception {
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		String result ="";
		List<com.norteksoft.product.api.entity.Option> options=ApiFactory.getSettingService().getOptionsByGroupCode("internation");
		for(com.norteksoft.product.api.entity.Option option:options){
			ZTreeNode root = new ZTreeNode(option.getId()+"","0",option.getName(), "false", "false", "", "", "folder", "");
			treeNodes.add(root);
		}
		result = JsonParser.object2Json(treeNodes);
		renderText(result);
		return null;
	}
	/**
	 * 验证编码是否存在
	 * @return
	 * @throws Exception
	 */
	@Action("internation-check-code")
	public String checkCode() throws Exception{
		this.renderText(internationManager.isInternationExist(interCode,id,internationManager.getInternationTypeByCode(type))+"");
		return null;
	}
	@Action("internation-update-cache")
	public String updateCache() throws Exception{
		internationManager.initAllInternations();
		addSuccessMessage(Struts2Utils.getText("basicSetting.updateCache"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.updateInternationCache"),ContextUtils.getSystemId("bs"));
		return "internation";
	}
	/**
	 * 删除国家化项目
	 * @return
	 * @throws Exception
	 */
	@Action("delete-internation-option")
	public String deleteInternationOption() throws Exception {
		internationManager.deleteInternationOption(id);
		String callback=Struts2Utils.getParameter("callback");
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.internationConfigure"), 
				ApiFactory.getBussinessLogService().getI18nLogInfo("bs.deleteInternationProject"), 
				ContextUtils.getSystemId("bs"));
		this.renderText(callback+"({msg:'"+Struts2Utils.getText("basicSetting.deleteSuccess")+"'})");
		return null;
	}

	public Internation getModel() {
		return internation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<Internation> getPages() {
		return pages;
	}

	public Page<InternationOption> getInterOptions() {
		return interOptions;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	public Internation getInternation() {
		return internation;
	}
	public void setInterCode(String interCode) {
		this.interCode = interCode;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}

}
