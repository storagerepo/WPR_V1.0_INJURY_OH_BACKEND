package com.deemsys.project.pdfparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.common.InjuryProperties;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

@Service
public class PDFParserOne {
	
	@Autowired
	InjuryProperties injuryProperties;
	
	public PDFParserData parsePdfFromFile(File file)
			throws IOException {
		
		Map<String, String> propertyValues=new HashMap<>();
		propertyValues=injuryProperties.getParserOneAllProperty();
		System.out.println("Start Parsing File.............................!!!!");
		//Return Data
		PDFParserData pdfParserData=new PDFParserData();
		
		//Read the PDF
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			PdfReader reader = new PdfReader(fileInputStream);
			
			String[] firstPageProperties = { "localReportNumber", "crashSeverity", "reportingAgencyCode",
					"reportingAgencyName", "numberOfUnits", "unitInError", "county", "cityVillageTownship", "crashDate"};
			
			for (String property : firstPageProperties) {
				propertyValues.put(property,getText(reader, propertyValues.get(property), 1));
				
			}
			
			//Set First Page Values
			pdfParserData.setLocalReportNumber(propertyValues.get("localReportNumber"));
			pdfParserData.setCrashSeverity(propertyValues.get("crashSeverity"));
			pdfParserData.setReportingAgencyCode(propertyValues.get("reportingAgencyCode"));
			pdfParserData.setReportingAgency(propertyValues.get("reportingAgencyName"));
			pdfParserData.setNumberOfUnits(propertyValues.get("numberOfUnits"));
			pdfParserData.setUnitInError(propertyValues.get("unitInError"));
			pdfParserData.setCounty(propertyValues.get("county"));
			pdfParserData.setCityVillageTownship(propertyValues.get("cityVillageTownship"));
			pdfParserData.setCrashDate(propertyValues.get("crashDate"));
			//End First Page Values
			
			//Modified
			//<!--Time of Crash-->
			if(!pdfParserData.getCrashDate().equals(""))
			{
				String[] dateSplit=pdfParserData.getCrashDate().split(" ");
				if(dateSplit.length==2){
					pdfParserData.setCrashDate(dateSplit[0]);
					pdfParserData.setTimeOfCrash(dateSplit[1]);
				}
					
			}
			//<! End Time of Crash-->
			
			//End Modified
			
			
			//Get Total PDF Pages
			int totalPages=reader.getNumberOfPages();
			int page;
			List<PDFParserVehicleData> pdfParserVehicleDatas=new ArrayList<>();
			List<PDFParserUnitData> pdfParserUnitDatas=new ArrayList<>();
			for(page=2; page<=totalPages; page++){
				switch (this.getText(reader,propertyValues.get("labelTofindPage"),page)) {
				case "UNIT":
					PDFParserVehicleData parserVehicleData=new PDFParserVehicleData();
					parserVehicleData=this.getVehicleDataFromUnitPage(reader, propertyValues, page);
					//AtFault Insurance & Policy Number Setup
					if(pdfParserData.getUnitInError()!=null&&pdfParserData.getUnitInError().equals(parserVehicleData.getUnitNumber())){
						pdfParserData.setAtFaultInsuranceCompany(parserVehicleData.getInsuranceCompany());
						pdfParserData.setAtFaultPolicyNumber(parserVehicleData.getPolicyNumber());
					}					
					pdfParserVehicleDatas.add(parserVehicleData);
					break;
				case "MOTORIST / NON-MOTORIST":		
					pdfParserUnitDatas.addAll(this.getUnitDataFromMotorlistPage(reader, propertyValues, page, pdfParserVehicleDatas));
					break;
				case "OCCUPANT / WITNESS ADDENDUM":
					pdfParserUnitDatas.addAll(this.getOccupantDataFromOccupantPage(reader, propertyValues, page, pdfParserVehicleDatas));
					break;
				default:
					System.out.println("default");
					break;
				}
			}
			pdfParserData.setPdfParserVehicleDatas(pdfParserVehicleDatas);
			pdfParserData.setPdfParserUnitDatas(pdfParserUnitDatas);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		
		
		
		System.out.println("Completed Parsing File.............................!!!!");
		return pdfParserData;
	}
	
