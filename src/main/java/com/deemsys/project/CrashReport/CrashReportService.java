package com.deemsys.project.CrashReport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deemsys.project.County.CountyDAO;
import com.deemsys.project.CrashReportError.CrashReportErrorDAO;
import com.deemsys.project.PoliceAgency.PoliceAgencyDAO;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.common.InjuryProperties;
import com.deemsys.project.entity.County;
import com.deemsys.project.entity.CrashReport;
import com.deemsys.project.entity.CrashReportError;
import com.deemsys.project.entity.PoliceAgency;
import com.deemsys.project.patient.PatientForm;
import com.deemsys.project.pdfparser.PDFParserData;

/**
 * 
 * @author Deemsys
 *
 * CrashReport 	 - Entity
 * crashReport 	 - Entity Object
 * crashReports 	 - Entity List
 * crashReportDAO   - Entity DAO
 * crashReportForms - EntityForm List
 * CrashReportForm  - EntityForm
 *
 */
@Service
@Transactional
public class CrashReportService {

	@Autowired
	CrashReportDAO crashReportDAO;
	
	@Autowired
	CrashReportErrorDAO crashReportErrorDAO;
	
	@Autowired
	CountyDAO countyDAO;
	
	@Autowired
	InjuryProperties injuryProperties;
	
	@Autowired
	PoliceAgencyDAO policeAgencyDAO;
	
	//Get All Entries
	public List<CrashReportForm> getCrashReportList()
	{
		List<CrashReportForm> crashReportForms=new ArrayList<CrashReportForm>();
		return crashReportForms;
	}
	
	//Get Particular Entry
	public CrashReportForm getCrashReport(Integer getId)
	{
		//TODO: Convert Entity to Form
		//Start
		CrashReportForm crashReportForm=new CrashReportForm();
		//End
		return crashReportForm;
	}
	
	//Merge an Entry (Save or Update)
	public int mergeCrashReport(CrashReportForm crashReportForm)
	{
		//TODO: Convert Form to Entity Here
		//Logic Starts
		CrashReport crashReport=new CrashReport();
		//Logic Ends
		crashReportDAO.merge(crashReport);
		return 1;
	}
	
	//Save an Entry
	public int saveCrashReport(CrashReportForm crashReportForm)
	{
		//TODO: Convert Form to Entity Here	
		//Logic Starts
		County county= countyDAO.get(Integer.parseInt(crashReportForm.getCounty()));
		CrashReportError crashReportError=crashReportErrorDAO.get(Integer.parseInt(crashReportForm.getCrashReportError()));
		String localReportNumber=crashReportForm.getLocalReportNumber();
		if(crashReportDAO.getCrashReportCountByLocalReportNumber(localReportNumber)>0){
			Long localReportNumberCount=crashReportDAO.getLocalReportNumberCount(localReportNumber+"(");
			localReportNumber=localReportNumber+"("+(localReportNumberCount+1)+")";
			
		}
		// Police Agency
		PoliceAgency policeAgency = policeAgencyDAO.get(crashReportForm.getReportFrom());
		
		CrashReport crashReport=new CrashReport(crashReportForm.getCrashId(), crashReportError, policeAgency, county, localReportNumber,  InjuryConstants.convertYearFormat(crashReportForm.getCrashDate()), 
					 InjuryConstants.convertYearFormat(crashReportForm.getAddedDate()), crashReportForm.getNumberOfPatients(), crashReportForm.getVehicleCount(), crashReportForm.getFilePath(), null, crashReportForm.getIsRunnerReport(),  null, 1, 
					 crashReportForm.getAddedDateTime(), crashReportForm.getRunnerReportAddedDateTime(), localReportNumber.replaceAll(" ", ""),null, null, null);
		
		//Logic Ends
		crashReportDAO.save(crashReport);
		return 1;
	}
	
	//Update an Entry
	public int updateCrashReport(CrashReportForm crashReportForm)
	{
		//TODO: Convert Form to Entity Here	
		//Logic Starts
		CrashReport crashReport=new CrashReport();
		//Logic Ends
		crashReportDAO.update(crashReport);
		return 1;
	}
	
