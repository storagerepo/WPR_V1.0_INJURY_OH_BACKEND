package com.deemsys.project.patient;

import java.math.BigDecimal;
import java.util.List;
import com.deemsys.project.common.IGenericDAO;
import com.deemsys.project.entity.Patient;

/**
 * 
 * @author Deemsys
 * 
 */
public interface PatientDAO extends IGenericDAO<Patient> {

	public List<Patient> getPatientListByCallerId(Integer staffId);

	public List<Patient> getPatientByStatus(Integer patientStatus);

	public List<Patient> patientFileRead(String fileName);

	public void releasePatientFromCaller(Integer id);

	public List<Patient> getpatientByDoctorId(Integer doctorId);

	public List<Patient> getpatientByClinicId(Integer clinicId);

	public void removeAssignedDoctor(Integer patientId);

	public void removeAssignedClinic(Integer patientId);

	public Integer activeStatusByPatientId(Integer id);

	public void updatePatientStatus(Integer patientId);
	
	public List<Patient> getPatientListByLimit(Integer pageNumber,
			Integer itemsPerPage,String name,String phoneNumber,String localReportNumber,String callerName);
	
	public Integer getTotalPatientCount(String localReportNumber, Integer county,
			String crashDate, String toDate, String recordedFromDate,
			String recordedToDate, String name);
	
	public List<Patient> searchPatients(Integer pageNumber, Integer itemsPerPage,String localReportNumber,Integer county, 
			String crashDate,String toDate,String recordedFromDate,String recordedToDate, String name);
	
	void savePatient(Patient entity) throws Exception;
	
	public Patient getPatientByPatientId(String patientId);
	
	public Patient getPatientByPatientIdAndCallerAdminId(String patientId,Integer callerAdminId);
	
	public void deletePatientByPatientId(String patientId);
	
	public List<Patient> getSixMonthPatientsList();
	
	public Patient checkPatientForRunnerReport(Integer countyId,String crashDate,String patientName);
	
	public List<Patient> getRunnerReportPatients(String crashId,Integer isRunnerReport);
	
	public List<Patient> getPatientsListByAddedOnDates(String fromDate,String toDate);
	
	public List<Patient> getPatientListForUpdateLatLong(String fromDate,String toDate,Integer noOfRecords);
	
	public void updateLatLongByAddress(BigDecimal latitude,BigDecimal longitude,String address);
	
	public Integer checkVehicleMakeMappedToPatients(String make);

	public void updatePatientTypeOfUse(String crashId, Integer unitNumber, Integer typeOfUse);
}
