package com.deemsys.project.patient;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deemsys.project.County.CountyDAO;
import com.deemsys.project.County.CountyService;
import com.deemsys.project.CrashReport.CrashReportDAO;
import com.deemsys.project.CrashReport.CrashReportService;
import com.deemsys.project.LawyerAdminCountyMapping.LawyerAdminCountyMappingDAO;
import com.deemsys.project.common.GeoLocation;
import com.deemsys.project.ReportingAgency.ReportingAgencyDAO;
import com.deemsys.project.UserLookupPreferences.UserLookupPreferenceMappedForm;
import com.deemsys.project.UserLookupPreferences.UserLookupPreferencesService;
import com.deemsys.project.VehicleMakeAbbreviation.VehicleMakeAbbreviationDAO;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.entity.County;
import com.deemsys.project.entity.CrashReport;
import com.deemsys.project.entity.LawyerAdminCountyMap;
import com.deemsys.project.entity.Patient;
import com.deemsys.project.entity.ReportingAgency;
import com.deemsys.project.entity.VehicleMakeAbbreviation;

/**
 * 
 * @author Deemsys
 * 
 *         Patient - Entity patient - Entity Object patients - Entity List
 *         patientDAO - Entity DAO patientForms - EntityForm List PatientForm
 *         - EntityForm
 * 
 */
@Service
@Transactional
public class PatientService {

	@Autowired
	PatientDAO patientDAO;

	@Autowired
	GeoLocation geoLocation;

	@Autowired
	CountyDAO countyDAO;
	
	@Autowired
	CrashReportDAO crashReportDAO;
	
	@Autowired
	ReportingAgencyDAO reportingAgencyDAO;
	
	@Autowired
	VehicleMakeAbbreviationDAO vehicleMakeAbbreviationDAO;
	
	@Autowired
	LawyerAdminCountyMappingDAO lawyerAdminCountyMappingDAO;
	
	@Autowired
	CrashReportService crashReportService;
	
	@Autowired
	UserLookupPreferencesService userLookupPreferencesService;
	
	@Autowired
	CountyService countyService;

	// Get Particular Entry
	public PatientForm getPatient(String patientId) {
		
		Patient patient=patientDAO.getPatientByPatientId(patientId);
		PatientForm patientForm=this.getPatientForm(patient);
		
		/* Map Geo Location Will Updated in Clicking Near By Clinics Button
		 * 
		 * if(patientForm.getLatitude().equals(0.0)&&patientForm.getLongitude().equals(0.0)){
			String latLong = geoLocation.getLocation(patientForm.getAddress());
			BigDecimal longitude = new BigDecimal(0);
			BigDecimal latitude = new BigDecimal(0);
			if (!latLong.equals("NONE")) {
				String[] latitudeLongitude = latLong.split(",");
				latitude = new BigDecimal(latitudeLongitude[0]);
				longitude = new BigDecimal(latitudeLongitude[1]);
			}
			patientForm.setLatitude(InjuryConstants.convertBigDecimaltoDouble(latitude));
			patientForm.setLongitude(InjuryConstants.convertBigDecimaltoDouble(longitude));
			patient.setLatitude(latitude);
			patient.setLongitude(longitude);
			
			// Update Patient With Latitude Longitude
			patientDAO.update(patient);
		}else{
			patientForm.setLatitude(InjuryConstants.convertBigDecimaltoDouble(patient.getLatitude()));
			patientForm.setLongitude(InjuryConstants.convertBigDecimaltoDouble(patient.getLongitude()));
		}
		*/
		return patientForm;
		
	}

	// Update an Entry
	public int updatePatientCurrentAddedDate(PatientForm patientForm) {
		// TODO: Convert Form to Entity Here

		// Logic Starts
		/*String latLong = geoLocation.getLocation(patientForm.getAddress());
		if (!latLong.equals("NONE")) {
			String[] latitudeLongitude = latLong.split(",");
			latitude = Double.parseDouble(latitudeLongitude[0]);
			longitude = Double.parseDouble(latitudeLongitude[1]);
		}*/
		double longitude = 0.0;
		double latitude = 0.0;
		
		
		patientForm.setLatitude(latitude);
		patientForm.setLongitude(longitude);
		
		patientDAO.update(this.getPatient(patientForm));

		return 1;
	}

