package com.norteksoft.mm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jwebap.plugin.tracer.TimeFilterAnalyser;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.mm.entity.MonitorInfor;
import com.norteksoft.mm.service.MonitorInforManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;
/**
 * 性能参监控信息
 */
@Namespace("/mm")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "monitor-infor", type = "redirectAction")})
public class MonitorInforAction extends CrudActionSupport<MonitorInfor> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private MonitorInfor monitorInfor;
	
	private Page<MonitorInfor> pages= new Page<MonitorInfor>(50, true);
	
	private List<org.jwebap.plugin.tracer.MonitorInfor> monitorInfors= new ArrayList<org.jwebap.plugin.tracer.MonitorInfor>();
	
	@Autowired
	private MonitorInforManager monitorInforManager;
	
	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private MenuManager menuManager;
	
	private List<BusinessSystem> businessSystems;
	
	private String systemCode;
	
	private String jwebType;
	
	@Override
	@Action("monitor-infor-delete")
	public String delete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Action("monitor-infor-input")
	public String input() throws Exception {
		return null;
	}
	
	/**
	 * 性能监控/性能监控
	 * @return
	 * @throws Exception
	 */
	@Action("monitor-infor-tree")
	public String tree() throws Exception {
		List<ZTreeNode> ztreeNodes = new ArrayList<ZTreeNode>();
		businessSystems=businessSystemManager.getMainBusiness();
		StringBuilder tree = new StringBuilder();
		for (BusinessSystem businessSystem : businessSystems) {
			Menu menu = menuManager.getMenuByCode(businessSystem.getCode());
			ZTreeNode node = new ZTreeNode("root_"+businessSystem.getCode(),"0",menuManager.getNameToi18n(menu.getName()), "true", "false", "", "", "folder", "");
			ztreeNodes.add(node);
			children(businessSystem,ztreeNodes);
//			tree.append("{\"attr\":{\"id\":\"root\"},\"state\":\"open\",\"data\":\""+ businessSystem.getName()+ "\",\"children\":["+children(businessSystem)+"]},");	
		}
		
//		StringBuilder tree = new StringBuilder();
//		tree.append("[");
//		tree.append(parent());
//		tree.append("]");
		
		
		this.renderText(JsonParser.object2Json(ztreeNodes));
		return null;
	}
	
//	private String parent(){
//		businessSystems=businessSystemManager.getMainBusiness();
//		StringBuilder tree = new StringBuilder();
//		for (BusinessSystem businessSystem : businessSystems) {
//			tree.append("{\"attr\":{\"id\":\"root\"},\"state\":\"open\",\"data\":\""+ businessSystem.getName()+ "\",\"children\":["+children(businessSystem)+"]},");	
//		}
//		return StringUtils.removeEnd(tree.toString(),",");
//	}
//	
	private void children(BusinessSystem businessSystem,List<ZTreeNode> zTreeNodes){
		ZTreeNode node = new ZTreeNode(businessSystem.getCode()+ "_http","root_"+businessSystem.getCode(),Struts2Utils.getText("mm.http.monitoring"), "true", "false", "", "", "folder", "");
		zTreeNodes.add(node);
		node = new ZTreeNode(businessSystem.getCode()+ "_jdbc","root_"+businessSystem.getCode(),Struts2Utils.getText("mm.jdbc.monitoring"), "true", "false", "", "", "folder", "");
		zTreeNodes.add(node);
		node = new ZTreeNode(businessSystem.getCode()+ "_meth","root_"+businessSystem.getCode(),Struts2Utils.getText("mm.meth.monitoring"), "true", "false", "", "", "folder", "");
		zTreeNodes.add(node);
//		tree.append("{\"attr\":{\"id\":\""+ businessSystem.getCode()+ "_http\"},\"state\":\"closed\",\"data\":\"http监控\"},");	
//		tree.append("{\"attr\":{\"id\":\""+ businessSystem.getCode()+ "_jdbc\"},\"state\":\"closed\",\"data\":\"jdbc监控\"},");	
//		tree.append("{\"attr\":{\"id\":\""+ businessSystem.getCode()+ "_meth\"},\"state\":\"closed\",\"data\":\"meth监控\"}");	
//		return tree.toString();
	}

	/**
	 * 性能监控/性能监控列表
	 * @return
	 * @throws Exception
	 */
	@Override
	@Action("monitor-infor")
	public String list() throws Exception {
		businessSystems=businessSystemManager.getAllBusiness();
		if(systemCode!=null){
			Map<String,List<String>> monitor_map=TimeFilterAnalyser.monitor_map;
			List<String> keys=monitor_map.get(systemCode+"_"+jwebType);
			if(keys!=null&&!keys.isEmpty()){
				for (String key : keys) {
					org.jwebap.plugin.tracer.MonitorInfor monitorInfor=(org.jwebap.plugin.tracer.MonitorInfor) MemCachedUtils.get(key);
					monitorInfors.add(monitorInfor);
				}
			}
		}
		return "monitor-infor";
	}
	
//	/**
//	 * 性能监控/性能参监控信息 
//	 * @return
//	 * @throws Exception
//	 */
//	 @Action(value="monitor-infor-getList",
//				results={@Result(name="monitor-infor", location="monitor-infor.jsp"),
//						 @Result(name="monitor-infor-getList", location="monitor-infor-getList.jsp")
//						 })
//	public String getList()throws Exception{
//		 monitorInforManager.getMonitorInfor(pages, systemCode, jwebType);
//		return "monitor-infor";
//	}


	@Override
	protected void prepareModel() throws Exception {
		
	}

	@Override
	@Action("monitor-infor-save")
	public String save() throws Exception {
		return null;
	}

	public MonitorInfor getModel() {
		return monitorInfor;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Page<MonitorInfor> getPages() {
		return pages;
	}

	public List<BusinessSystem> getBusinessSystems() {
		return businessSystems;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getJwebType() {
		return jwebType;
	}

	public void setJwebType(String jwebType) {
		this.jwebType = jwebType;
	}

	public List<org.jwebap.plugin.tracer.MonitorInfor> getMonitorInfors() {
		return monitorInfors;
	}
	
}
