package com.norteksoft.acs.service.authorization;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.acs.base.enumeration.BranchDataType;
import com.norteksoft.acs.base.enumeration.ConditionValueType;
import com.norteksoft.acs.base.orm.hibernate.SimpleHibernateTemplate;
import com.norteksoft.acs.entity.authority.Condition;
import com.norteksoft.acs.entity.authority.DataRule;
import com.norteksoft.acs.entity.authority.Permission;
import com.norteksoft.acs.entity.authority.PermissionItem;
import com.norteksoft.acs.entity.authority.PermissionItemCondition;
import com.norteksoft.acs.entity.authorization.BranchAuthority;
import com.norteksoft.acs.entity.authorization.BusinessSystem;
import com.norteksoft.acs.entity.authorization.Function;
import com.norteksoft.acs.entity.authorization.FunctionGroup;
import com.norteksoft.acs.entity.authorization.Role;
import com.norteksoft.acs.entity.authorization.RoleDepartment;
import com.norteksoft.acs.entity.authorization.RoleFunction;
import com.norteksoft.acs.entity.authorization.RoleUser;
import com.norteksoft.acs.entity.authorization.RoleWorkgroup;
import com.norteksoft.acs.entity.sale.Product;
import com.norteksoft.acs.service.sale.ProductManager;
import com.norteksoft.bs.options.entity.Option;
import com.norteksoft.bs.options.entity.OptionGroup;
import com.norteksoft.mms.form.dao.ListColumnDao;
import com.norteksoft.mms.form.dao.TableColumnDao;
import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.mms.form.entity.FormView;
import com.norteksoft.mms.form.entity.ListView;
import com.norteksoft.mms.form.enumeration.MenuType;
import com.norteksoft.mms.form.service.DataTableManager;
import com.norteksoft.mms.form.service.FormViewManager;
import com.norteksoft.mms.form.service.ListViewManager;
import com.norteksoft.mms.module.entity.Menu;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.AuthFunction;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.MemCachedUtils;
import com.norteksoft.product.util.ParameterUtils;
import com.norteksoft.product.util.ThreadParameters;

/**
 * 系统管理 李洪超 2009-3-2上午11:39:38
 */
@Service
@Transactional
public class BusinessSystemManager {

