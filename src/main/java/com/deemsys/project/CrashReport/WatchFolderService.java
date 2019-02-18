package com.deemsys.project.CrashReport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.deemsys.project.common.InjuryConstants;
import com.deemsys.project.pdfparser.PDFParserData;
import com.deemsys.project.pdfparser.PDFParserOne;
import com.deemsys.project.pdfreader.PDFCrashReportReader;

@Component
public class WatchFolderService {

	@Autowired
	PDFParserOne pdfParserOne;

	@Autowired
	PDFCrashReportReader pdfCrashReportReader;

	@Scheduled(fixedDelay = Long.MAX_VALUE)
	public void process() throws Exception {
		
		WatchService watchService;
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get("C:\\wamp\\www\\CRO-Shared\\DownloadFolder");

			path.register(watchService, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

			WatchKey watchKey;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					String fileName = event.context().toString();
					System.out.println("Incoming File...!");
					//Renaming to pdf on deleting Part file
					if (event.kind().toString().equals("ENTRY_DELETE")
							&& fileName.substring(fileName.lastIndexOf(".") + 1).equals("part")) {
						File file = new File("C:\\wamp\\www\\CRO-Shared\\DownloadFolder\\" + fileName.substring(0, fileName.lastIndexOf(".")));
						File newfile = new File("C:\\wamp\\www\\OHReports\\" + UUID.randomUUID() + ".pdf");
						file.renameTo(newfile);
						PDFParserData pdfParserData = pdfParserOne.parsePdfFromFile(newfile);
						RunnerCrashReportForm runnerCrashReportForm = new RunnerCrashReportForm("", "",pdfParserData.getLocalReportNumber(), pdfParserData.getCrashDate(),pdfParserData.getCounty(), "http://localhost/OHReports/" + newfile.getName(), 0, "", null);
						pdfCrashReportReader.saveRunnerDirectOrScannedReport(runnerCrashReportForm,pdfParserData);
						// write local number to json file
						this.writeJsonFile(pdfParserData);
						//Remove left over files on modify event
					} else if (event.kind().toString().equals("ENTRY_MODIFY")
							&& !fileName.substring(fileName.lastIndexOf(".") + 1).equals("part")) {
						File file = new File("C:\\wamp\\www\\CRO-Shared\\DownloadFolder\\" + fileName);
						if (file.length() == 0) {
							file.delete();
						}
					}

				}
				watchKey.reset();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void writeJsonFile(PDFParserData pdfParserData) throws org.json.simple.parser.ParseException {
		

		//Framing file name based on county Id and current Date
		String JsonFilePath = "C:\\wamp\\www\\CRO-Shared\\JsonFolder\\input_" + pdfParserData.getCounty() + "_"
				+ pdfParserData.getCrashDate().replaceAll("/", "") + ".json";
		File jsonFile = new File(JsonFilePath);
		// Create new file if not exists
		if (!jsonFile.exists()) {
			//{"ReportNo":["123456"]}  achieving this structure
			JSONArray reportsArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			//Suppressed
			reportsArray.add(pdfParserData.getLocalReportNumber());
			jsonObject.put("ReportNo", reportsArray);
			// Write JSON file
			try (FileWriter fileWriter = new FileWriter(jsonFile)) {

				fileWriter.write(jsonObject.toJSONString());
				fileWriter.flush();
				fileWriter.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// update already available file
		else {
			JSONParser jsonParser = new JSONParser();
			try {
				//{"ReportNo":["123456",""98765]}  achieving this structure
				Object obj = jsonParser.parse(new FileReader(jsonFile));
				JSONObject jsonObj = (JSONObject) obj;
				JSONArray jsonArray = (JSONArray) jsonObj.get("ReportNo");

				jsonArray.add(pdfParserData.getLocalReportNumber());
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ReportNo", jsonArray);
				FileWriter fileWriter = new FileWriter(jsonFile);
				fileWriter.write(jsonObject.toJSONString());
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
