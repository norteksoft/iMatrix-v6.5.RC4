package com.norteksoft.product.api;

import java.util.List;

import com.norteksoft.product.api.entity.WorkflowDefinition;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.wf.engine.entity.WorkflowType;

/**
 * 公开提供给用户使用的工作流定义api
 * @author wurong
 *
 */
public interface WorkflowDefinitionService {
	
	/**
	 * 获取启用且版本最高的流程定义
	 * @param workflowDefinitionName 工作流定义名称
	 * @param companyId 公司id
	 * @return 流程定义
	 */
	public WorkflowDefinition getEnabledHighestVersionWorkflowDefinition(String workflowDefinitionCode);
	
	  /**
     * 根据任务id获得流程定义的id. 如果对应的任务不存在，则返回null
     * @param taskId 任务id
     * @return 流程定义id
     */
    public Long getWorkflowDefinitionIdByTask(Long taskId);
    
    /**
     * 用定义id查询流程定义
     * @param workflowDefinitionId 定义id
     * @return 流程定义
     */
    public WorkflowDefinition getWorkflowDefinition(Long workflowDefinitionId);
    
    /**
	 * 查询指定类型的已启用的流程定义
	 * @param typeNo 流程类型编号
	 * @return 流程定义集合
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByTypeCode(String typeNo);
	/**
	 * 查询指定类型的已启用的流程定义
	 * @param workflowDefinitionCode 流程定义编号
	 * @return 流程定义集合
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByCode(String workflowDefinitionCode);
	/**
	 * 根据流程定义编号和版本获得流程定义
	 * @param workflowDefinitionCode
	 * @param workflowDefinitionVersion
	 * @return
	 */
	public WorkflowDefinition getWorkflowDefinitionByCodeAndVersion(String workflowDefinitionCode,Integer workflowDefinitionVersion);
	/**
	 * 获得是审批系统的流程类型
	 * @return
	 */
	public List<WorkflowType> getApproveSystemWorkflowTypes();
	
	public List<WorkflowDefinition> getWorkflowDefinitionsByFormCodeAndVersion(String formCode,Integer version);
	
	/**
	 * 根据流程名称模糊查询某类别下的流程
	 * @param companyId
	 * @param typeId
	 * @return
	 */
	public List<WorkflowDefinition> getWorkflowDefinitionsByName(String typeNo,String name);
	/**
	 * 根据jbpm流程id查询流程定义
	 * @param processId
	 * @return
	 */
	public WorkflowDefinition getWorkflowDefinitionByProcessId(String processId);
	/**
	 * 根据表单编码和表单版本查询流程定义列表
	 * @param formCode
	 * @param version
	 * @param systemId
	 * @return
	 */
	public List<WorkflowDefinition> getEnableWorkflowDefinitionsByFormCodeAndVersion(String formCode,Integer version,Long systemId);
	/**
	 * 启用流程定义
	 * @param definitionId
	 * @param state
	 */
	public void enableDefinition(Long definitionId);
	/**
	 * 启用流程定义
	 * @param workflowDefinitionCode
	 * @param workflowDefinitionVersion
	 * @param state
	 */
	public void enableDefinition(String workflowDefinitionCode,Integer workflowDefinitionVersion);
	/**
	 * 启用流程定义
	 * @param workflowDefinitionCode
	 * @param state
	 */
	public void enableDefinition(String workflowDefinitionCode);
	/**
	 * 删除流程定义
	 * @param definitionId
	 */
	public void deleteDefinition(Long definitionId);
	/**
	 * 删除流程定义
	 * @param definitionId
	 */
	public void deleteDefinition(String workflowDefinitionCode,Integer workflowDefinitionVersion);
	/**
	 * 删除流程定义
	 * @param definitionId
	 */
	public void deleteDefinition(String workflowDefinitionCode);
	/**
	 * 拷贝definitionId对应的流程定义并修改其流程编码为definitionCode，修改流程名称为definitionName
	 * @param definitionId
	 * @param definitionName 
	 * @param definitionCode 
	 */
	public Long cloneDefinition(Long definitionId,String definitionName,String definitionCode);
}
