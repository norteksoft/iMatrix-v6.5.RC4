package com.norteksoft.mms.form.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 附件文件描述信息
 * @author ldx
 *
 */
@Entity
@Table(name="MMS_ATTACHMENT")
public class FormAttachment extends IdEntityNoExtendField implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer fileSize;  //附件大小
	@Column(length=10)
	private String fileType;//文件类型
	@Column(length=150)
	private String filePath;//文件路径
	@Column(length=50)
	private String fileName;//文件名称
	@Column(length=50)
	private String recordInfo;//其值格式为：表名,记录id
	@Column(length=50)
	private String controlId;//附件控件id，用于记录该附件的记录
	@Column(length=20)
	private String pluginType;//上传控件类型：ATTACH_UPLOAD（附件上传控件） 、 IMAGE_UPLOAD（图片上传控件）
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getRecordInfo() {
		return recordInfo;
	}
	public void setRecordInfo(String recordInfo) {
		this.recordInfo = recordInfo;
	}
	public String getControlId() {
		return controlId;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
	public String getPluginType() {
		return pluginType;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
}
