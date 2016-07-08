package com.norteksoft.wf.base.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;



import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.WorkflowInstance;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.tree.ZTreeNode;
import com.norteksoft.product.web.struts2.Struts2Utils;
import com.norteksoft.wf.base.enumeration.TextOperator;

public class WorkflowUtil {
	private static final String SQUARE_BRACKETS_LEFT = "[";
	private static final String SQUARE_BRACKETS_RIGHT = "]";
	/**
	 * 生成减签树节点，树节点id规则为：根结点id为company_company~~company~~company~~company，办理人节点id为：user~~+ transactorName+~~+transactor+~~+transactorId+~~+taskId
	 * @param taskId
	 * @return 所有办理人节点
	 */
	public static List<ZTreeNode> generateRemoveSingerTree(Long taskId){
		List<ZTreeNode> treeNodes = new ArrayList<ZTreeNode>();
		if(taskId!=null){
			com.norteksoft.product.api.entity.WorkflowTask task = ApiFactory.getTaskService().getTask(taskId);
			
			WorkflowInstance instance = ApiFactory.getInstanceService().getInstance(task.getProcessInstanceId());
			String name = "";
			if(instance.getSubCompanyId()==null){
				name = ContextUtils.getCompanyName();
			}else{
				name = instance.getSubCompanyName();
			}
			String parentId = "company_company~~company~~company~~company";
			ZTreeNode root = new ZTreeNode(parentId,"0", name, "true", "false", "", "", "root", "");
			treeNodes.add(root);
			
			List<com.norteksoft.product.api.entity.WorkflowTask> currentTasks=ApiFactory.getTaskService().getActivityTaskByInstance(task.getProcessInstanceId());
			List<com.norteksoft.product.api.entity.WorkflowTask> currentPrincipalTasks=ApiFactory.getTaskService().getActivityPrincipalTasks(task.getProcessInstanceId());
			int transactNum=currentTasks.size()+currentPrincipalTasks.size();
			if(transactNum>1){
				boolean hasBranch = ContextUtils.hasBranch();
				for(int i=0;i<currentTasks.size();i++){
					com.norteksoft.product.api.entity.WorkflowTask currentTask=currentTasks.get(i);
					if(!taskId.equals(currentTask.getId())){//减签时如果不是当前打开的任务
						String showName = currentTask.getTransactorName();
						if(hasBranch){
							showName = currentTask.getTransactorName()+"/"+currentTask.getTransactorSubCompanyName();
						}
						Long transactorId = currentTask.getTransactorId();
						if(transactorId==null){
							User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTransactor());
							if(u!=null)transactorId = u.getId();
						}
						String nodeId = "user~~"+ currentTask.getTransactorName()+"~~"+currentTask.getTransactor()+"~~"+(transactorId==null?"":transactorId)+"~~"+currentTask.getId();
						root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
						treeNodes.add(root);
					}
				}
				for(com.norteksoft.product.api.entity.WorkflowTask currentTask:currentPrincipalTasks){
					if(!taskId.equals(currentTask.getId())){//减签时如果不是当前打开的任务
						String showName = currentTask.getTransactorName()+"（"+Struts2Utils.getText("workflow.history.entrusted.info")+currentTask.getTrustorName()+")";
						if(hasBranch){
							showName = currentTask.getTransactorName()+"/"+currentTask.getTransactorSubCompanyName()+"（"+Struts2Utils.getText("workflow.history.entrusted.info")+currentTask.getTrustorName()+"/"+currentTask.getTrustorSubCompanyName()+")";
						}
						Long trustorId = currentTask.getTrustorId();
						if(trustorId==null){
							User u = ApiFactory.getAcsService().getUserByLoginName(currentTask.getTrustor());
							if(u!=null)trustorId = u.getId();
						}
						String nodeId = "user~~"+ currentTask.getTrustorName()+"~~"+currentTask.getTrustor()+"~~"+(trustorId==null?"":trustorId)+"~~"+currentTask.getId();
						root = new ZTreeNode(nodeId,parentId, showName, "true", "false", "", "", "user", "");
						treeNodes.add(root);
					}
				}
			}
			
		}
		return treeNodes;
	}
	
	//是否是意见控件的结论
	public static boolean isOpinionConclusion(String fieldName){
		//意见1-结论[yijian1~~conclusion] operator.text.et '离职率[demo_plan_template~~lizhilv]'
		//fieldName为yijian1~~conclusion
		if(fieldName.indexOf("~~")>0){
			return true;
		}
		return false;
	}
	//解析意见控件的结论
	public static boolean parseOpinionConclusion(String atomicExpress,String workflowId ){
		//atomicExpress：意见1-结论[yijian1~~conclusion] operator.text.et '离职率[demo_plan_template~~lizhilv]'
		String name = atomicExpress.substring(atomicExpress.indexOf(SQUARE_BRACKETS_LEFT)+1, atomicExpress.indexOf(SQUARE_BRACKETS_RIGHT));//获得yijian1~~conclusion
		String value = atomicExpress.substring(atomicExpress.lastIndexOf(SQUARE_BRACKETS_LEFT)+1, atomicExpress.lastIndexOf(SQUARE_BRACKETS_RIGHT));//获得demo_plan_template~~lizhilv
		String controlId = name.substring(0,name.lastIndexOf("~~"));//意见控件id  yijian1
//		String groupCode = value.substring(0,value.lastIndexOf("~~"));//选项组编码 demo_plan_template
		String optionValue = value.substring(value.lastIndexOf("~~")+2);//选项值 lizhilv
		String  conclusion= ApiFactory.getOpinionService().getOpinionConclusion(workflowId, controlId);//根据意见控件id和实例id获得对应的意见结论
		if(StringUtils.isEmpty(conclusion))return false;
		String operator = atomicExpress.substring(atomicExpress.indexOf("]")+1,atomicExpress.indexOf("\'")).trim() ;
		if(StringUtils.contains(operator, TextOperator.NET.getCode())){
			return !conclusion.equals(optionValue);
		}else if(StringUtils.contains(operator, TextOperator.ET.getCode())){
			return conclusion.equals(optionValue);
		}
		throw new RuntimeException(atomicExpress + " is invalid expression.");
	}
}
