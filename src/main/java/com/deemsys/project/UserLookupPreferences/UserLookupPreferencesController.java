
package com.deemsys.project.UserLookupPreferences;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author Deemsys
 *
 */
@Controller
public class UserLookupPreferencesController {
	
	@Autowired
	UserLookupPreferencesService userLookupPreferencesService;

   
    @RequestMapping(value="/deleteUserLookupPreferences",method=RequestMethod.POST)
   	public String deleteUserLookupPreferences(@RequestParam("id") Integer id,ModelMap model)
   	{
    	
    	userLookupPreferencesService.deleteUserLookupPreferences(id);
    	model.addAttribute("requestSuccess",true);
   		return "/returnPage";
   	}
    
    @RequestMapping(value="/getAllUserLookupPreferencess",method=RequestMethod.GET)
   	public String getAllUserLookupPreferencess(ModelMap model)
   	{
    	model.addAttribute("userLookupPreferencesForms",userLookupPreferencesService.getUserLookupPreferencesList());
    	model.addAttribute("requestSuccess",true);
   		return "/returnPage";
   	}

}
