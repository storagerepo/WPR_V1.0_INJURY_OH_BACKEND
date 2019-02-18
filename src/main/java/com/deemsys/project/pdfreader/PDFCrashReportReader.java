package com.deemsys.project.pdfreader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.deemsys.project.County.CountyDAO;
import com.deemsys.project.CrashReport.CrashReportDAO;
import com.deemsys.project.CrashReport.CrashReportService;
import com.deemsys.project.CrashReport.RunnerCrashReportForm;
import com.deemsys.project.CrashReportError.CrashReportErrorDAO;
import com.deemsys.project.PoliceAgency.PoliceAgencyDAO;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.common.InjuryProperties;
import com.deemsys.project.entity.County;
import com.deemsys.project.entity.CrashReport;
import com.deemsys.project.entity.CrashReportError;
import com.deemsys.project.entity.Patient;
import com.deemsys.project.entity.PoliceAgency;
import com.deemsys.project.patient.PatientDAO;
import com.deemsys.project.patient.PatientForm;
import com.deemsys.project.patient.PatientService;
import com.deemsys.project.pdfparser.AWSFileUpload;
import com.deemsys.project.pdfparser.PDFParserData;
import com.deemsys.project.pdfparser.PDFParserOne;
import com.deemsys.project.pdfparser.PDFParserUnitData;
import com.deemsys.project.pdfparser.PDFParserVehicleData;
import com.deemsys.project.pdfparser.PdfParserService;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

@Service
@Transactional
public class PDFCrashReportReader {
	
	@Autowired
	PdfParserService pdfParserService;
	
	@Autowired
	InjuryProperties injuryProperties;
	
	@Autowired
	CrashReportService crashReportService;
	
	@Autowired
	PatientService patientsService;
	
	@Autowired
	AWSFileUpload awsFileUpload;
	
	@Autowired
	CrashReportDAO crashReportDAO;
	
	@Autowired
	PatientDAO patientDAO;
	
	@Autowired
	CrashReportErrorDAO crashReportErrorDAO;
	
	@Autowired
	PoliceAgencyDAO policeAgencyDAO;
	
	@Autowired
	CountyDAO countyDAO;
	
	@Autowired
	PDFParserOne pdfParserOne;
	
	/**
	 * Parses a Manual PDF
	 * 
	 * From PDF
	 * 
	 * @param txt
	 *            the resulting text
	 * @throws IOException
	 */
	public List<List<String>> parsePdfFromMultipartFile(MultipartFile file)
			throws IOException {
		List<List<String>> contentList = new ArrayList<List<String>>();
		try {
			PdfReader reader = new PdfReader(file.getBytes());
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			String pdfText = "";
			TextExtractionStrategy strategy;
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				strategy = parser.processContent(i,
						new SimpleTextExtractionStrategy());
				pdfText = strategy.getResultantText();
				contentList.add(Arrays.asList(pdfText.split("\n")));
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}

		return contentList;
	}

