package com.norteksoft.product.api;



public interface SmsSendService {
    
    /**
     * 短信平台/将短信添加至待发送列表
     * @param phoneTo 收信人
     * @param interCode 接口编号
     * @param args 短信内容或模板参数
     * @return
     */
    public String sendMessage(String phoneTo,String interCode,String...args);
    
}
