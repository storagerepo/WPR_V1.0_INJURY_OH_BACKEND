package com.deemsys.project.UserLookupPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deemsys.project.County.CountyList;
import com.deemsys.project.County.CountyService;
import com.deemsys.project.ReportingAgency.ReportingAgencyDAO;
import com.deemsys.project.Users.UsersDAO;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.entity.ReportingAgency;
import com.deemsys.project.entity.UserLookupPreferences;
import com.deemsys.project.entity.UserLookupPreferencesId;
import com.deemsys.project.entity.Users;
/**
 * 
 * @author Deemsys
 *
 * UserLookupPreferences 	 - Entity
 * userLookupPreferences 	 - Entity Object
 * userLookupPreferencess 	 - Entity List
 * userLookupPreferencesDAO   - Entity DAO
 * userLookupPreferencesForms - EntityForm List
 * UserLookupPreferencesForm  - EntityForm
 *
 */
@Service
@Transactional
public class UserLookupPreferencesService {

	@Autowired
	UserLookupPreferencesDAO userLookupPreferencesDAO;
	
	@Autowired
	UsersDAO usersDAO;
	
	@Autowired
	CountyService countyService;
	
	@Autowired
	ReportingAgencyDAO reportingAgencyDAO;
	
	//Get All Entries
	public List<UserLookupPreferencesForm> getUserLookupPreferencesList()
	{
		List<UserLookupPreferencesForm> userLookupPreferencesForms=new ArrayList<UserLookupPreferencesForm>();
		return userLookupPreferencesForms;
	}
	
	
	// Update Reporting Agency Preference While Subscribe county
	public int saveAndUpdateReportingAgencyUserLookupPreferencesByCounty(Integer userId,Integer countyId)
	{
		//TODO: Convert Form to Entity Here
		
		//Logic Starts
		
		Users users = usersDAO.get(userId);
		// Delete Previous Mapping
		this.deleteReportingAgencyPreferences(userId, countyId);
		
		// Get All Reporting Agency By countyId // Parameter is integer array
		Integer[] countyIds=new Integer[]{1};
		countyIds[0]=countyId;
		List<ReportingAgency> reportingAgencies = reportingAgencyDAO.getReportingAgencyListByCounties(countyIds);
		
		for (ReportingAgency reportingAgency : reportingAgencies) {
			UserLookupPreferencesId userLookupPreferencesId = new UserLookupPreferencesId(userId, InjuryConstants.REPORTING_AGENCY_LOOKUP, countyId, reportingAgency.getReportingAgencyId(), 1);
			UserLookupPreferences userLookupPreferences=new UserLookupPreferences(userLookupPreferencesId,users);
			userLookupPreferencesDAO.merge(userLookupPreferences);
		}	
		
		//Logic Ends
		
		return 1;
	}
		
	// Save Single Reporting Agency
	public int saveReportingAgencyPreference(Integer userId, UserLookupPreferenceMappedForm userLookupPreferenceMappedForm){
		
		Users users = usersDAO.get(userId);
		
		for (Integer mappedId : userLookupPreferenceMappedForm.getPreferredId()) {
			UserLookupPreferencesId userLookupPreferencesId = new UserLookupPreferencesId(userId, userLookupPreferenceMappedForm.getType(), userLookupPreferenceMappedForm.getCountyId(), mappedId, 1);
			UserLookupPreferences userLookupPreferences=new UserLookupPreferences(userLookupPreferencesId,users);
			userLookupPreferencesDAO.merge(userLookupPreferences);
		}	
		
		//Logic Ends
		
		return 1;
	}
	
	//Delete an Entry
	public int deleteUserLookupPreferences(Integer userId)
	{
		userLookupPreferencesDAO.deleteUserLookupPreferencesByUserId(userId);
		return 1;
	}
	
	public int deleteUserLookupPreferencesByType(Integer userId,Integer type)
	{
		userLookupPreferencesDAO.deleteUserLookupPreferencesByUserIdAndType(userId, type);
		return 1;
	}
	
	public int deleteReportingAgencyPreferences(Integer userId,Integer countyId)
	{
		userLookupPreferencesDAO.deleteReportingAgencyPreferences(userId,countyId);
		return 1;
	}

	public int deleteReportingAgencyPreferencesNotInCountyList(Integer userId,List<Integer> countyIds)
	{
		userLookupPreferencesDAO.deleteUserLookupPreferencesNotInCountyList(userId, countyIds);
		return 1;
	}

}
