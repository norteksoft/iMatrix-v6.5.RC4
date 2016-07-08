package com.norteksoft.wf.base.enumeration;

public enum TransactorPermission {
	
	READ_SUBORDINATE("reviewShow"),
	READ("review"),
	READ_TRANSACT("reviewdo"),
	TEAMWORKA("assistdo"),
	SIGNA("comments"),
	SIGNB("issuance"),
	HSIGNB("countersign");
	
	private String name;
	
	TransactorPermission(String name){
		this.name = name;
	}
	
	public Integer getCode(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getName(Integer code){
		for(TransactorPermission type : TransactorPermission.values()){
			if(type.ordinal() == code)
				return type.name;
		}
		return null;
	}
}
