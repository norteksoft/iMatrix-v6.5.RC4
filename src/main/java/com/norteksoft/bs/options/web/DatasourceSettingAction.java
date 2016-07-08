package com.norteksoft.bs.options.web;


import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.norteksoft.bs.options.entity.DatasourceSetting;
import com.norteksoft.bs.options.service.DatasourceSettingManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PageUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Namespace("/options")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "import-definition", type = "redirectAction")})
public class DatasourceSettingAction extends CrudActionSupport<DatasourceSetting> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private DatasourceSetting datasourceSetting;
	private Page<DatasourceSetting> page=new Page<DatasourceSetting>(0,true);
	private String ids;
	private String datasourceCode;
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	
	private String dataBaseUrlTest;
	private String userNameTest;
	private String dataBasePasswordTest;
	private String driveNameTest;
	private String testSqlTest;
	
	protected void addErrorMessage(String message){
		this.addActionMessage(ERROR_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	
	@Autowired
	private DatasourceSettingManager datasourceSettingManager;
	
	@Override
	@Action("datasource-setting-delete")
	public String delete() throws Exception {
		datasourceSettingManager.delete(ids);
		this.addSuccessMessage(Struts2Utils.getText("basicSetting.deleteSuccess"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.deleteDataSource"), ContextUtils.getSystemId("bs"));
		return "datasource-list-data";
	}

	@Override
	@Action("datasource-setting-input")
	public String input() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigurePage"), ContextUtils.getSystemId("bs"));
		return "datasource-input";
	}
	public void prepareView() throws Exception {
		prepareModel();
	}
	@Action("datasource-setting-view")
	public String view() throws Exception {
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.viewDataSourceConfigure"), ContextUtils.getSystemId("bs"));
		return "datasource-view";
	}

	@Override
	@Action("datasource-setting-list")
	public String list() throws Exception {
		return "datasource-list";
	}
	@Action("datasource-setting-list-data")
	public String listData() throws Exception {
		if(page.getPageSize()>1){
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigureList"), ContextUtils.getSystemId("bs"));
			datasourceSettingManager.getDatasourceSettingPage(page);
			this.renderText(PageUtils.pageToJson(page));
			return null;
		}
		return "datasource-list-data";
	}

	@Override
	protected void prepareModel() throws Exception {
		if(id==null){
			datasourceSetting=new DatasourceSetting();
			String tempDataBaseUrl = datasourceSetting.getDataBaseUrl();
			if(StringUtils.isNotEmpty(tempDataBaseUrl)&&tempDataBaseUrl.indexOf("${")==0){
				tempDataBaseUrl = tempDataBaseUrl.replace("${","").replace("}","");
				datasourceSetting.setDataBaseUrl(Struts2Utils.getText(tempDataBaseUrl));
			}
		}else{
			datasourceSetting=datasourceSettingManager.getDatasourceSetting(id);
		}
	}
	/**
	 * 验证数据源编码是否存在
	 * @return
	 * @throws Exception
	 */
	@Action("datasource-setting-validate-code")
	public String validateCode() throws Exception{
		boolean isExist=datasourceSettingManager.isDatasourceExist(datasourceCode,id);
		if(isExist){//存在
			this.renderText("true");
		}else{
			this.renderText("false");
		}
		return null;
	}

	@Override
	@Action("datasource-setting-save")
	public String save() throws Exception {
		if(id==null){
			datasourceSetting.setCreatorName(ContextUtils.getUserName());
			datasourceSetting.setCreatedTime(new Date());
		}
		datasourceSettingManager.saveDatasourceSetting(datasourceSetting);
		this.addSuccessMessage(Struts2Utils.getText("form.save.success"));
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.saveDataSourceConfigure"), ContextUtils.getSystemId("bs"));
		return "datasource-input";
	}
	/**
	 * 数据源连接测试
	 * @return
	 * @throws Exception
	 */
	@Action("datasource-setting-test")
	public String dataSourceTest() throws Exception{
		String testResult="";
		try {
			testResult=datasourceSettingManager.testDatasource(dataBaseUrlTest,userNameTest,dataBasePasswordTest,driveNameTest,testSqlTest);
			ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.dataSourceConfigure"), ApiFactory.getBussinessLogService().getI18nLogInfo("bs.testDataSource"), ContextUtils.getSystemId("bs"));
		}catch (SQLException e) {
			testResult = "false;"+e.getMessage();
		}catch (ClassNotFoundException e) {
			testResult = "false;"+e.getMessage();
		}
		this.renderText(testResult);
		return null;
	}
	/**
	 * 删除或修改时验证数据源是否被普通接口和定时使用
	 * @return
	 * @throws Exception
	 */
	@Action("datasource-setting-use-validate")
	public String interfaceUseDatasourceForDelete() throws Exception{
		this.renderText(datasourceSettingManager.validateDatasourceUse(ids));
		return null;
	}
	

	public DatasourceSetting getModel() {
		return datasourceSetting;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DatasourceSetting getDatasourceSetting() {
		return datasourceSetting;
	}

	public void setDatasourceSetting(DatasourceSetting datasourceSetting) {
		this.datasourceSetting = datasourceSetting;
	}

	public Page<DatasourceSetting> getPage() {
		return page;
	}

	public void setPage(Page<DatasourceSetting> page) {
		this.page = page;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setDatasourceCode(String datasourceCode) {
		this.datasourceCode = datasourceCode;
	}
	public void setDataBaseUrlTest(String dataBaseUrlTest) {
		this.dataBaseUrlTest = dataBaseUrlTest;
	}
	public void setUserNameTest(String userNameTest) {
		this.userNameTest = userNameTest;
	}
	public void setDataBasePasswordTest(String dataBasePasswordTest) {
		this.dataBasePasswordTest = dataBasePasswordTest;
	}
	public void setDriveNameTest(String driveNameTest) {
		this.driveNameTest = driveNameTest;
	}
	public void setTestSqlTest(String testSqlTest) {
		this.testSqlTest = testSqlTest;
	}
	
}
