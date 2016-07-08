package com.norteksoft.bs.options.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.norteksoft.bs.options.enumeration.InternationType;
import com.norteksoft.product.orm.IdEntityNoExtendField;

/**
 * 国际化类
 * @author liudongxia
 *
 */
@Entity
@Table(name="BS_INTERNATION")
public class Internation extends IdEntityNoExtendField{
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String code;//编码
	@Column(length=200)
	private String remark;//备注
	@Enumerated(EnumType.STRING)
	private InternationType internationType;
	@OneToMany(mappedBy="internation", cascade=CascadeType.REMOVE)
	private List<InternationOption> internationOptions;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<InternationOption> getInternationOptions() {
		return internationOptions;
	}
	public void setInternationOptions(List<InternationOption> internationOptions) {
		this.internationOptions = internationOptions;
	}
	public InternationType getInternationType() {
		return internationType;
	}
	public void setInternationType(InternationType internationType) {
		this.internationType = internationType;
	}
	
}
