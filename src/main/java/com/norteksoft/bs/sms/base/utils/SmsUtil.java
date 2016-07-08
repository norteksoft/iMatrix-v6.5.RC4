package com.norteksoft.bs.sms.base.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.norteksoft.bs.sms.entity.SendMail;

public class SmsUtil {
	/**
	 * 将格式为：id:1,name:2,tel:333的字符串转为map
	 * @param result
	 * @return
	 */
	public static Map<String, String> stringToMap(String result){
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isEmpty(result)) return map;
		for (String stringArr : result.split(",")) {
			String[] mapStrings = stringArr.split(":");
			if(mapStrings == null || mapStrings.length == 0){
				continue;
			}else if(mapStrings.length == 1){
				map.put(mapStrings[0], "");
			}else {
				map.put(mapStrings[0], mapStrings[1]);
			}
		}
		return map;
	}
	
	//读取properties文件
	public static String readProperties(String key) throws IOException{
		Properties propert = new Properties();
		propert.load(SmsUtil.class.getClassLoader().getResourceAsStream("application.properties"));
		return propert.getProperty(key);
	}
	
	//读取properties文件
	public static String readMailProperties(String key) throws IOException{
		Properties propert = new Properties();
		propert.load(SmsUtil.class.getClassLoader().getResourceAsStream("mail.properties"));
		return propert.getProperty(key);
	}
	
	/**
	 * 发邮件
	 * @throws Exception 
	 */
	public static void cgcslCancelOrderSendMail(String content) throws Exception{
		String str = readMailProperties("sms.mail.receiver");//email地址. 
		String mailsmtp = SmsUtil.readMailProperties("mail.smtp.host");
		String mailport = SmsUtil.readMailProperties("mail.smtp.port");
		String mailusername = SmsUtil.readMailProperties("mail.host.user");
		String mailpassword = SmsUtil.readMailProperties("mail.host.user.password");
		List<String> list =new ArrayList<String>();
		sendMailByToServerFileCgcslCancel(mailsmtp,Integer.valueOf(mailport), mailusername,mailpassword,
								list, str,"", mailusername,content);
	}
	/**
     *	邮件(审批失败后)
     * @return
     * @throws email 邮箱用户名，smtp  邮箱网址，port  端口，password 密码,affixList 附件地址，
     * 			acceptPeople 收件人地址，coptyTo 抄送人地址,senderAddress 发件人地址
     */ 
	private static boolean sendMailByToServerFileCgcslCancel(String smtp,
									int port, String email, String password, List<String> affixList,
									String acceptPeople, String coptyTo, String senderAddress,
									String content) throws Exception {
		SendMail sendMail = new SendMail(smtp, port);
		sendMail.setNeedAuth(true);//需要验证
		// 标题
		if(!sendMail.setSubject("短信平台警告:"+content)){
			return false;
		}
		// 内容
		if (!sendMail.setBody("短信平台警告:"+content+"失败！")){
			return false; // 设置内容
		}
		// 设置附件
		if(null!=affixList){
			for (String att : affixList){
				if (!sendMail.addFileAffix(att,att.split("\\\\")[att.split("\\\\").length-1]))return false;
			}
		}
		if (!sendMail.setTo(acceptPeople))return false; // 设置收信人
		if (!sendMail.setFrom(senderAddress, ""))return false; // 设置发信人
		//设置优先级
		if(!sendMail.setPriority("1"))return false;
		//设置是否要回执
		if(!sendMail.setReplySign(""+"<"+senderAddress+">"))return false;
		sendMail.setNamePass(senderAddress, password);
		if (!sendMail.sendOut())
			return false;
		return true;
	}
}
