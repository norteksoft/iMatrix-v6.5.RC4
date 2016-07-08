package com.norteksoft.acs.base.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.norteksoft.acs.base.enumeration.MailboxDeploy;
import com.norteksoft.acs.base.enumeration.SecretGrade;
import com.norteksoft.acs.entity.organization.Department;
import com.norteksoft.acs.service.organization.DepartmentManager;
import com.norteksoft.acs.service.organization.UserInfoManager;
import com.norteksoft.product.api.ApiFactory;
import com.norteksoft.product.api.entity.User;
import com.norteksoft.product.api.entity.UserInfo;
import com.norteksoft.product.orm.Page;
import com.norteksoft.product.util.ContextUtils;

public class ExportUserInfo {
	
	private static final Log logger = LogFactory.getLog(ExportUserInfo.class);
	
	public static void exportUser(OutputStream fileOut, List<Department> depts, boolean isBranchAdmin){
	Workbook wb;
    try
    {
		wb = new SXSSFWorkbook(500);
    	Sheet sheet=wb.createSheet("user-info");        
        Font boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        CellStyle boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont);
        
        Row row = sheet.createRow(0);
        Cell cell0 = row.createCell(0);
        cell0.setCellValue("部门");
        cell0.setCellStyle(boldStyle);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("姓名");
        cell1.setCellStyle(boldStyle);
        Cell cell2 = row.createCell(2);
        cell2.setCellValue("登录名");
        cell2.setCellStyle(boldStyle);
        Cell cell3 = row.createCell(3);
        cell3.setCellValue("电话");
        cell3.setCellStyle(boldStyle);
        Cell cell4 = row.createCell(4);
        cell4.setCellValue("性别");
        cell4.setCellStyle(boldStyle);
        Cell cell5 = row.createCell(5);
        cell5.setCellValue("电邮");
        cell5.setCellStyle(boldStyle);
        Cell cell6 = row.createCell(6);
        cell6.setCellValue("权重");
        cell6.setCellStyle(boldStyle);
        Cell cell7 = row.createCell(7);
        cell7.setCellValue("邮件大小(M)");
        cell7.setCellStyle(boldStyle);
        Cell cell8 = row.createCell(8);
        cell8.setCellValue("密级");
        cell8.setCellStyle(boldStyle);
        Cell cell9 = row.createCell(9);
        cell9.setCellValue("邮箱配置");
        cell9.setCellStyle(boldStyle);
        Cell cell10 = row.createCell(10);
        cell10.setCellValue("手机号码");
        cell10.setCellStyle(boldStyle);
        Cell cell11 = row.createCell(11);
        cell11.setCellValue("与部门关系");
        cell11.setCellStyle(boldStyle);
        Cell cell12 = row.createCell(12);
        cell12.setCellValue("正职部门");
        cell12.setCellStyle(boldStyle);
		
		UserInfoManager userInfoManager = (UserInfoManager)ContextUtils.getBean("userInfoManager");
        
        //导出部门和人员信息
        for(int i=0;i<depts.size();i++){
        	//List<User> users=ApiFactory.getAcsService().getUsersByDepartmentId(depts.get(i).getId());
        	//fillCell(depts.get(i),users,sheet,isBranchAdmin,depts);
			Page<com.norteksoft.acs.entity.organization.User> page = new Page<com.norteksoft.acs.entity.organization.User>(500, true);
        	List<User> users=userInfoManager.getPageUsersByDepartmentId(page,depts.get(i).getId());
        	int pageNo = page.getPageNo();
        	do {
        		Page<com.norteksoft.acs.entity.organization.User> pageTemp = new Page<com.norteksoft.acs.entity.organization.User>(500, true);
        		pageTemp.setPageNo(pageNo);
        		users=userInfoManager.getPageUsersByDepartmentId(pageTemp,depts.get(i).getId());
        		fillCell(depts.get(i),users,sheet,isBranchAdmin,depts);
        		pageNo++;
        		page.setPageNo(pageNo);
        	} while (page.getTotalPages()>=pageNo);
        }
        if(!isBranchAdmin){
        	//导出无部门人员
        	List<User> users=ApiFactory.getAcsService().getUsersWithoutDepartment();
        	fillCell(null,users,sheet,isBranchAdmin,depts);
        }
        wb.write(fileOut);
    }catch(IOException exception){
    	logger.debug(exception.getStackTrace());
	} 
}

