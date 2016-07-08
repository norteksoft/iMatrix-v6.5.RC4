package com.norteksoft.mms.base.autoTool.dataTable;

/**
 * 数据表类
 * @author wurong
 */
public class DataTable{
	private String name;//表名
	private String alias;//别名
	private String entityName;//实体名
//	@Enumerated(EnumType.STRING)
//	private DataState tableState;//数据表的状态
//	@Column(name="FK_MENU_ID")
	private Long menuId;//菜单id
	private Long companyId;//公司id
//	private String parentName;//父数据表名
//	private Long parentId;//父数据表id
//	private Boolean deleted=false;//是否已删除
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
