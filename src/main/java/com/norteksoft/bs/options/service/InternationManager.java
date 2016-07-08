package com.norteksoft.bs.options.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.options.dao.InternationDao;
import com.norteksoft.bs.options.dao.InternationOptionDao;
import com.norteksoft.bs.options.dao.OptionDao;
import com.norteksoft.bs.options.entity.Internation;
import com.norteksoft.bs.options.entity.InternationOption;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.JsonParser;
import com.norteksoft.product.util.MemCachedUtils;

@Service
@Transactional
public class InternationManager {
	@Autowired
	private InternationDao internationDao;
	@Autowired
	private InternationOptionDao internationOptionDao;
	@Autowired
	private OptionDao optionDao;
	public Internation getInternation(Long id){
		return internationDao.get(id);
	}
	
	public void getInternations(Page<Internation> page,InternationType type){
		internationDao.getInternations(page,type);
	}
	
	public void deleteInternations(String ids){
		String[] idList=ids.split(",");
		for(String id:idList){
			if(StringUtils.isNotEmpty(id)){
				Internation inter=getInternation(Long.parseLong(id));
				internationDao.delete(inter);
				MemCachedUtils.delete((inter.getCompanyId()+"_"+inter.getCode()+"_"+inter.getInternationType()).hashCode()+"");
			}
		}
	}
	public void save(Internation internation){
		internationDao.save(internation);
	}
	public void saveInternation(Internation internation,String oraginalInterCode){
		//当编码被修改后，删除原缓存中的值
		if(!oraginalInterCode.equals(internation.getCode()))MemCachedUtils.delete((internation.getCompanyId()+"_"+oraginalInterCode+"_"+internation.getInternationType()).hashCode()+"");
		internationDao.save(internation);
		List<Object> list=JsonParser.getFormTableDatas(InternationOption.class);
		for(Object obj:list){
			InternationOption inter=(InternationOption)obj;
			inter.setInternation(internation);
			internationOptionDao.save(inter);
		}
	}
	
	public Page<InternationOption> getInternationOptions(Page<InternationOption> page,Long interId){
		page = internationOptionDao.getInternationOptions(page, interId);
		return page;
	}
	 /**
	  * 验证编号是否存在
	  * @param code
	  * @return 存在返回true,反之
	  */
	public boolean isInternationExist(String code,Long id,InternationType type){
		Internation inter=internationDao.getInternationByCode(code,type);
		if(inter==null){
			return false;
		}else{
			if(id==null)return true;
			if(inter.getId().equals(id)){
				return false;
			}else{
				return true;
			}
		}
	}
	
	public void initAllInternations(){
		List<Internation> inters= internationDao.getAllInternations();
		for(Internation inter:inters){
			Map<String,String> interOpts=new HashMap<String, String>();
			List<InternationOption> opts=inter.getInternationOptions();
			if(opts!=null){
				for(InternationOption opt:opts){
					Option langu=optionDao.get(opt.getCategory());
					interOpts.put(langu.getValue(),opt.getValue());
				}
				MemCachedUtils.add((inter.getCompanyId()+"_"+inter.getCode()+"_"+inter.getInternationType()).hashCode()+"",interOpts);
			}
		}
	}
	public List<Internation> getInternations(){
		return internationDao.getInternations();
	}
	public List<InternationOption> getInternationOptions(Long interId){
		return internationOptionDao.getInternationOptions(interId);
	}
	public Internation getInternationByCode(String code,InternationType type){
		return internationDao.getInternationByCode(code,type);
	}
	public InternationOption getInternationOptionByInfo(Long category,String categoryName,String value,String internationCode){
		return internationOptionDao.getInternationOptionByInfo(category, categoryName,value, internationCode);
	}
	
	public void saveInternationOption(InternationOption interOpt){
		internationOptionDao.save(interOpt);
	}
	
	public void deleteInternationOption(Long inOptionId){
		internationOptionDao.delete(inOptionId);
	}
	
	public List<InternationOption> getInternations(Long companyId,Long userId){
		String language = ApiFactory.getPortalService().getUserLanguageById(userId);
		List<InternationOption> result = new ArrayList<InternationOption>();
		List<Internation> inters= internationDao.getInternations(companyId);
		for(Internation inter:inters){
			List<InternationOption> opts=inter.getInternationOptions();
			for(InternationOption opt:opts){
				Option langu=optionDao.get(opt.getCategory());
				if(language.equals(langu.getValue())){
					InternationOption item = new InternationOption();
					item.setCategoryName(inter.getCode());//国际化编码
					item.setValue(opt.getValue());//国际化编码对应的值
					result.add(item);
				}
			}
		}
		return result;
	}
	public InternationType getInternationTypeByCode(String code){
		for(InternationType type:InternationType.values()){
			if(type.getCode().equals(code)){
				return type;
			}
		}
		return null;
	}
	public InternationType getInternationTypeByName(String name){
		for(InternationType type:InternationType.values()){
			if(type.toString().equals(name))return type;
		}
		return null;
	}
}