	//Save an Entity
	public int savePatient(PatientForm patientForm) throws Exception {
		// TODO: Convert Form to Entity Here

		// Logic Starts
		try{
			/*String latLong = geoLocation.getLocation(patientForm.getAddress());
			if (!latLong.equals("NONE")) {
				String[] latitudeLongitude = latLong.split(",");
				latitude = Double.parseDouble(latitudeLongitude[0]);
				longitude = Double.parseDouble(latitudeLongitude[1]);
			}*/
			double longitude = 0.0;
			double latitude = 0.0;
			
	
			patientForm.setLatitude(latitude);
			patientForm.setLongitude(longitude);
			try{
				patientDAO.savePatient(this.getPatient(patientForm));
			}catch(Exception exception){
				throw exception;
			}
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
		
		return 1;
	}

	
	public Integer releasePatientFromCaller(Integer id) {
		/*List<Patient> patient = patientDAO.getPatientListByCallerId(id);
		for (Patient patients : patient) {
			int patientId = patients.getId();
			System.out.println("patientid:" + patientId);
			patientDAO.releasePatientFromCaller(patientId);

		}*/
		return 1;

	}

	// Remove More than One White Space
	public String replaceWithWhiteSpacePattern(String str, String replace) {

		Pattern ptn = Pattern.compile("\\s+");
		Matcher mtch = ptn.matcher(str);
		return mtch.replaceAll(replace);
	}

	
	public Integer activeStatusByPatientId(Integer id) {
		Integer status = 1;
		patientDAO.activeStatusByPatientId(id);
		return status;
	}

	// Get Patient List By Page Limit
	public List<PatientForm> getPatientByLimit(Integer pageNumber,
			Integer itemsPerPage, String name, String phoneNumber,
			String localReportNumber, String callerName) {
		List<Patient> patients = new ArrayList<Patient>();
		List<PatientForm> patientForms = new ArrayList<PatientForm>();
		

		for (Patient patient : patients) {
			patientForms.add(this.getPatientForm(patient));
		}

		return patientForms;
	}

	// Get Total Patient Count By Page Limit
	public Integer getTotalPatient(String localReportNumber,Integer county, 
			String crashDate,Integer days,String recordedFromDate,String recordedToDate, String name,String customDate) {
		Integer count = 0;
		String crashToDate="";
		if(crashDate!=""){
			DateTime crashStartDate=DateTime.parse(crashDate,DateTimeFormat.forPattern("MM/dd/yyyy"));
			DateTime crashEndDate=crashStartDate.plusDays(days);
			
			if(days!=0)
				crashToDate=crashEndDate.toString("MM/dd/yyyy");
			else
				crashToDate=customDate;
		}
		
		count = patientDAO.getTotalPatientCount(localReportNumber, county, crashDate, crashToDate, recordedFromDate, recordedToDate, name);
		
		return count;
	}
	
		
	//Patient -> Patient Form	
	public PatientForm getPatientForm(Patient patient) {

		String abbreviation="";
		if(patient.getVehicleMakeAbbreviation()!=null){
			abbreviation=patient.getVehicleMakeAbbreviation().getAbbreviation();
		}
		
		PatientForm patientForm = new PatientForm(patient.getPatientId(),patient.getCrashReport().getCrashId(),
				patient.getCrashReport().getLocalReportNumber(), patient.getCrashSeverity(),
				patient.getReportingAgencyNcic(),
				patient.getReportingAgencyName(), patient.getNumberOfUnits(),
				patient.getUnitInError(), patient.getCityVillageTownship(),
				InjuryConstants.convertMonthFormat(patient.getCrashDate()), InjuryConstants.convertMonthFormat(patient.getAddedDate()),
				patient.getTimeOfCrash(), patient.getUnitNumber(),
				patient.getName(), patient.getDateOfBirth(),patient.getAge(),patient.getGender(), 
				patient.getAddress(),
				InjuryConstants.convertBigDecimaltoDouble(patient.getLatitude()), InjuryConstants.convertBigDecimaltoDouble(patient.getLongitude()),
				patient.getPhoneNumber(), patient.getInjuries(),
				patient.getEmsAgency(), patient.getMedicalFacility(),
				patient.getAtFaultInsuranceCompany(),
				patient.getAtFaultPolicyNumber(),
				patient.getVictimInsuranceCompany(),
				patient.getVictimPolicyNumber(),patient.getTier(),
				abbreviation,patient.getVehicleYear(),
				patient.getVin(),patient.getLicensePlateNumber(), patient.getTypeOfUse(),
				patient.getIsOwner(), patient.getPatientStatus(),
				patient.getCrashReport().getFilePath(), patient.getStatus(),patient.getSeatingPosition(),patient.getDamageScale(),patient.getIsRunnerReport());

		// Null Exception Check
		if (patient.getCounty() != null) {
			patientForm.setCountyId(patient.getCounty().getCountyId());
			patientForm.setCounty(patient.getCounty().getName());
		}

		return patientForm;
	}
	
		//Patient -> Patient Form	
	public Patient getPatient(PatientForm patientForm) {
	
	//Check Condition
	County county=new County();
	patientForm.setCountyId(Integer.parseInt(patientForm.getCounty()));
	county=countyDAO.get(patientForm.getCountyId());
	
	//Crash Report
	CrashReport crashReport=new CrashReport();
	if(patientForm.getCrashId()!=null){
		crashReport=crashReportDAO.getCrashReport(patientForm.getCrashId());
	}
	
	// Vehicle Make Abbreviation
	VehicleMakeAbbreviation vehicleMakeAbbreviation = vehicleMakeAbbreviationDAO.getVehicleMakeAbbreviationByMake(patientForm.getVehicleMake());
	if(vehicleMakeAbbreviation==null&&patientForm.getVehicleMake()!=null){
		vehicleMakeAbbreviation=new VehicleMakeAbbreviation(patientForm.getVehicleMake(), patientForm.getVehicleMake(), 1, null);
		vehicleMakeAbbreviationDAO.save(vehicleMakeAbbreviation);
	}
	
	// Reporting Agency Check And Update the Agency List
	if(patientForm.getReportingAgencyNcic()!=null&&!patientForm.getReportingAgencyNcic().equals("")){
		Integer countyId=county.getCountyId();
		ReportingAgency reportingAgency = reportingAgencyDAO.getReportingAgencyByCodeAndCounty(countyId, patientForm.getReportingAgencyNcic());
		if(reportingAgency==null){
			reportingAgency = new ReportingAgency(county, patientForm.getReportingAgencyName(), patientForm.getReportingAgencyNcic(), 1);
			reportingAgencyDAO.save(reportingAgency);
			List<LawyerAdminCountyMap> lawyerAdminCountyMaps=lawyerAdminCountyMappingDAO.getLawyerAdminCountyMappingsByCountyId(countyId);
			List<Integer> preferredId=new ArrayList<Integer>();
			preferredId.add(reportingAgency.getReportingAgencyId());
			for (LawyerAdminCountyMap lawyerAdminCountyMap : lawyerAdminCountyMaps) {
				UserLookupPreferenceMappedForm userLookupPreferenceMappedForm = new UserLookupPreferenceMappedForm(InjuryConstants.REPORTING_AGENCY_LOOKUP, preferredId);
				userLookupPreferenceMappedForm.setCountyId(countyId);
				userLookupPreferencesService.saveReportingAgencyPreference(lawyerAdminCountyMap.getLawyerAdmin().getUsers().getUserId(), userLookupPreferenceMappedForm);
			}
		}else{
			patientForm.setReportingAgencyName(reportingAgency.getReportingAgencyName());
		}
	}
	
	//Date 
	LocalDate addedDate=new LocalDate();
	
	//Mapping
	Patient patient = new Patient(patientForm.getPatientId(),crashReport, county,
			vehicleMakeAbbreviation,
			patientForm.getLocalReportNumber(),
			patientForm.getCrashSeverity(),
			patientForm.getReportingAgencyNcic(),
			patientForm.getReportingAgencyName(),
			patientForm.getNumberOfUnits(), patientForm.getUnitInError(),
			 patientForm.getCityVillageTownship(),
			InjuryConstants.convertYearFormat(patientForm.getCrashDate()), addedDate.toDate(),
			patientForm.getTimeOfCrash(), patientForm.getUnitNumber(),
			patientForm.getName(), patientForm.getDateOfBirth(),patientForm.getAge(),
			patientForm.getGender(), patientForm.getAddress(),
			InjuryConstants.convertDoubleBigDecimal(patientForm.getLatitude()), InjuryConstants.convertDoubleBigDecimal(patientForm.getLongitude()),
			patientForm.getPhoneNumber(), patientForm.getInjuries(),
			patientForm.getEmsAgency(), patientForm.getMedicalFacility(),
			patientForm.getAtFaultInsuranceCompany(),
			patientForm.getAtFaultPolicyNumber(),
			patientForm.getVictimInsuranceCompany(),
			patientForm.getVictimPolicyNumber(), patientForm.getSeatingPosition(), patientForm.getDamageScale(),patientForm.getTier(),
			patientForm.getVehicleYear(),patientForm.getVin(),
			patientForm.getLicensePlateNumber(), patientForm.getTypeOfUse(),patientForm.getIsOwner(),
			patientForm.getPatientStatus(),
			patientForm.getCrashReportFileName(), patientForm.getIsRunnerReport(), patientForm.getStatus(),null,null);
		
		return patient;
	}
	

	// Update Lat and Long
	public void updateLatLong(String addedFromDate,String addedToDate,Integer noOfRecords){
		
		List<Patient> patients =patientDAO.getPatientListForUpdateLatLong(addedFromDate, addedToDate,noOfRecords);
	
		for (Patient patient : patients) {
			if(!patient.getAddress().equals("")){
				Double conlat=InjuryConstants.convertBigDecimaltoDouble(patient.getLatitude());
				Double conlong=InjuryConstants.convertBigDecimaltoDouble(patient.getLongitude());
				if(conlat.equals(0.0)&&conlong.equals(0.0)){
					String latLong = geoLocation.getLocation(patient.getAddress());
					BigDecimal longitude = new BigDecimal(0);
					BigDecimal latitude = new BigDecimal(0);
					if (latLong!=null) {
						String[] latitudeLongitude = latLong.split(",");
						latitude = new BigDecimal(latitudeLongitude[0]);
						longitude = new BigDecimal(latitudeLongitude[1]);
					}
					
					patient.setLatitude(latitude);
					patient.setLongitude(longitude);
					
					// Update Patient With Latitude Longitude
					patientDAO.update(patient);
				}
				
			}
		}
	}
	
	//Update Type of Use
	public Integer updatePatientTypeOfUse(String crashId, Integer unitNumber, Integer typeOfUse){
		try{
			patientDAO.updatePatientTypeOfUse(crashId, unitNumber, typeOfUse);
		}catch(Exception ex){
			return 0;
		}
		return 1;		
	}
	
	
}
