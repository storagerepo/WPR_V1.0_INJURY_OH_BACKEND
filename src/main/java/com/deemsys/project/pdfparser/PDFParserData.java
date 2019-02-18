package com.deemsys.project.pdfparser;

import java.util.List;

public class PDFParserData {
	private String numberOfUnits;
	private String unitInError;
	private String localReportNumber;
	private String crashSeverity;
	private String cityVillageTownship;
	private String crashDate;
	private String county;
	private String timeOfCrash;
	private String atFaultInsuranceCompany;
	private String atFaultPolicyNumber;
	private String victimInsuranceCompany;
	private String victimPolicyNumber;
	private String reportingAgency;
	private String reportingAgencyCode;
	private List<PDFParserUnitData> pdfParserUnitDatas;
	private List<PDFParserVehicleData> pdfParserVehicleDatas;
	
	public String getNumberOfUnits() {
		return numberOfUnits;
	}
	public void setNumberOfUnits(String numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}
	public String getUnitInError() {
		return unitInError;
	}
	public void setUnitInError(String unitInError) {
		this.unitInError = unitInError;
	}
	public String getLocalReportNumber() {
		return localReportNumber;
	}
	public void setLocalReportNumber(String localReportNumber) {
		this.localReportNumber = localReportNumber;
	}
	public String getCrashDate() {
		return crashDate;
	}
	public void setCrashDate(String crashDate) {
		this.crashDate = crashDate;
	}
	public String getTimeOfCrash() {
		return timeOfCrash;
	}
	public void setTimeOfCrash(String timeOfCrash) {
		this.timeOfCrash = timeOfCrash;
	}
	public String getAtFaultInsuranceCompany() {
		return atFaultInsuranceCompany;
	}
	public void setAtFaultInsuranceCompany(String atFaultInsuranceCompany) {
		this.atFaultInsuranceCompany = atFaultInsuranceCompany;
	}
	public String getAtFaultPolicyNumber() {
		return atFaultPolicyNumber;
	}
	public void setAtFaultPolicyNumber(String atFaultPolicyNumber) {
		this.atFaultPolicyNumber = atFaultPolicyNumber;
	}
	public String getVictimInsuranceCompany() {
		return victimInsuranceCompany;
	}
	public void setVictimInsuranceCompany(String victimInsuranceCompany) {
		this.victimInsuranceCompany = victimInsuranceCompany;
	}
	public String getVictimPolicyNumber() {
		return victimPolicyNumber;
	}
	public void setVictimPolicyNumber(String victimPolicyNumber) {
		this.victimPolicyNumber = victimPolicyNumber;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getReportingAgency() {
		return reportingAgency;
	}
	public void setReportingAgency(String reportingAgency) {
		this.reportingAgency = reportingAgency;
	}
	public String getReportingAgencyCode() {
		return reportingAgencyCode;
	}
	public void setReportingAgencyCode(String reportingAgencyCode) {
		this.reportingAgencyCode = reportingAgencyCode;
	}
	public List<PDFParserUnitData> getPdfParserUnitDatas() {
		return pdfParserUnitDatas;
	}
	public void setPdfParserUnitDatas(List<PDFParserUnitData> pdfParserUnitDatas) {
		this.pdfParserUnitDatas = pdfParserUnitDatas;
	}
	public List<PDFParserVehicleData> getPdfParserVehicleDatas() {
		return pdfParserVehicleDatas;
	}
	public void setPdfParserVehicleDatas(List<PDFParserVehicleData> pdfParserVehicleDatas) {
		this.pdfParserVehicleDatas = pdfParserVehicleDatas;
	}
	public PDFParserData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PDFParserData(String numberOfUnits, String unitInError, String localReportNumber, String crashDate,
			String county, String timeOfCrash, String atFaultInsuranceCompany, String atFaultPolicyNumber,
			String victimInsuranceCompany, String victimPolicyNumber, String reportingAgency,
			String reportingAgencyCode, List<PDFParserUnitData> pdfParserUnitDatas,
			List<PDFParserVehicleData> pdfParserVehicleDatas) {
		super();
		this.numberOfUnits = numberOfUnits;
		this.unitInError = unitInError;
		this.localReportNumber = localReportNumber;
		this.crashDate = crashDate;
		this.county = county;
		this.timeOfCrash = timeOfCrash;
		this.atFaultInsuranceCompany = atFaultInsuranceCompany;
		this.atFaultPolicyNumber = atFaultPolicyNumber;
		this.victimInsuranceCompany = victimInsuranceCompany;
		this.victimPolicyNumber = victimPolicyNumber;
		this.reportingAgency = reportingAgency;
		this.reportingAgencyCode = reportingAgencyCode;
		this.pdfParserUnitDatas = pdfParserUnitDatas;
		this.pdfParserVehicleDatas = pdfParserVehicleDatas;
	}
	public String getCrashSeverity() {
		return crashSeverity;
	}
	public void setCrashSeverity(String crashSeverity) {
		this.crashSeverity = crashSeverity;
	}
	public String getCityVillageTownship() {
		return cityVillageTownship;
	}
	public void setCityVillageTownship(String cityVillageTownship) {
		this.cityVillageTownship = cityVillageTownship;
	}
	
}
