package com.norteksoft.mms.page.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.norteksoft.mms.form.entity.DataTable;
import com.norteksoft.product.util.PropUtils;
import com.norteksoft.product.util.ZipUtils;
import com.norteksoft.product.web.struts2.CrudActionSupport;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Namespace("/page")
@ParentPackage("default")
@Results( { @Result(name = CrudActionSupport.RELOAD, location = "create-html", type = "redirectAction") })
public class CreateHtmlpageAction extends CrudActionSupport<DataTable> {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CreateHtmlpageAction.class);
	private static final String TEMPLATE_DIR="template/";
	public static final String TEMP_PATH="temphtml/";
	private static Configuration config = null;

	private static String  ZIP_NAME = "pagezip.zip";//压缩文件名
	private static String  LEFT_NAME = "{";//
	private static String  RIGHT_NAME = "}";//
	private static String  LEFT_NAME_S = "\\{";//
	private static String  RIGHT_NAME_S = "\\}";//
	private static String  SPLIT_NAME = "-";//分隔符
	private static final String YIN_CODE = "\"";
	
	private String inputShowType;//表单页面显示方式   popup或refresh
	private String  content;//要生成的页面的内容
	private String  pageType;//页面类型：三种list，input，view
	private String  fileName;//生成的文件名
	private String  filePath = "c:\\temphtml";//生成的文件路径

	private List<String> dateList = new ArrayList<String>();//日期的id集合
	private List<List<String>> valueList = new ArrayList<List<String>>();//td中的集合
	private List<String> nameList = new ArrayList<String>();//列表页面的首行集合
	private List<List<String>> valueJspList = new ArrayList<List<String>>();//td中的集合
	
	private static final String SUCCESS_MESSAGE_LEFT = "<font class=\"onSuccess\"><nobr>";
	private static final String MESSAGE_RIGHT = "</nobr></font>";
	private static final String ERROR_MESSAGE_LEFT = "<font class=\"onError\"><nobr>";
	protected void addErrorMessage(String message) {
		this.addActionMessage(ERROR_MESSAGE_LEFT + message + MESSAGE_RIGHT);
	}
	protected void addSuccessMessage(String message){
		this.addActionMessage(SUCCESS_MESSAGE_LEFT+message+MESSAGE_RIGHT);
	}
	@Action("page-index")
	public String listMenu() {
		return "page-index";
	}
	@Action("createhtml-editor")
	public String list() {
		return "createhtml-editor";
	}
	/**
	 * 只保留td中的文本，修改
	 * @param tableNode
	 */
	@Action("createhtml-editor-save")
	public String save() {
		Parser parser = null;
		try {
			if(content.contains("<table")&&content.contains("/table>")){
				content = content.substring(content.indexOf("<table"),content.lastIndexOf("/table>")+7);
			}
			parser = new Parser("<html>" + replaceSpcialString(content) + "</html>");
			int count = 0;
			for (NodeIterator i = parser.elements(); i.hasMoreNodes();) {
				Node node = i.nextNode();
				NodeList childrenNodes = node.getChildren();// 所有的table以及其他信息，需过滤掉
				for (int j = 0; j < childrenNodes.size(); j++) {
					String childEvery = childrenNodes.elementAt(j).toHtml();// 每个子节点
					if (childEvery.contains("<table") && childEvery.indexOf("/table>") > 0) {
						valueList = new ArrayList<List<String>>();
						count++;
						Node tableNode = childrenNodes.elementAt(j);// 每个table节点
						//解析单元格，放到  valueList 以及valueJspList
						parseContentNew(tableNode,"out");
						if("list".equals(pageType)){
							nameList = getTableNameList(tableNode);
						}
						//创建页面
						createPage(count);
					}
				}
			}
			//压缩生成的文件
			zipPage();
		} catch (Exception e) {
			log.error("服务器生成文件失败:" + PropUtils.getExceptionInfo(e));
			return "createhtml-editor";
		}
		return "createhtml-editor";
	}
	/**
	 * 只保留td中的文本，修改
	 * @param tableNode
	 */
	public List<List<String>> parseContentNew(Node tableNode,String flagStr) {
		List<List<String>> valueList2 = new ArrayList<List<String>>();//td中的集合
		try {
			NodeList trChildNodes = tableNode.getChildren();
			int count = 0;
			for (int k = 0; k < trChildNodes.size(); k++) {
				Node trChildNode = trChildNodes.elementAt(k);// 每个table的子节点
				String trChildS = trChildNode.toHtml();
				if (trChildS.contains("<tr") && trChildS.indexOf("/tr>") > 0) {// 是tr节点
					if(count == 0 && "list".equals(pageType)){//如果是list页面，且是第一个tr，continue,因为表头已在namelist中
						count++;
						continue;
					}
					List<String> tdsList = new ArrayList<String>();
					List<String> tdsJspList = new ArrayList<String>();
					NodeList tdChildNodes = trChildNode.getChildren();
					for (int l = 0; l < tdChildNodes.size(); l++) {
						Node tdChildNode = tdChildNodes.elementAt(l);
						String tdChildS = replaceString2(tdChildNode.toHtml());
						if (tdChildS.contains("<td") && tdChildS.indexOf("/td>") > 0) {
							String allInfo = tdChildNode.toHtml();
							String colspanS = parseAttributeN(tdChildNode.getText(), "colspan");
							String rowspanS = parseAttributeN(tdChildNode.getText(), "rowspan"); 
							String result = "";
							String resultJsp = "";
							if (allInfo.contains("<table") && allInfo.contains("/table>")) {//包含嵌套table
								result = parseTableInTable(allInfo);
								resultJsp = result;
							} else if (allInfo.contains(LEFT_NAME) && allInfo.contains(RIGHT_NAME)) {// 说明是控件
								if(!"input".equals(pageType)){//如果不是input页面，就是去掉所有样式的标签
									result = parseOhterTdValues(tdChildNode);//去掉所有样式，将p标签解析为换行，br标签解析为换行等
									resultJsp = result;
								}else {
									result = parseOhterTdValues(tdChildNode);//去掉所有样式，将p标签解析为换行，br标签解析为换行等
									String resultOld = result;
									result = parseInputTdValues(result);//解析每一个控件
									resultJsp = parseInputTdJspValues(resultOld);//解析每一个控件
								}
							} else {
								// 否则就是普通文本
								result = parseOhterTdValues(tdChildNode);//去掉所有样式，将p标签解析为换行，br标签解析为换行等
								resultJsp = result;
							}
							if(StringUtils.isEmpty(result)){
								result = "&nbsp;";
								resultJsp = "&nbsp;";
							}
							if(allInfo.contains("<img")){
								result += "<img src='' />";
								resultJsp += "<img src='' />";
							}
							result ="<td colspan='"+colspanS+"' rowspan='"+rowspanS+"'>" +result +"</td>";
							resultJsp ="<td colspan='"+colspanS+"' rowspan='"+rowspanS+"'>" +resultJsp +"</td>";
							tdsList.add(result);
							tdsJspList.add(resultJsp);
						}
					}
					if("in".equals(flagStr)){
						valueList2.add(tdsList);
					}else if("out".equals(flagStr)){
						valueList.add(tdsList);
						valueJspList.add(tdsJspList);
					}
				}
			}
		} catch (Exception e2) {
			log.error("服务器解析表格失败:" + PropUtils.getExceptionInfo(e2));
		}
		return valueList2;
	}
	/**
	 * 解析每一个控件
	 * @param result格式：{code-input-classname-value-}aaa{code-input-classname-value-}
	 * @return
	 */
	private String parseInputTdValues(String resultT) {
		String finalResult = "";
		String[] resultArr = resultT.split(LEFT_NAME_S);
		if(resultArr == null) return resultT;
		for (String everyStr : resultArr) {
			//对每一个内容再以}分割
			String[] resultFinalArr = everyStr.split(RIGHT_NAME_S);
			for (String everyStrIn : resultFinalArr) {
				if(everyStrIn != null && everyStrIn.contains(SPLIT_NAME)){
					finalResult += this.getControlValue(everyStrIn,"html");
				}else {
					finalResult += everyStrIn;
				}
			}
			
			
		}
		return finalResult;
	}
	/**
	 * 解析jsp的td，与html在复选框等多值情况下有差异
	 * @param resultT
	 * @return
	 */
	private String parseInputTdJspValues(String resultT) {
		String finalResult = "";
		String[] resultArr = resultT.split(LEFT_NAME_S);
		if(resultArr == null) return resultT;
		for (String everyStr : resultArr) {
			//对每一个内容再以}分割
			String[] resultFinalArr = everyStr.split(RIGHT_NAME_S);
			for (String everyStrIn : resultFinalArr) {
				if(everyStrIn != null && everyStrIn.contains(SPLIT_NAME)){
					finalResult += this.getControlValue(everyStrIn,"jsp");
				}else {
					finalResult += everyStrIn;
				}
			}
			
			
		}
		return finalResult;
	}
	public static void main(String[] args) {
		String resultT = "{code-input-classname-value-}aaa{code-input-classname-value-}";
		String finalResult = "";
		String[] resultArr = resultT.split(LEFT_NAME_S);
		if(resultArr == null) System.out.println( resultT);
		for (String everyStr : resultArr) {
			//对每一个内容再以}分割
			String[] resultFinalArr = everyStr.split(RIGHT_NAME_S);
			for (String everyStrIn : resultFinalArr) {
				if(everyStrIn != null && everyStrIn.contains(SPLIT_NAME)){
//					finalResult += this.getControlValue(everyStrIn);//去解析，创建控件代码
				}else {
					finalResult += everyStr;
				}
			}
		}
		System.out.println( finalResult);
	
	}
	/**
	 * 去掉标签节点的所有属性
	 * @param tdChildNode
	 */
	private String parseOhterTdValues(Node tdChildNode) {
		StringBuilder sb = new StringBuilder();
		//解析tdChildNode
		NodeList tdInChildNodes = tdChildNode.getChildren();
		if(tdInChildNodes != null && tdInChildNodes.size() > 0){
			for (int i = 0; i < tdInChildNodes.size(); i++) {
				Node tdInChildNode = tdInChildNodes.elementAt(i);
				//去掉所有node的所有样式
				if (tdInChildNode instanceof Tag) {
					Tag _tag = (Tag) tdInChildNode;
					if(_tag.getTagName().equals("P") || _tag.getTagName().equals("p")){
						sb.append(replaceString2(tdInChildNode.toPlainTextString())+"<br />");
					}else if(_tag.getTagName().equals("br")||_tag.getTagName().equals("BR")){
						sb.append(tdInChildNode.toPlainTextString()+"<br />");
					}else if(_tag.getTagName().equals("hr")||_tag.getTagName().equals("HR")){
						sb.append(tdInChildNode.toPlainTextString()+"<hr />");
					}else {
						sb.append(tdInChildNode.toPlainTextString());
					}
					_tag.removeAttribute("class");
					_tag.removeAttribute("style");
					_tag.removeAttribute("width");
					_tag.removeAttribute("height");
				}else {
					sb.append(tdInChildNode.toPlainTextString());
				}
				parseOhterTdValues(tdInChildNode);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 如果是列表页面，获得列表页面的表头
	 * @param tableNode
	 * @return
	 */
	public List<String> getTableNameList(Node tableNode) {
		try {
			NodeList trChildNodes = tableNode.getChildren();
			for (int k = 0; k < trChildNodes.size(); k++) {
				Node trChildNode = trChildNodes.elementAt(k);// 每个table的子节点
				String trChildS = trChildNode.toHtml();
				if (trChildS.contains("<tr") && trChildS.indexOf("/tr>") > 0) {// 是tr节点
					List<String> tdsList = new ArrayList<String>();
					NodeList tdChildNodes = trChildNode.getChildren();
					for (int l = 0; l < tdChildNodes.size(); l++) {
						Node tdChildNode = tdChildNodes.elementAt(l);
						String tdChildS = replaceString2(tdChildNode.toHtml());
						if (tdChildS.contains("<td") && tdChildS.indexOf("/td>") > 0) {
							String colspanS = parseAttributeN(tdChildNode.getText(), "colspan");
							String rowspanS = parseAttributeN(tdChildNode.getText(), "rowspan"); 
							String onlyContent = replaceString2(tdChildNode.toPlainTextString());
							onlyContent = "<td colspan='"+colspanS+"' rowspan='"+rowspanS+"'>" +onlyContent +"</td>";//添加纯文本
							tdsList.add(onlyContent);
						}
					}
					return tdsList;//直接返回第一个tr中的内容
				}
			}
		} catch (Exception e2) {
			log.error("服务器解析表格，获得list页面表头失败:" + PropUtils.getExceptionInfo(e2));
		}
		return null;
	}
	
	/**
	 * 解析嵌套table
	 * @param allInfo
	 * @return
	 * @throws ParserException 
	 */
	private String parseTableInTable(String allInfo) throws ParserException {
		StringBuilder result = new StringBuilder();
		allInfo = allInfo.substring(allInfo.indexOf("<table"),allInfo.lastIndexOf("/table>")+7);
		allInfo = "<html>"+allInfo+"</html>";
		Parser parser = new Parser(  replaceSpcialString(allInfo) );
		//在这里加一层判断，判断单元格中table的数量，以及table中单元格的数量，如果只有一个tr td，就只显示文本
		boolean flag =  parseTableCount(allInfo) ;//true表示直接返回纯文本
		for (NodeIterator i = parser.elements(); i.hasMoreNodes();) {
			Node node = i.nextNode();
			NodeList childrenNodes = node.getChildren();// 所有的table以及其他信息，需过滤掉
			for (int j = 0; j < childrenNodes.size(); j++) {
				String childEvery = childrenNodes.elementAt(j).toHtml();// 每个子节点
				if (childEvery.contains("<table") && childEvery.indexOf("/table>") > 0) {
					Node tableNode = childrenNodes.elementAt(j);// 每个table节点
					if(flag){
						return replaceString2(tableNode.toPlainTextString());
					}
					//解析单元格
					List<List<String>> value2List = parseContentNew(tableNode,"in");//这里有问题，不能定义类变量
					result.append("<table rules=all style=\"width: 300px;border: #87CEEB 1px solid;;text-align:center\">");
					for (List<String> list : value2List) {//每一个tr
						result.append("<tr>");
						for (String string : list) {
							result.append(string);
						}
						result.append("</tr>");
					}
					result.append("</table>");
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 替换字符串的\t\r\n为空
	 * @param result
	 * @return
	 */
	private String replaceSpcialString(String result){
		result = result == null ? "" : result;
		return result.replace("&amp;", "&").replace("&lt;", "<")
		  .replace("&gt;", ">").replace("&quot;", "\\");
	}
	/**
	 * 判断table中是否只有一个tr以及一个td
	 * @param allInfo
	 * @return
	 */
	private boolean parseTableCount(String allInfo) {
		try {
			Parser parser2 = new Parser(allInfo);
			Parser parser3 = new Parser(allInfo);
			Parser parser4 = new Parser(allInfo);
			NodeFilter tablefilter = new TagNameFilter("table");
			NodeFilter trfilter = new TagNameFilter("tr");
			NodeFilter tdfilter = new TagNameFilter("td");
			NodeList tablenodes = parser2.extractAllNodesThatMatch(tablefilter);
			NodeList trnodes = parser3.extractAllNodesThatMatch(trfilter);
			NodeList tdnodes = parser4.extractAllNodesThatMatch(tdfilter);
			if(tablenodes!=null &&tablenodes.size()<=1&&trnodes!=null &&trnodes.size()<=1&&tdnodes!=null &&tdnodes.size()<=1){
				return true;
			}
		} catch (ParserException e) {
			log.error("服务器解析属性时报错:" + PropUtils.getExceptionInfo(e));
		}
		return false;
	}
	/**
	 * 解析某个节点的某个属性
	 * @param parseStr
	 * @param attributeName
	 * @return
	 * @throws ParserException
	 */
	private String parseAttributeN(String parseStr,String attributeName) throws ParserException {
		String result = "";
		try {
			if(parseStr != null && attributeName!= null){
				int index = parseStr.indexOf(attributeName);
				if(index > 0){
					char[] charArr = parseStr.toCharArray();

					if (parseStr2Number(charArr[index + 9] + "")) {
						result += charArr[index + 9];
					}
					if (parseStr2Number(charArr[index + 10] + "")) {
						result += charArr[index + 10];
					}
					if (parseStr2Number(charArr[index + 11] + "")) {
						result += charArr[index + 11];
					}
				}
			}
		} catch (Exception e) {
			log.error("服务器解析属性时报错:" + PropUtils.getExceptionInfo(e));
			result = "1";
		}
		if(StringUtils.isEmpty(result)){
			result = "1";
		}
		return result;	
	}
	
	/**
	 * 判断是否是数字
	 * @param str
	 * @return
	 */
	private boolean parseStr2Number(String str){
		   Pattern pattern = Pattern.compile("[0-9]*");
           Matcher isNum = pattern.matcher(str);
           if (isNum.matches()) {
           		return true;
           }
           return false;
	}
	/**
	 * 替换字符串的\t\r\n为空
	 * @param result
	 * @return
	 */
	private static String replaceString2(String result){
		result = result == null ? "" : result;
		return result.replace("\t", "").replace("\r", "").replace("\n", "");
	}
	/**
	 * 压缩生成的文件
	 * @return
	 */
	private String zipPage()   {
		String path = getGenerateDir();
		String fileName = path + ZIP_NAME;//生成一个zip文件
		createFolder(path);
		File file = null;
		OutputStream outStream = null;
		try {
			file = createFile(fileName);
			outStream = new FileOutputStream(file);
			ZipUtils.zipFolder( filePath,outStream); //压缩filepath的文件
		} catch (FileNotFoundException e) {
			log.error("服务器压缩生成的文件时失败,文件没找到:" + PropUtils.getExceptionInfo(e));
		} catch (IOException e) {
			log.error("服务器压缩生成的文件时失败:" + PropUtils.getExceptionInfo(e));
		}finally{
			if(outStream != null ){
				try {
					outStream.close();
				} catch (IOException e) {
					log.error("服务器压缩生成的文件关闭流时失败:" + PropUtils.getExceptionInfo(e));
				}
			}
			//清理缓存
			try {
				FileUtils.deleteDirectory(createFolder(filePath));
			} catch (Exception e) {
				log.error("服务器压缩生成的文件后清缓存失败:" + PropUtils.getExceptionInfo(e));
			}
		}
		
		return fileName;
	}
	private String getGenerateDir() {
		String path = CreateHtmlpageAction.class.getClassLoader().getResource("application.properties").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/classes")) + TEMP_PATH;
		return path;
	}
	/**
	 * 创建file，不存在则创建
	 * @param projectPath
	 * @return
	 */
	private File createFolder(String projectPath) {
		File folder = new File(projectPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	private File createFile(String projectPath) throws IOException {
		File folder = new File(projectPath);
		if (!folder.exists()) {
			folder.createNewFile();
		}
		return folder;
	}
	/**
	 * 生成html以及jsp页面
	 * @param resultList
	 */
	private void createPage(int count){
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("fileName", fileName);
		dataModel.put("inputShowType", inputShowType);
		dataModel.put("valueList", valueList);
		dataModel.put("dateList", dateList);
		if("list".equals(pageType)){
			createListPage(dataModel,nameList,count);
		}else if("input".equals(pageType)){
			createInputPage(dataModel,count);
		}else if("view".equals(pageType)){
			createViewPage(dataModel,count);
		}
	}

		/**
		 * 创建查看页面
		 * @param dataModel
		 */
		private void createViewPage(Map<String, Object> dataModel,int count) {
			dataModel.put("templateName", "list-view-see.ftl");
			generateFile(dataModel, filePath ,"00"+count+fileName.replace(".html", "") + ".html",dataModel.get("templateName").toString());//html
			dataModel.put("templateName", "list-jsp-view.ftl");
			dataModel.put("valueList", valueJspList);
			dataModel = getPublicDataModel(dataModel,fileName);
			generateFile(dataModel, filePath,"00"+count+fileName.replace(".jsp", "") + ".jsp",dataModel.get("templateName").toString());//jsp
		}
		/**
		 * 创建表单页面
		 * @param dataModel
		 */
		private void createInputPage(Map<String, Object> dataModel,int count) {
			dataModel.put("templateName", "list-view-input.ftl");
			generateFile(dataModel, filePath ,"00"+count+fileName.replace(".html", "") + ".html",dataModel.get("templateName").toString());//html
			dataModel.put("templateName", "list-jsp-input.ftl");
			dataModel.put("valueList", valueJspList);
			dataModel = getPublicDataModel(dataModel,fileName);
			generateFile(dataModel, filePath,"00"+count+fileName.replace(".jsp", "") + ".jsp",dataModel.get("templateName").toString());//jsp
		}
		/**
		 * 创建列表页面
		 * @param dataModel
		 */
		private void createListPage(Map<String, Object> dataModel, List<String> nameList,int count) {
			dataModel.put("templateName", "list-view.ftl");
			dataModel.put("nameList", nameList);
			generateFile(dataModel, filePath ,"00"+count+fileName.replace(".html", "") + ".html",dataModel.get("templateName").toString());//html
			dataModel.put("templateName", "list-jsp.ftl");
			dataModel.put("valueList", valueJspList);
			dataModel = getPublicDataModel(dataModel,fileName);
			generateFile(dataModel, filePath,"00"+count+fileName.replace(".jsp", "") + ".jsp",dataModel.get("templateName").toString());//jsp
		}

		/**
		 * 调用模版生成文件
		 * @param dataModel   数据map
		 * @param filePath    路径
		 * @param fileName    待生成的文件名
		 * @param templateName  模版名称
		 */
		private void generateFile(Map<String, Object> dataModel,String filePath,String fileName,String templateName){
			BufferedReader reader=null;
			try {
				reader = new BufferedReader(new InputStreamReader(CreateHtmlpageAction.class.getClassLoader()
							.getResourceAsStream(CreateHtmlpageAction.TEMPLATE_DIR+"html/"+templateName),"UTF-8"));
				Template template = new Template(null, reader, config, "UTF-8");
				createFolder(filePath);
				File file = createFile(filePath + "/" + fileName);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
				template.process(dataModel, writer);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				log.error("服务器调用模版生成文件失败:" + PropUtils.getExceptionInfo(e));
			}
		}
		/**
		 * 生成jsp页面得到公共的dataModel
		 * @param dataModel
		 * @param fileName
		 * @return
		 */
		private Map<String, Object> getPublicDataModel(Map<String, Object> dataModel,String fileName){
			dataModel.put("id", "${id }");
			dataModel.put("ctx", "${ctx}");
			dataModel.put("resourcesCtx", "${resourcesCtx}");
			dataModel.put("fieldPermission", "${fieldPermission}");
			dataModel.put("autoFillOpinionInfo", "${autoFillOpinionInfo}");
			dataModel.put("imatrixCtx", "${imatrixCtx}");
			String entityAttribute = stringUpperTolowAndaddLine(fileName);
			dataModel.put("entityAttribute", entityAttribute);//action路径
			return dataModel;
		}

	/**
	 * 将字符串：changeUpperToLow转为change-upper-to-low
	 * 
	 * @param fileName
	 * @return
	 */
	private static String stringUpperTolowAndaddLine(String fileName) {
		StringBuilder sbs = new StringBuilder();
		char[] charArr = fileName.toCharArray();
		for (char c : charArr) {
			if (c >= 65 && c <= 90) {
				sbs.append("-" + c);
			} else {
				sbs.append(c);
			}
		}
		return sbs.toString().toLowerCase();
	}

	/**
	 * 得到控件的html
	 * 
	 * @param cellVal格式 : code-input-classname-value-
	 * @return
	 */
	public String getControlValue(String cellVal ,String flag) {
		String result = "";
		String[] cellValArr = cellVal.split(SPLIT_NAME);
		if (cellValArr.length < 2) {// 例如：code&，当作文本框处理
			result = "<input type=\"text\" id=\"" + cellValArr[0] + "\" name=\"" + cellValArr[0] + "\" />";// 为空，当作input文本框处理
		} else {
			String publicInfo = "id=" + YIN_CODE + cellValArr[0] + YIN_CODE + " name=" + YIN_CODE + cellValArr[0] + YIN_CODE;
			String idAndName = "id=" + YIN_CODE + cellValArr[0] + YIN_CODE + " name=" + YIN_CODE + cellValArr[0] + YIN_CODE;
			String className = "";
			if (cellValArr.length > 2) {
				className = YIN_CODE + cellValArr[2] + YIN_CODE;
				publicInfo += " class=" + YIN_CODE + cellValArr[2] + YIN_CODE;
			}
			String valueStr = "";
			String value3 = "";
			if (cellValArr.length > 3) {
				value3 = cellValArr[3];
				if("html".equals(flag)){
					valueStr = " value=" + YIN_CODE + cellValArr[3] + YIN_CODE;
					publicInfo += " value=" + YIN_CODE + cellValArr[3] + YIN_CODE;
				}else if ("jsp".equals(flag)) {
					valueStr = " value=${"  + cellValArr[0] + "}";
					publicInfo += " value=${" + cellValArr[0] + "}";
				}
			}
			String value4 = "";
			if (cellValArr.length > 4) {
				value4 = cellValArr[4] ;
			}
			
			if ("input".equals(cellValArr[1])) {
				result = "<input type=\"text\" " + publicInfo + " />";// 
			} else if ("password".equals(cellValArr[1])) {
				result = "<input type=\"password\" " + publicInfo + " />";// 
			} else if ("hidden".equals(cellValArr[1])) {
				result = "<input type=\"hidden\" " + publicInfo + " />";//  
			} else if ("textarea".equals(cellValArr[1])) {
				result = "<textarea cols=" + YIN_CODE + getTextAreaColAndRow(cellValArr)[1] + YIN_CODE + " rows=" + YIN_CODE + getTextAreaColAndRow(cellValArr)[0] + YIN_CODE;
				result += publicInfo + ">" + parseValueAtte(cellValArr,flag) + "</textarea>";
				result = "<div>" + result + "</div>";
			} else if ("checkbox".equals(cellValArr[1])) {
				result = getCheckBoxValue(cellValArr,idAndName,className,flag);
				return result;
			} else if ("select".equals(cellValArr[1])) {
				result = getSelectBoxValue(cellValArr,idAndName,className,flag);
				return result;
			} else if ("radio".equals(cellValArr[1])) {
				result = getRadioBoxValue(cellValArr,idAndName,className,flag);
				return result;
			} else if ("file".equals(cellValArr[1])) {
				result = "<input type=\"file\" " + idAndName + " class=" + className + " value=" + YIN_CODE + parseValueAtte(cellValArr,flag) + YIN_CODE + "/>";
			} else if ("button".equals(cellValArr[1])) {
				result = "<input type=\"button\" " + idAndName + " class=" + className + " value=" + YIN_CODE + parseValueAtte(cellValArr,flag) + YIN_CODE + "/>";
			} else if ("submit".equals(cellValArr[1])) {
				result = "<input type=\"submit\" " + idAndName + " class=" + className + " value=" + YIN_CODE + parseValueAtte(cellValArr,flag) + YIN_CODE + "/>";
			} else if ("reset".equals(cellValArr[1])) {
				result = "<input type=\"reset\" " + idAndName + " class=" + className + " value=" + YIN_CODE + parseValueAtte(cellValArr,flag) + YIN_CODE + "/>";
			} else if ("a".equals(cellValArr[1])) {
				result = "<a " + idAndName + " class=" + className + " href=" + YIN_CODE + value4 + YIN_CODE + ">"+value3+"</a>";
			} else if ("img".equals(cellValArr[1])) {
				result = "<img  " + idAndName + " class=" + className + " src=" + YIN_CODE + value3 + YIN_CODE + "/>";
			} else if ("label".equals(cellValArr[1])) {
				result = "<label  for='" + value3 + "' class=" + className;
			} else if ("date".equals(cellValArr[1])) {
				result = "<input type=\"text\" " + publicInfo + " />";//  date
				dateList.add(cellValArr[0]);
			}
		}
		return result;
	}

	private String getRadioBoxValue(String[] cellValArr, String idAndName, String className, String flag) {
		StringBuilder sb = new StringBuilder();
		if (getmutiplyValue(cellValArr) != null && getmutiplyValue(cellValArr).length > 0) {
			if ("html".equals(flag)) {
				int i = 0;
				for (String string : getmutiplyValue(cellValArr)) {
					if (i == 0) {
						i++;continue;
					}
					sb.append(" <input type=" + YIN_CODE + "radio" + YIN_CODE + " " + idAndName + " value= " + YIN_CODE + string + YIN_CODE + " class=" + className + "/> " + string + "\n");
				}
			}else if ("jsp".equals(flag)) {
				String listName = getmutiplyValue(cellValArr)[0];// 遍历的选项组的list的名称
				sb.append("<s:iterator value=" + YIN_CODE + listName + YIN_CODE + " var=" + YIN_CODE + "var" + YIN_CODE + ">");
				sb.append("<input type=" + YIN_CODE + "radio" + YIN_CODE + " " + idAndName + " class=" + className);
				sb.append(" value=" + YIN_CODE + "#var.name" + YIN_CODE + "/> " + "${var.name}" + "\n");
				sb.append("</s:iterator>");
			}
		} else {
			sb.append(" <input type=" + YIN_CODE + "radio" + YIN_CODE + " " + idAndName + " class=" + className + " value= '' /> " + cellValArr[0] + " \n");
		}
		return sb.toString();
	}
	private String getSelectBoxValue(String[] cellValArr, String idAndName, String className, String flag) {
		StringBuilder sb = new StringBuilder();
		if (getmutiplyValue(cellValArr) != null && getmutiplyValue(cellValArr).length > 0) {
			if ("html".equals(flag)) {
				sb.append(" <select width=\"150\" " + idAndName + " class=" + className + "  > \n");
				int i = 0;
				for (String string : getmutiplyValue(cellValArr)) {
					if (i == 0) {
						i++;continue;
					}
					sb.append(" <option value=" + YIN_CODE + string + YIN_CODE + ">" + string + "</option> \n");
				}
				sb.append("</select>");
			}else if ("jsp".equals(flag)) {
				sb.append("<select " + idAndName + " class=" + className + "  > \n");
				String listName = getmutiplyValue(cellValArr)[0];// 遍历的选项组的list的名称
				sb.append("<s:iterator value=" + YIN_CODE + listName + YIN_CODE + " var=" + YIN_CODE + "var" + YIN_CODE + ">");
				sb.append(" <option value=" + YIN_CODE + "#var.name" + YIN_CODE + ">" + "${var.name}" + "</option> \n");
				sb.append("</s:iterator>");
				sb.append("</select>");
				
			}
		} else {
			sb.append(" <select width=\"150\" " + idAndName + " class=" + className + " ></select> \n");
		}
		return sb.toString();
	}
	private String getCheckBoxValue(String[] cellValArr, String idAndName, String className, String flag) {
		StringBuilder sb = new StringBuilder();
		if (getmutiplyValue(cellValArr) != null && getmutiplyValue(cellValArr).length > 0) {
			if ("html".equals(flag)) {
				int i = 0;
				for (String string : getmutiplyValue(cellValArr)) {
					if (i == 0) {
						i++;continue;
					}
					sb.append("<input type=" + YIN_CODE + "checkbox" + YIN_CODE + " " + idAndName + " class=" + className);
					sb.append(" value=" + YIN_CODE + string + YIN_CODE + "/> " + string + "\n");
				}
			} else if ("jsp".equals(flag)) {
				String listName = getmutiplyValue(cellValArr)[0];// 遍历的选项组的list的名称
				sb.append("<s:iterator value=" + YIN_CODE + listName + YIN_CODE + " var=" + YIN_CODE + "var" + YIN_CODE + ">");
				sb.append("<input type=" + YIN_CODE + "checkbox" + YIN_CODE + " " + idAndName + " class=" + className);
				sb.append(" value=" + YIN_CODE + "#var.name" + YIN_CODE + "/> " + "${var.name}" + "\n");
				sb.append("</s:iterator>");
			}
		} else {
			sb.append(" <input type=" + YIN_CODE + "checkbox" + YIN_CODE + "" + idAndName + " class=" + className + " value= ''/>" + cellValArr[0] + " \n");
		}
		return sb.toString();
	}
	/**
	 * 得到控件的value
	 * 
	 * @param cellVal
	 * @return
	 */
	private static String parseValueAtte(String[] cellValArr,String flag) {
		if (cellValArr.length > 3) {// 例如：code-input-classname-value-3,4
			if("html".equals(flag)){
				return cellValArr[3].replace("<br />", "");
			} else if("jsp".equals(flag)){
				return "${"+ cellValArr[0].replace("<br />", "") +"}";
			}else {
				return "";
			}
		} else {
			return "";
		}
	}
	/**
	 * 获得TextArea的行号， 列号
	 * 
	 * @param cellValArr
	 * @return 行_列
	 */
	private static String[] getTextAreaColAndRow(String[] cellValArr) {
		String[] result = new String[2];
		if (cellValArr.length > 4) {// 例如：code-input-classname-value-3,4
			try {
				String[] valueArr = cellValArr[4].split(",");
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher matcher1 = pattern.matcher(valueArr[0]);
				Matcher matcher2 = pattern.matcher(valueArr[1]);
				if (matcher1.matches() && matcher2.matches()) {
					result[0] = valueArr[0];
					result[1] = valueArr[1];
					return result;
				}
			} catch (Exception e) {
				log.error("服务器获得TextArea的列号，行号失败，已默认将行列处理为0,0:" + PropUtils.getExceptionInfo(e));
				result[0] = "10";
				result[1] = "10";
			}
		} else {
			result[0] = "10";
			result[1] = "10";
		}
		return result;
	}
	/**
	 * 获得checkbox,getSelect,radio的值
	 * 
	 * @param cellValArr
	 * @return //例如：code-select-classname-value-，
	 *         //例如：code-checkbox-classname-value-，
	 *         //例如：code-radio-classname-value-，
	 */
	private static String[] getmutiplyValue(String[] cellValArr) {
		if (cellValArr.length > 3) {
			String resultA = cellValArr[3];
			String[] values = resultA.split(",");
			return values;
		}
		return null;
	}
		
	
	@Action("download-htmlpage")
	public String generateCodePostFile()    {
		FileInputStream fileinput = null;
		BufferedInputStream bis = null;
		OutputStream out = null;
		try {
			fileinput = new FileInputStream(getGenerateDir() + ZIP_NAME);
			bis = new BufferedInputStream(fileinput);
			HttpServletResponse response = ServletActionContext.getResponse();
			response.reset();
			response.setContentType("application/x-download");
			byte[] byname=ZIP_NAME.getBytes("gbk");
			fileName=new String(byname,"8859_1");
			response.addHeader("Content-Disposition", "attachment;filename="+ZIP_NAME);
			out=response.getOutputStream();
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
		} catch (IOException e) {
			log.error("生成表单文件下载时异常:" + PropUtils.getExceptionInfo(e));
		}finally{
			try {
				out.close();
				bis.close();
				fileinput.close();
			} catch (IOException e) {
				log.error("生成表单文件下载时关闭流异常:" + PropUtils.getExceptionInfo(e));
			}
		}
	return null;
	}
/************************************************************************************************************************************************************************************************/
	@Override
	protected void prepareModel() throws Exception {

	}
	@Override
	public String delete() throws Exception {
		return null;
	}
	@Override
	public String input() throws Exception {
		return null;
	}

	public DataTable getModel() {
		return null;
	}


	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setInputShowType(String inputShowType) {
		this.inputShowType = inputShowType;
	}
	public String getInputShowType() {
		return inputShowType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPageType() {
		return pageType;
	}
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
