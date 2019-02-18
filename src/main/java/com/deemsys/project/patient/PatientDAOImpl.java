package com.deemsys.project.patient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.deemsys.project.common.BasicQuery;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.entity.Patient;

/**
 * 
 * @author Deemsys
 *
 */
@Repository
public class PatientDAOImpl implements PatientDAO{
	
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public void savePatient(Patient entity) throws Exception {		
		try{
			String UUIDString=UUID.randomUUID().toString();
			entity.setPatientId(UUIDString.replaceAll("-", ""));
			this.sessionFactory.getCurrentSession().save(entity);
		}catch(Exception exception){
			throw exception;
		}
	}

	@Override
	public void merge(Patient entity) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().merge(entity);
	}	
	
	@Override
	public Patient get(Integer id) {
		// TODO Auto-generated method stub
		return (Patient) this.sessionFactory.getCurrentSession().get(Patient.class, id);
	}

	@Override
	public Patient update(Patient entity) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().merge(entity);
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().delete(this.get(id));
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> getAll() {
		// TODO Auto-generated method stub
		return this.sessionFactory.getCurrentSession().createCriteria(Patient.class).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> find(String paramName, String paramValue) {
		// TODO Auto-generated method stub
		return this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.eq(paramName, paramValue)).list();
	}

	@Override
	public List<Patient> find(String paramName, Long paramValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> find(String paramName, Integer paramValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> find(BasicQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> find(String queryString, String[] paramNames,
			String[] paramValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> find(String ParamName, Date date1, Date date2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> find(String ParamName, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean disable(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enable(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkName(Integer id, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Patient> getActiveList() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Patient> getPatientListByCallerId(Integer callerId) {
		// TODO Auto-generated method stub
		List<Patient> patient = new ArrayList<Patient>();
		
		patient= this.sessionFactory.getCurrentSession().createQuery("FROM Patient where caller.id="+callerId).list();
		return patient;
	}	
	

	@Override
	public List<Patient> patientFileRead(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releasePatientFromCaller(Integer id) {
		// TODO Auto-generated method stub
		Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set caller.id = NULL where id="+id);
		query.executeUpdate();
	}


	@Override
	public List<Patient> getpatientByDoctorId(Integer doctorId) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		List<Patient> patients=this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.eq("doctors.id", doctorId)).list();
		return patients;
	}

	@Override
	public List<Patient> getpatientByClinicId(Integer clinicId) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		List<Patient> patients=this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.eq("clinics.clinicId", clinicId)).list();
		return patients;
	}
	
	@Override
	public void removeAssignedDoctor(Integer patientId) {
		// TODO Auto-generated method stub
		Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set doctors.id=NULL where id="+patientId);
		query.executeUpdate();
	}
	
@Override
	public void updatePatientStatus(Integer patientId) {
		// TODO Auto-generated method stub
		Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set patientStatus='2' where id='"+patientId+"'");
		query.executeUpdate();
	
	}

@Override
public List<Patient> getPatientByStatus(Integer patientStatus) {
	// TODO Auto-generated method stub
	@SuppressWarnings("unchecked")
	List<Patient> patient=(List<Patient>) this.sessionFactory.getCurrentSession().createQuery("FROM  Patient WHERE patientStatus='"+patientStatus+"'").list();
	return patient;
}

@Override
public Integer activeStatusByPatientId(Integer id) {
	// TODO Auto-generated method stub
	Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set patientStatus = 1 where id="+id);
	query.executeUpdate();
	return 0;
}

@Override
public void removeAssignedClinic(Integer patientId) {
	// TODO Auto-generated method stub
	Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set clinics.clinicId=NULL where id="+patientId);
	query.executeUpdate();
}

@SuppressWarnings("unchecked")
@Override
public List<Patient> getPatientListByLimit(Integer pageNumber,
		Integer itemsPerPage,String name,String phoneNumber,String localReportNumber,String callerName) {
	// TODO Auto-generated method stub
	List<Patient> patient=new ArrayList<Patient>();
	if(name.equals("")&&phoneNumber.equals("")&&localReportNumber.equals("")&&callerName.equals("")){
	  patient=this.sessionFactory.getCurrentSession().createCriteria(Patient.class).setFirstResult((pageNumber-1)*itemsPerPage).setMaxResults(itemsPerPage).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	else if(callerName.equals("")){
		Criterion criterion=Restrictions.and(Restrictions.like("name", name, MatchMode.ANYWHERE), Restrictions.like("phoneNumber", phoneNumber, MatchMode.ANYWHERE));
		Criterion criterion2=Restrictions.and(criterion, Restrictions.like("localReportNumber", localReportNumber, MatchMode.ANYWHERE));
		patient=this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(criterion2).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	else if(!callerName.equals("")){
		patient=this.sessionFactory.getCurrentSession().createQuery("from Caller s1 join s1.patientes p1 where (s1.firstName like '%"+callerName+"%' or s1.lastName like '%"+callerName+"%') and p1.name like '%"+name+"%' and p1.phoneNumber like '%"+phoneNumber+"%' and p1.localReportNumber like '%"+localReportNumber+"%'").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	return patient;
}

@SuppressWarnings("unchecked")
@Override
public Integer getTotalPatientCount(String localReportNumber, Integer county,
		String crashDate,String crashToDate, String recordedFromDate,
		String recordedToDate, String name) {
	// TODO Auto-generated method stub
	Criteria cr=this.sessionFactory.getCurrentSession().createCriteria(Patient.class);
	
	Criterion countyCriterion=Restrictions.eq("county.countyId", county);
	cr.add(countyCriterion);
	
	Criterion nameCriterion=Restrictions.like("name", name,MatchMode.ANYWHERE);
	cr.add(nameCriterion);
	
	Criterion localReportNumberCriterion=Restrictions.like("localReportNumber",localReportNumber,MatchMode.START);
	cr.add(localReportNumberCriterion);
	
	if(recordedFromDate!=""&&recordedToDate!=""){
		Criterion addedDateCriterion=Restrictions.between("addedDate", recordedFromDate, recordedToDate);
		cr.add(addedDateCriterion);
	}
	
	if(crashDate!=""&&crashToDate!=""){
		Criterion crashDateCriterion=Restrictions.between("crashDate", crashDate, crashToDate);
		cr.add(crashDateCriterion);
	}
	List<Patient> patients=new ArrayList<Patient>();
	patients=cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	return patients.size();
}

@SuppressWarnings("unchecked")
@Override
public List<Patient> searchPatients(Integer pageNumber, Integer itemsPerPage,String localReportNumber, Integer county,
		String crashDate, String crashToDate, String recordedFromDate,
		String recordedToDate, String name) {
		// TODO Auto-generated method stub
		//(county_id=60 and name like '%%' )and (local_report_number like '%%'  and (added_date between '03/02/2016' and '03/21/2016')) and (crash_date between '03/06/2016' and '03/22/2016');
		Criteria cr=this.sessionFactory.getCurrentSession().createCriteria(Patient.class);
	
		Criterion countyCriterion=Restrictions.eq("county.countyId", county);
		cr.add(countyCriterion);
		
		Criterion nameCriterion=Restrictions.like("name", name,MatchMode.ANYWHERE);
		cr.add(nameCriterion);
		
		Criterion localReportNumberCriterion=Restrictions.like("localReportNumber",localReportNumber,MatchMode.START);
		cr.add(localReportNumberCriterion);
		
		if(recordedFromDate!=""&&recordedToDate!=""){
			Criterion addedDateCriterion=Restrictions.between("addedDate", recordedFromDate, recordedToDate);
			cr.add(addedDateCriterion);
		}
		
		if(crashDate!=""&&crashToDate!=""){
			Criterion crashDateCriterion=Restrictions.between("crashDate", crashDate, crashToDate);
			cr.add(crashDateCriterion);
		}
		
		
		List<Patient> patient=new ArrayList<Patient>();			
		patient=cr.setFirstResult((pageNumber-1)*itemsPerPage).setMaxResults(itemsPerPage).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return patient;
}



@Override
public void save(Patient entity) {
	// TODO Auto-generated method stub
	
}

@Override
public Patient getPatientByPatientId(String patientId) {
	// TODO Auto-generated method stub
	Patient patient=(Patient) this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.eq("patientId", patientId)).uniqueResult();
	return patient;
}

@Override
public Patient getPatientByPatientIdAndCallerAdminId(String patientId,Integer callerAdminId) {
	// TODO Auto-generated method stub
	Criteria criteria=this.sessionFactory.getCurrentSession().createCriteria(Patient.class);
	criteria.createAlias("patientCallerAdminMaps", "p1");
	criteria.add(Restrictions.and(Restrictions.eq("p1.callerAdmin.callerAdminId", callerAdminId),Restrictions.eq("p1.patient.patientId", patientId)));
	
	Patient patient=(Patient) criteria.uniqueResult();
	return patient;
}

@Override
public void deletePatientByPatientId(String patientId) {
	// TODO Auto-generated method stub
	Patient patient=this.getPatientByPatientId(patientId);
	if(patient!=null){
		this.sessionFactory.getCurrentSession().delete(patient);
	}
}

@Override
public List<Patient> getSixMonthPatientsList() {
	// TODO Auto-generated method stub
	LocalDate localDate1=new LocalDate().minusMonths(6);
	String date=localDate1.toString("yyyy-MM-dd");
	System.out.println("Previous 6 Month Date 1......"+date);
	List<Patient> patients=this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.le("addedDate", date)).addOrder(Order.desc("addedDate")).addOrder(Order.desc("patientId")).list();
	return patients;
}

@Override
public Patient checkPatientForRunnerReport(Integer countyId, String crashDate,
		String patientName) {
	// TODO Auto-generated method stub
	Criterion countyAndCrashDateCriterion=Restrictions.and(Restrictions.eq("county.countyId", countyId), Restrictions.eq("crashDate", InjuryConstants.convertYearFormat(crashDate)));
	return (Patient) this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.and(countyAndCrashDateCriterion, Restrictions.eq("name", patientName))).uniqueResult();
}

@SuppressWarnings("unchecked")
@Override
public List<Patient> getRunnerReportPatients(String crashId,
		Integer isRunnerReport) {
	// TODO Auto-generated method stub
	List<Patient> patients = this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.and(Restrictions.eq("crashReport.crashId", crashId),Restrictions.eq("isRunnerReport", isRunnerReport))).list();
	return patients;
}

public List<String[]> manupulateReportingAgency(String[] reportingAgency){
	
	List<String> reportingAgencyIds=new ArrayList<String>(Arrays.asList(reportingAgency));
	
	String[] reportingAgencyArray=new String[reportingAgencyIds.size()];
	String[] countyIdArray=new String[reportingAgencyIds.size()];
	int reportingAgencyIndex=0;
	int countyIndex=0;
	for (String reportingAgencyCode : reportingAgencyIds) {
		String[] splittedCode=reportingAgencyCode.split("-");
		reportingAgencyArray[reportingAgencyIndex++]=splittedCode[0];
		countyIdArray[countyIndex++]=splittedCode[1];
		
	}
	
	List<String[]> splittedResults=new ArrayList<String[]>();
	splittedResults.add(reportingAgencyArray);
	splittedResults.add(countyIdArray);
	return splittedResults;
}

@SuppressWarnings("unchecked")
@Override
public List<Patient> getPatientsListByAddedOnDates(String fromDate,
		String toDate) {
	// TODO Auto-generated method stub
	Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Patient.class);
	criteria.add(Restrictions.between("addedDate", InjuryConstants.convertYearFormat(fromDate), InjuryConstants.convertYearFormat(toDate)));
	criteria.add(Restrictions.isNull("reportingAgencyNcic"));
	criteria.add(Restrictions.or(Restrictions.or(Restrictions.eq("isRunnerReport", 0), Restrictions.eq("isRunnerReport", 2)), Restrictions.eq("isRunnerReport", 4)));
	
	return criteria.list();
}

@SuppressWarnings("unchecked")
@Override
public List<Patient> getPatientListForUpdateLatLong(String fromDate,
		String toDate,Integer noOfRecords) {
	// TODO Auto-generated method stub
	Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Patient.class);
	criteria.add(Restrictions.between("addedDate", InjuryConstants.convertYearFormat(fromDate), InjuryConstants.convertYearFormat(toDate)));
	criteria.add(Restrictions.and(Restrictions.eq("latitude", BigDecimal.valueOf(0.00000000000)), Restrictions.eq("longitude", BigDecimal.valueOf(0.00000000000))));
	criteria.add(Restrictions.isNotNull("address"));
	criteria.add(Restrictions.eq("isOwner", 0));
	return criteria.setFirstResult(0).setMaxResults(noOfRecords).list();
}

@Override
public void updateLatLongByAddress(BigDecimal latitude, BigDecimal longitude,
		String address) {
	// TODO Auto-generated method stub
	this.sessionFactory.getCurrentSession().createQuery("Update Patient set latitude='"+latitude+"',longitude='"+longitude+"' where address='"+address+"'").executeUpdate();
}

@Override
public Integer checkVehicleMakeMappedToPatients(String make) {
	
	List<Patient> patients=new ArrayList<Patient>();
	patients= this.sessionFactory.getCurrentSession().createCriteria(Patient.class).add(Restrictions.eq("vehicleMakeAbbreviation.make", make)).list();
	
	if(patients.isEmpty())
	{
		return 0;
	}
	else
	{
		return 1;
	}
}


@Override
public void updatePatientTypeOfUse(String crashId, Integer unitNumber, Integer typeOfUse) {
	// TODO Auto-generated method stub
	Query query=this.sessionFactory.getCurrentSession().createQuery("update Patient set typeOfUse='"+typeOfUse+"' where crashReport.crashId='"+crashId+"' and unitNumber='"+unitNumber+"'");
	query.executeUpdate();

}

}
