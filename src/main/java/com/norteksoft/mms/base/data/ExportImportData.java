package com.norteksoft.mms.base.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.organization.Company;
import com.norteksoft.acs.service.AcsUtils;
import com.norteksoft.acs.service.authorization.BusinessSystemManager;
import com.norteksoft.acs.service.organization.CompanyManager;
import com.norteksoft.mms.base.utils.ImportExportExecption;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.mms.module.service.MenuManager;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ThreadParameters;
import com.norteksoft.wf.engine.service.WorkflowDefinitionManager;

@Service
@Transactional
public class ExportImportData implements DataTransfer {
	private Log log = LogFactory.getLog(ExportImportData.class);

	@Autowired
	private BusinessSystemManager businessSystemManager;
	@Autowired
	private WorkflowDefinitionManager workflowDefinitionManager;
	@Autowired
	private FormViewManager formViewManager;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private DataHandle dataHandle;
	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private AcsUtils acsUtils;
	
	/**
	 * 导出
	 * @param systemIds 导出的系统id集合，以逗号隔开
	 * @param companyId 数据所在的公司id
	 * @param fileConfig 文件配置
	 */
	public void backup(String systemIds, Long companyId,
			FileConfigModel fileConfig) throws RuntimeException{
		boolean isStartWith=false;
		try {
			ThreadParameters parameters=new ThreadParameters(companyId, null);
			ParameterUtils.setParameters(parameters);
			File file =null;
			OutputStream out=null;
			if(StringUtils.isNotEmpty(fileConfig.getFilename())){
				file=new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileConfig.getFilename()+".xls");
				out=new FileOutputStream(file);
			}
			isStartWith="ACS_FUNCTION".equals(fileConfig.getData())||"ACS_ROLE".equals(fileConfig.getData())||"MMS_DATA_TABLE".equals(fileConfig.getData())||"MMS_LIST_VIEW".equals(fileConfig.getData())||"MMS_FORM_VIEW".equals(fileConfig.getData());
			if(!isStartWith){
				printlExportMsg(fileConfig);
			}
			if("ACS_SYSTEM".equals(fileConfig.getData())){
				dataHandle.exportSystem(out, systemIds);
			}else if("ACS_PRODUCT".equals(fileConfig.getData())){
				dataHandle.exportProductBySystem(out, systemIds);
			}else if("WF_TYPE".equals(fileConfig.getData())){
				dataHandle.exportDefinitionType(out,companyId);
			}else if("WF_DEFINATION".equals(fileConfig.getData())){
				dataHandle.exportDefinition(out,companyId,systemIds);
			}else if("JOB_INFO".equals(fileConfig.getData())){
				dataHandle.exportJobInfo(out,companyId,systemIds);
			}else if("MMS_MENU".equals(fileConfig.getData())){
				dataHandle.exportMenuBySystem(out,systemIds,companyId);
			}else if("OPTION_GROUP".equals(fileConfig.getData())){
				dataHandle.exportOption(out,systemIds,companyId);
			}else if("PORTAL_INFO".equals(fileConfig.getData())){
				dataHandle.exportPortal(out,systemIds,companyId);
			}else if("RANK".equals(fileConfig.getData())){
				dataHandle.exportRank(out,companyId);
			}else if("DATA_DICT".equals(fileConfig.getData())){
				dataHandle.exportDataDictionary(out,companyId);
			}else if("INTERNATION".equals(fileConfig.getData())){
				dataHandle.exportInternation(out,companyId);
			}else if("OPERATION".equals(fileConfig.getData())){
				dataHandle.exportOperation(out,systemIds,companyId);
			}else if(isStartWith){
				if(StringUtils.isNotEmpty(systemIds)){
					String[] sysIds=systemIds.split(",");
					for(String systemId:sysIds){
						if(StringUtils.isNotEmpty(systemId)){
							exportDatas(fileConfig,Long.parseLong(systemId),companyId);
						}
					}
				}else{
					List<BusinessSystem> systems=null;
					try {
						systems=businessSystemManager.getAllSystems();
					} catch (Exception e) {
						throw new ImportExportExecption(ContextUtils.showTraces(e));
					}
					for(BusinessSystem system:systems){
						exportDatas(fileConfig,system.getId(),companyId);
					}
				}
			}
		} catch (IOException e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
	}
	
