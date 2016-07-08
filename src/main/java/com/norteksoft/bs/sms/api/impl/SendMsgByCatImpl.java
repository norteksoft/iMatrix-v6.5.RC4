package com.norteksoft.bs.sms.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageEncodings;
import org.smslib.modem.SerialModemGateway;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norteksoft.bs.sms.api.SmsService;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.util.ContextUtils;
import com.norteksoft.product.util.PropUtils;


/**
 * 短信平台/短信猫方式
 * @author c
 */

@Repository
@Transactional
public class SendMsgByCatImpl  implements SmsService{
	private static final String SIMPIN = "0000";
	private static final String SENDSUCCESSMSG = "success";
	private static final String SENDFAILMSG = "error";
	/** 
	 * modem.com1:网关ID（即短信猫端口编号）
	 * COM4:串口名称（在window中以COMXX表示端口名称，在linux,unix平台下以ttyS0-N或ttyUSB0-N表示端口名称），通过端口检测程序得到可用的端口
	 * 115200：串口每秒发送数据的bit位数,必须设置正确才可以正常发送短信，可通过程序进行检测。常用的有115200、9600。可以通过超级终端测试出来
	 * Huawei：短信猫生产厂商，不同的短信猫生产厂商smslib所封装的AT指令接口会不一致，必须设置正确.常见的有Huawei、wavecom等厂商
	 * 最后一个参数表示设备的型号，可选
	 */
	private Log log = LogFactory.getLog(getClass());
	
	/**
	 * @param phoneTo 发送的目标手机号
	 * @param content 发送的内容
	 */
	public String sendMessage(Map<String, String> settings,String phoneTo,String content) {
		OutboundNotification outboundNotification = new OutboundNotification();
		Service srv = new Service();// 构造服务对象
		SerialModemGateway gateway = new SerialModemGateway(settings.get("gatewayId"), settings.get("comName"),
				Integer.valueOf(settings.get("bitTimer")), settings.get("creater"), "");
		gateway.setInbound(true); // 设置网关可以写入信息     表示该网关可以接收短信
		gateway.setOutbound(true);// 设置网关可以读取信息    ,表示该网关可以发送短信,
		gateway.setSimPin(SIMPIN); // 设置SIM PIN
		gateway.setOutboundNotification(outboundNotification);// 设置入信回调实现,发送短信成功后的回调函方法
		srv.addGateway(gateway); //发信服务中添加设定的网关 ,将网关添加到短信猫服务中
		try {
			srv.startService();// 初始化所有的网关,启动服务，进入短信发送就绪状态
			OutboundMessage msg = new OutboundMessage(phoneTo, content);
			msg.setEncoding(MessageEncodings.ENCUCS2);// 这句话是发中文短信必须的
			srv.sendMessage(msg);// 执行发送
			System.in.read();
			srv.stopService();
		} catch (Exception e) {
			log.error(PropUtils.getExceptionInfo(e));
			return SENDFAILMSG;
		}
		//ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"), "短信平台发送短信,短信猫方式发送" +
				//"收信人:" + phoneTo + ",内容:" + content,ContextUtils.getSystemId("bs"));
		List<String> messages = new ArrayList<String>();
		messages.add(phoneTo);
		messages.add(content);
		ApiFactory.getBussinessLogService().log(ApiFactory.getBussinessLogService().getI18nLogInfo("bs.messagePlatform"),
				ApiFactory.getBussinessLogService().getI18nLogInfo("bs.cmsCatSend", messages),ContextUtils.getSystemId("bs"));
		return SENDSUCCESSMSG;
	}
}

/**
 * 短信猫回调
 * @author c
 */
class OutboundNotification implements IOutboundMessageNotification{
	private Log log = LogFactory.getLog(OutboundNotification.class);
	public void process(String gatewayId, OutboundMessage msg){
		log.debug("短信猫回调,gatewatId:"+gatewayId +" msg:" + msg);
	}
}
