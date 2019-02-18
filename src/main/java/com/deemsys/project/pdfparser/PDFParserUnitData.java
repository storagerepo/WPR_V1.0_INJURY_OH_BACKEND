package com.deemsys.project.pdfparser;

public class PDFParserUnitData {
	private String name;
	private String dateOfBirth;
	private String phoneNumber;
	private String age;
	private String gender;
	private String address;
	private String unitNumber;
	private String seatingPosition;
	private String injuries;
	private String insuranceCompany;
	private String policyNumber;
	private String EMSAgency;
	private String medicalFacility;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	public String getSeatingPosition() {
		return seatingPosition;
	}
	public void setSeatingPosition(String seatingPosition) {
		this.seatingPosition = seatingPosition;
	}
	public String getInjuries() {
		return injuries;
	}
	public void setInjuries(String injuries) {
		this.injuries = injuries;
	}
	public String getInsuranceCompany() {
		return insuranceCompany;
	}
	public void setInsuranceCompany(String insuranceCompany) {
		this.insuranceCompany = insuranceCompany;
	}
	public String getPolicyNumber() {
		return policyNumber;
	}
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}
	public PDFParserUnitData(String name, String dateOfBirth, String phoneNumber, String age,
			String gender, String address, String unitNumber, String seatingPosition, String injuries,
			String insuranceCompany, String policyNumber, String EMSAgency, String medicalFacility) {
		super();
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.age = age;
		this.gender = gender;
		this.address = address;
		this.unitNumber = unitNumber;
		this.seatingPosition = seatingPosition;
		this.injuries = injuries;
		this.insuranceCompany = insuranceCompany;
		this.policyNumber = policyNumber;
		this.EMSAgency=EMSAgency;
		this.medicalFacility=medicalFacility;
	}
	public PDFParserUnitData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getEMSAgency() {
		return EMSAgency;
	}
	public void setEMSAgency(String eMSAgency) {
		EMSAgency = eMSAgency;
	}
	public String getMedicalFacility() {
		return medicalFacility;
	}
	public void setMedicalFacility(String medicalFacility) {
		this.medicalFacility = medicalFacility;
	}
}