	public File getPDFFile(String crashId,Integer reportType,String pdfLink) throws Exception{
		File file=null;
		HttpURLConnection httpURLConnection=null;
		try{
			String pdfAccessURL=injuryProperties.getProperty("ncLink")+crashId+injuryProperties.getProperty("pdfNameSuffix");
			if(reportType==2){
				String originURL=pdfLink.substring(0,pdfLink.lastIndexOf("/")+1);
				String fileName=pdfLink.substring(pdfLink.lastIndexOf("/")+1,pdfLink.length());
				fileName=fileName.replaceAll(" ", "%20");
				pdfAccessURL=originURL+fileName;
			}
	/*		// Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                }
	                public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	 
	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	           @Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}
	        };*/
	        
			URL url=new URL(pdfAccessURL);
			httpURLConnection=(HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(10000);
			//httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
			
			Integer tryCount=Integer.parseInt(injuryProperties.getProperty("reportAccessTry"));
			
			//Try access the same link twice to avoid loosing reports.
			for (int accessTry = 0; accessTry < tryCount; accessTry++) {
				if(httpURLConnection.getResponseCode()==200)
				{
					String filePath=injuryProperties
							.getProperty("tempFolder")
							+ "CrashReport_"
							+ crashId + ".pdf";
					try {
						
						InputStream in = httpURLConnection.getInputStream();	
						Files.copy(in, Paths.get(filePath),
								StandardCopyOption.REPLACE_EXISTING);
						
						in.close();
						file=new File(filePath);	
						if(file.length()>2000)
							break;
						else
							System.out.println("Lets hit one more time..");
						}
					catch(Exception ex){
						throw ex;
					}
					finally{
						if(httpURLConnection!=null)
						    httpURLConnection.disconnect();
					}
				}else if(httpURLConnection.getResponseCode()==404){
					file=null;
				}
			}			
		}catch(Exception ex){
			if(httpURLConnection!=null)
			    httpURLConnection.disconnect();
			
			throw ex;
		}
		
		return file;
	}
		
	public void parsePDFDocument(File file,String crashId, Integer reportFrom, PDFParserData pdfParserData) throws Exception{
		
		UUID randomUUID=UUID.randomUUID();
		String fileName="";
		String uuid="";
		
		if(Integer.parseInt(injuryProperties.getProperty("env"))==0){
			fileName="CrashReport_"+crashId+".pdf";
			uuid=crashId.toString();
		}else{
			fileName=randomUUID+"_"+ crashId + ".pdf";
			uuid=randomUUID.toString();
		}
		
		// Vehicle Details with Owner Information
		List<PatientForm> vehicleOwnerDetails = this.getVehicleOwnerDetails(pdfParserData);
		// Vehicle Count From Report First Page Number of Units
		Integer vehicleCount=0;
		if(pdfParserData.getNumberOfUnits()!=null&&!pdfParserData.getNumberOfUnits().equals(""))
			 vehicleCount=vehicleOwnerDetails.size();
		
		
		Integer patientsCount=0;
		List<PatientForm> patientsForms = new ArrayList<PatientForm>();
			List<PatientForm> filteredPatientsForms=new ArrayList<PatientForm>();
		//Check for report status
		Integer tierType = this.getReportType(pdfParserData);
		patientsForms=this.getTierPatientForm(pdfParserData, tierType, vehicleOwnerDetails);
		// Check Crash Report is Runner Report
		//CrashReport runnerReportCrashId =this.checkODPSReportWithRunnerReport(pdfParserData.getFirstPageForm());
		
	
		
	switch (tierType) {
		case 1:
			filteredPatientsForms=filterPatientForms(patientsForms);
			if(filteredPatientsForms.size()==0){
				patientsCount=0;
				patientsForms=this.getTierPatientForm(pdfParserData, 9, vehicleOwnerDetails);
				for (PatientForm patientForm : patientsForms) {
					if(patientForm.getIsOccupantAvailable()!=0){
						patientsCount++;
					}
					patientForm.setTier(0);
				}
				//#9 Tier 1 Error None of the Patient are not having address and the phone number
				crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 9,patientsCount,vehicleCount));
				/*if(runnerReportCrashId==null){
					
				}else{
					crashReportService.updateCrashReport(runnerReportCrashId.getCrashId(), crashId, fileName, 9, runnerReportCrashId.getIsRunnerReport());
				}*/
				
				this.savePatientList(patientsForms, crashId.toString(),null);
			}else{
				//Insert patients
				Integer patientAvailableTier=checkTierFourPatientsAvailable(filteredPatientsForms);
				if(patientAvailableTier==3){
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 13,filteredPatientsForms.size(),vehicleCount));
					
				}else if(patientAvailableTier==1){
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 1,filteredPatientsForms.size(),vehicleCount));
				}else if(patientAvailableTier==2){
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 14,filteredPatientsForms.size(),vehicleCount));
				}else{
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId,reportFrom, fileName, 17,filteredPatientsForms.size(),vehicleCount));
				}
				
				try {
					this.savePatientList(filteredPatientsForms, crashId.toString(),null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw e;
				}
			}
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		case 2:
			if(patientsForms.size()==0){
				//#7 Victim units not having insurance
				patientsForms=this.getTierPatientForm(pdfParserData, 7, vehicleOwnerDetails);
				patientsCount=0;
				for (PatientForm patientForm : patientsForms) {
					if(patientForm.getIsOccupantAvailable()!=0){
						patientsCount++;
					}
				}
				crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 7,patientsCount,vehicleCount));
				
				this.savePatientList(patientsForms, crashId.toString(),null);
			}else{
				filteredPatientsForms=filterPatientForms(patientsForms);
				if(filteredPatientsForms.size()==0){
					//#10 Tier2 No patient have address and phone number\
					patientsForms=this.getTierPatientForm(pdfParserData, 10, vehicleOwnerDetails);
					patientsCount=0;
					for (PatientForm patientForm : patientsForms) {
						if(patientForm.getIsOccupantAvailable()!=0){
							patientsCount++;
						}
						patientForm.setTier(0);
					}
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 10,patientsCount,vehicleCount));
					
					this.savePatientList(patientsForms, crashId.toString(),null);
				}else{
					Integer crashReportType=2;
					Integer count=0;
					for (PatientForm patientForm : filteredPatientsForms) {
						if(patientForm.getTier()==0){
							count++;
						}
					}
					if(filteredPatientsForms.size()==count){
						crashReportType=18;
					}
					//Insert patients
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, crashReportType,filteredPatientsForms.size(),vehicleCount));
					
					try {
						this.savePatientList(filteredPatientsForms, crashId.toString(),null);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw e;
					}
					}
			}
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		case 3:
			if(patientsForms.size()==0){
				//#8 Error None of the Patient satisfy injuries scale 2 to 4
				patientsForms=this.getTierPatientForm(pdfParserData, 8, vehicleOwnerDetails);
				patientsCount=0;
				for (PatientForm patientForm : patientsForms) {
					if(patientForm.getIsOccupantAvailable()!=0){
						patientsCount++;
					}
				}
				crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 8,patientsCount,vehicleCount));
				
				this.savePatientList(patientsForms, crashId.toString(),null);
			}else{
				filteredPatientsForms=filterPatientForms(patientsForms);
				if(filteredPatientsForms.size()==0){
					//#11 Tier3 Patient Not having address and phone numbers
					patientsForms=this.getTierPatientForm(pdfParserData, 11, vehicleOwnerDetails);
					patientsCount=0;
					for (PatientForm patientForm : patientsForms) {
						if(patientForm.getIsOccupantAvailable()!=0){
							patientsCount++;
						}
						patientForm.setTier(0);
					}
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 11,patientsCount,vehicleCount));
					
					this.savePatientList(patientsForms, crashId.toString(),null);
				}else{
					Integer crashReportType=3;
					Integer count=0;
					for (PatientForm patientForm : filteredPatientsForms) {
						if(patientForm.getTier()==0){
							count++;
						}
					}
					if(filteredPatientsForms.size()==count){
						crashReportType=19;
					}
					//Insert patients
					crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, crashReportType,filteredPatientsForms.size(),vehicleCount));
					try {
						this.savePatientList(filteredPatientsForms, crashId.toString(),null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw e;
					}
					
				}
			}
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		case 4:
			//Insert into crash report table number of units > 1 and unit in error is an animal
			patientsCount=0;
			for (PatientForm patientForm : patientsForms) {
				if(patientForm.getIsOccupantAvailable()!=0){
					patientsCount++;
				}
			}
			crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 4,patientsCount,vehicleCount));
			this.savePatientList(patientsForms, crashId.toString(),null);
			
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		case 5:
			//Insert into crash report table number of units == 1 and unit in error is an animal
			patientsCount=0;
			for (PatientForm patientForm : patientsForms) {
				if(patientForm.getIsOccupantAvailable()!=0){
					patientsCount++;
				}
			}
			crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 5,patientsCount,vehicleCount));
			this.savePatientList(patientsForms, crashId.toString(),null);
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		case 6:
			//Insert into crash report table number of units == 1 and unit in error not having insurance details
			patientsCount=0;
			for (PatientForm patientForm : patientsForms) {
				if(patientForm.getIsOccupantAvailable()!=0){
					patientsCount++;
				}
			}
			crashReportService.saveCrashReport(crashReportService.getCrashReportFormDetails(pdfParserData, crashId, reportFrom, fileName, 6,patientsCount,vehicleCount));
			this.savePatientList(patientsForms, crashId.toString(),null);
			// Save Vehicle and Owner Details
			this.savePatientList(vehicleOwnerDetails, crashId, null);
			break;
		default:
			break;
		}
		if(Integer.parseInt(injuryProperties.getProperty("awsUpload"))==1)				
			awsFileUpload.uploadFileToAWSS3(file.getAbsolutePath(), fileName);
		if(Integer.parseInt(injuryProperties.getProperty("env"))==1)
			file.delete();
}

	
	public Integer getReportType(PDFParserData pdfParserData) {

		// #1 Tire 1 Patient
		// #2 Tire 2 Patient
		// #3 Tire 3 Patient
		// #4 Number of units > 1 and Unit in Error is an animal
		// #5 Number of units == 1 and Unit in Error is an animal
		// #6 Number of units == 1 and Unit in Error not having Insurance
		
		// Check for the report
		try {
			Integer unitInError = -1;
			if(pdfParserData.getUnitInError()!=null&&!pdfParserData.getUnitInError().equals("")){
				unitInError = Integer.parseInt(pdfParserData.getUnitInError());
			}
			
			
			Integer numberOfUnits=Integer.parseInt(pdfParserData.getNumberOfUnits());
			
			//Check for unit > 1
			if(numberOfUnits==1){
				if(unitInError==1){					
					//Check for insurance company
					if(!pdfParserData.getAtFaultInsuranceCompany().equals("")){
						// #3 Tier Patient 
						return 3;
					}else{
						//Skip the report
						// #6 Number of units == 1 and Unit in Error not having Insurance
						return 6;
					}
					
				}else{
					//Skip the Report
					// #5 Number of units == 1 and Unit in Error is an animal
					return 5;
				}
			}else{
				if(unitInError!=98&&unitInError!=99&&unitInError!=-1){
					//Check for unit has insurance
					//NOTE POLICY NUMBER IS NOT CHECKING
					if(!pdfParserData.getAtFaultInsuranceCompany().equals("")){
						//#1 Tier 1 patients
						return 1;
					}else{
						//Check victim have insurance
						//#2 Tier 2 patients
						return 2;
					}
					
				}else{
					//Skip the Report
					//# 4 Unit in Error is an animal
					return 4;
				}
			}	

		} catch (Exception ex) {
			System.out.println(ex.toString());
			return 0;
		}
	}
	
	public List<PatientForm> getTierPatientForm(
			PDFParserData pdfParserData,Integer tier, List<PatientForm> filteredVehicleDetails) {

		List<PatientForm> patientsForms = new ArrayList<PatientForm>();
		
		//Units Page
		List<PDFParserUnitData> reportUnitDetails=pdfParserData.getPdfParserUnitDatas();
		
		if(tier==1){
			if(reportUnitDetails.size()>0){
				for (PDFParserUnitData parserUnitData : reportUnitDetails) {
					if(parserUnitData.getUnitNumber()!=null&&!parserUnitData.getUnitNumber().equals("")){
						PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
						if(patientsForm!=null){
							if(patientsForm.getUnitNumber().equals(pdfParserData.getUnitInError())){
								/*
								 * Removed the Fatal Conditions
								 * if(patientsForm.getInjuries()!=null){
								if(!patientsForm.getInjuries().equals("1")){
									patientsForm.setTier(4);
									patientsForms.add(patientsForm);
									}
								}else{
									patientsForm.setTier(4);
									patientsForms.add(patientsForm);
								}*/
								patientsForm.setTier(4);
								patientsForms.add(patientsForm);
							}else{
								/*
								 *Removed the Fatal Conditions 
								 * if(patientsForm.getInjuries()!=null){
									if(!patientsForm.getInjuries().equals("1")){
											patientsForm.setTier(1);
											patientsForms.add(patientsForm);
									}
								}
								else{
											patientsForm.setTier(1);
											patientsForms.add(patientsForm);
								}*/
								patientsForm.setTier(1);
								patientsForms.add(patientsForm);
							}
						}
					}else{
						// Unit Number is Not Mentioned in Motorist Page improper details in Motorist page
						PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
						if(patientsForm!=null){
							patientsForm.setTier(0);
							patientsForms.add(patientsForm);
						}
					}
					
				}
			}else{
				PatientForm patientsForm=this.getPatientForm(pdfParserData,null, filteredVehicleDetails);
				if(patientsForm!=null){
					patientsForm.setTier(0);
					patientsForm.setIsOwner(0);
					// Occupants Or Motorist Details is Not Available
					patientsForm.setIsOccupantAvailable(0);
					patientsForms.add(patientsForm);
				}
			}
			
		}				
		else if(tier==2){
			for (PDFParserUnitData parserUnitData : reportUnitDetails) {
				if(!parserUnitData.getUnitNumber().equals(pdfParserData.getUnitInError())){
					if(parserUnitData.getInsuranceCompany()!=null&&!parserUnitData.getInsuranceCompany().equals("")){
						if(parserUnitData.getUnitNumber()!=null){
								PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
								if(patientsForm!=null){
									/*
									 *Removed the Fatal Conditions 
									 * if(patientsForm.getInjuries()!=null){
										if(!patientsForm.getInjuries().equals("1")){
											patientsForm.setTier(2);
											patientsForms.add(patientsForm);
										}
									}else{
											patientsForm.setTier(2);
											patientsForms.add(patientsForm);
									}*/
									patientsForm.setTier(2);
									patientsForms.add(patientsForm);
								}
						}else{
							// Unit Number is Not Mentioned in Motorist Page improper details in Motorist page
							PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
							if(patientsForm!=null){
								patientsForm.setTier(0);
								patientsForms.add(patientsForm);
							}
						}
					}else{
						//#7 Skip the unit
						// Insert into Crash report Table.
					}
				}
			}
					
		}else if(tier==3){
			for (PDFParserUnitData parserUnitData : reportUnitDetails) {
				if(parserUnitData.getUnitNumber()!=null){
					PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
					/*if(parserUnitData.getInjuries()!=null){
						if(!parserUnitData.getInjuries().equals("1")){
							PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
							if(patientsForm!=null){
								patientsForm.setTier(3);
								patientsForms.add(patientsForm);
							}
						}else{
							//#8 Skip the patient low injury
						}
					}else{
						PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
						if(patientsForm!=null){
							if(patientsForm.getInjuries()!=null){
								if(!patientsForm.getInjuries().equals("1")){							
								patientsForm.setTier(3);
								patientsForms.add(patientsForm);
								}
							}else{
								patientsForm.setTier(3);
								patientsForms.add(patientsForm);
							}
						}
					}*/
					if(patientsForm!=null){
						patientsForm.setTier(3);
						patientsForms.add(patientsForm);
					}
				}else{
					// Unit Number is Not Mentioned in Motorist Page improper details in Motorist page
					PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
					if(patientsForm!=null){
						patientsForm.setTier(0);
						patientsForms.add(patientsForm);
					}
				}
					
			}
		}else if(tier==4||tier==5||tier==6||tier==7||tier==8||tier==9||tier==10||tier==11){
			if(reportUnitDetails.size()>0){
				for (PDFParserUnitData parserUnitData : reportUnitDetails) {
					PatientForm patientsForm=this.getPatientForm(pdfParserData,parserUnitData, filteredVehicleDetails);
					if(patientsForm!=null){
						patientsForm.setTier(0);
						patientsForms.add(patientsForm);
					}
				}
			}else{
				PatientForm patientsForm=this.getPatientForm(pdfParserData,null, filteredVehicleDetails);
				if(patientsForm!=null){
					patientsForm.setTier(0);
					patientsForm.setIsOwner(0);
					patientsForm.setIsOccupantAvailable(0);
					patientsForms.add(patientsForm);
				}
			}
		}
		return patientsForms;
	}
	
	public PatientForm getPatientForm(PDFParserData pdfParserData, PDFParserUnitData pdfParserUnitData,List<PatientForm> filteredVehicleDetails){
		
		PatientForm patientsForm = null;
		if(pdfParserUnitData!=null){
			if(pdfParserUnitData.getDateOfBirth().equals("")&&pdfParserUnitData.getUnitNumber().equals("")&&pdfParserUnitData.getName().replaceAll(",", "").replaceAll(" ", "").equals("")&&pdfParserUnitData.getAddress().replaceAll(",", "").replaceAll(" ", "").equals("")
				&&pdfParserUnitData.getPhoneNumber()==null&&pdfParserUnitData.getPhoneNumber().equals("")&&pdfParserUnitData.getAge().equals("")&&pdfParserUnitData.getGender().equals("")&&pdfParserUnitData.getSeatingPosition().equals("")
				&&pdfParserUnitData.getInjuries().equals("")){
				// Empty Values
				System.out.println("All are empty values");
			}
			else if(pdfParserUnitData.getUnitNumber().equals("")&&pdfParserUnitData.getName().replaceAll(",", "").replaceAll(" ", "").equals("")&&((pdfParserUnitData.getAddress()!=null&& pdfParserUnitData.getAddress().replaceAll(",", "").replaceAll(" ", "").equals(""))||(pdfParserUnitData.getPhoneNumber()!=null&&pdfParserUnitData.getPhoneNumber().equals(""))))
			{
				//Basic details are empty
				System.out.println("All are empty values");
			}
			 else if(!pdfParserUnitData.getUnitNumber().equals("")&&pdfParserUnitData.getName().replaceAll(",", "").replaceAll(" ", "").equals("")&&((pdfParserUnitData.getAddress()!=null&& pdfParserUnitData.getAddress().replaceAll(",", "").replaceAll(" ", "").equals(""))||(pdfParserUnitData.getPhoneNumber()!=null&&pdfParserUnitData.getPhoneNumber().equals(""))))
			{
				//Basic details are empty
				System.out.println("All are empty values");
			}
			 else
			 {
				patientsForm=new PatientForm();
				patientsForm.setName(pdfParserUnitData.getName());
				
				patientsForm.setDateOfBirth(pdfParserUnitData.getDateOfBirth());
				
				if(this.getTotalYear(pdfParserUnitData.getDateOfBirth())!=null)
					patientsForm.setAge(this.getTotalYear(pdfParserUnitData.getDateOfBirth()));
				else
					patientsForm.setAge(null);
				
				patientsForm.setGender(pdfParserUnitData.getGender());
				patientsForm.setAddress(pdfParserUnitData
						.getAddress());
				patientsForm.setPhoneNumber(pdfParserUnitData.getPhoneNumber());
				patientsForm.setInjuries(pdfParserUnitData.getInjuries());
				patientsForm.setEmsAgency(pdfParserUnitData.getEMSAgency());
				patientsForm.setMedicalFacility(pdfParserUnitData.getMedicalFacility());
				patientsForm.setLocalReportNumber(pdfParserData
						.getLocalReportNumber());
				patientsForm.setCrashSeverity(pdfParserData.getCrashSeverity());
				patientsForm.setReportingAgencyNcic(pdfParserData.getReportingAgencyCode());
				patientsForm.setReportingAgencyName(pdfParserData
						.getReportingAgency());
				patientsForm.setCounty(pdfParserData.getCounty());
				patientsForm.setNumberOfUnits(pdfParserData.getNumberOfUnits());
				patientsForm.setUnitInError(pdfParserData.getUnitInError());
				patientsForm.setCityVillageTownship(pdfParserData.getCityVillageTownship());
				patientsForm.setCrashDate(pdfParserData.getCrashDate());
				patientsForm.setTimeOfCrash(pdfParserData.getTimeOfCrash());
				if(pdfParserUnitData.getUnitNumber()!=null&&!pdfParserUnitData.getUnitNumber().equals("")){
					patientsForm.setUnitNumber(pdfParserUnitData.getUnitNumber()
							.trim());
				}
				patientsForm.setVictimInsuranceCompany(pdfParserUnitData.getInsuranceCompany());
				patientsForm.setVictimPolicyNumber(pdfParserUnitData.getPolicyNumber());
				patientsForm.setAtFaultInsuranceCompany(pdfParserData.getAtFaultInsuranceCompany());
				patientsForm.setAtFaultPolicyNumber(pdfParserData.getAtFaultPolicyNumber());
				
				patientsForm.setSeatingPosition(pdfParserUnitData.getSeatingPosition());
				patientsForm.setPatientStatus(1);
				patientsForm.setIsRunnerReport(0);
				
				patientsForm.setIsOwner(0);
				patientsForm.setIsOccupantAvailable(1);
				
				//Vehicle Details
				if(pdfParserUnitData.getUnitNumber()!=null&&!pdfParserUnitData.getUnitNumber().equals("0")&&pdfParserUnitData.getUnitNumber().matches("[0-9]+")&&!pdfParserUnitData.getUnitNumber().equals("")&&Integer.parseInt(pdfParserUnitData.getUnitNumber())<=filteredVehicleDetails.size()){
					patientsForm.setTypeOfUse(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getTypeOfUse());
					patientsForm.setVehicleMake(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getVehicleMake());
					patientsForm.setVehicleYear(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getVehicleYear());
					patientsForm.setVin(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getVin());
					patientsForm.setLicensePlateNumber(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getLicensePlateNumber());
					//patientsForm.setEstimatedPropertyDamage(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getEstimatedPropertyDamage());
				    patientsForm.setDamageScale(filteredVehicleDetails.get(Integer.parseInt(pdfParserUnitData.getUnitNumber())-1).getDamageScale());
				}
			}
		}else{	
			patientsForm=new PatientForm();
			patientsForm.setLocalReportNumber(pdfParserData
				.getLocalReportNumber());
			patientsForm.setCrashSeverity(pdfParserData.getCrashSeverity());
			patientsForm.setReportingAgencyNcic(pdfParserData.getReportingAgencyCode());
			patientsForm.setReportingAgencyName(pdfParserData.getReportingAgency());
			patientsForm.setCounty(pdfParserData.getCounty());
			patientsForm.setNumberOfUnits(pdfParserData.getNumberOfUnits());
			patientsForm.setUnitInError(pdfParserData.getUnitInError());
			patientsForm.setCityVillageTownship(pdfParserData.getCityVillageTownship());
			patientsForm.setCrashDate(pdfParserData.getCrashDate());
			patientsForm.setTimeOfCrash(pdfParserData.getTimeOfCrash());
			patientsForm.setPatientStatus(1);
			patientsForm.setIsRunnerReport(0);
			patientsForm.setIsOwner(0);
			patientsForm.setIsOccupantAvailable(0);
		}
		
		return patientsForm;
	}
	
	public List<PatientForm> getVehicleOwnerDetails(PDFParserData pdfParserData){
		List<PatientForm> vehicleOwnerDetails=new ArrayList<PatientForm>();
		
		/*ReportFirstPageForm reportFirstPageForm = pdfCrashReportJson.getFirstPageForm();
		List<ReportUnitPageForm> reportUnitPageForms = pdfCrashReportJson.getReportUnitPageForms();
		List<ReportMotoristPageForm> reportMotoristPageForms = pdfCrashReportJson.getReportMotoristPageForms();
		String atFaultInsuranceCompany=null;
		String atFaultPolicyNumber=null;
		for (ReportUnitPageForm reportUnitPageForm : reportUnitPageForms) {
			if((reportFirstPageForm.getUnitInError()!=null&&!reportFirstPageForm.getUnitInError().equals(""))
					&&(reportUnitPageForm.getUnitNumber()!=null&&!reportUnitPageForm.getUnitNumber().equals(""))){
				if(Integer.parseInt(reportFirstPageForm.getUnitInError())==Integer.parseInt(reportUnitPageForm.getUnitNumber())){
					atFaultInsuranceCompany=reportUnitPageForm.getInsuranceCompany();
					atFaultPolicyNumber=reportUnitPageForm.getPolicyNumber();
				}
			}
		}*/
		
		for (PDFParserVehicleData parserVehicleData:pdfParserData.getPdfParserVehicleDatas()) {
			if(parserVehicleData.getUnitNumber().equals("")&&parserVehicleData.getOwnerName().equals("")&&parserVehicleData.getOwnerAddress().replaceAll(",", "").replaceAll(" ", "").equals("")&&parserVehicleData.getVehicleMake().equals("")
					&&parserVehicleData.getVehicleYear().equals("")&&parserVehicleData.getVehicleModel().equals("")&&parserVehicleData.getVehicleType().equals("")&&parserVehicleData.getPlateNumber().equals("")
					&&parserVehicleData.getVIN().equals("")){
				System.out.println("Empty Vehicle Value...");
			}else{
				PatientForm patientForm = new PatientForm();
				patientForm.setLocalReportNumber(pdfParserData.getLocalReportNumber());
				patientForm.setCrashSeverity(pdfParserData.getCrashSeverity());
				patientForm.setReportingAgencyNcic(pdfParserData.getReportingAgencyCode());
				patientForm.setReportingAgencyName(pdfParserData.getReportingAgency());
				patientForm.setCounty(pdfParserData.getCounty());
				patientForm.setNumberOfUnits(pdfParserData.getNumberOfUnits());
				patientForm.setUnitInError(pdfParserData.getUnitInError());
				patientForm.setCityVillageTownship(pdfParserData.getCityVillageTownship());
				patientForm.setCrashDate(pdfParserData.getCrashDate());
				patientForm.setTimeOfCrash(pdfParserData.getTimeOfCrash());
				
				patientForm.setUnitNumber(parserVehicleData.getUnitNumber());
				patientForm.setName(parserVehicleData.getOwnerName());
				if(parserVehicleData.getOwnerAddress().replaceAll(",", "").replaceAll(" ", "").equals("")){
					patientForm.setAddress("");
				}else{
					patientForm.setAddress(parserVehicleData.getOwnerAddress());
				}
				patientForm.setPhoneNumber(parserVehicleData.getPhoneNumber());
				patientForm.setVehicleMake(parserVehicleData.getVehicleMake());
				patientForm.setVehicleYear(parserVehicleData.getVehicleYear());
				patientForm.setVin(parserVehicleData.getVIN());
				patientForm.setLicensePlateNumber(parserVehicleData.getPlateNumber());
				//Change Made - Corrected
				if(parserVehicleData.getTypeOfUse()!=null&&!parserVehicleData.getTypeOfUse().equals("")){
					patientForm.setTypeOfUse(Integer.parseInt(parserVehicleData.getTypeOfUse()));
				}
				patientForm.setIsOwner(1);
				//patientForm.setEstimatedPropertyDamage(parserVehicleData.getEstimatedPropertyDamage());
				//patientForm.setDamageScale(this.getDamageScale(parserVehicleData.getEstimatedPropertyDamage()));
				if(parserVehicleData.getDamageScale()!=null&&!parserVehicleData.getDamageScale().equals("")){
					patientForm.setDamageScale(Integer.parseInt(parserVehicleData.getDamageScale()));
				}
				
				patientForm.setVictimInsuranceCompany(parserVehicleData.getInsuranceCompany());
				patientForm.setVictimPolicyNumber(parserVehicleData.getPolicyNumber());
				
				// At fault Policy and Insurance
				patientForm.setAtFaultInsuranceCompany(pdfParserData.getAtFaultInsuranceCompany());
				patientForm.setAtFaultPolicyNumber(pdfParserData.getAtFaultPolicyNumber());
				
				patientForm.setPatientStatus(1);
				patientForm.setIsRunnerReport(0);
				
				/*ReportMotoristPageForm motoristPageForm=this.compareUnitPageWithMotoristPage(reportUnitPageForm, reportMotoristPageForms);
				if(motoristPageForm!=null){
					if(motoristPageForm.getAge()!=null&&!motoristPageForm.equals(""))
						patientForm.setAge(Integer.parseInt(motoristPageForm.getAge()));
					
					patientForm.setGender(motoristPageForm.getGender());
					patientForm.setDateOfBirth(motoristPageForm.getDateOfBirth());
					
				}*/
				vehicleOwnerDetails.add(patientForm);
			}
		}
		return vehicleOwnerDetails;
	}
	
	public List<PatientForm> filterPatientForms(List<PatientForm> patientsForms){
		List<PatientForm> filteredPatientForms=new ArrayList<PatientForm>();
		filteredPatientForms.addAll(patientsForms);
		for (PatientForm patientsForm : patientsForms) {
			if(patientsForm.getUnitNumber()!=null){
				if ((patientsForm.getAddress()==null || patientsForm.getAddress().equals(""))
						&& (patientsForm.getPhoneNumber()==null || patientsForm.getPhoneNumber().equals(""))) {
					filteredPatientForms.remove(patientsForm);
				}
			}else{
				// If Unit Number is Null
				filteredPatientForms.remove(patientsForm);
			}
		}
		return filteredPatientForms;
	}
	
	public Integer checkTierFourPatientsAvailable(List<PatientForm> patientForms){
		
		// # 0 No Patients available Under Tier 1 and Tier 4
		// # 1 Only Tier 1 Patients available
		// # 2 Only Tier 4 Patients available
		// # 3 Both Tier 1 and Tier 4 Patients available
		
		Integer isAvailable=0;
		Integer tierFour=0;
		Integer tierOne=0;
		for (PatientForm patientsForm : patientForms) {
			if(patientsForm.getTier()==4){
				tierFour++;
			}else if(patientsForm.getTier()==1){
				tierOne++;
			}
		}
		
		if(tierOne>0&&tierFour>0){
			isAvailable=3;
		}else if(tierOne>0){
			isAvailable=1;
		}else if(tierFour>0){
			isAvailable=2;
		}
		
		return isAvailable;
		
	}
	
	public void savePatientList(List<PatientForm> patientsForms,String crashId,CrashReport runnerReportCrashId) throws Exception{
		Integer patientCount=0;
		Integer vehicleCount=0;
		for (PatientForm patientsForm : patientsForms) {
			//patientsForm.setCrashReportFileName(uuid+"_"+crashId+".pdf");
			patientsForm.setCrashReportFileName(crashId+".pdf");
			patientsForm.setCrashId(crashId);
			try {
				if(patientsForm.getIsOwner()==1){
					patientsService.savePatient(patientsForm);
					vehicleCount++;
					System.out.println("Save Vehicle And Owner Details");
				}else{
					if(runnerReportCrashId!=null){
						patientsService.savePatient(patientsForm);
						patientCount++;
						System.out.println("Not Matched----->>>Save Patient Details");
					}else{
						patientsService.savePatient(patientsForm);
						System.out.println("No Need To Check----->>>Save Patient Details");
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw e;
			}
		}
		
		// Update Patients Count
		if(runnerReportCrashId!=null){
			CrashReport crashReport = crashReportDAO.getCrashReport(crashId);
			Integer oldPatientsCount=crashReport.getNumberOfPatients();
			crashReport.setNumberOfPatients(oldPatientsCount+patientCount);
			crashReport.setVehicleCount(vehicleCount);
			LocalDate addedDate = new LocalDate();
			LocalDateTime addedDateTime = new LocalDateTime();
			crashReport.setAddedDate(addedDate.toDate());
			crashReport.setAddedDateTime(addedDateTime.toDate());
			crashReportDAO.update(crashReport);
		}
		
		// Update patient Added on Date To Not Matched Runner Patients. 1---> Is Runner Report Status
	/*	List<Patient> patients = patientDAO.getRunnerReportPatients(crashId, 1);
		for (Patient patient : patients) {
			LocalDate currentDate=new LocalDate();
			patient.setAddedDate(currentDate.toDate());
			patientDAO.update(patient);
		}*/
	}
	
	// Save Scanned Report From Automation Script Night watch
	public RunnerCrashReportForm saveRunnerDirectOrScannedReport(RunnerCrashReportForm runnerCrashReportForm, PDFParserData parserData) throws Exception{
		int status=1;
		
		Integer crashReportErrorId=16;
		Integer isRunnerReport=3;
		UUID randomUUID = UUID.randomUUID();
		String crashId=randomUUID.toString().replaceAll("-", "");
		File file = null;
		
		try{
			boolean isSaveReport=false;
			String fileName="";
			
			//Initialize Variables
			PoliceAgency policeAgency = policeAgencyDAO.get(runnerCrashReportForm.getReportFrom());
			County county= countyDAO.get(Integer.parseInt(runnerCrashReportForm.getCounty()));
			CrashReportError crashReportError=crashReportErrorDAO.get(crashReportErrorId);
			//End Initialize Variables

			
			if(runnerCrashReportForm.getReportFrom()==0){
				file = getPDFFile(crashId, 2, runnerCrashReportForm.getFilePath());
				if (file.length() > 2000) {// 2000 File Size refers an crash report not found exception
					// Parse the PDF Using Type one parser
					if(parserData==null)
						parserData=pdfParserOne.parsePdfFromFile(file);
					PDFParserData pdfParserData = parserData;
					if(pdfParserData!=null){
						isRunnerReport=0;
						runnerCrashReportForm.setLocalReportNumber(pdfParserData.getLocalReportNumber());
						runnerCrashReportForm.setCrashDate(pdfParserData.getCrashDate());
						//Setup to check already exist Data
						runnerCrashReportForm.setCounty(pdfParserData.getCounty());
						//End Set up
					}
					if (!this.isDirectReportAlreadyAvailable(runnerCrashReportForm)) {
						if(pdfParserData!=null){
							parsePDFDocument(file, crashId, runnerCrashReportForm.getReportFrom(), pdfParserData);
							isSaveReport = true;
						}else{
							// Save into scanned
							if(Integer.parseInt(injuryProperties.getProperty("env"))==1){
								fileName=crashId + ".pdf";
							}else{
								fileName="CrashReport_"+ crashId + ".pdf";
							}					
							String localReportNumber=runnerCrashReportForm.getLocalReportNumber();
							if(crashReportDAO.getCrashReportCountByLocalReportNumber(localReportNumber)>0){
								Long localReportNumberCount=crashReportDAO.getLocalReportNumberCount(localReportNumber+"(");
								localReportNumber=localReportNumber+"("+(localReportNumberCount+1)+")";
							}
							
							Integer numberOfPatients=0;
							Integer vehicleCount=0;
							CrashReport crashReport=new CrashReport(crashId, crashReportError, policeAgency, county, localReportNumber, InjuryConstants.convertYearFormat(runnerCrashReportForm.getCrashDate()), 
										 new Date(), numberOfPatients, vehicleCount, fileName, null, isRunnerReport, new Date(), 1, new Date(), new Date(), localReportNumber.replaceAll(" ", ""), null, null, null);
							
							crashReportDAO.save(crashReport);
							if(Integer.parseInt(injuryProperties.getProperty("awsUpload"))==1)				
								awsFileUpload.uploadFileToAWSS3(file.getAbsolutePath(), fileName);
							isSaveReport = true;
							//End Save
							
						}
					} else{
						status=2;
					}
				} else {
					isSaveReport = false;
					status = 0;
				}
				
			}			
			if(isSaveReport){
				// Update Last Updated Date
				policeAgencyDAO.update(policeAgency);
			}
			
		}catch(Exception e){
			throw e;
		}
		runnerCrashReportForm.setStatus(status);
		return runnerCrashReportForm;
	}
	
	public boolean isDirectReportAlreadyAvailable(RunnerCrashReportForm runnerCrashReportForm){
		Long oldReportCount=crashReportDAO.getLocalReportNumberCount(runnerCrashReportForm.getLocalReportNumber());
		Integer countyId=Integer.parseInt(runnerCrashReportForm.getCounty());
		boolean isExist=false;
		CrashReport crashReport=null;
		Integer isCheckAll=1;
		for (int i = 0; i <=oldReportCount; i++) {
			String localReportNumber=runnerCrashReportForm.getLocalReportNumber();
			if(i!=0){
				localReportNumber=localReportNumber+"("+i+")";
			}
			crashReport=crashReportDAO.getCrashReportForChecking(localReportNumber,runnerCrashReportForm.getCrashDate(), countyId, isCheckAll);
			if(crashReport!=null){
				isExist=true;
				break;
			}
		}
			
		return isExist;
	}
	
	// Check Crash Report Id Availability for Manual Upload
	public boolean isCrashIdAvailable(String crashId) {
		boolean isAvailable = false;
		CrashReport crashReport = crashReportDAO.getCrashReport(crashId);
		if (crashReport != null) {
			isAvailable = true;
		}
		
		return isAvailable;
	}
	
	// Calculate Year
	public Integer getTotalYear(String dob){
		if(dob!=null&&InjuryConstants.validateDate(dob)){
			DateTime date1 = new DateTime(InjuryConstants.convertYearFormat(dob));
			DateTime date2 = new DateTime();
			Period period = new Period(date1,date2);
			return period.getYears();
		}
		return null;
		
	}
	
	//Set Damage scale based on range
	public Integer getDamageScale(String estimatedDamage)
	{
		Integer DamageScale=0;
		if(!estimatedDamage.equals("")){
			estimatedDamage=estimatedDamage.replaceAll("\\$", "").replaceAll(",", "");
			BigDecimal costStriped = new BigDecimal(estimatedDamage);  
			estimatedDamage=costStriped.stripTrailingZeros().toPlainString();
			Integer ConvertedInteger=Integer.parseInt(estimatedDamage);
			if(ConvertedInteger==0){
				DamageScale= 1;
			}
			if(ConvertedInteger>=1&&ConvertedInteger<=1000){
				DamageScale= 2;
			}
			if(ConvertedInteger>1000&&ConvertedInteger<=3000){
				DamageScale= 3;
			}
			if(ConvertedInteger>3000){
				DamageScale= 4;
			}
		}
		else{
			DamageScale= 9;
		}
		return DamageScale;
		
	}
	//Get patient forms
	public List<PatientForm> getPatientFormsGoingToSave(PDFParserData pdfParserData){
		// Vehicle Details with Owner Information
				List<PatientForm> vehicleOwnerDetails = this.getVehicleOwnerDetails(pdfParserData);
				// Vehicle Count From Report First Page Number of Units
				List<PatientForm> patientsForms = new ArrayList<PatientForm>();
				//Check for report status
				Integer tierType = this.getReportType(pdfParserData);
				patientsForms=this.getTierPatientForm(pdfParserData, tierType, vehicleOwnerDetails);
				return patientsForms;
	}
	
	//Update for Commercial
	public File processCrashReport(String crashId,String pdfLink) throws Exception{
		return this.getPDFFile(crashId, 2, pdfLink);
	}
}
