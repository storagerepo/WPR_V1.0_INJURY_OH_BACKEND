package com.deemsys.project.pdfparser;

public class PDFParserVehicleData {
	private String unitNumber;
	private String ownerName;
	private String ownerAddress;
	private String phoneNumber;
	private String vehicleMake;
	private String vehicleYear;
	private String vehicleModel;
	private String vehicleType;
	private String VIN;
	private String plateNumber;
	private String insuranceCompany;
	private String policyNumber;
	private String damageScale;
	private String typeOfUse;

	public String getUnitNumber() {
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerAddress() {
		return ownerAddress;
	}
	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getVehicleMake() {
		return vehicleMake;
	}
	public void setVehicleMake(String vehicleMake) {
		this.vehicleMake = vehicleMake;
	}
	public String getVehicleYear() {
		return vehicleYear;
	}
	public void setVehicleYear(String vehicleYear) {
		this.vehicleYear = vehicleYear;
	}
	public String getVehicleModel() {
		return vehicleModel;
	}
	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getVIN() {
		return VIN;
	}
	public void setVIN(String VIN) {
		this.VIN = VIN;
	}
	public String getPlateNumber() {
		return plateNumber;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
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
	public String getDamageScale() {
		return damageScale;
	}
	public void setDamageScale(String damageScale) {
		this.damageScale = damageScale;
	}
	public PDFParserVehicleData(String unitNumber,String ownerName, String ownerAddress,String phoneNumber, String vehicleMake, String vehicleYear,
			String vehicleModel, String vehicleType, String VIN, String plateNumber, String insuranceCompany, String policyNumber,String damageScale, String typeOfUse) {
		super();
		this.unitNumber = unitNumber;
		this.ownerName = ownerName;
		this.ownerAddress = ownerAddress;
		this.phoneNumber = phoneNumber;
		this.vehicleMake = vehicleMake;
		this.vehicleYear = vehicleYear;
		this.vehicleModel = vehicleModel;
		this.vehicleType = vehicleType;
		this.VIN = VIN;
		this.plateNumber = plateNumber;
		this.insuranceCompany = insuranceCompany;
		this.policyNumber = policyNumber;
		this.damageScale=damageScale;
		this.typeOfUse=typeOfUse;
	}
	public PDFParserVehicleData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTypeOfUse() {
		return typeOfUse;
	}
	public void setTypeOfUse(String typeOfUse) {
		this.typeOfUse = typeOfUse;
	}
	
}