	//Delete an Entry
	public int deleteCrashReport(Integer id)
	{
		crashReportDAO.delete(id);
		return 1;
	}
	
	// Get Crash Report Form Details from Patient Form
	public CrashReportForm getCrashReportFormDetails(PatientForm patientForm,Integer crashId,String filePath,Integer crashReportErrorId){
		Integer reportFrom=0;
		CrashReportForm crashReportForm=new CrashReportForm(crashReportErrorId.toString(), patientForm.getLocalReportNumber(), crashId.toString(), patientForm.getCrashDate(), patientForm.getCounty(),
				InjuryConstants.convertMonthFormat(new Date()), filePath, 0, 0 , 0, InjuryConstants.convertMonthFormat(new Date()),1,reportFrom, null, null);
		return crashReportForm;
	}
	
	// Get Crash Report Form Details from PdfJson Form
	public CrashReportForm getCrashReportFormDetails(PDFParserData parserData,String crashId,Integer reportFrom, String filePath,Integer crashReportErrorId,Integer numberOfPatients,Integer vehicleCount){
		Integer isRunnerReport=0;
		CrashReportForm crashReportForm=new CrashReportForm(crashReportErrorId.toString(), parserData.getLocalReportNumber(), crashId.toString(), parserData.getCrashDate(), parserData.getCounty(),
				InjuryConstants.convertMonthFormat(new Date()), filePath,numberOfPatients, vehicleCount, isRunnerReport, null, 1, reportFrom, new Date(), null);
		return crashReportForm;
	}
	
	public DirectReportGroupResult groupCrashReportListByArchive(CrashReportList crashReportList,Integer isArchived){
		List<DirectReportGroupListByArchive> directReportGroupListByArchives = new ArrayList<DirectReportGroupListByArchive>();
		if(isArchived==1){
			Integer rowCount=0;
			String archivedDate="";
			DirectReportGroupListByArchive directReportGroupListByArchive = new DirectReportGroupListByArchive();
			for (CrashReportForm crashReportForm : crashReportList.getCrashReportForms()) {
				if(!archivedDate.equals(crashReportForm.getArchivedDate())){
					archivedDate=crashReportForm.getArchivedDate();
					if(rowCount!=0){
						directReportGroupListByArchives.add(directReportGroupListByArchive);
					}
					directReportGroupListByArchive = new DirectReportGroupListByArchive(archivedDate, new ArrayList<CrashReportForm>());
				}
				// Set Crash Report
				directReportGroupListByArchive.getCrashReportForms().add(crashReportForm);
				rowCount++;
			}
			if(rowCount>0)
				directReportGroupListByArchives.add(directReportGroupListByArchive);
		}else{
			DirectReportGroupListByArchive directReportGroupListByArchive = new DirectReportGroupListByArchive("" , crashReportList.getCrashReportForms());
			directReportGroupListByArchives.add(directReportGroupListByArchive);
		}
		
		
		DirectReportGroupResult directReportGroupResult = new DirectReportGroupResult(crashReportList.getTotalNoOfRecords(), directReportGroupListByArchives);
		
		return directReportGroupResult;
	}
	
	// Split the PDF County and get Exact county Name
	public String splitCountyName(String countyName){
		String[] countySplit=countyName.split("\\s+");
		String splittedcountyName="";
		if(countySplit.length==2){
			splittedcountyName=countyName.split("\\s+")[0];
		}else if(countySplit.length==3){
			splittedcountyName=countyName.split("\\s+")[0]+" "+countyName.split("\\s+")[1];
		}
		return splittedcountyName;
	}
	
