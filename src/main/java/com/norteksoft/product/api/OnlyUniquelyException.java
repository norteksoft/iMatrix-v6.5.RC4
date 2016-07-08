package com.norteksoft.product.api;

public class OnlyUniquelyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1589985952897356154L;

/**
	 * 
	 */

public OnlyUniquelyException() {
	super("uniqely这个标识不是唯一的，暂不处理。");
}
}
