package com.norteksoft.task.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.portal.service.IndexManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.SystemUrls;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.task.entity.Task;
import com.norteksoft.task.service.TaskManager;

//@WebService(endpointInterface = "com.norteksoft.task.webservice.TaskWebservice")
@Service
@Transactional
public class TaskWebserviceImpl implements TaskWebservice{
	
	private TaskManager taskManager;
	public final static String TASK_SYSTEM_CODE = "task";
	public final static String TASK_INPUT_URL = "/task/task-input.htm?id=";
	
	@Autowired
	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	public String personalTasks(List<String> prmtNames, List<String> prmtValues) {
		Map<String, String> prmts = processParameter(prmtNames, prmtValues);
		String loginName = prmts.get("loginName");
		Long userId = Long.valueOf(prmts.get("userId"));
		Long companyId = Long.valueOf(prmts.get("companyId"));
		return personalTasks(loginName,userId, companyId, 5,"createdTime",Struts2Utils.ZH_CN);
	}
	
	@Transactional(readOnly=true)
	public String personalTasks(String loginName,Long userId, Long companyId, Integer size, String order,String currentLanguage) {	
		return getTaskTable(loginName,userId,companyId,size,order,null,currentLanguage);
	}
	@Transactional(readOnly=true)
	public String detailTasks(String loginName,Long userId, Long companyId, Integer size, String order, String typeName,String currentLanguage) {	
		return getTaskTable(loginName,userId,companyId,size,order,typeName,currentLanguage);
	}
	
	private String getTaskTable(String loginName,Long userId, Long companyId, Integer size, String order, String typeName,String currentLanguage){
		List<Task> tasks = null;
		if(StringUtils.isNotEmpty(typeName)){
			tasks = taskManager.getDetailTasksByUserType(companyId,loginName,userId,typeName,size,order);
		}else{
			tasks = taskManager.getPersonalTasks(loginName,userId, companyId, size,order);
		}
		processTaskCreator(tasks);
		List<String> headNames = new ArrayList<String>();
		headNames.add(Struts2Utils.getText("task.title",currentLanguage));
		headNames.add(Struts2Utils.getText("task.createdTime",currentLanguage));
		headNames.add(Struts2Utils.getText("task.creatorName",currentLanguage));
		
		List<String> propNames = new ArrayList<String>();
		propNames.add("title");
		propNames.add("createdTime");
		propNames.add("creatorName");
		
		return generatTable(headNames, tasks, propNames);
	}
	
	private void processTaskCreator(List<Task> tasks) {
		for(Task task : tasks){
			Object o = null;
			if(task.getCreatorId()==null){
				o = ApiFactory.getAcsService().getUserById(task.getCreatorId());
			}else{
				o = ApiFactory.getAcsService().getUserByLoginName(task.getCreator());
			}
			if(o != null){
				task.setCreator(getBeanProp(o, "name"));
			}
		}
	}

	private Map<String, String> processParameter(List<String> prmtNames, List<String> prmtValues){
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < prmtNames.size(); i++){
			map.put(prmtNames.get(i), prmtValues.get(i));
		}
		return map;
	}
	
	private String generatTable(List<String> headNames, List<? extends Object> objs, List<String> propNames){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("TABLE");
		root.addAttribute("class", "leadTable");
		generatTHead(root, headNames);
		generatTBody(root, objs, propNames);
		return root.asXML();
	}
	
	/*
	 * 生成表头 
	 */
	private void generatTHead(Element root, List<String> headNames){
		Element thead = root.addElement("THEAD");
		Element tr = thead.addElement("TR");
		Element td = null;
		for(String headName : headNames){
			td = tr.addElement("TH");
			td.setText(headName);
			if(!Struts2Utils.getText("task.title").equals(headName)){
				td.addAttribute("style", "width: 15%;");
			}
		}
	}

	/*
	 * 生成表体
	 */
	private void generatTBody(Element root, List<? extends Object> values, List<String> props){
		Element tbody = root.addElement("TBODY");
		if(CollectionUtils.isEmpty(values) || CollectionUtils.isEmpty(props)) 
			return;
		Element tr = null;
		Element tagA = null;
		for(Object bean : values){
			tr = tbody.addElement("TR");
			for(String prop : props){
				if("createdTime".equals(prop)){
					tr.addElement("TD").setText(getDataProp(bean, prop));
				}else if("creatorName".equals(prop)){
					String name = getBeanProp(bean, prop);
					if(StringUtils.isEmpty(name)){
						name = getBeanProp(bean, "creator");
					}
					tr.addElement("TD").setText(name);
				}else if("title".equals(prop)){
					String taskActionInputUrl;
					String style="";
					String title = getBeanProp(bean, prop);
					String transferName = getBeanProp(bean, "transferName");
					String trustorName = getBeanProp(bean, "trustorName");
					if(StringUtils.isNotEmpty(transferName)){
						if(StringUtils.isNotEmpty(trustorName)){
							style = "color:#CC0000;";
							title = "（"+transferName+"移交于"+trustorName+","+trustorName+"委托）"+title;
						}else{
							style = "color:#CC0000;";
							title = "（"+transferName+"移交）"+title;
						}
					}else{
						if(StringUtils.isNotEmpty(trustorName)){
							style = "color:#CC0000;";
							title = "（"+trustorName+"委托）"+title;
						}
					}
					try {
						taskActionInputUrl = getSystemUrl(TASK_SYSTEM_CODE);
						Task task = taskManager.getTaskById(Long.valueOf(getBeanProp(bean, "id")));
						if(!task.getRead()){
							tagA = tr.addElement("TD").addElement("A")
							.addAttribute("href", "#")
							.addAttribute("onclick", "popWindow(this,'"+taskActionInputUrl
									+getBeanProp(bean, "id")
									+"', 'task');")
									.addAttribute("style", "font-weight:bold;"+style);
						}else{
							tagA = tr.addElement("TD").addElement("A")
							.addAttribute("href", "#")
							.addAttribute("onclick", "popWindow(this,'"+taskActionInputUrl
									+getBeanProp(bean, "id")
									+"', 'task');")
										.addAttribute("style", style);
						}
						tagA.setText(title);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private String getSystemUrl(String key) throws Exception{
		String url = SystemUrls.getSystemUrl(key);
		url += TASK_INPUT_URL;
		return url;
	}
	
	private String getDataProp(Object bean, String propName){
		String value = null;
		try {
			value = BeanUtils.getProperty(bean, propName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if(value != null && value.length() >= 19){
			value = value.substring(0, 10);
		}
		return value == null?  "" : value;
	}
	
	/*
	 * 根据属性名从对象中取属性值
	 */
	private String getBeanProp(Object bean, String propName){
		String value = null;
		try {
			value = BeanUtils.getProperty(bean, propName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return value == null?  "" : value;
	}
}
