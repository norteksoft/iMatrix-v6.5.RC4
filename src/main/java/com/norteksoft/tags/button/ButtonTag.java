package com.norteksoft.tags.button;

import java.util.ArrayList;
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

import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.mms.module.entity.Button;
import com.norteksoft.mms.module.entity.ModulePage;
import com.norteksoft.mms.module.service.ButtonManager;
import com.norteksoft.mms.module.service.ModulePageManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.freemarker.TagUtil;
import com.norteksoft.product.web.struts2.Struts2Utils;
public class ButtonTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private String code;

	private Log log = LogFactory.getLog(ButtonTag.class);
	
	private ModulePageManager modulePageManager;
	
	private ButtonManager buttonManager;
	
	@Override
	public int doStartTag() throws JspException {
		modulePageManager = (ModulePageManager)ContextUtils.getBean("modulePageManager");
		
		ModulePage modulePage = modulePageManager.getModulePage(code);
		
		buttonManager = (ButtonManager)ContextUtils.getBean("buttonManager");
		List<Button> buttons = null;
		if(modulePage != null){
//			buttons = modulePage.getButtons();
			buttons = buttonManager.getUnDeletedButtonsByPageId(modulePage.getId());
		}
		if(buttons != null && buttons.size()>0){
			JspWriter out=pageContext.getOut(); 
			try {
				out.print(readScriptTemplate(buttons));
			} catch (Exception e) {
				log.error(e);
				throw new JspException(e);
			}
		}
		return Tag.EVAL_PAGE;
	}
	
	private String readScriptTemplate(List<Button> buttons) throws Exception {
		Map<String, Object> root=new HashMap<String, Object>();
		for(Button button:buttons){
			button.setInternationalizationName(getNameToi18n(button.getName()));
		}
		root.put("buttons", buttons);
		String result =TagUtil.getContent(root, "button/ButtonTag.ftl");
		return result;
	}
	
	private String getNameToi18n(String name){
		if(name==null)return name;
		if(name.contains("${")&&name.contains("}")){
			name = name.substring(name.indexOf("${")+2,name.indexOf("}"));
			name = ApiFactory.getSettingService().getInternationOptionValue(name, ContextUtils.getCurrentLanguage(), InternationType.PUBLIC_RESOURCE.toString());
			if(StringUtils.isNotEmpty(name)){
				return name;
			}
		}
		return name;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return Tag.EVAL_PAGE;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
