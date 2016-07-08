package com.norteksoft.acs.service.organization;

/**
 * 同步组织结构接口
 * 
 * 在acs权限系统/集成参数设置/iMatrix集成下的同步组织结构功能
 * @author ldx
 *
 */
public abstract class SynchroOrganizationService {
	/**
	 * 获得公司的组织结构信息
	 * 例如：
	 * iMatrix服务1和iMatrix服务2同步组织机构，且1和2不是相同的版本，如1的iMatrix版本为6.0,2的iMatrix版本为6.1，
	 * 如果1同步到2中，则在2中配置1的iMatrix服务地址，且在1的代码中需要有个实现该接口的类，且重写了该方法，
	 * 使1的版本返回的组织机构格式可以与2所需的格式匹配，使2的版本可以正确解析1版本的组织机构
	 * @return 组织结构信息
	 */
	public String findOrganization(Long companyId){return null;}
	/**
	 * 同步远程iMatrix服务指定公司的组织结构
	 *例如：
	 * iMatrix服务1和iMatrix服务2同步组织机构，且1和2不是相同的版本，如1的iMatrix版本为6.0,2的iMatrix版本为6.1，
	 * 如果1同步到2中，则在2中配置1的iMatrix服务地址，且在2的代码中需要有个实现该接口的类，且重写了该方法，
	 * 使2的版本可以正确的解析1的组织机构格式，完成同步
	 * @param remoteUserInfo 配置的iMatrix服务的地址返回的用户信息
	 * @return 如果同步成功请将返回值设为"success",否则返回异常信息
	 */
	public String synchroOrganization(String remoteUserInfo){return null;}
}
