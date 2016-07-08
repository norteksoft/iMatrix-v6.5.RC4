package com.norteksoft.bs.sms.entity;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

  
 /**
  * 发送邮件 
  * @author Administrator
  *
  */
 public class SendMail {   
   
     private MimeMessage mimeMsg; //MIME邮件对象   
     private Session session; //邮件会话对象   
     private Properties props; //系统属性   
     //smtp认证用户名和密码   
     private String username;   
     private String password;  
     private int port=25;//端口号
     private Multipart mp; //Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象   
     private Integer fileSize=0;
        
     /** 
      * Constructor 
      * @param smtp 邮件发送服务器 
      */  
     public SendMail(String smtp,int port){   
         setSmtpHost(smtp);   
         setPort(port);
         createMimeMessage();   
     }   
   
     /**
      * 设置端口号
      * @param port
      */
     public void setPort(int port) {
		this.port = port;
	}

	/** 
      * 设置邮件发送服务器 
      * @param hostName String  
      */  
     public void setSmtpHost(String hostName) {   
         if(props == null)  
             props = System.getProperties(); //获得系统属性对象    
         props.put("mail.smtp.host",hostName); //设置SMTP主机   
     }   
   
   
     /** 
      * 创建MIME邮件对象   
      * @return 
      */  
     public boolean createMimeMessage()   
     {   
         try {   
             session = Session.getInstance(props,null); //获得邮件会话对象   
         }   
         catch(Exception e){   
             System.err.println("获取邮件会话对象时发生错误！"+e);   
             return false;   
         }   
       
         try {   
             mimeMsg = new MimeMessage(session); //创建MIME邮件对象   
             mp = new MimeMultipart();   
           
             return true;   
         } catch(Exception e){   
             System.err.println("创建MIME邮件对象失败！"+e);   
             return false;   
         }   
     } 
     
     /**
      * 设置优先级
      * @param priority
      */
     public Boolean setPriority(String priority){
    	 try{ 
    		 mimeMsg.setHeader("X-Priority", priority);
    	 }catch(Exception e) {   
             System.err.println("设置优先级发生错误！");   
             return false;
         }   
    	 return true;
     }
     
     /**
      * 设置是否要回执
      * "xie" <xieruolan@norteksoft.com>
      */
     public Boolean setReplySign(String email){
    	 try{ 
    		 mimeMsg.setHeader("Disposition-Notification-To", email);
    	 }catch(Exception e) {   
             System.err.println("设置是否要回执发生错误！");   
             return false;   
         }   
    	 return true;
     }
     
     /** 
      * 设置SMTP是否需要验证 
      * @param need 
      */  
     public void setNeedAuth(boolean need) {   
         if(props == null) props = System.getProperties();   
         if(need){   
             props.put("mail.smtp.auth","true");   
         }else{   
             props.put("mail.smtp.auth","false");   
         }   
     }   
   
     /** 
      * 设置用户名和密码 
      * @param name 
      * @param pass 
      */  
     public void setNamePass(String name,String pass) {   
         username = name;   
         password = pass;   
     }   
   
     /** 
      * 设置邮件主题 
      * @param mailSubject 
      * @return 
      */  
     public boolean setSubject(String mailSubject) {   
         try{   
             mimeMsg.setSubject(mailSubject);   
             return true;   
         }   
         catch(Exception e) {   
             System.err.println("设置邮件主题发生错误！");   
             return false;   
         }   
     }  
       
     /**  
      * 设置邮件正文 
      * @param mailBody String  
      */   
     public boolean setBody(String mailBody) {   
         try{   
             BodyPart bp = new MimeBodyPart();   
             bp.setContent(""+mailBody,"text/html;charset=UTF-8");   
             mp.addBodyPart(bp);   
           
             return true;   
         } catch(Exception e){   
         System.err.println("设置邮件正文时发生错误！"+e);   
         return false;   
         }   
     }   
     /**  
      * 添加附件 
      * @param filename String  
      */   
     public boolean addFileAffix(String path,String filename) {   
       
         try{   
             BodyPart bp = new MimeBodyPart();   
             FileDataSource fileds = new FileDataSource(path); 
             fileSize+=fileds.getInputStream().available();
             bp.setDataHandler(new DataHandler(fileds));   
             sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
             if(filename.length()>18){
	             String type=filename.substring(filename.lastIndexOf("."));
	             filename= filename.substring(0,18)+type;
             }
             String encoding="utf-8";
             if(StringUtils.containsIgnoreCase(encoding, "utf")){ 
            	 bp.setFileName("=?utf8?B?"+enc.encode(filename.getBytes("utf-8"))+"?=");
             }else{
            	 bp.setFileName("=?"+encoding+"?B?"+enc.encode(filename.getBytes(encoding))+"?=");
             }

             mp.addBodyPart(bp);   
               
             return true;   
         } catch(Exception e){   
             System.err.println("增加邮件附件："+filename+"发生错误！"+e);   
             return false;   
         }   
     }   
     /**  
      * 添加附件 html
      * @param filename String  
      */   
     public boolean addFileAffixHtml(String path,String filename,String contentID,String html) {   
    	 
    	 try{   
    		 String encoding="utf-8";
    		 BodyPart bp = new MimeBodyPart();   
             bp.setContent(""+html,"text/html;charset="+encoding);   
    		 
             BodyPart bp1 = new MimeBodyPart();   
             FileDataSource fileds = new FileDataSource(path); 
             fileSize+=fileds.getInputStream().available();
             bp1.setDataHandler(new DataHandler(fileds));   
             bp.setDataHandler(new DataHandler(fileds));   
             sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
             if(filename.length()>18){
	             String type=filename.substring(filename.lastIndexOf("."));
	             filename= filename.substring(0,18)+type;
             }
             if(StringUtils.containsIgnoreCase(encoding, "utf")){ 
            	 bp.setFileName("=?utf8?B?"+enc.encode(filename.getBytes("utf-8"))+"?=");
             }else{
            	 bp.setFileName("=?"+encoding+"?B?"+enc.encode(filename.getBytes(encoding))+"?=");
             }
            
             bp1.setHeader("Content-ID", contentID);
             
    		 mp.addBodyPart(bp);   
    		 mp.addBodyPart(bp1);   
    		 
    		 return true;   
    	 } catch(Exception e){   
    		 System.err.println("增加邮件附件："+filename+"发生错误！"+e);   
    		 return false;   
    	 }   
     }   
     
     /**
      * 返回文件大小
      * @return
      */
     public Integer getFileSize() {
		return fileSize;
	}
       
     /**  
      * 设置发信人 
      * @param from String  
      */   
     public boolean setFrom(String from,String name) {   
         try{   
        	 if(name!=null){
        		 mimeMsg.setFrom(new InternetAddress(from,name)); //设置发信人   
             }else{
            	 mimeMsg.setFrom(new InternetAddress(from)); //设置发信人  
             }
             return true;   
         } catch(Exception e) {   
             return false;   
         }   
     }   
     /**  
      * 设置收信人 
      * @param to String  
      */   
     public boolean setTo(String to){   
         if(to == null)return false;   
         try{   
             mimeMsg.setRecipients(Message.RecipientType.TO,(Address[])InternetAddress.parse(to));   
             return true;   
         } catch(Exception e) {   
             return false;   
         }     
     }   
       
     /**  
      * 设置抄送人 
      * @param copyto String   
      */   
     public boolean setCopyTo(String copyto)   
     {   
         if(copyto == null)return false;   
         try{   
         mimeMsg.setRecipients(Message.RecipientType.CC,(Address[])InternetAddress.parse(copyto));   
         return true;   
         }   
         catch(Exception e)   
         { return false; }   
     }   
       
     /**  
      * 设置密送人 
      * @param BlindCopyto String   
      */   
     public boolean setBlindCopyTo(String BlindCopyto)   
     {   
    	 if(BlindCopyto == null)return false;   
    	 try{   
    		 mimeMsg.setRecipients(Message.RecipientType.BCC,(Address[])InternetAddress.parse(BlindCopyto));   
    		 return true;   
    	 }   
    	 catch(Exception e)   
    	 { return false; }   
     }  
     
    public int getMimeMessageSize()throws Exception{
    	return this.mimeMsg.getSize();
    }
     
     /**  
      * 发送邮件 
      */   
     public boolean sendOut()   
     {   
    	 Transport transport =null;
         try{   
             mimeMsg.setContent(mp);   
             mimeMsg.setSentDate(new Date());
             mimeMsg.saveChanges();   
             System.out.println("正在发送邮件....");   
               
             Session mailSession = Session.getInstance(props,null);   
             transport =mailSession.getTransport("smtp");   
             transport.connect((String)props.get("mail.smtp.host"),this.port,username,password);  
             if(mimeMsg.getRecipients(Message.RecipientType.TO)!=null){
            	 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));
             }  
             if(mimeMsg.getRecipients(Message.RecipientType.CC)!=null){
            	 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));
             }  
             if(mimeMsg.getRecipients(Message.RecipientType.BCC)!=null){
            	 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.BCC));   
             }
