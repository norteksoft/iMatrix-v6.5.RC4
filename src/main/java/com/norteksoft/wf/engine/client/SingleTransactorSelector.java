package com.norteksoft.wf.engine.client;

import java.util.Set;



/**
 * 选择具体的办理人
 */
public interface SingleTransactorSelector {

	/**
	 * 过滤传过来的办理人
	 * @param dataId 业务实体id
	 * @param transactors 可供选择的办理人
	 * @param moreTransactor 是否为多人办理
	 * @param taskName 当前环节名称
	 * @param taskCode 当前环节编码
	 * @return 具体办理人的id
	 */
	public Set<Long> filter(Long dataId,Set<Long> transactors,boolean moreTransactor,String taskName,String taskCode);
	
}
