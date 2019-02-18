package com.deemsys.project.pdfparser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.deemsys.project.CrashReport.CrashReportForm;
import com.deemsys.project.CrashReport.CrashReportList;
import com.deemsys.project.CrashReport.CrashReportSearchForm;
import com.deemsys.project.CrashReport.CrashReportService;
import com.deemsys.project.CrashReport.ImportCrashReportStatus;
import com.deemsys.project.CrashReport.RunnerCrashReportForm;
import com.deemsys.project.captcharesolver.CaptchaResolverForm;
import com.deemsys.project.captcharesolver.CaptchaResolverService;
import com.deemsys.project.captcharesolver.CaptchaResultForm;
import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.entity.CrashReport;
import com.deemsys.project.patient.PatientService;
import com.deemsys.project.pdfreader.PDFCrashReportReader;

import net.sourceforge.tess4j.TesseractException;

@Controller
public class PDFParserController {
	
	@Autowired
	PDFCrashReportReader pdfCrashReportReader;
	
	@Autowired
	PdfParserService parserService;
	
	@Autowired
	PDFParserOne pdfParserOne;
	
	@Autowired
	ScannedParser scannedParser;
	
	@Autowired
	CaptchaResolverService captchaResolverService;
	
	@Autowired
	CrashReportService crashReportService;
	
	@Autowired
	PatientService patientService; 
	
