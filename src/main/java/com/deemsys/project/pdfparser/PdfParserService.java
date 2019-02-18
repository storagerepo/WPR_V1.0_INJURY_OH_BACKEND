package com.deemsys.project.pdfparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.deemsys.project.common.InjuryConstants;

@Service
public class PdfParserService {
	
	public PDFParserData getValuesFromPDF(List<List<String>> content)
			throws IOException{		
		// First Page
		List<String> firstPage = content.get(0);
		String[] firstPageArray = (String[]) firstPage.toArray();
		try{
			PDFParserData pdfParserData=new PDFParserData();
			List<PDFParserUnitData> pdfParserUnitDatas=new ArrayList<PDFParserUnitData>();
			List<PDFParserVehicleData> pdfParserVehicleDatas = new ArrayList<PDFParserVehicleData>();
			System.out.println("Length--"+firstPageArray.length);
			if(firstPageArray.length<=1){
				return pdfParserData=null;
			}else{
				String crashDate=firstPage.get(firstPage.indexOf("Date") + 1);
				String county=firstPage.get(firstPage.indexOf("County") + 1);
				String crashTime=firstPage.get(firstPage.indexOf("Time")+1);
				String reportNumber=this.getLocalReportNumber(firstPage.get(firstPage.indexOf("Local Use/Patrol Area")+1));
				Integer numberOfUnits=Integer.parseInt(firstPage.get(firstPage.size()-1));
			
				String victimInsurance="",victimPolicy="",faultInsurance="",faultPolicy="",reportingAgency="",reportingAgencyCode="",unitInError="";
				
				
				
				Integer loopLimit=1;
				loopLimit=content.size();
			/*	if(numberOfUnits!=1){
					loopLimit=content.size();
				}*/
				
				for (int i = 1; i <=loopLimit; i++) {
					List<String> parsingPage=new ArrayList<String>();
					String[] parsingArray= null;;
					if(i%2==0){
						parsingPage=content.get(i-2);
						parsingArray= (String[]) parsingPage.toArray();
					}else{
						parsingPage=content.get(i-1);
						parsingArray= (String[]) parsingPage.toArray();
					}
					
					String firstName = "",middleName="",lastName="",phoneNumber="",unitNumber="",dob="",seatingPosition="",gender="",injuries="",
							ownerName="",ownerAddress="",vehicleMake="",vehicleYear="",plateNumber="",vehicleModel="",VIN="",vehicleType="",estimatedPropertyDamage="";
					
					String unitValue=this.getValueByStartWith(parsingArray,"UNIT #",i);
					if(unitValue.equals("UNIT #")){
						Integer firstUnitIndex=this.getIndexWithPosition(parsingArray,"UNIT #",i);
						if(firstUnitIndex!=-1){
							unitNumber=parsingPage.get(firstUnitIndex+1);
						}
					}
					else if(unitValue.indexOf("PEDESTRIAN")!=-1)
					{
						Integer index=unitValue.indexOf("#");
						unitNumber=Character.toString(unitValue.charAt(index+2));
						if(!unitNumber.matches("[1-9]+")){
							unitNumber="";
						}
					}
					
					if(unitInError.equals("")){
						String[] unitNumbersInPage=this.getUnitNumbersInSinglePage(parsingArray, parsingPage, i);
						unitInError=this.getUnitInError(parsingArray, parsingPage, unitNumbersInPage);
					}
					
					// First Name
					//Integer nameIndex=parsingPage.indexOf("First")==-1?parsingPage.indexOf("First Middle"):-1;
					String nameValue=this.getValueByStartWith(parsingArray,"First",i);
					if(!nameValue.equals("")){
						Integer fnameIndex=this.getIndexWithPosition(parsingArray, nameValue, i);
						if(fnameIndex!=-1){
							firstName=this.getDriverName(parsingPage.get(fnameIndex-1));
						}
						
					}
					// Middle Name parsingPage.indexOf("Middle")
					if(!nameValue.equals("First Middle")&&!nameValue.equals("First Middle Last")){
						String mNameValue=this.getValueByStartWith(parsingArray,"Middle",i);
						if(!mNameValue.equals("")){
							Integer mnameIndex=this.getIndexWithPosition(parsingArray, mNameValue, i);
							if(mnameIndex!=-1){
								if(parsingPage.get(mnameIndex-2).equals("First"))
									middleName=parsingPage.get(mnameIndex-1);
								else
									middleName=parsingPage.get(mnameIndex-2)+parsingPage.get(mnameIndex-1);
							}
							
						}
					}
					
					//Last Name parsingPage.indexOf("Last")
					if(!nameValue.equals("First Middle Last")){
						String lNameValue=this.getValueByStartWith(parsingArray,"Last",i);
						if(!lNameValue.equals("")){
							Integer lnameIndex=this.getIndexWithPosition(parsingArray, lNameValue, i);
							if(lnameIndex!=-1){
								if(parsingPage.get(lnameIndex-2).equals("Middle")||parsingPage.get(lnameIndex-2).equals("First Middle"))
									lastName=parsingPage.get(lnameIndex-1);
								else
									lastName=parsingPage.get(lnameIndex-2)+parsingPage.get(lnameIndex-1);
							}	
						}
					}
					
					// Address
					String address = this.getValueByStartWith(parsingArray,"Address",i);
					if(address!=null&&!address.equals("")){
						address=this.getDriverAddress(address);
					}
					
					// City State Zip
					String cityStateZip = this.getValueByStartWith(parsingArray,"City State Zip",i);
					if(cityStateZip!=null&&!cityStateZip.equals("")&&cityStateZip.equals("City State Zip"))
					{
						Integer cityStateZipIndex=this.getIndexWithPosition(parsingArray, "City State Zip", i);
						cityStateZip=parsingPage.get(cityStateZipIndex+1);
						if(!cityStateZip.startsWith("Same")){
							address=address+", "+this.getDriverCityStateZip(cityStateZip);

						}
						// Check Address Contains Empty Values except , and spaces
						if(address.replaceAll(",", "").replaceAll(" ", "").equals("")){
							address="";
						}
					}
					else if(cityStateZip!=null&&!cityStateZip.equals("")&&!cityStateZip.equals("City State Zip")){
						address=address+", "+this.getDriverCityStateZip(cityStateZip);
					}
					
					// Phone Number
					Integer phoneNumberIndex=this.getIndexWithPosition(parsingArray, "Numbers", i);
					if(phoneNumberIndex!=-1){
						phoneNumber=parsingPage.get(phoneNumberIndex+1).equals("H")?"":parsingPage.get(phoneNumberIndex+1);
						if(phoneNumber.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(" ", "").replaceAll("-", "").equals("")){
							phoneNumber="";
						}
					}
					
					// DOB
					String dobValue = this.getValueByStartWith(parsingArray,"DOB",i);
					if(dobValue!=null&&!dobValue.equals("")){
						dob=this.getDOB(dobValue);
						if(dobValue.equals("DOB")){
						Integer dobIndex=this.getIndexWithPosition(parsingArray, "DOB", i);
						dob=parsingPage.get(dobIndex+1);
						if(dob.equals("34 Vision")){
							dob="";
						}
						}
					}
					
					if(i%2!=0){
						// Seating Position
						String seatingPositionValue=parsingPage.get(parsingPage.indexOf("Unit 1-Drv 1, Ped 1, etc. ")-1);
						
						if(!seatingPositionValue.equals("B")){
							String occupantUnitNumber=this.getUnitNumberFromOccupant(seatingPositionValue);
							if(!occupantUnitNumber.equals("")&&Integer.parseInt(occupantUnitNumber)<=numberOfUnits&&occupantUnitNumber.equals(unitNumber)){
								// Always One This is Driver Details
								seatingPosition="1";
								// Get Injuries, Gender
								Integer unitOneOccupantIndex=this.getIndexWithPosition(parsingArray, "Unit 1-Drv 1, Ped 1, etc. ", 1);
								if(unitOneOccupantIndex!=-1){
									if(parsingPage.get(unitOneOccupantIndex+2).equals("C")){
										String combinedValueOfOccupant=parsingPage.get(unitOneOccupantIndex+1);
										String[] splittedResult=this.getGenderAndInjuries(combinedValueOfOccupant);
										if(splittedResult.length>0){
											gender=splittedResult[0];
											injuries=splittedResult[1];
										}
									}
									else if(parsingPage.get(unitOneOccupantIndex+1).equals("see above")){
										String combinedValueofOccupant1=parsingPage.get(unitOneOccupantIndex+2);
										String[] splittedResult1=this.getGenderAndInjuries(combinedValueofOccupant1);
										if(splittedResult1.length>0){
											gender=splittedResult1[0];
											injuries=splittedResult1[1];
										}
									}
								}
							}
						}
						
					}else if(i%2==0){
						// Seating Position
						String seatingPositionValue="";
						if(!parsingPage.get(parsingPage.indexOf("Unit 2-Drv 2, Ped 2, etc. ")-1).equals("H")){
							seatingPositionValue=parsingPage.get(parsingPage.indexOf("Unit 2-Drv 2, Ped 2, etc. ")-1);
							String occupantUnitNumber=this.getUnitNumberFromOccupant(seatingPositionValue);
							if(!occupantUnitNumber.equals("")&&Integer.parseInt(occupantUnitNumber)<=numberOfUnits&&occupantUnitNumber.equals(unitNumber)){
								// Always One This is Driver Details
								seatingPosition="1";
								// Get Injuries, Gender
								Integer unitOneOccupantIndex=this.getIndexWithPosition(parsingArray, "Unit 2-Drv 2, Ped 2, etc. ", 1);
								if(unitOneOccupantIndex!=-1){
									if(!parsingPage.get(unitOneOccupantIndex+1).equals("see above")){
										String combinedValueOfOccupant=parsingPage.get(unitOneOccupantIndex+1);
										String[] splittedResult=this.getGenderAndInjuries(combinedValueOfOccupant);
										if(splittedResult.length>0){
											gender=splittedResult[0];
											injuries=splittedResult[1];
										}
									}
									else if(parsingPage.get(unitOneOccupantIndex+1).equals("see above")){
										String combinedValueOfOccupant1=parsingPage.get(unitOneOccupantIndex+2);
										String[] splittedResult1=this.getGenderAndInjuries(combinedValueOfOccupant1);
										if(splittedResult1.length>0){
											gender=splittedResult1[0];
											injuries=splittedResult1[1];
										}
									}
								}
							}
						}
					}
					
					
					// Vehicle Owner Details
					Integer ownerIndex=this.getIndexWithPosition(parsingArray, "Owner", i);
					if(ownerIndex!=-1){
						if(!parsingPage.get(ownerIndex-1).equals("Same as Driver?")){
							if(parsingPage.get(ownerIndex-2).equals("Same as Driver?"))
								ownerName=this.formatName(parsingPage.get(ownerIndex-1));
							else if(parsingPage.get(ownerIndex-3).equals("Same as Driver?"))
								ownerName=this.formatName(parsingPage.get(ownerIndex-2)+" "+parsingPage.get(ownerIndex-1));
						}
						
						if(!parsingPage.get(ownerIndex-2).equals("Same as Driver?")&&!parsingPage.get(ownerIndex-2).equals("Address")){
							if(parsingPage.get(ownerIndex-3).equals("Same as Driver?")){
								ownerAddress=this.getOwnerAddress(parsingPage.get(ownerIndex-4));
							}else{
								ownerAddress=this.getOwnerAddress(parsingPage.get(ownerIndex-2));
							}
							
						}
						else if(!parsingPage.get(ownerIndex-3).equals("Address")&&!parsingPage.get(ownerIndex-3).contains("City")){
							ownerAddress=this.getOwnerAddress(parsingPage.get(ownerIndex-3));
						}
						
						// City State Zip
						if(parsingPage.get(ownerIndex-3).contains("City")&&!parsingPage.get(ownerIndex-3).equals("City")){
							String[] ownerCityStateZip=this.getOwnerCityStateZip(parsingPage.get(ownerIndex-3));
							ownerAddress=ownerAddress+", "+ownerCityStateZip[0]+", "+ownerCityStateZip[1]+", "+ownerCityStateZip[2];
						}
						else if(parsingPage.get(ownerIndex-4).contains("City")&&!parsingPage.get(ownerIndex-4).equals("City")){
							String[] ownerCityStateZip=this.getOwnerCityStateZip(parsingPage.get(ownerIndex-4));
							ownerAddress=ownerAddress+", "+ownerCityStateZip[0]+", "+ownerCityStateZip[1]+", "+ownerCityStateZip[2];
						}else if(parsingPage.get(ownerIndex-5).contains("City")&&!parsingPage.get(ownerIndex-5).equals("City")){
							String[] ownerCityStateZip=this.getOwnerCityStateZip(parsingPage.get(ownerIndex-5));
							ownerAddress=ownerAddress+", "+ownerCityStateZip[0]+", "+ownerCityStateZip[1]+", "+ownerCityStateZip[2];
						}
						
						if(parsingPage.get(ownerIndex-3).equals("City")||parsingPage.get(ownerIndex-4).equals("City")||parsingPage.get(ownerIndex-5).equals("City"))
						{
						//Logic for fetching city state zipcode in Greenville like departments
						String[] convertedArray=parsingPage.toArray(new String[0]);
						String[] SlicedArray=Arrays.copyOfRange(convertedArray, ownerIndex-15, ownerIndex);
						String state="";
						String city="";
						String zip="";
						for(int k=0;k<SlicedArray.length;k++){
							if(SlicedArray[k].equals("City")){
								if(!SlicedArray[k-1].equals("")&&SlicedArray[k-1].equals("State")){
									city="";
								}
								else if(!SlicedArray[k-1].equals("")){
									city=SlicedArray[k-1]+", ";
								}
							}
							if(SlicedArray[k].equals("State")){
								if(!SlicedArray[k-1].equals("")&&SlicedArray[k-1].equals("Zip")){
									state="";
								}
								else if(!SlicedArray[k-1].equals(""))
								{
									state=SlicedArray[k-1]+", ";
								}
							}
							if(SlicedArray[k].equals("Zip")){
								if(!SlicedArray[k-1].equals("")&&!Character.isAlphabetic(SlicedArray[k-1].charAt(0))){
									zip=SlicedArray[k-1];
								}
							}
						}
						ownerAddress=ownerAddress+city+state+zip;
						}
					}
					
					// Vehicle Make
					Integer vehicleMakeIndex=this.getIndexWithPosition(parsingArray, "Make", i);
					if(vehicleMakeIndex!=-1){
						if(!parsingPage.get(vehicleMakeIndex+1).equals("Vehicle ")){
							vehicleMake=parsingPage.get(vehicleMakeIndex+1);
						}
					}
					
					// Vehicle Year
					if(parsingPage.get(vehicleMakeIndex+3).equals("Year")){
						if(!parsingPage.get(vehicleMakeIndex+4).equals("41 Vehicle ")){
							String[] yearAndType=this.getVehicleYear(parsingPage.get(vehicleMakeIndex+4));
							vehicleYear=yearAndType[0];
							vehicleType=yearAndType[1];
						}
					}else if(parsingPage.get(vehicleMakeIndex+2).equals("Year")){
						if(!parsingPage.get(vehicleMakeIndex+3).equals("41 Vehicle ")){
							String[] yearAndType=this.getVehicleYear(parsingPage.get(vehicleMakeIndex+3));
							vehicleYear=yearAndType[0];
							vehicleType=yearAndType[1];
						}
					}
					
					// Vehicle VIN
					String VINValue=parsingPage.get(vehicleMakeIndex-5);
					if(VINValue.contains("Plate #")){
						VIN=this.getVIN(parsingPage.get(vehicleMakeIndex-4));
					}else{
						VIN=this.getVIN(VINValue);
					}
					
					// Vehicle Policy #
					Pattern vinpattern = Pattern
							.compile("(?<!\\w)VIN(?!\\w)");
					String policyValue=parsingPage.get(vehicleMakeIndex-4);
					Matcher matcher = vinpattern.matcher(policyValue);
					if(matcher.find()){
						policyValue=this.getPolicyNumber(parsingPage.get(vehicleMakeIndex-3));
					}else{
						policyValue=this.getPolicyNumber(policyValue);
					}
					
					// Plate #
					Pattern statepattern = Pattern
							.compile("(?<!\\w)State(?!\\w)");
					String plateNumberValue=parsingPage.get(vehicleMakeIndex-6);
					Matcher statematcher = statepattern.matcher(plateNumberValue);
					if(statematcher.find()){
						plateNumber=this.getPlateNumber(parsingPage.get(vehicleMakeIndex-5));
					}else{
						plateNumber=this.getPlateNumber(plateNumberValue);
					}
					//Vehicle Damage
					Integer vehicleDamage=this.getIndexWithPosition(parsingArray, "Damage", i);
					if((parsingPage.get(vehicleDamage+1).contains("$")))
					{
						estimatedPropertyDamage=parsingPage.get(vehicleDamage+1);	
					}
				
					String insuranceCompanyValue=parsingPage.get(vehicleMakeIndex-3);
					if(insuranceCompanyValue.contains("Policy #")){
						insuranceCompanyValue="";
					}
					
					// Assign Insurance and Policy 
					if(unitInError.equals(unitNumber)){
						if(faultInsurance.equals("")){
							faultInsurance=insuranceCompanyValue;
							victimInsurance=insuranceCompanyValue;
						}
						if(faultPolicy.equals("")){
							faultPolicy=policyValue;
							victimPolicy=policyValue;
						}
					}else{
						victimInsurance=insuranceCompanyValue;
						victimPolicy=policyValue;
					}
					
					
					
					// Check Unit Data is Same for all pages
					boolean checkUnitDataSame=false;
					for (PDFParserUnitData pdfParserUnit : pdfParserUnitDatas) {
						if(pdfParserUnit.getName().equals(lastName+", "+firstName+", "+middleName)&&pdfParserUnit.getUnitNumber().equals(unitNumber)){
							checkUnitDataSame=true;
						}
							
					}
					if(!checkUnitDataSame){
						PDFParserUnitData parserUnitData = new PDFParserUnitData(lastName+", "+firstName+", "+middleName, dob, phoneNumber, "", gender, address, unitNumber, seatingPosition, injuries,victimInsurance,victimPolicy,"","");
						pdfParserUnitDatas.add(parserUnitData);
						
					}
					
					// Get Additional Occupants
					if(i%2==0){
						pdfParserUnitDatas.addAll(this.getAdditionalOccupantDetails(parsingPage,parsingArray,Integer.toString(i),pdfParserUnitDatas));
					}
					
					// check Vehicle Data Is Same for All Pages
					boolean checkVehicleDataSame=false;
					for (PDFParserVehicleData pdfParserVehicle : pdfParserVehicleDatas) {
						if(pdfParserVehicle.getOwnerName().equals(ownerName)&&pdfParserVehicle.getVIN().equals(VIN)){
							checkVehicleDataSame=true;
						}
							
					}
					
					if(!checkVehicleDataSame){
						// Compare Name with Unit Details and Get Phone Number from unit data
						String ownerPhoneNumber="";
						if(ownerName.equals(lastName+", "+firstName+", "+middleName)){
							ownerPhoneNumber=phoneNumber;
						}
						if(!vehicleType.equals("24")){
							PDFParserVehicleData pdfParserVehicleData = new PDFParserVehicleData(unitNumber, ownerName, ownerAddress, ownerPhoneNumber, vehicleMake, vehicleYear, vehicleModel, vehicleType, VIN, plateNumber, victimInsurance, victimPolicy,estimatedPropertyDamage,"");
							pdfParserVehicleDatas.add(pdfParserVehicleData);
						}
					}
				}
				
				// Reporting Agency
				String reportingAgencyValue=content.get(1).get(content.get(1).indexOf("Department")+1);
				if(!reportingAgency.equals("Date of Report")){
					String[] reportingAgencyAndCode=this.getReportingAgencyAndCode(reportingAgencyValue);
					reportingAgency=reportingAgencyAndCode[0];
					reportingAgencyCode=reportingAgencyAndCode[1];
				}
				
				pdfParserData.setCrashDate(crashDate);
				pdfParserData.setCounty(county);
				pdfParserData.setTimeOfCrash(crashTime);
				pdfParserData.setLocalReportNumber(reportNumber);
				pdfParserData.setNumberOfUnits(numberOfUnits.toString());
				pdfParserData.setReportingAgency(reportingAgency);
				pdfParserData.setReportingAgencyCode(reportingAgencyCode);
				pdfParserData.setUnitInError(unitInError);
				pdfParserData.setAtFaultInsuranceCompany(faultInsurance);
				pdfParserData.setAtFaultPolicyNumber(faultPolicy);
				pdfParserData.setVictimInsuranceCompany(victimInsurance);
				pdfParserData.setVictimPolicyNumber(victimPolicy);
				pdfParserData.setPdfParserUnitDatas(pdfParserUnitDatas);
				pdfParserData.setPdfParserVehicleDatas(pdfParserVehicleDatas);
				
				return pdfParserData;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	// Get Additional Occupant Details
	public List<PDFParserUnitData> getAdditionalOccupantDetails(List<String> parsingPage,String[] parsingArray,String labelUnitNumber,List<PDFParserUnitData>pdfParserUnitDatass){
		List<PDFParserUnitData> pdfParserUnitDatas = new ArrayList<PDFParserUnitData>();
		// Check For Occupant Details
		Integer beforeIndex=parsingPage.indexOf("Unit 2-Drv 2, Ped 2, etc. ");
		Integer occupantCount=0;
		for (int j = 2; j <= 7; j++) {
			if(parsingPage.get(beforeIndex+j).equals("see above")){
				break;
			}
			else if(parsingPage.get(beforeIndex+1).equals("see above") && parsingPage.get(beforeIndex+j+1).equals("see above")){
				if(!Character.isDigit(parsingPage.get(beforeIndex+2).charAt(0))){
					break;
				}
				else if(Character.isDigit(parsingPage.get(beforeIndex+2).charAt(0))){
					occupantCount++;
					continue;
				}
			}
			occupantCount++;
		}
		
		// Additional Occupant Details
		Integer occupantDetailsIndex=-1;
		String resultValue=this.getValueByStartWith(parsingArray, "Veh# ", 2);
		if(!resultValue.equals("")){
			occupantDetailsIndex=this.getIndexWithPosition(parsingArray, resultValue, 1);
		}else{
			resultValue=this.getValueByStartWith(parsingArray, "Veh# "+labelUnitNumber+" Towed To/By: ", 1);
			occupantDetailsIndex=this.getIndexWithPosition(parsingArray, resultValue, 1);
		}
		//Integer occupantDetailsIndex=this.getIndexWithPosition(parsingArray, "Veh#  Towed To/By:", 1)==-1?this.getIndexWithPosition(parsingArray, "Veh# "+labelUnitNumber+" Towed To/By: ", 1):-1;
		int nameCheckIndex=1;
		int nameCount=0;
		while(true){
			String indexValue=parsingPage.get(occupantDetailsIndex+nameCheckIndex);
			if(!indexValue.contains("46 Name of EMS")&&!indexValue.contains("47 Injured Taken")&&!indexValue.contains("by EMS to")){
				// Check Additional Value of Index
				if(!InjuryConstants.isAdditionalAddressValue(indexValue))
					nameCount++;
			}else{
				break;	
			}
			nameCheckIndex++;
		}
		int innerIndex=1;
		// Check For Vehicle Towed by content More than one line
		if(nameCount>(occupantCount*2)){
			innerIndex=innerIndex+1;
		}
		
		for (int k = 1; k <=occupantCount; k++) {
			String name="";
			String address="";
			String combinedOccupantValue=parsingPage.get(beforeIndex+(k+1));
			if(parsingPage.get(beforeIndex+1).equals("see above")&&!Character.isDigit(parsingPage.get(beforeIndex+2).charAt(0))){
				combinedOccupantValue=parsingPage.get(beforeIndex+(k+2));
			}
			String[] splitDetails=combinedOccupantValue.split(" ");
			String unitNumber=splitDetails[0];
			/*if (!unitNumber.matches("[1-9]+")) 
			{
			String combinedOccupantValue1=parsingPage.get(beforeIndex-(k));
			String[] splitDetails2=combinedOccupantValue1.split(" ");
			unitNumber=splitDetails2[0];
			}*/
			String dob="";
			String seatingPosition="";
			String[] genderInjuries={"",""};
			if(splitDetails.length>3){
				dob=splitDetails[3];
				seatingPosition=this.getSeatingPosition(combinedOccupantValue.substring(0,6));
				genderInjuries=this.getGenderAndInjuries(combinedOccupantValue.substring(combinedOccupantValue.indexOf("REDACTED")+1));
			}
			
			
			/*if(k!=1){
				innerIndex=k+(k-1);
			}*/
			if(occupantDetailsIndex!=-1){
				// Name
				if(!parsingPage.get(occupantDetailsIndex+innerIndex).contains("46 Name of EMS")&&!parsingPage.get(occupantDetailsIndex+innerIndex).contains("47 Injured Taken")&&!parsingPage.get(occupantDetailsIndex+innerIndex).contains("by EMS to")){
					if(parsingPage.get(occupantDetailsIndex+innerIndex).contains(",")){
						String inputValue=parsingPage.get(occupantDetailsIndex+innerIndex);
						// Check Additional Value of Index
						if(InjuryConstants.isAdditionalAddressValue(inputValue)){
							innerIndex=+innerIndex+1;
						}
						// Check Last Index Space is available or not
						if(inputValue.lastIndexOf(" ")!=-1){
							// Check Last Index Space value greater than Comma
							if(inputValue.lastIndexOf(",")+1<=inputValue.lastIndexOf(" ")-1){
								if(isZipcode(inputValue.substring(inputValue.lastIndexOf(" ")+1, inputValue.length()))||isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
									address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+innerIndex));
									innerIndex=innerIndex+1;
								}else{
									name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
									address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
									innerIndex=innerIndex+2;
								}
							}
							else{
								name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
								address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
								innerIndex=innerIndex+2;
							}
						}else{
							if(isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
								address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+innerIndex));
								innerIndex=innerIndex+1;
							}else{
								name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
								address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
								innerIndex=innerIndex+2;
							}
						}
					}else if(parsingPage.get(occupantDetailsIndex+innerIndex).length()<=2){
						innerIndex=innerIndex+1;
						if(parsingPage.get(occupantDetailsIndex+innerIndex).contains(",")){
							String inputValue=parsingPage.get(occupantDetailsIndex+innerIndex);
							// Check Last Index Space is available or not
							if(inputValue.lastIndexOf(" ")!=-1){
								// Check Last Index Space value greater than Comma
								if(inputValue.lastIndexOf(",")+1<=inputValue.lastIndexOf(" ")-1){
									if(isZipcode(inputValue.substring(inputValue.lastIndexOf(" ")+1, inputValue.length()))||isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
										address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+innerIndex));
									}else{
										name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
										address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
									}
								}else{
									name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
									address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
								}
							}else{
								if(isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
									address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+innerIndex));
								}else{
									name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
									address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
								}
							}
							innerIndex=innerIndex+1;
							// Check Additional Value of Index
							if(InjuryConstants.isAdditionalAddressValue(parsingPage.get(occupantDetailsIndex+(innerIndex+1)))){
								innerIndex=+innerIndex+1;
							}
						}else{
							name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
							// Address
							if(!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("46 Name of EMS")&&!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("47 Injured Taken")&&!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("by EMS to")){
								address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
							}
							innerIndex=innerIndex+1;
							// Check Additional Value of Index
							if(InjuryConstants.isAdditionalAddressValue(parsingPage.get(occupantDetailsIndex+(innerIndex+1)))){
								innerIndex=+innerIndex+1;
							}
						}
					}else{
						name=this.formatName(parsingPage.get(occupantDetailsIndex+innerIndex));
						// Address
						if(!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("46 Name of EMS")&&!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("47 Injured Taken")&&!parsingPage.get(occupantDetailsIndex+(innerIndex+1)).equals("by EMS to")){
							address=this.formatAdditionalOccupantAddress(parsingPage.get(occupantDetailsIndex+(innerIndex+1)));
						}
						innerIndex=innerIndex+2;
					}
					
				}
				
			}
			PDFParserUnitData parserUnitData = new PDFParserUnitData(name, dob, "", "", genderInjuries[0], address, unitNumber, seatingPosition, genderInjuries[1], "", "","","");			
			for(PDFParserUnitData pdfParserUnitData1:pdfParserUnitDatass){
				if(unitNumber.equals(pdfParserUnitData1.getUnitNumber())){
					parserUnitData.setInsuranceCompany(pdfParserUnitData1.getInsuranceCompany());
					parserUnitData.setPolicyNumber(pdfParserUnitData1.getPolicyNumber());
				}
			}
			pdfParserUnitDatas.add(parserUnitData);
		}
		
		return pdfParserUnitDatas;
	}
	
	public String getUnitInError(String[] parsingPageArray,List<String> parsingPage,String[] unitNumber){
		String unitInError="";
		//Unit in Error
		Integer unitInErrorIndex=this.getIndexWithPosition(parsingPageArray, "14", 1);
		Integer unitOneErrorValue=-1, unitTwoErrorValue=-1;
		
		//Check Unit One
		if(!parsingPage.get(unitInErrorIndex+1).equals("15")){
			unitOneErrorValue=Integer.parseInt(parsingPage.get(unitInErrorIndex+1));
		}else if(!parsingPage.get(unitInErrorIndex+2).equals("16"))
			unitOneErrorValue=Integer.parseInt(parsingPage.get(unitInErrorIndex+2));
		else if(!parsingPage.get(unitInErrorIndex+3).equals("17"))
			unitOneErrorValue=Integer.parseInt(parsingPage.get(unitInErrorIndex+3));
		else if(parsingPage.get(unitInErrorIndex+4).equals("17"))
			unitOneErrorValue=Integer.parseInt(parsingPage.get(unitInErrorIndex+3));
		
		
		Integer unitTwoErrorIndexBase=parsingPage.indexOf("33 Relation to ");
		Integer unitTwoErrorIndexStart=0;
		if(parsingPage.get(unitTwoErrorIndexBase-2).equals("12")){
			unitTwoErrorIndexStart=unitTwoErrorIndexBase-2;
		}else if(parsingPage.get(unitTwoErrorIndexBase-1).equals("12")){
			unitTwoErrorIndexStart=unitTwoErrorIndexBase-1;
		}
		
		
		if(!parsingPage.get(unitTwoErrorIndexStart-1).equals("19"))
			unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
		else if(parsingPage.get(unitTwoErrorIndexStart-1).equals("19")){
			if(parsingPage.get(unitTwoErrorIndexStart-2).equals("19")){
				unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
			}else{
				unitTwoErrorIndexStart=unitTwoErrorIndexStart-1;
			}
		}
		
		if(unitTwoErrorValue==-1){
			if(!parsingPage.get(unitTwoErrorIndexStart-1).equals("18")){
				unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
			}else if(parsingPage.get(unitTwoErrorIndexStart-1).equals("18")){
				if(parsingPage.get(unitTwoErrorIndexStart-2).equals("18")){
					unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
				}else{
					unitTwoErrorIndexStart=unitTwoErrorIndexStart-1;
				}
			}
		}
		
		if(unitTwoErrorValue==-1){
			if(!parsingPage.get(unitTwoErrorIndexStart-1).equals("17")){
				unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
			}else if(parsingPage.get(unitTwoErrorIndexStart-1).equals("17")){
				if(parsingPage.get(unitInErrorIndex-2).equals("17")){
					unitTwoErrorValue=Integer.parseInt(parsingPage.get(unitTwoErrorIndexStart-1));
				}else{
					unitTwoErrorIndexStart=unitTwoErrorIndexBase-1;
				}
			}
		}
		
		if(unitOneErrorValue!=0&&unitOneErrorValue!=-1){
			unitInError=unitNumber[0];
		}else if(unitTwoErrorValue!=0&&unitTwoErrorValue!=-1){
			unitInError=unitNumber[1];
		}
		
		return unitInError;
	}
	
	// Driver Name
	public String getDriverName(String inputName){
		String outputName="";
		String[] splittedName=inputName.split(" ");
		if(splittedName.length>1){
			outputName=splittedName[1];
		}
		else{
			outputName=splittedName[0];
			if(outputName.equals("Driver")){
				outputName="";
			}
		}
		return outputName;
	}
	
	// Local Report Number
	public String getLocalReportNumber(String inputNumber){
		String outputNumber="";
		String[] splittedNumber=inputNumber.split(" ");
		if(splittedNumber.length>0){
			if(splittedNumber[0].matches("[a-zA-Z]+")){
				outputNumber=inputNumber;
			}else{
				outputNumber=splittedNumber[0];
			}
		}
		return outputNumber;
	}
	
	// Driver Address
	public String getDriverAddress(String inputAddress){
		String outputAddress="";
		if(inputAddress.length()>9){
			if(inputAddress.substring(0,8).equals("Address "))
			{
				outputAddress=inputAddress.substring(8,inputAddress.length());
			}
			else{
				outputAddress=inputAddress.substring(7,inputAddress.length());
			}
		}
		
		/*String[] splittedAddress=inputAddress.split(" ");
		if(splittedAddress.length>0){
			outputAddress=splittedAddress[1];
		}*/
		return outputAddress;
	}
	
	// Driver City State Zip
	public String getDriverCityStateZip(String inputCityStateZip){
		String outputCityStateZip="";
		if(!inputCityStateZip.startsWith("City")){
			outputCityStateZip=inputCityStateZip;
		}
		else if(inputCityStateZip.startsWith("City")&&inputCityStateZip.length()>15){
			outputCityStateZip=inputCityStateZip.substring(15, inputCityStateZip.length());
		}
			String[] splittedCityStateZip= new String[]{"","","",""};
			if(outputCityStateZip.length()>0){
				if((outputCityStateZip.lastIndexOf(" ")-3)>=0)
					splittedCityStateZip[0]=outputCityStateZip.substring(0,outputCityStateZip.lastIndexOf(" ")-3);
				if((outputCityStateZip.lastIndexOf(" ")>=0)&&((outputCityStateZip.lastIndexOf(" ")-2)<=(outputCityStateZip.lastIndexOf(" "))))
				    splittedCityStateZip[1]=outputCityStateZip.substring(outputCityStateZip.lastIndexOf(" ")-2,outputCityStateZip.lastIndexOf(" "));
				
				splittedCityStateZip[2]=outputCityStateZip.substring(outputCityStateZip.lastIndexOf(" ")+1);
			}
			
			if(splittedCityStateZip.length>=3){
				splittedCityStateZip[2]=this.formatZip(splittedCityStateZip[2]);
				outputCityStateZip=splittedCityStateZip[0]+", "+splittedCityStateZip[1]+", "+splittedCityStateZip[2];
			}else if(splittedCityStateZip.length==2){
				outputCityStateZip=splittedCityStateZip[0]+", "+splittedCityStateZip[1];
			}else if(splittedCityStateZip.length==1){
				outputCityStateZip=splittedCityStateZip[0];
			}
		
		
		
		return outputCityStateZip;
}
	
	
	// Format Zip
	public String formatZip(String inputZip){
		String outputZip="";
		if(inputZip.contains("-")){
			if(inputZip.substring(0, inputZip.indexOf("-")).length()>0){
				outputZip=inputZip.substring(0, inputZip.indexOf("-"));
			}
		}else{
			outputZip=inputZip;
		}
		
		return outputZip;
	}
	
	// Value Starts With in Array
	public String getValueByStartWith(String[] inputArray,String searchValue,Integer position){
		Integer indexPosition=0;
		if(position%2==0){
			indexPosition=1;
		}
		String outputValue="";
		List<String> resultList = Arrays.stream(inputArray)
                .filter(x -> x.startsWith(searchValue))
                .collect(Collectors.toList());
		if(!resultList.isEmpty()){
			if(resultList.size()==1){
				outputValue=resultList.get(0);
			}else if(resultList.size()>1){
				outputValue=resultList.get(indexPosition);
			}
		}
		return outputValue;
	}
	
	// get Index Position in Array
	public Integer getIndexWithPosition(String[] inputArray,String searchValue,Integer position){
		Integer indexPosition=1;
		if(position%2==0){
			indexPosition=2;
		}
		Integer outputValue=-1;
		if(indexPosition==1){
			outputValue = Arrays.asList(inputArray).indexOf(searchValue);
		}else if(indexPosition==2){
			outputValue = Arrays.asList(inputArray).lastIndexOf(searchValue);
		}
		return outputValue;
	}
	
	// Get DOB
	public String getDOB(String inputDOB){
		String outputDOB="";
		String[] splittedDOB=inputDOB.split(" ");
		if(splittedDOB.length>1){
			outputDOB=splittedDOB[1];
		}
		return outputDOB;
	}
	
	// Get Gender and Injuries
	public String[] getGenderAndInjuries(String inputValue){
		String[] resultValue=new String[2];
		String[] splittedArray=new String[0];
		if(inputValue.length()>10){
			if(inputValue.length()==15){
				splittedArray=inputValue.split(" ");
			}
			else{
				splittedArray=inputValue.substring(10, inputValue.length()).split(" ");		
			}
			if(splittedArray.length>=8){
				if(splittedArray[1].equals("F")||splittedArray[1].equals("M")||splittedArray[1].equals("U")){
					resultValue[0]=splittedArray[1];
				}
				resultValue[1]=splittedArray[splittedArray.length-1];
			}else if(splittedArray.length>=7){
				if(splittedArray[0].equals("F")||splittedArray[0].equals("M")||splittedArray[0].equals("U")){
					resultValue[0]=splittedArray[0];
				}
				resultValue[1]=splittedArray[splittedArray.length-1];
			}
		}
		return resultValue;
	}
	
	// Get Seating Position
	public String getSeatingPosition(String inputValue){
		String resultValue="";
		String[] splittedArray=inputValue.trim().split(" ");
		if(splittedArray.length>=3){
			resultValue=splittedArray[splittedArray.length-1];
		}
		return resultValue;
	}
	
	// Get Owner Address
	public String getOwnerAddress(String inputAddress){
		return inputAddress.substring(0,inputAddress.indexOf("Address")-1);
	}
	
	// Get Owner City State Zip
	public String[] getOwnerCityStateZip(String inputCityStateZip){
		String[] ownerCityStateZip={"","","",""};
		if(!inputCityStateZip.equals("Zip State City")){
			if((inputCityStateZip.indexOf("State")+6)<=(inputCityStateZip.indexOf("City")-1)){
				ownerCityStateZip[0]=inputCityStateZip.substring(inputCityStateZip.indexOf("State")+6,inputCityStateZip.indexOf("City")-1);
			}
			if((inputCityStateZip.indexOf("Zip")+4)<=(inputCityStateZip.indexOf("State")-1)){
				ownerCityStateZip[1]=inputCityStateZip.substring(inputCityStateZip.indexOf("Zip")+4,inputCityStateZip.indexOf("State")-1);
			}
			if(inputCityStateZip.indexOf("Zip")-1>=0){
				ownerCityStateZip[2]=this.formatZip(inputCityStateZip.substring(0,inputCityStateZip.indexOf("Zip")-1));
			}
		}
		return ownerCityStateZip;
	}
	
	// Get Vehicle Year
	public String[] getVehicleYear(String inputVehicleOwner){
		String[] outputVehicleOwner={"","",""};
		String[] splittedVehicleYear=inputVehicleOwner.split(" ");
		if(splittedVehicleYear.length>=2){
			outputVehicleOwner[0]=splittedVehicleYear[0];
			outputVehicleOwner[1]=splittedVehicleYear[1];
		}else if(splittedVehicleYear.length==1){
			if(Pattern.matches("[0-9]{4}", splittedVehicleYear[0].trim())){
				outputVehicleOwner[0]=splittedVehicleYear[0];
			}else{
				outputVehicleOwner[1]=splittedVehicleYear[0];
			}
		}
		return outputVehicleOwner;
	}
	
	// Get Plate Number
	public String getPlateNumber(String inputValue){
		String outputValue="";
		if(inputValue.length()>7){
			outputValue=inputValue.substring(0,inputValue.indexOf("Plate")-1);
		}
		return outputValue;
	}
	
	// Get VIN
	public String getVIN(String inputValue){
		String outputValue="";
		if(inputValue.length()>3){
			outputValue=inputValue.substring(0,inputValue.lastIndexOf("VIN")-1);
		}
		return outputValue;
	}
	
	// Get Policy Number
	public String getPolicyNumber(String inputValue){
		String outputValue="";
		if(inputValue.length()>8){
			outputValue=inputValue.substring(0,inputValue.indexOf("Policy")-1);
		}
		return outputValue;
	}
	
	// Get Reporting Agency and Code
	public String[] getReportingAgencyAndCode(String inputValue){
		String[] outputValue=new String[2];
		outputValue[0]=inputValue.substring(0, inputValue.lastIndexOf(" "));
		outputValue[1]=inputValue.substring(inputValue.lastIndexOf(" ")+1,inputValue.length());
		return outputValue;
	}
	
	// Format the Name
	public String formatName(String inputName){
		String outputValue="";
		if(!inputName.equals("")){
			String[] splittedName = inputName.split(" ");
			String firstName="";
			String lastName="";
			String middleName="";
			for (int i = 0; i < splittedName.length; i++) {
				if(i==0){
					firstName=splittedName[0];
				}else if(i==splittedName.length-1){
					if(InjuryConstants.getNamesSuffix().contains(splittedName[i].toLowerCase())){
						lastName=splittedName[splittedName.length-2]+" "+splittedName[splittedName.length-1];
						middleName=middleName.substring(0, middleName.lastIndexOf(" "));
					}else{
						lastName=splittedName[splittedName.length-1];
					}
				}else{
					middleName=middleName+" "+splittedName[i];
				}
			}
			outputValue=lastName+", "+firstName+", "+middleName.trim();
		}
		return outputValue;
	}
	
	// Get Unit number From occupant Details
	public String getUnitNumberFromOccupant(String inputValue){
		String resultValue="";
		String[] splittedArray=inputValue.split(" ");
		if(splittedArray.length>=1){
			resultValue=splittedArray[0];
		}
		return resultValue;
	}
	
	// Get Search Result String with Starting
	public String getResultByStartWith(String[] inputArray,String searchValue,Integer position){
	
		String outputValue="";
		List<String> resultList = Arrays.stream(inputArray)
                .filter(x -> x.startsWith(searchValue))
                .collect(Collectors.toList());
		if(!resultList.isEmpty()){
			outputValue=resultList.get(0);
		}
		return outputValue;
	}
	
	// Get Unit Numbers in Single Page
	public String[] getUnitNumbersInSinglePage(String[] parsingArray,List<String> parsingPage,int i){
		String[] unitNumber=new String[]{"",""};
		for (int j = 0; j < 2; j++) {
			String unitValue=this.getValueByStartWith(parsingArray,"UNIT #",i);
			if(unitValue.equals("UNIT #")){
				Integer firstUnitIndex=this.getIndexWithPosition(parsingArray,"UNIT #",i);
				if(firstUnitIndex!=-1){
					unitNumber[j]=parsingPage.get(firstUnitIndex+1);
				}
			}
			else if(unitValue.indexOf("PEDESTRIAN")!=-1)
			{
				Integer index=unitValue.indexOf("#");
				unitNumber[j]=Character.toString(unitValue.charAt(index+2));
				if(!unitNumber[j].matches("[1-9]+")){
					unitNumber[j]="";
				}}
			i++;
		}
		return unitNumber;
	}
	
	// Format Additional Occupant Address
	public String formatAdditionalOccupantAddress(String inputValue){
		String outputValue="";
		if(inputValue.lastIndexOf(" ")!=-1){
			// Check Last Index Space value greater than Comma
			if(isZipcode(inputValue.substring(inputValue.lastIndexOf(" ")+1, inputValue.length()))){
				outputValue=inputValue.substring(0, inputValue.lastIndexOf(" "))+", "+inputValue.substring(inputValue.lastIndexOf(" ")+1, inputValue.length());
			}else if(inputValue.lastIndexOf(",")+1<=inputValue.lastIndexOf(" ")-1){
				if(isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
					outputValue=inputValue;
				}
			}else{
				outputValue=inputValue;
			}
		}else if(isState(inputValue.substring(inputValue.lastIndexOf(",")+1, inputValue.lastIndexOf(" ")-1))){
			outputValue=inputValue;
		}
		return outputValue;
	}
	
	//Check for Zipcode
	public boolean isZipcode(String checkStr){
		if(checkStr.matches("\\d+")&&checkStr.length()==5){
			return true;
		}else{
			return false;
		}		
	}
	
	//Check for State
	public boolean isState(String checkStr){
		if(checkStr.matches("[a-zA-Z]+")&&checkStr.length()==2){
			return true;
		}else{
			return false;
		}
	}
}
