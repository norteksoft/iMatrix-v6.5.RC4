package com.norteksoft.bs.options.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.DatasourceSettingDao;
import com.norteksoft.bs.options.dao.InterfaceSettingDao;
import com.norteksoft.bs.options.entity.DatasourceSetting;
import com.norteksoft.bs.options.entity.InterfaceSetting;
import com.norteksoft.product.enumeration.DataState;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.web.struts2.Struts2Utils;

/**
 * 数据源配置
 * @author Administrator
 *
 */
@Service
@Transactional
public class InterfaceSettingManager {
	@Autowired
	private InterfaceSettingDao interfaceSettingDao;
	@Autowired
	private DatasourceSettingDao datasourceSettingDao;

	public InterfaceSetting getInterfaceSetting(Long id){
		return interfaceSettingDao.getInterfaceSettingById(id);
	}
	public void getInterfaceSettingPage(Page<InterfaceSetting> page) {
		interfaceSettingDao.getInterfaceSettingPage(page);
	}
	
	public void saveInterfaceSetting(InterfaceSetting interfaceSetting){
		interfaceSettingDao.save(interfaceSetting);
	}
	
	public void delete(String ids){
		if(StringUtils.isNotEmpty(ids)){
			String[] idStrs = ids.split(",");
			for(String idstr:idStrs){
				interfaceSettingDao.delete(Long.parseLong(StringUtils.trim(idstr)));
			}
		}
	}
	
	 /**
	  * 验证数据源是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	 public boolean isDatasourceExist(String code,Long sourceId){
		 InterfaceSetting ds=interfaceSettingDao.getInterfaceSettingByCode(code);
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
	 /**
	  * 启用/禁用接口
	  * @param ids
	  * @return
	  */
	 public String changeInterfaceState(String ids){
		 StringBuilder sb = new StringBuilder();
		 int draftToEnableNum = 0,disableToEnableNum=0,enableToDisableNum=0;
		 if(StringUtils.isNotEmpty(ids)){
				String[] idStrs = ids.split(",");
				for(String idstr:idStrs){
					InterfaceSetting interfaceSetting = getInterfaceSetting(Long.parseLong(StringUtils.trim(idstr)));
					if(interfaceSetting.getDataState()==DataState.DRAFT){//草稿到启用
						interfaceSetting.setDataState(DataState.ENABLE);
						draftToEnableNum++;
					}else if(interfaceSetting.getDataState()==DataState.DISABLE){//禁用到启用
						interfaceSetting.setDataState(DataState.ENABLE);
						disableToEnableNum++;
					}else if(interfaceSetting.getDataState()==DataState.ENABLE){//启用到禁用
						interfaceSetting.setDataState(DataState.DISABLE);
						enableToDisableNum++;
					}
				}
				sb.append(Struts2Utils.getText("interfaceManager.draftToStart")).append(draftToEnableNum)
				.append(Struts2Utils.getText("interfaceManager.forbiddenToStart")).append(disableToEnableNum)
				.append(Struts2Utils.getText("interfaceManager.startToforbidden")).append(enableToDisableNum);
			}
		 return sb.toString();
	 }
	 
	 public DatasourceSetting getDatasourceByInterfaceCode(String interfaceCode){
		InterfaceSetting interfaceSetting = interfaceSettingDao.getInterfaceSettingByCode(interfaceCode);
		return datasourceSettingDao.getDatasourceSettingById(interfaceSetting.getDataSourceId());
	 }
	 
	 public InterfaceSetting getInterfaceSettingByCode(String code) {
		 return interfaceSettingDao.getInterfaceSettingByCode(code);
	 }
	 
}
