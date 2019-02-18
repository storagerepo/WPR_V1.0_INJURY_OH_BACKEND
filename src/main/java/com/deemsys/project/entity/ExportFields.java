package com.deemsys.project.entity;

// Generated 19 Aug, 2017 10:03:05 AM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ExportFields generated by hbm2java
 */
@Entity
@Table(name = "export_fields", catalog = "injuryreportsdb")
public class ExportFields implements java.io.Serializable {

	private Integer fieldId;
	private String fieldName;
	private Integer sequenceNo;
	private Integer isCustom;
	private String defaultValue;
	private Integer format;
	private Integer isCallerLawyer;
	private Integer autoDealerSequence;
	private Integer isAutoDealer;
	private Integer status;
	private Set<UserExportPreferences> userExportPreferenceses = new HashSet<UserExportPreferences>(
			0);

	public ExportFields() {
	}

	public ExportFields(String fieldName, Integer sequenceNo, Integer isCustom,
			String defaultValue, Integer format, Integer isCallerLawyer,
			Integer autoDealerSequence, Integer isAutoDealer, Integer status,
			Set<UserExportPreferences> userExportPreferenceses) {
		this.fieldName = fieldName;
		this.sequenceNo = sequenceNo;
		this.isCustom = isCustom;
		this.defaultValue = defaultValue;
		this.format = format;
		this.isCallerLawyer = isCallerLawyer;
		this.autoDealerSequence = autoDealerSequence;
		this.isAutoDealer = isAutoDealer;
		this.status = status;
		this.userExportPreferenceses = userExportPreferenceses;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "field_id", unique = true, nullable = false)
	public Integer getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	@Column(name = "field_name", length = 45)
	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Column(name = "sequence_no")
	public Integer getSequenceNo() {
		return this.sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name = "is_custom")
	public Integer getIsCustom() {
		return this.isCustom;
	}

	public void setIsCustom(Integer isCustom) {
		this.isCustom = isCustom;
	}

	@Column(name = "default_value", length = 600)
	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Column(name = "format")
	public Integer getFormat() {
		return this.format;
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	@Column(name = "is_caller_lawyer")
	public Integer getIsCallerLawyer() {
		return this.isCallerLawyer;
	}

	public void setIsCallerLawyer(Integer isCallerLawyer) {
		this.isCallerLawyer = isCallerLawyer;
	}

	@Column(name = "auto_dealer_sequence")
	public Integer getAutoDealerSequence() {
		return this.autoDealerSequence;
	}

	public void setAutoDealerSequence(Integer autoDealerSequence) {
		this.autoDealerSequence = autoDealerSequence;
	}

	@Column(name = "is_auto_dealer")
	public Integer getIsAutoDealer() {
		return this.isAutoDealer;
	}

	public void setIsAutoDealer(Integer isAutoDealer) {
		this.isAutoDealer = isAutoDealer;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "exportFields")
	public Set<UserExportPreferences> getUserExportPreferenceses() {
		return this.userExportPreferenceses;
	}

	public void setUserExportPreferenceses(
			Set<UserExportPreferences> userExportPreferenceses) {
		this.userExportPreferenceses = userExportPreferenceses;
	}

}