//             transport.send(mimeMsg);   
               
             System.out.println("发送邮件成功！");   
               
             return true;   
         } catch(Exception e) {   
             System.out.println("邮件发送失败！"+e);   
             return false;   
         }finally{
        	 try {
				transport.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			} 
         }
     }   
     
     /**
      * 转发附件
      * @param part
      */
     private void saveAttachMent(Part part){
    	 try {
    		 MimeMessage message = (MimeMessage)part;
    		 message.getFolder().open(javax.mail.Folder.READ_ONLY);
			if (part.isMimeType("multipart/*")){
			     Multipart mp = (Multipart) part.getContent(); 
			     for (int i = 0; i < mp.getCount(); i++) {    
			    	 BodyPart mpart = mp.getBodyPart(i); 
			    	 if (mpart.isMimeType("multipart/*")) {    
			    		 saveAttachMent(mpart);    
		             } else{
		            	 this.mp.addBodyPart(mpart);
		             }
			     }
			  }else if (part.isMimeType("message/rfc822")) {    
				  saveAttachMent((Part) part.getContent());    
		      }
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
     }
     
     /**
      * 转发   
      * @param smtp
      * @param from
      * @param to
      * @param mailSubject
      * @param part
      * @param content
      * @param username
      * @param password
      * @return
      */
     public boolean forwardSend(String smtp,int port,String to,String mailSubject,Part part,String content,String username,String password,String fromName){
    	 Transport transport =null;
    	 try{
             mimeMsg.setSubject(mailSubject); 
             mimeMsg.setFrom(new InternetAddress(username,fromName)); //设置发信人   
             mimeMsg.setSentDate(new Date());
             mimeMsg.setRecipients(Message.RecipientType.TO,(Address[])InternetAddress.parse(to));   
             saveAttachMent(part);
             
             BodyPart bp = new MimeBodyPart();   
             bp.setContent(content,"text/html;charset=UTF-8");   
             mp.addBodyPart(bp);   
             
    		 mimeMsg.setContent(mp);
//    		 mimeMsg.setContent(content,"text/html;charset=UTF-8");
    		 mimeMsg.saveChanges();
    		 System.out.println("正在发送邮件....");
    		 
    		 Session mailSession = Session.getInstance(props,null);
    		 transport =mailSession.getTransport("smtp");
    		 transport.connect((String)props.get("mail.smtp.host"),port,username,password);
    		 if(mimeMsg.getRecipients(Message.RecipientType.TO)!=null){
    			 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));
    		 }  
    		 if(mimeMsg.getRecipients(Message.RecipientType.CC)!=null){
    			 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));
    		 }  
    		 if(mimeMsg.getRecipients(Message.RecipientType.BCC)!=null){
    			 transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.BCC));   
    		 }
