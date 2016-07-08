package com.norteksoft.acs.base.enumeration;
/**
 * 初始化租户的状态
 * @author admin
 *
 */
public enum InitState {
	SUCCESS("tenant.init.state.success"), // 成功
	FAIL("tenant.init.state.fail"); // 失败
	
	private String code;
	
	InitState(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
