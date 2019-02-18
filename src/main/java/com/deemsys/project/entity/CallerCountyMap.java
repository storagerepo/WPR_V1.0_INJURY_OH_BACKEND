package com.deemsys.project.entity;

// Generated 19 Aug, 2017 10:03:05 AM by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * CallerCountyMap generated by hbm2java
 */
@Entity
@Table(name = "caller_county_map", catalog = "injuryreportsdb")
public class CallerCountyMap implements java.io.Serializable {

	private CallerCountyMapId id;
	private County county;
	private Caller caller;
	private Date subscribedDate;
	private Integer status;

	public CallerCountyMap() {
	}

	public CallerCountyMap(CallerCountyMapId id, County county, Caller caller) {
		this.id = id;
		this.county = county;
		this.caller = caller;
	}

	public CallerCountyMap(CallerCountyMapId id, County county, Caller caller,
			Date subscribedDate, Integer status) {
		this.id = id;
		this.county = county;
		this.caller = caller;
		this.subscribedDate = subscribedDate;
		this.status = status;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "callerId", column = @Column(name = "caller_id", nullable = false)),
			@AttributeOverride(name = "countyId", column = @Column(name = "county_id", nullable = false)) })
	public CallerCountyMapId getId() {
		return this.id;
	}

	public void setId(CallerCountyMapId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "county_id", nullable = false, insertable = false, updatable = false)
	public County getCounty() {
		return this.county;
	}

	public void setCounty(County county) {
		this.county = county;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caller_id", nullable = false, insertable = false, updatable = false)
	public Caller getCaller() {
		return this.caller;
	}

	public void setCaller(Caller caller) {
		this.caller = caller;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "subscribed_date", length = 10)
	public Date getSubscribedDate() {
		return this.subscribedDate;
	}

	public void setSubscribedDate(Date subscribedDate) {
		this.subscribedDate = subscribedDate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
