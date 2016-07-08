package com.norteksoft.product.api.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.product.api.impl.AcsServiceImpl;
import com.norteksoft.product.api.utils.BeanUtil;
import com.norteksoft.product.util.ContextUtils;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	//entity
	private boolean deleted;
	//user
	private Long id;
	private Date loginStart;
	private Integer failedCounts; // 登录失败次数统计，用户失败几次锁定
	private String name;
	private String loginName;
	private String email;
	private Integer weight; //权重
	private Boolean updated;  // ldap 密码是否更新过
	private String honorificName; //尊称
	private Float mailSize;
	private Boolean sex;
	private Long mainDepartmentId;//正职部门
	private String password;
	private SecretGrade secretGrade;
	private String cardNumber;  // 集成打印系统，记录打印卡的卡号
	private Boolean enabled;//是否启用账户
	private Boolean accountExpired;//账户到期标志
	private Boolean accountLocked;	//账户锁定标志
	private Date accountUnlockedTime;//账户解锁的时间
	private Long companyId;
	private String roleCodes;
	private MailboxDeploy mailboxDeploy;//邮箱配置
	private Boolean resetPassword=false;//重新设置用户密码：true表示已经重新设置过密码了，false表示没有重新设置过密码
	
	private Long subCompanyId;//分支机构id
	private String subCompanyName;//分支机构名称
	private String mobileTelephone;//手机号码
	
	public User() {
	}

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}





	public Date getLoginStart() {
		return loginStart;
	}


	public void setLoginStart(Date loginStart) {
		this.loginStart = loginStart;
	}


	public Integer getFailedCounts() {
		return failedCounts;
	}


	public void setFailedCounts(Integer failedCounts) {
		this.failedCounts = failedCounts;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLoginName() {
		return loginName;
	}


	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Integer getWeight() {
		return weight;
	}


	public void setWeight(Integer weight) {
		this.weight = weight;
	}


	public Boolean getUpdated() {
		return updated;
	}


	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}


	public String getHonorificName() {
		if(StringUtils.isEmpty(honorificName)){
			return name;
		}else{
			return honorificName;
		}
	}


	public void setHonorificName(String honorificName) {
		this.honorificName = honorificName;
	}


	public Float getMailSize() {
		return mailSize;
	}


	public void setMailSize(Float mailSize) {
		this.mailSize = mailSize;
	}


	public Boolean getSex() {
		return sex;
	}


	public void setSex(Boolean sex) {
		this.sex = sex;
	}


	public Long getMainDepartmentId() {
		return mainDepartmentId;
	}


	public void setMainDepartmentId(Long mainDepartmentId) {
		this.mainDepartmentId = mainDepartmentId;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public SecretGrade getSecretGrade() {
		return secretGrade;
	}


	public void setSecretGrade(SecretGrade secretGrade) {
		this.secretGrade = secretGrade;
	}


	public String getCardNumber() {
		return cardNumber;
	}


	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}


	public Boolean getEnabled() {
		return enabled;
	}


	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}


	public Boolean getAccountExpired() {
		return accountExpired;
	}


	public void setAccountExpired(Boolean accountExpired) {
		this.accountExpired = accountExpired;
	}


	public Boolean getAccountLocked() {
		return accountLocked;
	}


	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}


	public Date getAccountUnlockedTime() {
		return accountUnlockedTime;
	}


	public void setAccountUnlockedTime(Date accountUnlockedTime) {
		this.accountUnlockedTime = accountUnlockedTime;
	}


	public Long getCompanyId() {
		return companyId;
	}


	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}


	public String getRoleCodes() {
		return roleCodes;
	}


	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}


	public MailboxDeploy getMailboxDeploy() {
		return mailboxDeploy;
	}


	public void setMailboxDeploy(MailboxDeploy mailboxDeploy) {
		this.mailboxDeploy = mailboxDeploy;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getTelephone() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getTelephone();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getBirthday() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getBirthday();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getNativePlace() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getNativePlace();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getNation() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getNation();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getPoliticalStatus() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getPoliticalStatus();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getHeight() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getHeight();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getIdCardNumber() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getIdCardNumber();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getHireDate() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getHireDate();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getTreatment() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getTreatment();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMaritalStatus() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMaritalStatus();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getEducationGrade() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getEducationGrade();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getGraduatedSchool() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getGraduatedSchool();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMajor() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMajor();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getDegree() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getDegree();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getGraduatedDate() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getGraduatedDate();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getFirstForeignLanguage() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getFirstForeignLanguage();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getSkilledDegree() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getSkilledDegree();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getSecondForeignLanguage() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getSecondForeignLanguage();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getBloodGroup() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getBloodGroup();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getHomeAddress() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getHomeAddress();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getHomePostCode() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getHomePostCode();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getCityArea() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getCityArea();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getInterest() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getInterest();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMarriageDate() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMarriageDate();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateName() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateName();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateBirthday() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateBirthday();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateNation() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateNation();
		return null;
	}


	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateWorkPlace() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateWorkPlace();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateAddress() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateAddress();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMatePostCode() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMatePostCode();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMateTelephone() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMateTelephone();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getFatherName() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getFatherName();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getMotherName() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getMotherName();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getParentAddress() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getParentAddress();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getParentPostCode() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getParentPostCode();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getPhotoPath() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getPhotoPath();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getNickName() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getNickName();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getAge() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getAge();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public Integer getDr() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getDr();
		return null;
	}

	public boolean isDeleted() {
		return deleted;
	}


	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public Date getPasswordUpdatedTime() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getPasswordUpdatedTime();
		return null;
	}

	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public Long getUserInfoId() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)return userInfo.getId();
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof User){
			return ((User)o).getId().equals(this.id);
		}
		return false;
	}
	
	public Long getSubCompanyId() {
		return subCompanyId;
	}

	public void setSubCompanyId(Long subCompanyId) {
		this.subCompanyId = subCompanyId;
	}

	public String getSubCompanyName() {
		return subCompanyName;
	}

	public void setSubCompanyName(String subCompanyName) {
		this.subCompanyName = subCompanyName;
	}
	/**
	 * 请使用user.getUserInfo方法获得com.norteksoft.product.api.entity.UserInfo userInfo,
	 * 通过userInfo.getTelephone();userInfo.getBirthday();userInfo.getNativePlace();....获得该实体对象的相应值，不要再使用user实体对象获得，否则会影响性能
	 * @return
	 */
	@Deprecated
	public String getBodyWeight() {
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		if(userInfo!=null)
			return userInfo.getBodyWeight();
		return null;
	}

	public Boolean getResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(Boolean resetPassword) {
		this.resetPassword = resetPassword;
	}
	
	public UserInfo getUserInfo(){
		com.norteksoft.acs.entity.organization.UserInfo userInfo = getUserInfoEntity();
		return BeanUtil.turnToModelUserInfo(userInfo);
	}
	
	private com.norteksoft.acs.entity.organization.UserInfo getUserInfoEntity(){
		if(id!=null){
			AcsServiceImpl acsServiceImpl = (AcsServiceImpl)ContextUtils.getBean("acsServiceImpl");
			return acsServiceImpl.getUserInfoByUserId(id);
		}
		return null;
	}

	public String getMobileTelephone() {
		return mobileTelephone;
	}

	public void setMobileTelephone(String mobileTelephone) {
		this.mobileTelephone = mobileTelephone;
	}
}
