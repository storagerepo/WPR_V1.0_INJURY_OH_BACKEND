package com.deemsys.project.entity;

// Generated 10 Jan, 2018 4:37:43 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * IpGeoLocation generated by hbm2java
 */
@Entity
@Table(name = "ip_geo_location", catalog = "injuryreportsdb")
public class IpGeoLocation implements java.io.Serializable {

	private String ipAddress;
	private String city;
	private String region;
	private String countryName;
	private String countryCode;
	private String continentName;
	private String continentCode;
	private String postal;
	private String latitude;
	private String longitude;
	private String timeZone;
	private String flag;
	private Integer status;

	public IpGeoLocation() {
	}

	public IpGeoLocation(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public IpGeoLocation(String ipAddress, String city, String region,
			String countryName, String countryCode, String continentName,
			String continentCode, String postal, String latitude,
			String longitude, String timeZone, String flag, Integer status) {
		this.ipAddress = ipAddress;
		this.city = city;
		this.region = region;
		this.countryName = countryName;
		this.countryCode = countryCode;
		this.continentName = continentName;
		this.continentCode = continentCode;
		this.postal = postal;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timeZone = timeZone;
		this.flag = flag;
		this.status = status;
	}

	@Id
	@Column(name = "ip_address", unique = true, nullable = false, length = 45)
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(name = "city", length = 45)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "region", length = 45)
	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Column(name = "country_name", length = 45)
	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Column(name = "country_code", length = 45)
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name = "continent_name", length = 45)
	public String getContinentName() {
		return this.continentName;
	}

	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

	@Column(name = "continent_code", length = 45)
	public String getContinentCode() {
		return this.continentCode;
	}

	public void setContinentCode(String continentCode) {
		this.continentCode = continentCode;
	}

	@Column(name = "postal", length = 45)
	public String getPostal() {
		return this.postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	@Column(name = "latitude", length = 45)
	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude", length = 45)
	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Column(name = "time_zone", length = 45)
	public String getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Column(name = "flag", length = 45)
	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