	public String getText(PdfReader reader, String coordinates, Integer page) throws IOException{
		String[] propertyCoordinates=coordinates.split(",");
		Rectangle rect = new Rectangle(Double.parseDouble(propertyCoordinates[0]),
				Double.parseDouble(propertyCoordinates[1]), Double.parseDouble(propertyCoordinates[2]),
				Double.parseDouble(propertyCoordinates[3]));
		RenderFilter[] filter = { new RegionTextRenderFilter(rect) };
		String returnText = "";
		TextExtractionStrategy strategy;
		strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
		//Reading the file
		returnText = PdfTextExtractor.getTextFromPage(reader, page, strategy);
		return returnText;
	}
	
	public PDFParserVehicleData getVehicleDataFromUnitPage(PdfReader reader, Map<String, String> propertyValues, Integer page) throws IOException{
		//Temporary Hash Map
		Map<String, String> propertyValuesParam=new HashMap<>();
		propertyValuesParam.putAll(propertyValues);
		String[] unitPageProperties = {"driverUnitNumber","ownerName","ownerPhoneNumber","ownerDamageScale","ownerAddress"
				,"licensePlateNumber","VIN","noOfOccupants","vehicleYear","vehicleMake","vehicleModal","insuranceCompany"
				,"policyNumber","typeOfUseOne","typeOfUseTwo","typeOfUseThree"
				};
		
		for (String unitProperty : unitPageProperties) {
			propertyValuesParam.put(unitProperty,getText(reader, propertyValuesParam.get(unitProperty), page));	
		}
		
		PDFParserVehicleData pdfParserVehicleData=new PDFParserVehicleData();		
		pdfParserVehicleData.setUnitNumber(propertyValuesParam.get("driverUnitNumber"));
		pdfParserVehicleData.setOwnerName(propertyValuesParam.get("ownerName"));
		pdfParserVehicleData.setPhoneNumber(propertyValuesParam.get("ownerPhoneNumber"));
		pdfParserVehicleData.setDamageScale(propertyValuesParam.get("ownerDamageScale"));
		pdfParserVehicleData.setOwnerAddress(propertyValuesParam.get("ownerAddress"));
		pdfParserVehicleData.setPlateNumber(propertyValuesParam.get("licensePlateNumber"));
		pdfParserVehicleData.setVIN(propertyValuesParam.get("VIN"));
		pdfParserVehicleData.setVehicleYear(propertyValuesParam.get("vehicleYear"));
		pdfParserVehicleData.setVehicleMake(propertyValuesParam.get("vehicleMake"));
		pdfParserVehicleData.setVehicleModel(propertyValuesParam.get("vehicleModal"));
		pdfParserVehicleData.setInsuranceCompany(propertyValuesParam.get("insuranceCompany"));
		pdfParserVehicleData.setPolicyNumber(propertyValuesParam.get("policyNumber"));
		
		//Type of user
		if(propertyValuesParam.get("typeOfUseOne").equals("X")){
			pdfParserVehicleData.setTypeOfUse("1");
		}else if(propertyValuesParam.get("typeOfUseTwo").equals("X")){
			pdfParserVehicleData.setTypeOfUse("2");
		}else if(propertyValuesParam.get("typeOfUseThree").equals("X")){
			pdfParserVehicleData.setTypeOfUse("3");
		}
		
		return pdfParserVehicleData;
	}
	