private static void fillCell(Department dept,List<User> users,Sheet sheet, boolean isBranchAdmin, List<Department> manageDepts){
	String deptName = "";
	Department oldDept=dept;
	DepartmentManager departmentManager=(DepartmentManager)ContextUtils.getBean("departmentManager");
	if(dept!=null){
		//处理部门名称,如:办公室/后勤
		deptName=dept.getName();
		if(dept.getBranch()){
			deptName+="#";
		}else{
			if(dept.getSubCompanyId()!=null){
				deptName+="#"+departmentManager.getDepartmentById(dept.getSubCompanyId()).getName();
			}
		}
		if(isBranchAdmin){
			while(dept.getParent()!=null && hasDepartment(manageDepts,dept.getParent())){
				dept=dept.getParent();
				if(dept.getBranch()){
					deptName=dept.getName()+"#/"+deptName;
				}else{
					if(dept.getSubCompanyId()!=null){
						deptName=dept.getName()+"#"+departmentManager.getDepartmentById(dept.getSubCompanyId()).getName()+"/"+deptName;
					}else{
						deptName=dept.getName()+"/"+deptName;
					}
				}
			}
		}else{
			while(dept.getParent()!=null){
				dept=dept.getParent();
				if(dept.getBranch()){
					deptName=dept.getName()+"#/"+deptName;
				}else{
					if(dept.getSubCompanyId()!=null){
						deptName=dept.getName()+"#"+departmentManager.getDepartmentById(dept.getSubCompanyId()).getName()+"/"+deptName;
					}else{
						deptName=dept.getName()+"/"+deptName;
					}
				}
			}
		}
	}
	for(User user:users){
		if(user.getLoginName().contains(".systemAdmin")||
				user.getLoginName().contains(".securityAdmin")||
				user.getLoginName().contains(".auditAdmin")) continue;
		Row rowi = sheet.createRow(sheet.getLastRowNum()+1);
		Cell celli0 = rowi.createCell(0);
		celli0.setCellValue(deptName);
        Cell celli1 = rowi.createCell(1);
        celli1.setCellValue(user.getName());
        Cell celli2 = rowi.createCell(2);
        celli2.setCellValue(user.getLoginName());
        Cell celli3 = rowi.createCell(3);
        UserInfo userInfo = user.getUserInfo();
        if(userInfo!=null){
        	if(userInfo.getTelephone()==null){celli3.setCellValue("");}else{celli3.setCellValue(userInfo.getTelephone());}
        }
        Cell celli4 = rowi.createCell(4);
        if(user.getSex()==null){celli4.setCellValue("");}else{celli4.setCellValue(user.getSex()?"男":"女");}
        Cell celli5 = rowi.createCell(5);
        if(user.getEmail()==null){celli5.setCellValue("");}else{celli5.setCellValue(user.getEmail());}
        Cell celli6 = rowi.createCell(6);
        if(user.getWeight()==null){celli6.setCellValue("");}else{celli6.setCellValue(user.getWeight());}
        Cell celli7 = rowi.createCell(7);
        if(user.getMailSize()==null){celli7.setCellValue("");}else{celli7.setCellValue(user.getMailSize());}
        Cell celli8 = rowi.createCell(8);
        if(user.getSecretGrade()==null){celli8.setCellValue("一般");}else{celli8.setCellValue(getGrade(user.getSecretGrade()));}
        Cell celli9 = rowi.createCell(9);
        if(user.getMailboxDeploy()==null){celli9.setCellValue("");}else{celli9.setCellValue(getDeploy(user.getMailboxDeploy()));}
        Cell celli10 = rowi.createCell(10);
        if(StringUtils.isNotEmpty(user.getMobileTelephone())){
        	celli10.setCellValue(user.getMobileTelephone());
        }else{
        	celli10.setCellValue("");
        }
        Cell celli11 = rowi.createCell(11);
        if(oldDept!=null&&user.getMainDepartmentId()!=null&&user.getMainDepartmentId().equals(oldDept.getId())){
        	celli11.setCellValue("");
        }else if(oldDept==null||oldDept.getBranch()){
        	celli11.setCellValue("");
        }else{
        	celli11.setCellValue("兼职");
        }
        Cell celli12 = rowi.createCell(12);
        String mainDepartmentName="";
        Long id=null;
        com.norteksoft.product.api.entity.Department dept1=null;
        if((id=user.getMainDepartmentId())!=null){
        	if((dept1=ApiFactory.getAcsService().getDepartmentById(id))!=null){
        		mainDepartmentName=dept1.getName();
        	}
        }
        celli12.setCellValue(mainDepartmentName);
        
	}
}

private static boolean hasDepartment(List<Department> depts,Department dept){
	if(depts!=null && depts.size()>0 && dept != null){
		for(Department d:depts){
			if(d.getId().equals(dept.getId())){
				return true;
			}
		}
	}
	return false;
}

private static String getDeploy(MailboxDeploy deploy){
	switch (deploy) {
	case INSIDE:
		return "内网";
	case EXTERIOR:
		return "外网";
	default:
		return "";
	}
}

private static String getGrade(SecretGrade grade){
	switch (grade) {
	case COMMON:
		return "一般";
	case CENTRE:
		return "核心";
	case MAJOR:
		return "重要";
	default:
		return "一般";
	}
}

}
