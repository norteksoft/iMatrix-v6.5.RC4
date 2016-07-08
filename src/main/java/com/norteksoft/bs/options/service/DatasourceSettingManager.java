package com.norteksoft.bs.options.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.DatasourceSettingDao;
import com.norteksoft.bs.options.dao.InterfaceSettingDao;
import com.norteksoft.bs.options.dao.JobInfoDao;
import com.norteksoft.bs.options.entity.DatasourceSetting;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Service
@Transactional
public class DatasourceSettingManager {
	@Autowired
	private DatasourceSettingDao datasourceSettingDao;
	@Autowired
	private InterfaceSettingDao interfaceSettingDao;
	@Autowired
	private JobInfoDao jobInfoDao;
	private static Log logger = LogFactory.getLog(DatasourceSettingManager.class);

	public DatasourceSetting getDatasourceSetting(Long id){
		return datasourceSettingDao.getDatasourceSettingById(id);
	}
	public void getDatasourceSettingPage(Page<DatasourceSetting> page) {
		datasourceSettingDao.getDatasourceSettingPage(page);
	}
	
	public void saveDatasourceSetting(DatasourceSetting datasourceSetting){
		datasourceSettingDao.save(datasourceSetting);
	}
	
	public void delete(String ids){
		if(StringUtils.isNotEmpty(ids)){
			String[] idStrs = ids.split(",");
			for(String idstr:idStrs){
				datasourceSettingDao.delete(Long.parseLong(StringUtils.trim(idstr)));
			}
		}
	}
	
	 /**
	  * 验证数据源是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isDatasourceExist(String code,Long sourceId){
		 DatasourceSetting ds=datasourceSettingDao.getDatasourceSettingByCode(code);
		 if(ds==null){
			 return false;
		 }else{
			 if(sourceId==null)return true;
			 if(ds.getId().equals(sourceId)){
				 return false;
			 }else{
				 return true;
			 }
		 }
	 }
	 
	 public String testDatasource(String url,String userName,String password,String driveName,String testSql) throws SQLException,ClassNotFoundException{
		//创建连接
			Connection con = null;
			try {
				Class.forName(driveName);
				con = DriverManager.getConnection(url,userName,password);
				if(con!=null){
					return "true";
				}else{
					return "false";
				}
			}catch (SQLException e) {
				logger.debug(" SQLException : "+e.getMessage()); 
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}catch (ClassNotFoundException e) {
				logger.debug(" ClassNotFoundException : "+e.getMessage()); 
				e.printStackTrace();
				throw new ClassNotFoundException(e.getMessage());
			}finally{
				if(con!=null){
					con.close();
				}
			}
	 }
	 
	 public List<DatasourceSetting> getAllDatasourceSettings(){
		 return datasourceSettingDao.getAllDatasourceSettings();
	 }
	 
	 public DatasourceSetting getDatasourceSettingByCode(String code) {
		 return datasourceSettingDao.getDatasourceSettingByCode(code);
	 }
	 /**
	  * 验证数据源是否被使用
	  * @param datasourceIds
	  * @return
	  */
	 public String validateDatasourceUse(String datasourceIds){
		 StringBuilder useInfo = new StringBuilder();//被使用数据源
		 if(StringUtils.isNotEmpty(datasourceIds)){
				String[] idStrs = datasourceIds.split(",");
				for(String idstr:idStrs){
					DatasourceSetting ds = getDatasourceSetting(Long.parseLong(idstr));
					List<String> interfaceNames = interfaceSettingDao.getInterfaceSettingByDatasource(Long.parseLong(idstr));
					String inames = interfaceNames.toString().replace("[", "").replace("]", "");
					if(interfaceNames.size()>0){
						useInfo.append(Struts2Utils.getText("interfaceManager.dataSourceb"))
						.append(ds.getCode()).append(Struts2Utils.getText("interfaceManager.b"))
						.append(Struts2Utils.getText("interfaceManager.commonInferaceb")).append(inames).append(Struts2Utils.getText("interfaceManager.used")).append(";");
					}
					 interfaceNames = jobInfoDao.getTimeTaskByDatasource(Long.parseLong(idstr));
					 inames = interfaceNames.toString().replace("[", "").replace("]", "");
					if(interfaceNames.size()>0){
						useInfo
						.append(Struts2Utils.getText("interfaceManager.timeTask")).append(inames).append(Struts2Utils.getText("interfaceManager.used")).append(";");
					}
				}
		 }
		 return useInfo.toString();
	 }
}