	public List<PDFParserUnitData> getUnitDataFromMotorlistPage(PdfReader reader, Map<String, String> propertyValues, Integer page,List<PDFParserVehicleData> pdfParserVehicleDatas) throws IOException{
		//Temporary Hash Map
		Map<String, String> propertyValuesParam=new HashMap<>();
		propertyValuesParam.putAll(propertyValues);
		
		String[] occupantsProperties={"occupantOneUnitNumber","occupantOneName",
				"occupantOneDateOfBirth",
				"occupantOnePatientAge",
				"occupantOneGender",
				"occupantOneAddress",
				"occupantOnePhoneNumber",
				"occupantOneInjury",
				"occupantOneEMSAgency",
				"occupantOneMedicalFacility",
				"occupantOneSeatingPosition",
				"occupantTwoUnitNumber",
				"occupantTwoName",
				"occupantTwoDateOfBirth",
				"occupantTwoPatientAge",
				"occupantTwoGender",
				"occupantTwoAddress",
				"occupantTwoPhoneNumber",
				"occupantTwoInjury",
				"occupantTwoEMSAgency",
				"occupantTwoMedicalFacility",
				"occupantTwoSeatingPosition",
				"occupantThreeUnitNumber",
				"occupantThreeName",
				"occupantThreeDateOfBirth",
				"occupantThreePatientAge",
				"occupantThreeGender",
				"occupantThreeAddress",
				"occupantThreePhoneNumber",
				"occupantThreeInjury",
				"occupantThreeEMSAgency",
				"occupantThreeMedicalFacility",
				"occupantThreeSeatingPosition"
				};
	

		for (String occupantProperty : occupantsProperties) {
			propertyValuesParam.put(occupantProperty,getText(reader, propertyValuesParam.get(occupantProperty), page));	
		}
		
		List<PDFParserUnitData> pdfParserUnitDatas=new ArrayList<>();
		System.out.println(propertyValuesParam.get("occupantOneUnitNumber"));
		if(!propertyValuesParam.get("occupantOneUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantOneUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantOneName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantOneDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantOnePatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantOneGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantOneAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantOnePhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantOneInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantOneEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantOneMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantOneSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		if(!propertyValuesParam.get("occupantTwoUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantTwoUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantTwoName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantTwoDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantTwoPatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantTwoGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantTwoAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantTwoPhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantTwoInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantTwoEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantTwoMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantTwoSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		if(!propertyValuesParam.get("occupantThreeUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantThreeUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantThreeName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantThreeDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantThreePatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantThreeGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantThreeAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantThreePhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantThreeInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantThreeEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantThreeMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantThreeSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		
		for (PDFParserUnitData pdfParserUnitData : pdfParserUnitDatas) {
			for (PDFParserVehicleData parserVehicleData : pdfParserVehicleDatas) {
				if(Integer.parseInt(parserVehicleData.getUnitNumber())==Integer.parseInt(pdfParserUnitData.getUnitNumber())){
					pdfParserUnitData.setInsuranceCompany(parserVehicleData.getInsuranceCompany());
					pdfParserUnitData.setPolicyNumber(parserVehicleData.getPolicyNumber());
				}
			}
		}
		
		return pdfParserUnitDatas;
	}
	
	
	public List<PDFParserUnitData> getOccupantDataFromOccupantPage(PdfReader reader, Map<String, String> propertyValues, Integer page, List<PDFParserVehicleData> pdfParserVehicleDatas) throws IOException{
		//Temporary Hash Map
		Map<String, String> propertyValuesParam=new HashMap<>();
		propertyValuesParam.putAll(propertyValues);
		
		String[] occupantsProperties={"occupantFourUnitNumber","occupantFourName",
				"occupantFourDateOfBirth",
				"occupantFourPatientAge",
				"occupantFourGender",
				"occupantFourAddress",
				"occupantFourPhoneNumber",
				"occupantFourInjury",
				"occupantFourEMSAgency",
				"occupantFourMedicalFacility",
				"occupantFourSeatingPosition",
				"occupantFiveUnitNumber","occupantFiveName",
				"occupantFiveDateOfBirth",
				"occupantFivePatientAge",
				"occupantFiveGender",
				"occupantFiveAddress",
				"occupantFivePhoneNumber",
				"occupantFiveInjury",
				"occupantFiveEMSAgency",
				"occupantFiveMedicalFacility",
				"occupantFiveSeatingPosition",
				"occupantSixUnitNumber","occupantSixName",
				"occupantSixDateOfBirth",
				"occupantSixPatientAge",
				"occupantSixGender",
				"occupantSixAddress",
				"occupantSixPhoneNumber",
				"occupantSixInjury",
				"occupantSixEMSAgency",
				"occupantSixMedicalFacility",
				"occupantSixSeatingPosition",
				"occupantSevenUnitNumber","occupantSevenName",
				"occupantSevenDateOfBirth",
				"occupantSevenPatientAge",
				"occupantSevenGender",
				"occupantSevenAddress",
				"occupantSevenPhoneNumber",
				"occupantSevenInjury",
				"occupantSevenEMSAgency",
				"occupantSevenMedicalFacility",
				"occupantSevenSeatingPosition"
				};
		
		for (String occupantProperty : occupantsProperties) {
			propertyValuesParam.put(occupantProperty,getText(reader, propertyValuesParam.get(occupantProperty), page));	
		}
		
		List<PDFParserUnitData> pdfParserUnitDatas=new ArrayList<>();
		if(!propertyValuesParam.get("occupantFourUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantFourUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantFourName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantFourDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantFourPatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantFourGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantFourAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantFourPhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantFourInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantFourEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantFourMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantFourSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		if(!propertyValuesParam.get("occupantFiveUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantFiveUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantFiveName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantFiveDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantFivePatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantFiveGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantFiveAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantFivePhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantFiveInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantFiveEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantFiveMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantFiveSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		if(!propertyValuesParam.get("occupantSixUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantSixUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantSixName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantSixDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantSixPatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantSixGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantSixAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantSixPhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantSixInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantSixEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantSixMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantSixSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		if(!propertyValuesParam.get("occupantSevenUnitNumber").equals("")){
			PDFParserUnitData pdfParserUnitData=new PDFParserUnitData();
			pdfParserUnitData.setUnitNumber(propertyValuesParam.get("occupantSevenUnitNumber"));
			pdfParserUnitData.setName(propertyValuesParam.get("occupantSevenName"));
			pdfParserUnitData.setDateOfBirth(propertyValuesParam.get("occupantSevenDateOfBirth"));
			pdfParserUnitData.setAge(propertyValuesParam.get("occupantSevenPatientAge"));
			pdfParserUnitData.setGender(propertyValuesParam.get("occupantSevenGender"));
			pdfParserUnitData.setAddress(propertyValuesParam.get("occupantSevenAddress"));
			pdfParserUnitData.setPhoneNumber(propertyValuesParam.get("occupantSevenPhoneNumber"));
			pdfParserUnitData.setInjuries(propertyValuesParam.get("occupantSevenInjury"));
			pdfParserUnitData.setEMSAgency(propertyValuesParam.get("occupantSevenEMSAgency"));
			pdfParserUnitData.setMedicalFacility(propertyValuesParam.get("occupantSevenMedicalFacility"));
			pdfParserUnitData.setSeatingPosition(propertyValuesParam.get("occupantSevenSeatingPosition"));
			//Add it
			pdfParserUnitDatas.add(pdfParserUnitData);
		}
		
		for (PDFParserUnitData pdfParserUnitData : pdfParserUnitDatas) {
			for (PDFParserVehicleData parserVehicleData : pdfParserVehicleDatas) {
				if(Integer.parseInt(parserVehicleData.getUnitNumber())==Integer.parseInt(pdfParserUnitData.getUnitNumber())){
					pdfParserUnitData.setInsuranceCompany(parserVehicleData.getInsuranceCompany());
					pdfParserUnitData.setPolicyNumber(parserVehicleData.getPolicyNumber());
				}
			}
		}
		
		return pdfParserUnitDatas;
		
	}
	

}