	private static final String hql = "from BusinessSystem b where b.deleted=?";
	private SimpleHibernateTemplate<BusinessSystem, Long> businessDao;
	private SimpleHibernateTemplate<Role, Long> roleDao;
	private SimpleHibernateTemplate<FunctionGroup, Long> functionGroupDao;
	private SimpleHibernateTemplate<Function, Long> functionDao;
	private SimpleHibernateTemplate<RoleUser, Long> roleUserDao;
	private SimpleHibernateTemplate<RoleFunction, Long> roleFunctionDao;
	private SimpleHibernateTemplate<BranchAuthority, Long> branchAuthorityDao;
	private SimpleHibernateTemplate<Menu, Long> menuDao;
	private SimpleHibernateTemplate<RoleDepartment, Long> roleDepartmentDao;
	private SimpleHibernateTemplate<RoleWorkgroup, Long> roleWorkgroupDao;
	private SimpleHibernateTemplate<DataRule, Long> dataRuleDao;
	private SimpleHibernateTemplate<Condition, Long> conditionDao;
	private SimpleHibernateTemplate<Permission, Long> permissionDao;
	private SimpleHibernateTemplate<PermissionItem, Long> permissionItemDao;
	private SimpleHibernateTemplate<PermissionItemCondition, Long> permissionItemConditionDao;
	private SimpleHibernateTemplate<Product, Long> productDao;
	private SimpleHibernateTemplate<DataTable, Long> dataTableDao;
	private SimpleHibernateTemplate<OptionGroup, Long> optionGroupDao;
	private SimpleHibernateTemplate<Option, Long> optionDao;
	private String deleted = "deleted";
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private DataTableManager dataTableManager;
	private Long companyId;
	@Autowired
	private ProductManager productManager;
	@Autowired
	FormViewManager formViewManager;
	@Autowired
	ListViewManager listViewManager;
	public Long getCompanyId() {
		if (companyId == null) {
			return ContextUtils.getCompanyId();
		} else
			return companyId;
	}
	
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
		
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		dataTableDao = new SimpleHibernateTemplate<DataTable, Long>(sessionFactory,DataTable.class);
		productDao = new SimpleHibernateTemplate<Product, Long>(
				sessionFactory, Product.class);
		businessDao = new SimpleHibernateTemplate<BusinessSystem, Long>(
				sessionFactory, BusinessSystem.class);
		menuDao = new SimpleHibernateTemplate<Menu, Long>(
				sessionFactory, Menu.class);
		functionGroupDao = new SimpleHibernateTemplate<FunctionGroup, Long>(
				sessionFactory, FunctionGroup.class);
		functionDao = new SimpleHibernateTemplate<Function, Long>(
				sessionFactory, Function.class);
		roleDao = new SimpleHibernateTemplate<Role, Long>(
				sessionFactory, Role.class);
		roleUserDao= new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		roleDepartmentDao=new SimpleHibernateTemplate<RoleDepartment, Long>(
				sessionFactory, RoleDepartment.class);
		roleWorkgroupDao=new SimpleHibernateTemplate<RoleWorkgroup, Long>(
				sessionFactory, RoleWorkgroup.class);
		roleUserDao= new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		roleUserDao= new SimpleHibernateTemplate<RoleUser, Long>(
				sessionFactory, RoleUser.class);
		roleFunctionDao= new SimpleHibernateTemplate<RoleFunction, Long>(
				sessionFactory, RoleFunction.class);
		branchAuthorityDao=new SimpleHibernateTemplate<BranchAuthority, Long>(
				sessionFactory, BranchAuthority.class);
		dataRuleDao=new SimpleHibernateTemplate<DataRule, Long>(sessionFactory, DataRule.class);
		conditionDao=new SimpleHibernateTemplate<Condition, Long>(sessionFactory, Condition.class);
		permissionDao=new SimpleHibernateTemplate<Permission, Long>(sessionFactory, Permission.class);
		permissionItemDao=new SimpleHibernateTemplate<PermissionItem, Long>(sessionFactory, PermissionItem.class);
		permissionItemConditionDao=new SimpleHibernateTemplate<PermissionItemCondition, Long>(sessionFactory, PermissionItemCondition.class);
		optionGroupDao=new SimpleHibernateTemplate<OptionGroup, Long>(sessionFactory, OptionGroup.class);
		optionDao=new SimpleHibernateTemplate<Option, Long>(sessionFactory, Option.class);
	}

	/**
	 * 查询所有业务系统信息
	 */
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllBusiness() {
		return getAllBusiness(getCompanyId());
	}
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllBusiness(Long companyId) {
//		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>=? and si.deleted=?";
//		Calendar cal = Calendar.getInstance();
//		Date current = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			current = sdf.parse(sdf.format(cal.getTime()));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		List<Long> idList = businessDao.find(hql, companyId, current,false);
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=?   and si.deleted=?";
		List<Long> idList = businessDao.find(hql, companyId, false);
		if(idList.isEmpty()){
			return new ArrayList<BusinessSystem>();
		}
		return businessDao.findByCriteria(Restrictions.in("id",idList),Restrictions.eq("deleted",false));
	}
	/**
	 * 查询订单中对应的系统id集合
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getSubsciberSystemId() {
//		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? and si.invalidDate>=?";
//		Calendar cal = Calendar.getInstance();
//		Date current = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			current = sdf.parse(sdf.format(cal.getTime()));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		List<Long> idList = businessDao.find(hql, getCompanyId(),current);
		String hql = "select si.product.systemId from SubscriberItem si join si.subsciber s where s.tenantId=? ";
		List<Long> idList = businessDao.find(hql, getCompanyId());
		if(idList.isEmpty()){
			return new ArrayList<Long>();
		}
		StringBuilder sysHql = new StringBuilder("select b.id from BusinessSystem b where b.deleted=? and (");
		Object[] objs = new Object[1+idList.size()];
		objs[0]= false;
		int i=1;
		for(Long id:idList){
			sysHql.append(" b.id = ? ");
			if(i<idList.size())sysHql.append(" or ");
			objs[i]=id;
			i++;
		}
		sysHql.append(")");
		return businessDao.find(sysHql.toString(),objs);
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getMainBusiness() {
		return businessDao.find("from BusinessSystem b where b.parentCode is null or b.parentCode='' ");
		
	}
	/**
	 * 同步系统url和菜单上的url
	 */
	public void synUrl(){
		List<BusinessSystem> bs=getAllSystem();
		for(BusinessSystem b:bs){
			List<Menu> ms=menuDao.findList("from Menu m where m.parent is null and m.systemId=?", b.getId());
			for(Menu m:ms){
				if(b.getCode().equals("portal")){
					m.setUrl(b.getPath()+"/index/index.htm");
					continue;
				}
				if(b.getCode().equals("mms")){
					if(m.getType().equals(MenuType.CUSTOM)){
						m.setUrl(b.getPath()+"/common/list.htm");
						continue;
					}
				}
				m.setUrl(b.getPath());
			}
		}
	}
	/**
	 * 获取单条业务系统信息
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getBusiness(Long id) {
		List<BusinessSystem> bses =  businessDao.find("from BusinessSystem bs  where bs.id=?", id);
		if(bses.size()>0)return bses.get(0);
		return null;
	}

	/**
	 * 分页查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getAllBusiness(Page<BusinessSystem> page) {
		String hql = "select p.systemId from Product p join p.subscibers s join s.tenant t"
				+ " where t.id=? and s.validDate > ? and s.deleted = ?";
		List<Long> idList = businessDao.find(hql, getCompanyId(), new Date(), false);
		if (idList.size() <= 0)
			idList.add(-1L);
		return businessDao.findByCriteria(page, Restrictions.in("id", idList),
				Restrictions.eq(deleted, false));

	}

	/**
	 * 保存业务系统信息，如果是新建业务系统，需要为系统建立三个标准角色
	 */
	public void saveBusiness(BusinessSystem businessSystem, boolean isCreate) {
		businessDao.save(businessSystem);
		Role role=roleManager.getRoleByCodeNotUnique(businessSystem.getCode()+"_Everyone");
		if(role==null){
			role=new Role();
			role.setBusinessSystem(businessSystem);
			role.setCode(businessSystem.getCode()+"_Everyone");
			role.setName("普通用户");
			roleDao.save(role);
			Long count = roleUserDao.findLong("select count(*) from RoleUser ru where ru.role.id=? and ru.allUser=? and ru.deleted=?",role.getId(),"ALL_USER",false);
			if(count==0){
				RoleUser ru=new RoleUser();
				ru.setAllUser("ALL_USER");
				ru.setRole(role);
				roleUserDao.save(ru);
			}
		}else{
			Long count = roleUserDao.findLong("select count(*) from RoleUser ru where ru.role.id=? and ru.allUser=? and ru.deleted=?",role.getId(),"ALL_USER",false);
			if(count==0){
				RoleUser ru=new RoleUser();
				ru.setAllUser("ALL_USER");
				ru.setRole(role);
				roleUserDao.save(ru);
			}
		}
		
		//为业务系统添加三个管理员角色(标准角色)
//		if(isCreate){
//			Role systemAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("SystemAdmin")).toString(),"系统管理员");
//			Role securityAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("SecurityAdmin")).toString(),"安全管理员");
//			Role auditAdmin = new Role((new StringBuffer(
//					businessSystem.getCode()).append("AuditAdmin")).toString(),"审计管理员");
//			systemAdmin.setBusinessSystem(businessSystem);
//			securityAdmin.setBusinessSystem(businessSystem);
//			auditAdmin.setBusinessSystem(businessSystem);
//			roleDao.save(systemAdmin);
//			roleDao.save(securityAdmin);
//			roleDao.save(auditAdmin);
//		}
	}

	/**
	 * 删除业务系统信息
	 */
	public void deleteBusiness(Long id) {
		BusinessSystem businessSystem = businessDao.get(id);
		clearSystem(businessSystem);
		businessDao.delete(businessSystem);
	}
	/**
	 * 清理与该系统相关的垃圾数据
	 * @param businessSystem
	 */
	private void clearSystem(BusinessSystem businessSystem) {
		ListColumnDao listColumnDao = (ListColumnDao)ContextUtils.getBean("listColumnDao");
		TableColumnDao tableColumnDao = (TableColumnDao)ContextUtils.getBean("tableColumnDao");
		
		//系统里的角色
		List<Role> roles=roleManager.getRolesBySystem(businessSystem);
		//删除产品和价格策略和销售包
		List<Product> ps=productDao.findList("from Product p where p.systemId=?", businessSystem.getId());
		for(Product p:ps){
			productManager.deleteProduct(p.getId());
		}
		//删除对应的菜单 数据表 列表、表单、页面
		List<Menu> menus=menuDao.findList("from Menu m where m.systemId=?", businessSystem.getId());
		if(menus!=null){
			for(Menu menu:menus){
				ThreadParameters parameters = new ThreadParameters(menu.getCompanyId());
				ParameterUtils.setParameters(parameters);
				List<DataTable> dts=dataTableManager.getUnCompanyAllDataTablesByMenu(menu.getId());
				for(DataTable dt:dts){
					// 删列表、表单、页面
					List<FormView> fvs = formViewManager.getFormViewByDataTable(dt.getId());
					for(FormView fv : fvs){
						formViewManager.deleteFormViewComplete(fv.getId());
					}
					List<ListView> lvs = listViewManager.getListViewByTabelId(dt.getId());
					for(ListView lv : lvs){
						listColumnDao.deleteListColumnsByView(lv.getId());
						listViewManager.deleteEnable(lv.getId());
					}
					//删除数据表对应的字段
					tableColumnDao.deleteTableColumnsByTable(dt.getId());
					
					dataTableDao.delete(dt);
				}
			}
		}
		//删除菜单
		menuDao.createQuery("update Menu m set m.parent=null where m.systemId=?", businessSystem.getId()).executeUpdate();
		menuDao.createQuery("delete from Menu m where m.systemId=?", businessSystem.getId()).executeUpdate();
		//设置该系统菜单下其他系统菜单的当前系统id为自己系统的id
		menuDao.createQuery("update Menu m set m.currentSystemId=m.systemId where m.currentSystemId=?", businessSystem.getId()).executeUpdate();
		if(roles!=null){
			for(Role role:roles){
				//删除分支机构管理对应的角色的中间表
				branchAuthorityDao.createQuery("delete from BranchAuthority ba where ba.branchDataType=? and ba.dataId=?", BranchDataType.ROLE,role.getId()).executeUpdate();
				//删除角色与资源中间表
				roleFunctionDao.createQuery("delete from RoleFunction rf where rf.role.id=?",role.getId()).executeUpdate();
				//删除角色用户中间表
				roleDepartmentDao.createQuery("delete from RoleDepartment ru where ru.role.id=?", role.getId()).executeUpdate();
				//删除角色用户中间表
				roleWorkgroupDao.createQuery("delete from RoleWorkgroup ru where ru.role.id=?", role.getId()).executeUpdate();
				//删除角色用户中间表
				roleUserDao.createQuery("delete from RoleUser ru where ru.role.id=?", role.getId()).executeUpdate();
			}
		}
		//删除系统里对应的所有资源
		functionDao.createQuery("delete from Function f where f.businessSystem.id=?",businessSystem.getId()).executeUpdate();
		//数据权限相关
		List<DataRule> dataRules=dataRuleDao.findList("from DataRule dr where dr.systemId=?", businessSystem.getId());
		if(dataRules!=null){
			for(DataRule dataRule:dataRules){
				conditionDao.createQuery("delete from Condition c where c.dataRule.id=?", dataRule.getId()).executeUpdate();
				List<Permission> permissions=permissionDao.findList("from Permission p where p.dataRule.id=?", dataRule.getId());
				if(permissions!=null){
					for(Permission permission:permissions){
						permissionItemDao.createQuery("delete from PermissionItem c where c.permission.id=?", permission.getId()).executeUpdate();
						permissionItemConditionDao.createQuery("delete from PermissionItemCondition c where c.valueType=? and c.dataId.id=?",ConditionValueType.PERMISSION,permission.getId()).executeUpdate();
					}
				}
				permissionItemConditionDao.createQuery("delete from PermissionItemCondition c where c.valueType=? and c.dataId.id=?",ConditionValueType.DATA_RULE,dataRule.getId()).executeUpdate();
				permissionDao.createQuery("delete from Permission p where p.dataRule.id=?",dataRule.getId()).executeUpdate();
			}
		}
		dataRuleDao.createQuery("delete from DataRule dr where dr.systemId=?", businessSystem.getId()).executeUpdate();
		List<OptionGroup> optionGroups=optionGroupDao.findList("from OptionGroup og where og.systemId=?", businessSystem.getId());
		for(OptionGroup og:optionGroups){
			optionDao.createQuery("delete from Option o where o.optionGroup.id=?", og.getId()).executeUpdate();
			optionGroupDao.delete(og);
		}
		roleDao.createQuery("delete from Role dr where dr.businessSystem.id=?",businessSystem.getId()).executeUpdate();
	}

	/**
	 * 公司添加业务系统(保存公司与业务系统的关系)
	 */
	public List<BusinessSystem> saveBusiness(List<Long> businessIds) {
		return businessDao.findByCriteria(Restrictions.in("id", businessIds));
	}

	
	
	/**
	 * 按条件检索部门
	 */
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getSearchBusiness(Page<BusinessSystem> page,
			BusinessSystem businessSystem, boolean deleted) {
		StringBuilder businessHql = new StringBuilder(hql);
		if (businessSystem != null) {
			String code = businessSystem.getCode().trim();
			String businessName = businessSystem.getName().trim();
			String path = businessSystem.getPath().trim();

			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(businessName)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.name like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + businessName + "%","%" + path + "%");
			}
			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(businessName)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.name like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + businessName + "%");
			}
			if (!StringUtils.isEmpty(businessName)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.name like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + businessName + "%","%" + path + "%");
			}
			if (!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.code like ?");
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%", "%" + path + "%");
			}

			if (!StringUtils.isEmpty(code)) {
				businessHql.append(" and b.code like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + code + "%");
			}

			if (!StringUtils.isEmpty(businessName)) {
				businessHql.append(" and b.name like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + businessName + "%");
			}
			if (!StringUtils.isEmpty(path)) {
				businessHql.append(" and b.path like ?");
				return businessDao.find(page, businessHql.toString(), false,
						"%" + path + "%");
			}
		}
		return businessDao.find(page, hql, false);
	}

	public SimpleHibernateTemplate<BusinessSystem, Long> getBusinessDao() {
		return businessDao;
	}

	public SimpleHibernateTemplate<Role, Long> getRoleDao() {
		return roleDao;
	}

	public SimpleHibernateTemplate<FunctionGroup, Long> getFunctionGroupDao() {
		return functionGroupDao;
	}

	/**
	 * 专供销售系统使用：查询所有业务系统信息
	 */
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllSystem() {
		return businessDao.findByCriteria(Restrictions.eq(deleted, false));

	}

	/**
	 *  专供销售系统使用：分页查询所有业务系统信息
	 */
	@Transactional(readOnly = true)
	public Page<BusinessSystem> getAllSystem(Page<BusinessSystem> page) {
		return businessDao.findByCriteria(page, Restrictions.eq(deleted, false));

	}
	

	/**
	 * 根据系统编码获取业务系统
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getSystemBySystemCode(String code){
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.code=? and bs.deleted=?", code, false);
		return bs;
	}
	
	/**
	 * 根据业务系统访问路径获取业务系统
	 */
	@Transactional(readOnly = true)
	public BusinessSystem getSystemBySystemPath(String path){
		BusinessSystem bs = (BusinessSystem) businessDao.findUnique(
				"from BusinessSystem bs where bs.path=? and bs.deleted=?", path, false);
		return bs;
	}
	
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllSystems() {
		return businessDao.find("from BusinessSystem bs where bs.deleted=? order by id", false);
	}
	/**
	 * 查询所有业务系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getAllSystemCodes() {
		return businessDao.find("select bs.code from BusinessSystem bs where bs.deleted=? order by id", false);
	}
	/**
	 * sales中更新资源缓存功能
	 */
	public void updateFunctionCache(){
		List<Function> functions = functionDao.findByCriteria(Restrictions.eq("deleted", false));
		String pathHashCode = "";
		for(Function function: functions){
			AuthFunction authFun=new AuthFunction();
			authFun.setFunctionPath(function.getPath());
			authFun.setFunctionId(function.getCode());
			String funPath=function.getPath();
			if(StringUtils.isNotEmpty(funPath)){
				//底层系统应用地址
				if(function.getBusinessSystem()!=null){
					if(StringUtils.isNotEmpty(function.getBusinessSystem().getParentCode())){//表示是子系统，则在资源路径前加系统编码
						pathHashCode = String.valueOf(("/"+function.getBusinessSystem().getCode()+function.getPath()).hashCode());
						MemCachedUtils.add(pathHashCode, authFun);
					}else{
						pathHashCode = String.valueOf(function.getPath().hashCode());
						MemCachedUtils.add(pathHashCode, authFun);
					}
				}
			}
		}
		List<BusinessSystem> systems=getAllParentSystems();
//		boolean ifImatrixCache=false;
		for(BusinessSystem system:systems){
			String url=system.getPath();
			if(StringUtils.isNotEmpty(url)){
//				//底层系统应用地址
//				String imatrixCode=PropUtils.getProp("host.imatrix");
//				imatrixCode=imatrixCode.substring(imatrixCode.lastIndexOf("/")+1);
//				if(StringUtils.isNotEmpty(url)&&url.contains(imatrixCode)){//表示是imatrix底层应用
//					if(!ifImatrixCache){
//						url=PropUtils.getProp("host.imatrix")+"/portal/autoAuth.action";
//						ifImatrixCache=true;
//						//更新不受保护的资源缓存
//						getHttpConnection(url);
//					}
//				}else{
					url=url+"/portal/autoAuth.action?systemCode="+system.getCode();
					//更新不受保护的资源缓存
					getHttpConnection(url);
//				}
			}
		}
	}
	
	private void getHttpConnection(String url){
		HttpGet httpget = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
	}
	
	/**
	 * 根据父系统编码查询系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Long> getSystemIdsByParentCode(String parentCode) {
		return businessDao.find("select bs.id from BusinessSystem bs where bs.parentCode=? and bs.deleted=? order by id", parentCode,false);
	}
	/**
	 * 查询所有父系统信息，即父系统编码字段为null的系统信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BusinessSystem> getAllParentSystems() {
		return businessDao.find("from BusinessSystem bs where (bs.parentCode=null or bs.parentCode='') and bs.deleted=? order by id", false);
	}
	/**
	 * 获得平台系统
	 * @return
	 */
	public List<BusinessSystem> getParentSystem(){
		String hql="from BusinessSystem bs where (bs.parentCode is null or bs.parentCode=?) and bs.deleted=? order by id";
		List<BusinessSystem> imatrixSystems= businessDao.find(hql,"",false);
		return imatrixSystems;
	}
	public boolean isParentCodeEmpty(Long systemId){
		BusinessSystem system=getBusiness(systemId);
		if(StringUtils.isEmpty(system.getParentCode())){
			return true;
		}
		return false;
	}
}
