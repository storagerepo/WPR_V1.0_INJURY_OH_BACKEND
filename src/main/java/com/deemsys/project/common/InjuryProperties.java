package com.deemsys.project.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InjuryProperties {


	//Default Get Property
	public String getProperty(String propertyName){
		String propertyValue="";
		try {
			Properties prop = new Properties();
			InputStream input=null;	
			input=this.getClass().getResourceAsStream("/application.properties");
			prop.load(input);
			propertyValue=prop.getProperty(propertyName);
			//  tooclose Input Stream Propeties file, 
            input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return propertyValue;
	}
	
	// Get Parser One Property
	public String getParserOneProperty(String propertyName){
		String propertyValue="";
		try {
			Properties prop = new Properties();
			InputStream input=null;	
			input=this.getClass().getResourceAsStream("/typeoneparser.properties");
			prop.load(input);
			propertyValue=prop.getProperty(propertyName);
			//  tooclose Input Stream Propeties file, 
            input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return propertyValue;
	}
	
	// Get Parser One Property
		public Map<String, String> getParserOneAllProperty(){
			Enumeration<?> properties = null;
			Map<String, String> keyValuePairs=new HashMap<>();
			try {
				Properties prop = new Properties();
				InputStream input=null;	
				input=this.getClass().getResourceAsStream("/typeoneparser.properties");
				prop.load(input);
				properties= prop.propertyNames();
				String property="";
				while(properties.hasMoreElements()){
					property=properties.nextElement().toString();
					keyValuePairs.put(property, prop.getProperty(property));
				}
					
				
				input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.out.println(keyValuePairs);
			return keyValuePairs;
		}
	
}
