package com.norteksoft.acs.entity.sysSetting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.norteksoft.acs.entity.IdEntity;

@Entity
@Table(name="ACS_SERVER_CONFIG")
public class ServerConfig extends IdEntity{

	private static final long serialVersionUID = 1L;
	
    
    private Long companyId;
    private LdapType ldapType;
    private String ldapUsername;
    
    private String ldapPassword;
    
    private String ldapUrl;
    
    private String rtxUrl;
    
    private Boolean ldapInvocation = false; //(ldap是否开启单点登录)
    
    private Boolean rtxInvocation = false; //(rtx是否开启单点登录)
    
    private Boolean synLdapInvocation = false; //(同步组织结构是否启用)
    private Boolean synImatrixInvocation = false; //(同步组织结构是否启用)
    private Boolean synRtxInvocation = false; //(同步组织结构是否启用)
    private Boolean synOtherInvocation = false; //(同步组织结构是否启用)
    
    private String imatrixUrl;//(同步组织结构时对方imatrix的地址)
    private String companyCode;//(需要同步组织结构的公司编码)
    private Boolean sysOrgAllowable=true;//是否允许其他公司同步本公司的组织结构
    
    private Boolean extern = false;//其他方式是否开启单点登录
    private ExternalType externalType = ExternalType.HTTP;
    private String externalUrl;
    
    private String ldapBaseDomain;//ldap基础域
    private String ldapPrefix;//ldap普通用户前缀
    private String ldapSuffix;//ldap普通用户后缀
    
    public Boolean getSynLdapInvocation() {
		return synLdapInvocation;
	}

	public void setSynLdapInvocation(Boolean synLdapInvocation) {
		this.synLdapInvocation = synLdapInvocation;
	}

	public Boolean getSynImatrixInvocation() {
		return synImatrixInvocation;
	}

	public void setSynImatrixInvocation(Boolean synImatrixInvocation) {
		this.synImatrixInvocation = synImatrixInvocation;
	}

	public Boolean getSynRtxInvocation() {
		return synRtxInvocation;
	}

	public void setSynRtxInvocation(Boolean synRtxInvocation) {
		this.synRtxInvocation = synRtxInvocation;
	}

	public Boolean getSynOtherInvocation() {
		return synOtherInvocation;
	}

	public void setSynOtherInvocation(Boolean synOtherInvocation) {
		this.synOtherInvocation = synOtherInvocation;
	}

	@Column(length=150)
	public String getImatrixUrl() {
		return imatrixUrl;
	}

	public void setImatrixUrl(String imatrixUrl) {
		this.imatrixUrl = imatrixUrl;
	}
    
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(length=50)
	public String getLdapUsername() {
		return ldapUsername;
	}

	public void setLdapUsername(String ldapUsername) {
		this.ldapUsername = ldapUsername;
	}

	@Column(length=50)
	public String getLdapPassword() {
		return ldapPassword;
	}

	public void setLdapPassword(String ldapPassword) {
		this.ldapPassword = ldapPassword;
	}

	@Column(length=150)
	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public Boolean getLdapInvocation() {
		return ldapInvocation;
	}

	public void setLdapInvocation(Boolean ldapInvocation) {
		this.ldapInvocation = ldapInvocation;
	}

	public Boolean getRtxInvocation() {
		return rtxInvocation;
	}

	public void setRtxInvocation(Boolean rtxInvocation) {
		this.rtxInvocation = rtxInvocation;
	}

	@Column(length=150)
	public String getRtxUrl() {
		return rtxUrl;
	}

	public void setRtxUrl(String rtxUrl) {
		this.rtxUrl = rtxUrl;
	}

	
	public Boolean getExtern() {
		return extern;
	}

	public void setExtern(Boolean extern) {
		this.extern = extern;
	}

	@Enumerated(EnumType.STRING)
	public LdapType getLdapType() {
		return ldapType;
	}

	public void setLdapType(LdapType ldapType) {
		this.ldapType = ldapType;
	}

	@Enumerated(EnumType.STRING)
	public ExternalType getExternalType() {
		return externalType;
	}

	public void setExternalType(ExternalType externalType) {
		this.externalType = externalType;
	}

	@Column(length=150)
	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}
	
	@Column(length=50)
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public Boolean getSysOrgAllowable() {
		return sysOrgAllowable;
	}

	public void setSysOrgAllowable(Boolean sysOrgAllowable) {
		this.sysOrgAllowable = sysOrgAllowable;
	}

	@Column(length=200)
	public String getLdapBaseDomain() {
		return ldapBaseDomain;
	}

	public void setLdapBaseDomain(String ldapBaseDomain) {
		this.ldapBaseDomain = ldapBaseDomain;
	}
	
	@Column(length=100)
	public String getLdapPrefix() {
		return ldapPrefix;
	}

	public void setLdapPrefix(String ldapPrefix) {
		this.ldapPrefix = ldapPrefix;
	}

	@Column(length=100)
	public String getLdapSuffix() {
		return ldapSuffix;
	}

	public void setLdapSuffix(String ldapSuffix) {
		this.ldapSuffix = ldapSuffix;
	}
	
}