	// Parse PDF File as Array
	@RequestMapping(value = "/readCrashReportArray", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
	public @ResponseBody List<List<String>> readUploadCrashReportArray(
			@RequestParam("file") MultipartFile file, ModelMap model)
			throws IOException {

		return pdfCrashReportReader.parsePdfFromMultipartFile(file);

	}
	
	// Parse PDF File
	@RequestMapping(value = "/readRequiredDate", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
	public @ResponseBody PDFParserData readRequiredData(
			@RequestParam("file") MultipartFile file, ModelMap model)
			throws IOException {

		return parserService.getValuesFromPDF(pdfCrashReportReader.parsePdfFromMultipartFile(file));

	}
		
	
	@RequestMapping(value="/importReport",method=RequestMethod.POST)
	public String saveDirectRunnerOrScannedCrashReport(@RequestBody RunnerCrashReportForm runnerCrashReportForm,ModelMap model) {
		try {
			runnerCrashReportForm = pdfCrashReportReader.saveRunnerDirectOrScannedReport(runnerCrashReportForm,null);
			Integer status=runnerCrashReportForm.getStatus();
			if (status == 1) {
				model.addAttribute("data",runnerCrashReportForm);
				model.addAttribute("reportSave", true);
				model.addAttribute("message", "Report Saved Successfully");
			} else if (status == 0) {
				model.addAttribute("reportSave", false);
				model.addAttribute("data",runnerCrashReportForm);
				model.addAttribute("message", "File is Not Available");
			} else if (status == 2) {
				model.addAttribute("reportSave", false);
				model.addAttribute("data",runnerCrashReportForm);
				model.addAttribute("message", "Report Already Exist");
			}

			model.addAttribute("requestSuccess", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("message", e.toString() + " for - " + runnerCrashReportForm.getLocalReportNumber());
			model.addAttribute("requestSuccess", false);
		}

		return "/returnPage";
	}
	
	@RequestMapping(value = "/importReportThroughFile", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
	public String importReportThroughFile(@RequestParam("file") MultipartFile file,ModelMap model) {
		try {
			
		    String filePath = "C:\\wamp\\www\\OHReports";
		    File convFile = new File(filePath+"\\"+UUID.randomUUID()+".pdf");
		    file.transferTo(convFile);
		    
			PDFParserData pdfParserData=pdfParserOne.parsePdfFromFile(convFile);
			RunnerCrashReportForm runnerCrashReportForm=new RunnerCrashReportForm("", "", pdfParserData.getLocalReportNumber(),pdfParserData.getCrashDate(), pdfParserData.getCounty(), "http://localhost/OHReports/"+convFile.getName(), 0, "", null);
			int status = pdfCrashReportReader.saveRunnerDirectOrScannedReport(runnerCrashReportForm, pdfParserData).getStatus();
			if (status == 1) {
				model.addAttribute("data",pdfParserData);
				model.addAttribute("reportSave", true);
				model.addAttribute("message", "Report Saved Successfully");
			} else if (status == 0) {
				model.addAttribute("reportSave", false);
				model.addAttribute("data",runnerCrashReportForm);
				model.addAttribute("message", "File is Not Available");
			} else if (status == 2) {
				model.addAttribute("reportSave", false);
				model.addAttribute("data",runnerCrashReportForm);
				model.addAttribute("message", "Report Already Exist");
			}

			model.addAttribute("requestSuccess", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("requestSuccess", false);
		}

		return "/returnPage";
	}
	
	@RequestMapping(value = "/getParsingDataFromImport", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
	public String getParsingDataFromImport(@RequestParam("file") MultipartFile file,ModelMap model) {
		try {
			
		    String filePath = "C:\\wamp\\www\\OHReports";
		    File convFile = new File(filePath+"\\"+UUID.randomUUID()+".pdf");
		    file.transferTo(convFile);
		    //Parsing Data
			PDFParserData pdfParserData=pdfParserOne.parsePdfFromFile(convFile);
			
			model.addAttribute("data",pdfParserData);
			model.addAttribute("patientForms",pdfCrashReportReader.getPatientFormsGoingToSave(pdfParserData));
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("requestSuccess", false);
		}

		return "/returnPage";
	}
	
	//Update Patient For Commercial Vehicle
	@RequestMapping(value = "/updateCommercial", headers = "content-type=multipart/form-data", method = RequestMethod.POST)
	public String getParsingDataFromImport(@RequestParam("date") String date,@RequestParam("localReportNumber") String localReportNumber,ModelMap model) {
		try {
			
			CrashReportSearchForm crashReportSearchForm=new CrashReportSearchForm();
			crashReportSearchForm.setLocalReportNumber(localReportNumber);
			crashReportSearchForm.setCrashId("");
			crashReportSearchForm.setCrashFromDate("");
			crashReportSearchForm.setNumberOfDays("1");
			crashReportSearchForm.setCrashToDate("");
			crashReportSearchForm.setAddedFromDate(date);
			crashReportSearchForm.setAddedToDate(date);
			crashReportSearchForm.setRecordsPerPage(25);
			crashReportSearchForm.setIsRunnerReport(0);
			crashReportSearchForm.setIsArchived(0);
			crashReportSearchForm.setDirectReportStatus(0);
			crashReportSearchForm.setArchivedFromDate("");
			crashReportSearchForm.setArchivedToDate("");
			crashReportSearchForm.setReportFrom(-1);
			crashReportSearchForm.setPageNumber(1);
			crashReportSearchForm.setCountyId(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88});
						
			List<CrashReport> crashReportList=new ArrayList<>();
			crashReportList=crashReportService.getCrashReportByParams(crashReportSearchForm);
			
			for (CrashReport crashReportForm : crashReportList) {
				 //Parsing Data
				PDFParserData pdfParserData=pdfParserOne.parsePdfFromFile(pdfCrashReportReader.getPDFFile(crashReportForm.getCrashId(), 2, "https://cdn.crashreportsonline.com/crashreports/"+crashReportForm.getFilePath()));
				
				for (PDFParserVehicleData pdfParserVehicleData : pdfParserData.getPdfParserVehicleDatas()) {
					if(pdfParserVehicleData.getTypeOfUse()!=null&&!pdfParserVehicleData.getTypeOfUse().equals(""))
						patientService.updatePatientTypeOfUse(crashReportForm.getCrashId(), Integer.parseInt(pdfParserVehicleData.getUnitNumber()), Integer.parseInt(pdfParserVehicleData.getTypeOfUse()));
				}						
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("requestSuccess", false);
		}

		return "/returnPage";
	}
		
	
	@RequestMapping(value = "/multiImportFile", method = RequestMethod.POST)
	public String multiProcessFile(@RequestParam("folder") Integer folder,ModelMap model) {
		try {
			 	File file = new File("C:\\wamp\\www\\CRO-Shared\\"+folder);
		        File[] files = file.listFiles();
		        for(File report: files){
		        	String fileName=UUID.randomUUID()+".pdf";
		        	report.renameTo(new File("C:\\wamp\\www\\OHReports\\"+fileName));
		        	PDFParserData pdfParserData=pdfParserOne.parsePdfFromFile(new File("C:\\wamp\\www\\OHReports\\"+fileName));
					RunnerCrashReportForm runnerCrashReportForm=new RunnerCrashReportForm("", "", pdfParserData.getLocalReportNumber(),pdfParserData.getCrashDate(), pdfParserData.getCounty(), "http://localhost/OHReports/"+fileName, 0, "", null);
					pdfCrashReportReader.saveRunnerDirectOrScannedReport(runnerCrashReportForm, pdfParserData);
					
		        }
		        model.addAttribute("requestSuccess", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("requestSuccess", false);
		}

		return "/returnPage";
	}
	
	@RequestMapping(value="/getTotalYear",method=RequestMethod.GET)
	public String getHomepage(ModelMap model,@RequestParam("dob") String dob)
	{
		model.addAttribute("Year",pdfCrashReportReader.getTotalYear(dob));
    	model.addAttribute("Success",true);
		return "/index";
	}

	@RequestMapping(value="/resolveCaptcha",method=RequestMethod.POST)
	public String resolveCaptcha(@RequestBody CaptchaResolverForm captchaResolverForm, ModelMap model)
	{
		CaptchaResultForm captchaResultForm=captchaResolverService.resolveCaptcha(captchaResolverForm.getUsername(),captchaResolverForm.getPassword(),captchaResolverForm.getCaptchafile());
		model.addAttribute("response",captchaResultForm);
		return "/returnPage";		
   	}
	
	//Report Incorrect Captcha
	@RequestMapping(value="/reportCaptcha",method=RequestMethod.POST)
	public String reportCaptcha(@RequestBody CaptchaResolverForm captchaResolverForm, ModelMap model) throws NumberFormatException, IOException, com.deathbycaptcha.Exception 
	{
		Boolean status=this.captchaResolverService.reportCaptcha(captchaResolverForm);
		model.addAttribute("requestStatus",status);
		return "/returnPage";
	}
	
	@RequestMapping(value="/getTextbyCaptcha",method=RequestMethod.GET)
	public String resolveCaptcha(@RequestParam("captchaId") String captchaId, ModelMap model) throws JSONException
	{

        try {
			URL url = new URL("http://api.dbcapi.me/api/captcha/"+captchaId);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("accept", "application/json");
	        String response=this.read(con.getInputStream());
	        CaptchaResultForm captchaResultForm=captchaResolverService.getTextbyCaptcha(response);
	        
	        model.addAttribute("response",captchaResultForm);
			return "/returnPage";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			model.addAttribute("requestStatus",false);
	        model.addAttribute("response","Try Again Later!");
			return "returnPage";
		}
	}

	
	protected void sendData(HttpURLConnection con, String data) throws IOException {
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
        } catch(IOException exception) {
            throw exception;
        } finally {
            this.closeQuietly(wr);
        }
    }
	
	 private String read(InputStream is) throws IOException {
	        BufferedReader in = null;
	        String inputLine;
	        StringBuilder body;
	        try {
	            in = new BufferedReader(new InputStreamReader(is));

	            body = new StringBuilder();

	            while ((inputLine = in.readLine()) != null) {
	                body.append(inputLine);
	            }
	            in.close();

	            return body.toString();
	        } catch(IOException ioe) {
	            throw ioe;
	        } finally {
	            this.closeQuietly(in);
	        }
	    }
	protected void closeQuietly(Closeable closeable) {
        try {
            if( closeable != null ) {
                closeable.close();
            }
        } catch(IOException ex) {

        }
    }
	
	
}