	// Save Runner Crash Report 
	/*public int saveRunnerCrashReport(RunnerCrashReportForm runnerCrashReportForm)
	{
		//TODO: Convert Form to Entity Here	
		
		//Logic Starts
		// 15 - Runner Crash Reports
		Integer crashReportErrorId=15;
		Integer isRunnerReport=1;
		Integer vehicleCount=0;
		Integer numberOfPatients=runnerCrashReportForm.getPatientForms().size();
		County county= countyDAO.get(Integer.parseInt(runnerCrashReportForm.getCounty()));
		CrashReportError crashReportError=crashReportErrorDAO.get(crashReportErrorId);
		String localReportNumber=runnerCrashReportForm.getLocalReportNumber();
		if(crashReportDAO.getCrashReportCountByLocalReportNumber(localReportNumber)>0){
			Long localReportNumberCount=crashReportDAO.getLocalReportNumberCount(localReportNumber+"(");
			localReportNumber=localReportNumber+"("+(localReportNumberCount+1)+")";
		}
		
		String crashId=localReportNumber;
		if(runnerCrashReportForm.getReportFrom()==Integer.parseInt(injuryProperties.getProperty("reportFromDeemsys"))){
			crashId=injuryProperties.getProperty("deemsysCrashIdPrefix")+localReportNumber;
		}else{
			crashId=runnerCrashReportForm.getReportPrefixCode()+localReportNumber;
		}
		// Police Agency
		PoliceAgency policeAgency = policeAgencyDAO.get(runnerCrashReportForm.getReportFrom());
		// Update Last Updated Date
		policeAgency.setLastUpdatedDate(new Date());
		policeAgencyDAO.update(policeAgency);
		CrashReport crashReport=new CrashReport(crashId, crashReportError, policeAgency, county, localReportNumber,  InjuryConstants.convertYearFormat(runnerCrashReportForm.getCrashDate()), 
					 new Date(), numberOfPatients, vehicleCount, runnerCrashReportForm.getFilePath(), null, isRunnerReport, new Date(), 1, null, new Date(), null,null,null);
		
		
		CrashReport crashReportExist=this.checkRunnerReportWithODPSReport(runnerCrashReportForm);
		if(crashReportExist==null){
			crashReportDAO.save(crashReport);
		
			for (PatientForm patientForm : runnerCrashReportForm.getPatientForms()) {
				try {
					patientForm.setCrashId(crashId);
					patientForm.setAge(null);
					patientForm.setTier(null);
					patientForm.setCallerId(null);
					patientForm.setLatitude(null);
					patientForm.setLongitude(null);
					patientForm.setIsOwner(0);
					patientForm.setCountyId(Integer.parseInt(runnerCrashReportForm.getCounty()));
					patientService.savePatient(patientForm);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		//Logic Ends
		
		return 1;
	}*/
	
	public void updateCrashReport(String runnerReportCrashId, String crashId, String fileName, Integer crashReportErrorId, Integer isRunnerReport){
		Integer reportConverstionStatus=2;
		if(isRunnerReport==3){
			reportConverstionStatus=4;
		}
		crashReportDAO.updateCrashReportByQuery(runnerReportCrashId, crashId.toString(), crashReportErrorId, fileName, reportConverstionStatus);
	}
	
	//Check Runner Report With ODPS
	public CrashReport checkRunnerReportWithODPSReport(RunnerCrashReportForm runnerCrashReportForm){
		Long oldReportCount=crashReportDAO.getLocalReportNumberCount(runnerCrashReportForm.getLocalReportNumber());
		Integer countyId=Integer.parseInt(runnerCrashReportForm.getCounty());
		CrashReport crashReport=null;
		for (int i = 0; i <=oldReportCount; i++) {
			String localReportNumber=runnerCrashReportForm.getLocalReportNumber();
			if(i!=0){
				localReportNumber=localReportNumber+"("+i+")";
			}
			crashReport=crashReportDAO.getCrashReportForChecking(localReportNumber,runnerCrashReportForm.getCrashDate(), countyId,1);
			if(crashReport!=null&&!crashReport.equals("")){
				break;
			}
		}
		
		return crashReport;
	}
	
	public void backupSixMonthOldReportsDataByStoredProcedure(String date){
		crashReportDAO.backupSixMonthOldDataByStoredProcedure(date);
	}
	
	public List<CrashReport> getCrashReportByParams(CrashReportSearchForm crashReportSearchForm){
		return crashReportDAO.getCrashReport(crashReportSearchForm.getLocalReportNumber(), crashReportSearchForm.getAddedFromDate());				
	}
	
}
