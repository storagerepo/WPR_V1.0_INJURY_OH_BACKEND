package com.deemsys.project.entity;

// Generated 19 Aug, 2017 10:03:05 AM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * VehicleMakeAbbreviation generated by hbm2java
 */
@Entity
@Table(name = "vehicle_make_abbreviation", catalog = "injuryreportsdb")
public class VehicleMakeAbbreviation implements java.io.Serializable {

	private String make;
	private String abbreviation;
	private Integer status;
	private Set<Patient> patients = new HashSet<Patient>(0);

	public VehicleMakeAbbreviation() {
	}

	public VehicleMakeAbbreviation(String make) {
		this.make = make;
	}

	public VehicleMakeAbbreviation(String make, String abbreviation,
			Integer status, Set<Patient> patients) {
		this.make = make;
		this.abbreviation = abbreviation;
		this.status = status;
		this.patients = patients;
	}

	@Id
	@Column(name = "make", unique = true, nullable = false, length = 45)
	public String getMake() {
		return this.make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@Column(name = "abbreviation", length = 45)
	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vehicleMakeAbbreviation")
	public Set<Patient> getPatients() {
		return this.patients;
	}

	public void setPatients(Set<Patient> patients) {
		this.patients = patients;
	}

}