//             transport.send(mimeMsg);   
    		 
    		 System.out.println("发送邮件成功！");
    		 
    		 return true;   
    	 } catch(Exception e) {   
    		 System.out.println("邮件发送失败！"+e);   
    		 return false;   
    	 }finally{
    		 try {
    			 transport.close();
    		 } catch (MessagingException e) {
    			 e.printStackTrace();
    		 } 
    	 }
     }   
   
     /** 
      * 调用sendOut方法完成邮件发送 
      * @param smtp 
      * @param from 
      * @param to 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @return boolean 
      */  
     public static boolean send(String smtp,int port,String to,String subject,String content,String email,String password,String fromName) {  
         SendMail theMail = new SendMail(smtp,port);  
         theMail.setNeedAuth(true); //需要验证  
         theMail.setPort(port);
         if(!theMail.setSubject(subject)) return false;  
        if(!theMail.setBody(content)) return false;  
         if(!theMail.setTo(to)) return false;  
         if(!theMail.setFrom(email,fromName)) return false; 
         theMail.setNamePass(email,password);  
           
         if(!theMail.sendOut()) return false;  
         return true;  
     }  

     /** 
      * 调用sendOut方法完成邮件发送,带抄送 
      * @param smtp 
      * @param from 
      * @param to 
      * @param copyto 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @return boolean 
     */  
     public static boolean sendAndCc(String smtp,int port,String from,String fromName,String to,String copyto,String subject,String content,String username,String password) {  
         SendMail theMail = new SendMail(smtp,port);  
         theMail.setNeedAuth(true); //需要验证  
         theMail.setPort(port);
         if(!theMail.setSubject(subject)) return false;  
         if(!theMail.setBody(content)) return false;  
         if(!theMail.setTo(to)) return false;  
         if(!theMail.setCopyTo(copyto)) return false;  
         if(!theMail.setFrom(from,fromName)) return false;  
         theMail.setNamePass(username,password);  
           
         if(!theMail.sendOut()) return false;  
         return true;  
     }  
       
     /** 
      * 调用sendOut方法完成邮件发送,带附件 
      * @param smtp 
      * @param from 
      * @param to 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @param filename 附件路径 
      * @return 
      */  
     public static boolean send(String smtp,int port,String from,String fromName,String to,String subject,String content,String username,String password,String path,String filename) {  
         SendMail theMail = new SendMail(smtp,port);  
         theMail.setNeedAuth(true); //需要验证  
         theMail.setPort(port);
         if(!theMail.setSubject(subject)) return false;  
         if(!theMail.setBody(content)) return false;  
         if(!theMail.addFileAffix(path,filename)) return false;   
         if(!theMail.setTo(to)) return false;  
         if(!theMail.setFrom(from,fromName)) return false;  
         theMail.setNamePass(username,password);  
           
         if(!theMail.sendOut()) return false;  
         return true;  
     }  
       
     /** 
      * 调用sendOut方法完成邮件发送,带附件和抄送 
      * @param smtp 
      * @param from 
      * @param to 
      * @param copyto 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @param filename 
      * @return 
      */  
     public static boolean sendAndCc(String smtp,int port,String from,String fromName,String to,String copyto,String subject,String content,String username,String password,String path,String filename) {  
         SendMail theMail = new SendMail(smtp,port);  
         theMail.setNeedAuth(true); //需要验证  
         theMail.setPort(port);
         if(!theMail.setSubject(subject)) return false;  
         if(!theMail.setBody(content)) return false;  
         if(!theMail.addFileAffix(path,filename)) return false;   
         if(!theMail.setTo(to)) return false;  
         if(!theMail.setCopyTo(copyto)) return false;  
         if(!theMail.setFrom(from,fromName)) return false;  
         theMail.setNamePass(username,password);  
           
         if(!theMail.sendOut()) return false;  
         return true;  
     }  
     
     /** 
      * 调用sendOut方法完成邮件发送,带附件和抄送 密送
      * @param smtp 
      * @param from 
      * @param to 
      * @param copyto 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @param filename 
      * @return 
      */  
     public static boolean sendAndCcAndBcc(String smtp,int port,String from,String fromName,String to,String copyto,
    		 String BlindCopyTo,String subject,String content,String username,String password,String path,String filename) {  
    	 SendMail theMail = new SendMail(smtp,port);  
    	 theMail.setNeedAuth(true); //需要验证  
    	 theMail.setPort(port);
    	 if(subject !=null){ if(!theMail.setSubject(subject)) return false;}  
    	 if(!theMail.setBody(content)) return false;  
    	 if(filename!=null){if(!theMail.addFileAffix(path,filename)) return false;}   
    	 if(!theMail.setTo(to)) return false;  
    	 if(copyto!=null){if(!theMail.setCopyTo(copyto)) return false;}  
    	 if(BlindCopyTo!=null){ if(!theMail.setBlindCopyTo(BlindCopyTo)) return false;}
    	 if(!theMail.setFrom(from,fromName)) return false;  
    	 theMail.setNamePass(username,password);  
    	 if((theMail.getFileSize()+content.getBytes().length)*1024>10*1024){
    		 return false;
    	 }
    	 
    	 if(!theMail.sendOut()) return false;  
    	 return true;  
     }  
     
     /** 
      * 调用sendOut方法完成 html邮件发送,带附件和抄送 密送
      * @param smtp 
      * @param from 
      * @param to 
      * @param copyto 
      * @param subject 
      * @param content 
      * @param username 
      * @param password 
      * @param filename 
      * @return 
      */  
     public static boolean sendAndBccHtml(String smtp,int port,String from,String fromName,String to,String copyto,String BlindCopyTo,String subject,String html,String contentID,String username,String password,String path,String filename) {  
    	 SendMail theMail = new SendMail(smtp,port);  
    	 theMail.setNeedAuth(true); //需要验证  
    	 theMail.setPort(port);
    	 if(!theMail.setSubject(subject)) return false;  
    	 if(!theMail.setTo(to)) return false;  
    	 if(!theMail.setCopyTo(copyto)) return false;  
    	 if(!theMail.setBlindCopyTo(BlindCopyTo)) return false;  
    	 if(!theMail.setFrom(from,fromName)) return false;  
    	 if(!theMail.addFileAffixHtml(path,filename,contentID,html))return false;
    	 theMail.setNamePass(username,password);  
    	 if(theMail.getFileSize()>100){
    		 return false;
    	 }
    	 if(!theMail.sendOut()) return false;  
    	 return true;  
     } 
     
 }   