	public void printlExportMsg(FileConfigModel fileConfig) {
		if(fileConfig!=null&&fileConfig.getFilename()!=null&&!fileConfig.getFilename().equals("null")){
			System.out.println("正在获取导出文件："+fileConfig.getFilename()+".xls");
		}
	}
	public void printlExportMsg(String fileName) {
			System.out.println("正在获取导出文件："+fileName+".xls");
		
	}
	


	private void exportDatas(FileConfigModel fileConfig,Long systemId,Long companyId) throws ImportExportExecption{
		try {
			String fileName=fileConfig.getFilenameStartwith();
			BusinessSystem system=null;
			if(systemId!=null){
				system=businessSystemManager.getBusiness(systemId);
				fileName=fileName+"-"+system.getCode();
			}
			File file = null;
			OutputStream out=null;
			if("ACS_FUNCTION".equals(fileConfig.getData())){
				file = new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileName+".xls");
				out=new FileOutputStream(file);
				printlExportMsg(fileName);
				dataHandle.exportFunctions(out, systemId,null);
				
			}else if("ACS_ROLE".equals(fileConfig.getData())){
				//导出系统公共角色
				file = new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileName+".xls");
				out=new FileOutputStream(file);
				printlExportMsg(fileName);
				dataHandle.exportRole(out, systemId,null,companyId);
				//导出公司角色
				FileConfigModel config=dataHandle.getFileConfigByData("ACS_ROLE_COMPANY");
				//创建导出文件夹，导出的文件暂存的位置
				File folder = new File(config.getExportRootPath()+"/"+config.getExportPath());
				if(!folder.exists()){
					folder.mkdirs();
				}
				
				fileName=config.getFilenameStartwith();
				if(system!=null){
					fileName=fileName+"-"+system.getCode();
				}
				file = new File(config.getExportRootPath()+"/"+config.getExportPath()+"/"+fileName+".xls");
				out=new FileOutputStream(file);
				printlExportMsg(folder.getName()+"/"+fileName);
				dataHandle.exportCompanyRole(out, systemId,null,companyId);
			}else{
				exportMmsData(fileConfig,system,companyId);
			}
		}catch (ImportExportExecption e) {
			log.debug(PropUtils.getExceptionInfo(e));
			throw e;
		}catch (Exception e) {
			log.debug(PropUtils.getExceptionInfo(e));
		}
		
	}
	
	
	private void exportMmsData(FileConfigModel fileConfig,BusinessSystem system,Long companyId) throws Exception{
		try {
			if("MMS_DATA_TABLE".equals(fileConfig.getData())||
					"MMS_LIST_VIEW".equals(fileConfig.getData())||
					"MMS_FORM_VIEW".equals(fileConfig.getData())){
				if(system!=null){
					Menu menu=menuManager.getMenuByCode(system.getCode(),companyId);
					if(menu!=null){
						File file = new File(fileConfig.getExportRootPath()+"/"+fileConfig.getExportPath()+"/"+fileConfig.getFilenameStartwith()+"-"+menu.getCode()+".xls");
						OutputStream out=null;
						out=new FileOutputStream(file);
						printlExportMsg(fileConfig.getFilenameStartwith()+"-"+menu.getCode());
						if("MMS_DATA_TABLE".equals(fileConfig.getData())){
							dataHandle.exportDataTable(out,null,menu.getId());
						}else if("MMS_LIST_VIEW".equals(fileConfig.getData())){
							dataHandle.exportListView(out,null,menu.getId());
						}else if("MMS_FORM_VIEW".equals(fileConfig.getData())){
							dataHandle.exportFormView(out,null,menu.getId());
						}
						
					}
				}
			}
		}catch (ImportExportExecption e) {
			log.debug(PropUtils.getExceptionInfo(e));
			throw e;
		}
	}
	/**
	 * 导入
	 * @param companyId 要导入数据的公司id
	 * @param fileConfig 文件配置
	 * @param imatrixInfo 导入基础数据时，底层平台的ip、端口、服务名称
	 */
	public void restore(boolean isBasicData,Long companyId, FileConfigModel fileConfig,String... imatrixInfo) {
		try {
			File file =null;
			if(StringUtils.isNotEmpty(fileConfig.getFilename())){
				file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilename()+".xls");
				if(file.exists()){
					if("ACS_SYSTEM".equals(fileConfig.getData())){
						String imatrixIp=null;
						String imatrixPort=null;
						String imatrixName=null;
						if(imatrixInfo!=null){
							List<String> imatrixInfoList=Arrays.asList(imatrixInfo);
							imatrixIp=imatrixInfoList.size()>0?imatrixInfoList.get(0):null;
							imatrixPort=imatrixInfoList.size()>1?imatrixInfoList.get(1):null;
							imatrixName=imatrixInfoList.size()>2?imatrixInfoList.get(2):null;
						}
						dataHandle.importSystem(isBasicData,file, imatrixIp, imatrixPort, imatrixName);
					}else if("ACS_PRODUCT".equals(fileConfig.getData())){
						dataHandle.importProduct(isBasicData,file);
					}else if("WF_TYPE".equals(fileConfig.getData())){
						dataHandle.importDefinitionType(isBasicData,file, companyId);
					}else if("WF_DEFINATION".equals(fileConfig.getData())){
						dataHandle.importDefinition(isBasicData,file, companyId);
						//读取流程定义内容
						File dir=new File(fileConfig.getImportRootPath()+"/flowChar");
						if(dir.exists()){
							File[] files=dir.listFiles();
							if(files!=null){
								for(int i=0;i<files.length;i++){
									File filei=files[i];
									String fileName=filei.getName().split("\\.")[0];
									String defCode=fileName.substring(0,fileName.indexOf("#"));
									String defVersion=fileName.substring(fileName.indexOf("#")+1,fileName.lastIndexOf("#"));
									String systemCode=fileName.substring(fileName.lastIndexOf("#")+1);
									BusinessSystem system=businessSystemManager.getSystemBySystemCode(systemCode);
									if(system!=null&&(acsUtils.isBasicSystem(system)==isBasicData)){
										if(companyId==null){
											List<Company> companys=companyManager.getCompanys();
											for(Company company:companys){
												dataHandle.definitionFile(company.getId(),defCode,filei,Integer.parseInt(StringUtils.trim(defVersion)),system.getId());
											}
										}else{
											dataHandle.definitionFile(companyId,defCode,filei,Integer.parseInt(StringUtils.trim(defVersion)),system.getId());
										}
									}
								}
							}
						}
					}else if("JOB_INFO".equals(fileConfig.getData())){
						dataHandle.importJobInfo(isBasicData,file, companyId);
					}else if("MMS_MENU".equals(fileConfig.getData())){
						dataHandle.importMenu(isBasicData,file, companyId);
					}else if("OPTION_GROUP".equals(fileConfig.getData())){
						dataHandle.importOption(isBasicData,file, companyId);
					}else if("PORTAL_INFO".equals(fileConfig.getData())){
						dataHandle.importPortal(isBasicData,file, companyId);
					}else if("RANK".equals(fileConfig.getData())){
						dataHandle.importRank(isBasicData,file, companyId);
					}else if("DATA_DICT".equals(fileConfig.getData())){
						dataHandle.importDataDict(isBasicData,file, companyId);
					}else if("INTERNATION".equals(fileConfig.getData())){
						dataHandle.importInternation(isBasicData,file, companyId);
					}else if("OPERATION".equals(fileConfig.getData())){
						dataHandle.importOperation(isBasicData,file, companyId);
					}
				}
			}else if(StringUtils.isNotEmpty(fileConfig.getFilenameStartwith())){
				List<BusinessSystem> systems=businessSystemManager.getAllSystems();
				for(BusinessSystem system:systems){
					if(acsUtils.isBasicSystem(system)==isBasicData){
						file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilenameStartwith()+"-"+system.getCode()+".xls");
						if(file.exists()){
							if("ACS_FUNCTION".equals(fileConfig.getData())){
								dataHandle.importFunction(file, system.getId());
							}else if("ACS_ROLE".equals(fileConfig.getData())){
								dataHandle.importRole(file, system.getId(),companyId);
							}else if("ACS_ROLE_COMPANY".equals(fileConfig.getData())){
								dataHandle.importRole(file, system.getId(),companyId);
							}else if("MMS_DATA_TABLE".equals(fileConfig.getData())){
								dataHandle.importDataTable(file, companyId);
							}else if("MMS_LIST_VIEW".equals(fileConfig.getData())){
								dataHandle.importListView(file, companyId);
							}else if("MMS_FORM_VIEW".equals(fileConfig.getData())){
								dataHandle.importFormView(file, companyId);
							}
						}
					}
				}
				if("MMS_FORM_VIEW".equals(fileConfig.getData())){
					//读取表单内容
					File dir=new File(fileConfig.getImportRootPath()+"/formview");
					if(dir.exists()){
						File[]files=dir.listFiles();
						ThreadParameters parameters=new ThreadParameters(companyId, null);
						ParameterUtils.setParameters(parameters);
						for(int i=0;i<files.length;i++){
							File filei=files[i];
							String fileName=filei.getName().split("\\.")[0];
							String formCode=fileName.substring(0,fileName.lastIndexOf("#"));
							String formVersion=fileName.substring(fileName.lastIndexOf("#")+1);
							FormView formview=formViewManager.getCurrentFormViewByCodeAndVersion(formCode, Integer.parseInt(formVersion));
							if(formview==null)break;
							try {
								String html=FileUtils.readFileToString(filei, "UTF-8");
								if(StringUtils.isNotEmpty(html.toString())){
									formview.setHtml(html.toString());
								}
								formViewManager.save(formview);
							} catch (IOException e) {
								log.debug(PropUtils.getExceptionInfo(e));
							}catch (Exception e){
								log.debug(PropUtils.getExceptionInfo(e));
								throw new ImportExportExecption(ContextUtils.showTraces(e));
							}
						}
					}
				}
			}
		} catch (ImportExportExecption e) {
			throw e;
		}
		
	}

	public void deleteJunkData(FileConfigModel fileConfig) {
		try {
			File file =null;
			if(StringUtils.isNotEmpty(fileConfig.getFilenameStartwith())){
				List<BusinessSystem> systems=businessSystemManager.getAllSystems();
				for(BusinessSystem system:systems){
					file=new File(fileConfig.getImportRootPath()+"/"+fileConfig.getImportPath()+"/"+fileConfig.getFilenameStartwith()+"-"+system.getCode()+".xls");
					if(file.exists()){
						if("ACS_FUNCTION".equals(fileConfig.getData())){
							dataHandle.deleteFunction(file,system.getId());
						}else if("ACS_ROLE".equals(fileConfig.getData())){
							dataHandle.deleteRole(file,system.getId(),"ACS_ROLE");
						}else if("ACS_ROLE_COMPANY".equals(fileConfig.getData())){
							dataHandle.deleteRole(file,system.getId(),"ACS_ROLE_COMPANY");
						}
					}
				}
			}
		} catch (ImportExportExecption e) {
			throw e;
		}
	}
}
