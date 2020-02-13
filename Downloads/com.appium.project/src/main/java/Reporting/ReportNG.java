package Reporting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.rmi.server.UID;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import Library.ConfigFile;
import org.testng.annotations.Test;

public class ReportNG extends ConfigFile implements ITestListener {
	Logger logger=Logger.getLogger("HAC");
	public static List<String> AllList = new ArrayList<String>();
	public static List<String> PassedList = new ArrayList<String>();
	public static List<String> FailedList = new ArrayList<String>();
	public static List<String> skippedList = new ArrayList<String>();
	public static List<String> ClassList = new ArrayList<String>();
	public static List<String> FailedClassList = new ArrayList<String>();
	public static List<String> bufferList = new ArrayList<String>();
	public static String ClassName;
	private static Writer bufWriter1, bufWriter2;
	private File BufDirectory, DetailDirectory;
	public static Date StartTime,EndTime;
	public static Date lastStepExecutionTime;
	public boolean firstStep = true;
	public boolean FailFlag = false;
	String PassPath = System.getProperty("user.dir") + "/test-output/PassScreenshot/";
	String FailPath = System.getProperty("user.dir") + "/test-output/FailScreenshot/";
	public ReportNG(){
		PropertyConfigurator.configure("Log4j.properties");
	}

	public void CreateFilesHTML(){
		StartTime = getDateFormat();
		try{
			if(reportingFormat.contains("Complex")){
				CreateBufferFile();
				CreateDetailsFile();
			}else if(reportingFormat.contains("Simple")){
				CreateSimpleFormatFile();
				createSimpleTemplate();
			}
		}catch(Exception e){
			logger.info("Failed to create HTML files due to exception "+e.getMessage());
		}
	}

	public void StartReport(){
		//CreateFilesExtent();
		CreateFilesHTML();
	}

	public void InitiateMethodLevelReport(Method method,String classname){
		System.out.println("------------------------------------"+method.getName()+"------------------------------------");
		if(reportingFormat.contains("Complex")){
			bufferList.add("0");
			Test test = method.getAnnotation(Test.class);
			String testDescription = test.description();
			if(testDescription.length() > 0) {
				testDescription = testDescription.replace('.', ' ');
				testDescription = testDescription.replace('/', '|');
			} else{
				testDescription = method.getName();
			}

			UID methodId = new UID();
			String uniqueMethodName = method.getName()+"/"+testDescription+"/"+methodId;
			verifyTestCase(uniqueMethodName);
		}else if(reportingFormat.contains("Simple")){
			addMethodNameInSimpleReport(method.getName());
		}
	}

	public void CreateDetailsFile() {
		try{
			String name = "ExecutionReport";
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			DetailDirectory = new File(String.valueOf("./test-output/HtmlReport"));
			if(!DetailDirectory.exists())
				DetailDirectory.mkdir();
			filePath = "./test-output/HtmlReport/" + name + timestamp.getTime()+ ".html";
			openDetailsFile(filePath);
		}catch(Exception e){
			logger.error("Fail to CreateDetailsFile due to exception "+e.getMessage());
		}
	}

	public void CreateBufferFile(){
		try{
			String name = "ExecutionReport";
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			BufDirectory = new File(String.valueOf("./test-output/BufferReport"));
			if(!BufDirectory.exists())
				BufDirectory.mkdir();
			BufferfilePath = "./test-output/BufferReport/" + name + timestamp.getTime()+ ".html";
			openBufferFile(BufferfilePath);
		}catch(Exception e){
			logger.error("Fail to CreateBufferFile due to exception "+e.getMessage());
		}
	}

	public void openBufferFile(String BufferfilePath) {
		try {
			Writer fileWriter = new FileWriter(BufferfilePath,true);
			bufWriter2 = new BufferedWriter(fileWriter);
		} catch (Exception e) {
			logger.error("Exception caught while trying to open a file "+e);
		}
	}

	public void openDetailsFile(String filePath) {
		try {
			Writer fileWriter = new FileWriter(filePath,true);
			bufWriter1 = new BufferedWriter(fileWriter);
		} catch (Exception e) {
			logger.error("Exception caught while trying to open a file "+e);
		}
	}

	public void writeTitle(){
		String title = "FRESHDIRECT REPORT";
		writeDetails("<h1>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<u>"+title+"</u></h1>");
	}

	public void createReport(String scriptName){
		try{
//			writeDetails("<html><head><style>");
//			writeDetails(stylingTemplate());
//			writeDetails("</style></head><body>");
			testCaseDetailsTemplateNew(scriptName);
		}catch(Exception e){
			logger.error("Fail to createReport due to exception "+e.getMessage());
		}
	}

	public void createSummaryReport(){ 
		try{
			writeSummary("<html><head><style>"); // Html Started
			writeSummary(stylingTemplateNew()); // Style
			writeSummary("</style>"); // end of style
			writeSummaryChart(); //Pie Chart Scrip and Pie chart

			testCaseSummaryTemplateNew();
			//writeDetails("<br style = \"line-height:1;\"><br>");

			logSummaryNew();
		}catch(Exception e){
			logger.error("Fail to create Summary Report due to exception "+e.getMessage());
		}
	}

	private void logSummaryNew(){
		try{
			String result = null;
			String color = null;
			int cls = 0;
			int met = 0;
			for (int i=0; i<bufferList.size(); i++){
				if(bufferList.get(i).equals("1")){
					String TestClassName = ClassList.get(cls);
					cls=cls+1;

					writeSummary("<tr><td colspan='2' class='passFail className'><a href=\"#"  +TestClassName+"\"> "+TestClassName+" </a></td></tr>");

				}else if(bufferList.get(i).equals("0")){
					String TestCaseName = AllList.get(met);
					met=met+1;
					if(TestCaseName!=null){
						for(String scriptname : PassedList){
							if(scriptname.equals(TestCaseName)){
								result = "&#10004;";
								color = "right";
								break;
							}
						}
						for(String skip : skippedList){
							if(skip.equals(TestCaseName)){
								result = "&#8252;";
								color = "skipped";
								break;
							}
						}

						for(String fail : FailedList){
							if(fail.equals(TestCaseName)){
								result = "&#10006;";
								color = "wrong";
								break;
							}
						}
					}

					String fullMethodInformation = TestCaseName.split("\\.")[1];
					String methodName = fullMethodInformation.split("/")[0];
					String methodDescription = fullMethodInformation.split("/")[1];
					String methodID = fullMethodInformation.split("/")[2];
					/*
					System.out.println("fullMethodInformation " + fullMethodInformation);
					System.out.println("methodName " + methodName);
					System.out.println("methodDescription " + methodDescription);
					System.out.println("methodID " + methodID);
					*/
					writeSummary("<tr><td class='"+color+"'>"+result+"</td><td><a href=\"#"+methodName+"/"+methodID+"\">"+methodDescription+"</a></td></tr>");

				}
			}
			writeSummary("</table><table id='methodSummary'><tr><th colspan='7'> Details Report</th></tr>");
		}
		catch(Exception e){
			logger.error("Fail to log summary due to exception "+e.getMessage());
		}
	}

	private void logSummary(){
		try{
//			testFailedList();
//			listOfAllTestCases();
//			listOfPassedTestCases();
//			ListOfAllClasses();
//			listOfAllFailedClasses();
			String result = null;
			String color = null;
			int cls = 0;
			int met = 0;
			for (int i=0; i<bufferList.size(); i++){
				if(bufferList.get(i).equals("1")){
					String TestClassName = ClassList.get(cls);
					cls=cls+1;
					String Testclass = ("<th class=clsInSummary bgcolor = \"#ffffff\"><b><a href=\"#"+TestClassName+"\">"
							+""+TestClassName+"</a></b></th>");
					String resultLog = ("<td bgcolor = \"#ffffff\"></td>");
					String summarylogs = "<tr><b>"+Testclass+resultLog+"</b></tr>";
					writeSummary(summarylogs);
				}else if(bufferList.get(i).equals("0")){
					String TestCaseName = AllList.get(met);
					met=met+1;
					if(TestCaseName!=null){
						for(String scriptname : PassedList){
							if(scriptname.equals(TestCaseName)){
								result = "Pass";
								color = "#47d147";
								break;
							}
						}
						if((result==null)&&(color==null)){
							result = "Skipped";
							color = "#f2f551";
						}
						for(String fail : FailedList){
							if(fail.equals(TestCaseName)){
								result = "Fail";
								color = "#ff3333";
								break;
							}
						}
					}

					String fullMethodInformation = TestCaseName.split("\\.")[1];
					String methodName = fullMethodInformation.split("/")[0];
					String methodDescription = fullMethodInformation.split("/")[1];
					String methodID = fullMethodInformation.split("/")[2];
					/*
					System.out.println("fullMethodInformation " + fullMethodInformation);
					System.out.println("methodName " + methodName);
					System.out.println("methodDescription " + methodDescription);
					System.out.println("methodID " + methodID);
					*/
					String tstcasename = TestCaseName.split("\\.")[1];
					String removedIDTestCase = tstcasename.split("/")[0];
					String Testcase = ("<td><a href=\"#"+methodName+"/"+methodID+"\">"+methodDescription+"</a></td>");
					String resultLog = ("<td bgcolor = "+color+"><p>"+result+"</p></td>");
					String summarylogs = "<tr>"+Testcase+resultLog+"</tr>";
					writeSummary(summarylogs);
				}
			}
			writeSummary("</table>");
			writeSummary("<BR/><BR/>");

		}
		catch(Exception e){
			logger.error("Fail to log summary due to exception "+e.getMessage());
		}
	}

	private void testCaseDetailsTemplateNew(String testCaseName) {
		String methodName = testCaseName.split("/")[0];
		String methodDescription = testCaseName.split("/")[1];
		String methodID = testCaseName.split("/")[2];
		writeDetails("<tr><td width='2%'></td><td class='methodName' colspan='6' id='"+methodName+"/"+methodID+"'>" +methodDescription+ "</td></tr>");
		writeDetails("<tr class='detailsReport'><td width='2%'></td><td width='2%'></td><td width='2%'></td><td><b>Step</b></td>" +
				"<td ><b>Expected</b></td><td><b>Actual</b></td><td><b>Time</b></td></tr>");
	}


	private void testCaseDetailsTemplate(String testCaseName) {
		String methodName = testCaseName.split("/")[0];
		String methodDescription = testCaseName.split("/")[1];
		String methodID = testCaseName.split("/")[2];

		//writeDetails("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<table width=100%>");
		String[] splitTestCaseName = testCaseName.split("/");
		writeDetails("<th id="+methodName+"/"+methodID+" colspan=7>"+methodDescription+"</th>");
		writeDetails("<tr class=d0><td width=12%><b>Time</b></td><td width=20%><b>Step</b></td>"
				+ "<td width=30%><b>Expected</b></td><td wisth=30%><b>Actual</b></td><td width=8%><b>Result</b></td></tr>");
	}

	private void testCaseSummaryTemplate() {
		writeSummary("<table style=\"float: left; width: 100%\">");
		writeSummary("<th bgcolor=\"#0000e6\" style=\"color:white\" colspan=7><h2 class=summaryheader>SUMMARY</h2></th>");
		writeSummary("<tr class=d0><td width=50%><b>Test Cases</b></td>"
				+ "<td width=50%><b>Result</b></td></tr>");
	}

	private void testCaseSummaryTemplateNew() {
		writeSummary("<table id='classSummary'><tr><th colspan='2'> Summary</th></tr>");
	}

	private String stylingTemplate() {
		String str = "body {background-color: #ffffff;  border: 2px solid grey; border-radius: 5px}";
		str = str + "table {background-color: #d9d9d9; text-align: center;}";
		str += "th { background-color: #19b9e5;text-align: center;  color: #000000; font-family: Candara, Calibri;font-size: 20px;}";
		str += "th.beforeTest { background-color: #0086b3; text-align: center; color: #ffffff; font-family: Candara, Calibri;font-size: 20px;}";
		str += "h2.summaryheader { background-color: #004994;text-align: center;  color: #ffffff; font-family: Candara, Calibri;font-size: 20px;}";
		str += "h2.exesum {background-color:#ffffff; text-align:left; color: #000000; font-size:23px;}";
		str += "tr { background-color: #E6E6E6; color: #1F1F7A;  font-family:Calibri; font-size: 15px;}";
		str += "tr.d0 td {background-color:#ffffff; height:30px}";
		str += "p {color: #000000 }";
		str += "h1 { align=center; text-align: center;color: #000000; font-family:  Candara,Calibri;font-size: 28px; }";
		str += "h2 { align=center; text-align: center;background-color: #004994; color: #000000; font-family:  Candara,Calibri;font-size: 28px; }";
		str += "table.exeSumTable {background-color: #ffffff; text-align: center;}";
		str += "h3 {text-align=right;font-family: Candara, Calibri; font-size: 20px; }";
		str += "h4 {text-align=right;font-family: Candara, Calibri; font-size: 18px; }";
		str += "tr.b0 td {text-align:left; font-family:Candara;  font-size: 18px ; background-color: #ffffff; color:#004994}";
		str += "th.clsInSummary {text-align:center; font-family:Candara;  font-size: 16px ; background-color: #ffffff; color:#004994}";
		str +=" #leftsection { position: absolute; left: 0; top: 0; width: 50%; }";
		str +="#rightsection { position: absolute; right: 0; top: 0; width: 50%; }";
		return str;
	}

	private String stylingTemplateNew() {
		String str = "table#pieChartTable{width:90%;margin-left:5%;border: 2px solid #bcf4f1;}\n" +
				"table#pieChartTable th{text-align: center;}" +
				"table#pieChartTable th p{color:black;font-size:32px;margin:0px;text-align: center;height: 5px;text-shadow: 4px 4px #d7dce2;}\n" +
				"table#pieChartTable td{border-bottom: 1px solid #bcf4f1;}\n" +
				"#piChart{text-align: center;}\n" +
				"#automationReport{color:black;margin-top: 30px;margin-bottom: 11px;font-size: 22px;height:15px;}\n" +
				"table#executionSummary{width: 100%;border:none;padding-top: 25px;}\n" +
				"table#passFailCountTable{width:70%;}\n" +
				"table#passFailCountTable .count{text-align: right;}\n" +
				".passText{color:green;}\n" +
				".failText{color:red;}\n" +
				".totalText{color:Black;}\n" +
				"table#classSummary{width:90%;margin-left:5%;border: 2px solid #bcf4f1;}\n" +
				"table#classSummary tr:nth-child(even) {background-color: #e8f5f7;}\n" +
				"table#classSummary th{background-color:white;height: 35px;padding-top: 8px;font-size: 22px;}\n" +
				"table#classSummary a{text-decoration: none;color: black;font-size: 17px;text-indent: 5px;}\n" +
				"table#classSummary .className{background-color:#bcf4f1;height:30px;padding-top: 5px;font-weight: bold;}\n" +
				"table#methodSummary{width:90%;margin-left:5%;border: 2px solid #bcf4f1;}\n" +
				"table#methodSummary tr:nth-child(even) {background-color: #e8f5f7;}\n" +
				"table#methodSummary th{background-color:white;height: 35px;padding-top: 8px;font-size: 22px;}\n" +
				"table#methodSummary a{text-decoration: none;}\n" +
				"table#methodSummary p{color:black;font-size:17px;margin: 0px;}\n" +
				"table#methodSummary .className{background-color:#bcf4f1;height:30px;padding-top: 10px;}\n" +
				"table#methodSummary .detailsReport{padding-left:15px;margin-left: 10px;}\n" +
				"table#methodSummary .methodName{background-color:#cfe1f7;font-size: 18px;height: 28px;} \n" +
				".right{text-align: center;width: 2%;color:green;}\n" +
				".wrong{text-align: center;width: 2%;color:red;}\n" +
				".skipped{ color:#F89938;font-weight:bold;font-size: 18px;text-align: center;}\n";
		return str;
	}

	public void writeDetails(String lines) {
		try {
			bufWriter2.write(lines);
		} catch (IOException e) {
			logger.error("Exception caught while writing details in HTML :"+e);
		}
	}

	public void verifyTestCase(String TestcaseName){
		testMethod = TestcaseName;
		if(testMethod!=null){
			String UniqueMethodName = ClassName+"."+testMethod;
			AllList.add(UniqueMethodName);
		}
		createReport(TestcaseName);	
	}

	public void testClassDetailsTemplate(String className) {
		writeDetails("<br style = \"line-height:1;\"><br>");
		writeDetails("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<table width=100%>");
		writeDetails("<tr class=classname><th> colspan=7>"+className+"</th>");
	}


	/*public void log(String PF, String Step, String Expected, String Actual, String colorCode){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String ImageFileName = "FD_Screenshot_"+timestamp.getTime();
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			File directory = new File(String.valueOf("./test-output/screenshots"));
			if(!directory.exists())
				directory.mkdir();
			String userDirector = System.getProperty("user.dir") + "/test-output/screenshots/";
			FileUtils.copyFile(scrFile, new File(userDirector+ImageFileName));
			String DateAndTime = getDateAndTime();
			String time = "<td><p>"+DateAndTime+"</p></td>";
			String step = "<td><p>"+Step+"</p></td>";
			String expected = "<td><p>"+Expected+"</p></td>";
			String actual  = "<td><p><a href=\""+ userDirector + ImageFileName+"\">"+Actual+"</a></p></td>";
			String result = "<td bgcolor="+colorCode+"><p>"+PF+"</p></td>";
			String logs = "<tr>"+time+step+expected+actual+result+"</tr>";
			writeDetails(logs);
			if(PF.equalsIgnoreCase("Pass"))
				logger.info(Step+" : "+Actual);
			else
				logger.error(Step+" : "+Actual);	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}*/


	public void logPass(String Step, String Expected, String Actual){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			String DateAndTime = getDateAndTime();
			String timeTakenString = timeBetweenSteps();
			writeDetails("<tr><td >" + timeTakenString + "</td><td ></td><td class='right'>&#10004;</td><td><p>"+Step+"</p></td>\n" +
					"<td><p>"+Expected+"</p></td><td><p><a>"+Actual+"</a></p></td><td><p>"+DateAndTime+"</p> </td></tr>");
			logger.info(Step+" : "+Actual);
		} catch (Exception e1) {
			logger.error("Fail to log pass due to exception "+e1.getMessage());
		}
	}


	public void logPass(String Step, String Expected, String Actual, String colorCode){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			String DateAndTime = getDateAndTime();
			String time = "<td><p>"+DateAndTime+"</p></td>";
			String step = "<td><p>"+Step+"</p></td>";
			String expected = "<td><p>"+Expected+"</p></td>";
			String actual  = "<td><p><a>"+Actual+"</a></p></td>";
			String result = "<td bgcolor="+colorCode+"><p>Pass</p></td>";
			String logs = "<tr>"+time+step+expected+actual+result+"</tr>";
			writeDetails(logs);
			logger.info(Step+" : "+Actual);	
		} catch (Exception e1) {
			logger.error("Fail to log pass due to exception "+e1.getMessage());
		}
	}
	
	public void logHold(String Step, String Expected, String Actual, String colorCode){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			String DateAndTime = getDateAndTime();
			String time = "<td><p>"+DateAndTime+"</p></td>";
			String step = "<td><p>"+Step+"</p></td>";
			String expected = "<td><p>"+Expected+"</p></td>";
			String actual  = "<td><p><a>"+Actual+"</a></p></td>";
			String result = "<td bgcolor="+colorCode+"><p>OnHold</p></td>";
			String logs = "<tr>"+time+step+expected+actual+result+"</tr>";
			writeDetails(logs);
			logger.info(Step+" : "+Actual);	
		} catch (Exception e1) {
			logger.error("Fail to log pass due to exception "+e1.getMessage());
		}
	}


	public void logFail(String Step, String Expected, String Actual){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String ImageFileName = "FD_Screenshot_"+timestamp.getTime()+".jpg";
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			File directory = new File(String.valueOf("./test-output/screenshots"));
			if(!directory.exists())
				directory.mkdir();
			String userDirector = System.getProperty("user.dir") + "/test-output/screenshots/";
			String userDirector1 = "..\\screenshots\\";
			FileUtils.copyFile(scrFile, new File(userDirector+ImageFileName));
			String DateAndTime = getDateAndTime();
			String timeTakenString = timeBetweenSteps();

			writeDetails("<tr><td >" + timeTakenString + "</td><td ></td><td class='wrong'>&#10006;</td><td><p>"+Step+"</p></td>\n" +
					"<td><p>"+Expected+"</p></td><td><p><a style='color:red' href='"+ userDirector1 + ImageFileName+"'>"+Actual+"</a></p></td><td><p>"+DateAndTime+"</p></td></tr>");

			logger.error(Step+" : "+Actual);
		} catch (Exception e1) {
			logger.error("Fail to log failure due to exception "+e1.getMessage());
		}
	}


	public void logFail(String Step, String Expected, String Actual, String colorCode){
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String ImageFileName = "FD_Screenshot_"+timestamp.getTime()+".jpg";
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			File directory = new File(String.valueOf("./test-output/screenshots"));
			if(!directory.exists())
				directory.mkdir();
			String userDirector = System.getProperty("user.dir") + "/test-output/screenshots/";
			String userDirector1 = "..\\screenshots\\";
			FileUtils.copyFile(scrFile, new File(userDirector+ImageFileName));
			String DateAndTime = getDateAndTime();
			String time = "<td><p>"+DateAndTime+"</p></td>";
			String step = "<td><p>"+Step+"</p></td>";
			String expected = "<td><p>"+Expected+"</p></td>";
			String actual  = "<td><p><a href=\""+ userDirector1 + ImageFileName+"\">"+Actual+"</a></p></td>";
			String result = "<td bgcolor="+colorCode+"><p>\"Fail\"</p></td>";
			String logs = "<tr>"+time+step+expected+actual+result+"</tr>";
			writeDetails(logs);
			logger.error(Step+" : "+Actual);	
		} catch (Exception e1) {
			logger.error("Fail to log failure due to exception "+e1.getMessage());
		}
	}

	public void testFailedList(){
		Object[] failedlist = FailedList.toArray();
		for (Object i : failedlist) {
			if (FailedList.indexOf(i) != FailedList.lastIndexOf(i)) {
				FailedList.remove(FailedList.lastIndexOf(i));
			}
		}
	}

	public void testSkippedList(){
	Object[] skippedListTemp = skippedList.toArray();
		for (Object i : skippedListTemp) {
			if (skippedList.indexOf(i) != skippedList.lastIndexOf(i)) {
				skippedList.remove(skippedList.lastIndexOf(i));
			}
		}
	}

	public void listOfPassedTestCases(){
		Object[] passedlist = PassedList.toArray();
		for (Object i : passedlist) {
			if (PassedList.indexOf(i) != PassedList.lastIndexOf(i)) {
				PassedList.remove(PassedList.lastIndexOf(i));
			}
		}
	}

	public void listOfAllTestCases(){
		Object[] allList = AllList.toArray();
		for (Object i : allList) {
			if (AllList.indexOf(i) != AllList.lastIndexOf(i)) {
				System.out.println("**** List of all test cases: "+ i + "  ::::: "+AllList.lastIndexOf(i));
				AllList.remove(AllList.lastIndexOf(i));
			}
		}
	}

	public void ListOfAllClasses(){
		Object[] classList = ClassList.toArray();
		for (Object i : classList) {
			if (ClassList.indexOf(i) != ClassList.lastIndexOf(i)) {
				ClassList.remove(ClassList.lastIndexOf(i));
			}
		}
	}

	public void listOfAllFailedClasses(){
		Object[] failedclassList = FailedClassList.toArray();
		for (Object i : failedclassList) {
			if (FailedClassList.indexOf(i) != FailedClassList.lastIndexOf(i)) {
				FailedClassList.remove(FailedClassList.lastIndexOf(i));
			}
		}
	}

	public String getDateAndTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String DateAndTime = dateFormat.format(date);
		return DateAndTime;
	}

	public Date getDateFormat(){
		Date date = new Date();
		return date;
	}

	public void GenerateFinalReport() {
		try {
			if(reportingFormat.contains("Complex")){
				createSummaryReport();
				closeBufferFile2();
				mergeDetailsAndSummaryFile();
				deleteBufferFile();
				//closeExtentReports();
			}else if(reportingFormat.contains("Simple")){
				endTheHTML();
				closeBufferFile2();
			}
		} catch (IOException e) {
			logger.error("Exception caught while closing details file "+e);
		} 
	}

	public void deleteBufferFile(){
		boolean file = (new File(BufferfilePath)).delete();
		boolean folder = (new File("./test-output/BufferReport")).delete();
	}

	public void writeSummaryChart(){

		testFailedList();
		testSkippedList();
		listOfAllTestCases();
		listOfPassedTestCases();
		ListOfAllClasses();
		listOfAllFailedClasses();

		try{
			String failed = String.valueOf(FailedList.size());
			String skippedCount = String.valueOf(skippedList.size());
			String AllTest = String.valueOf(PassedList.size());
			String passed = String.valueOf(PassedList.size()-(FailedList.size() + skippedList.size()));
			//writeSummary("</style>");
			writeSummary("<script>");
			writeSummary("function init(){");
			writeSummary("var canvas = document.getElementById('can'); "
					+ "var ctx = canvas.getContext('2d'); var lastend = 0;"
					+ "var data =[ "
					+ failed 
					+ ","
					+ passed
					+ ","
					+ skippedCount
					+ "]; ; var myTotal = 0; var labels= ['Fail','Pass', 'Skip']; "
					+ "var myColor = ['#ff3333', '#47d147', '#F89938'];"
					+ " for (var e = 0; e < data.length; e++) {  myTotal += data[e];} "
					+ "for (var i = 0; i < data.length; i++) { ctx.save();  "
					+ " ctx.fillStyle = myColor[i];   ctx.beginPath();  "
					+ " ctx.moveTo(canvas.width / 2, canvas.height / 2);  "
					+ " ctx.arc(canvas.width / 2, canvas.height / 2, canvas.height / 2, lastend, lastend + (Math.PI * 2 * (data[i] / myTotal)), false); "
					+ "  ctx.lineTo(canvas.width / 2, canvas.height / 2); "

				+ " ctx.fill();if(data[i]>0){"
				+ "var radius = (canvas.height / 2)/1.5;"
				+ " var endAngle = lastend + (Math.PI*(data[i]/myTotal ));"
				+ " var setX = (canvas.width / 2)+ Math.cos(endAngle) * radius;  "
				+ "var setY =(canvas.height / 2 )+ Math.sin(endAngle) * radius;ctx.fillStyle = '#ffffff'; "
				+ " ctx.font = '10px Calibri'; "
				+ "ctx.fillText(labels[i],setX,setY);}"
				+ "lastend += Math.PI * 2 * (data[i] / myTotal);  ctx.restore();");
			writeSummary("} }");
			writeSummary("</script>");
			writeSummary("</head><body onLoad=init()>");

			testSetTemplateNew(passed, failed, skippedCount, AllTest);
		}catch(Exception e){
			logger.error("Failed to write SummaryChart due to exception "+e.getMessage());
		}
	}


	private void testSetTemplateNew(String passed, String failed, String skipped, String  testCases) {
		try{
			EndTime = getDateFormat();
			String executionUrl = exeURL;

			long hours=0, minutes=0;
			if(StartTime!=null && EndTime!=null)
			{LocalDateTime LocalStartTime =StartTime.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();
				LocalDateTime LocalEndTime=EndTime.toInstant()
						.atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				hours = Duration.between(LocalStartTime, LocalEndTime).toHours();
				minutes = Duration.between(LocalStartTime, LocalEndTime).toMinutes();
				if(minutes > 60)
				{
					minutes %= 60;
				}
			}
			writeSummary("<table id='pieChartTable'>" +
					"<tr><th width='10%'></th>" +
					"<th colspan='3'><p style='color:#669B41;float: left'>Fresh</p><p style='color:#F89938;float: left'>direct</p></th>" +
					"<th width='10%'></th></tr>" +
					"<tr><td></td><td colspan='4'><p id='automationReport'>Automation Report</p></td>" +
					"</tr><tr><td > </td><td >" +
					"<table id='executionSummary'><tr><td> Start Time</td><td>" +StartTime+ "</td></tr>" +
					"<tr><td> End Time</td><td>" +EndTime+ "</td></tr>" +
					"<tr><td> Time Taken</td><td>" +hours+":"+minutes+"</td></tr>" +
					"<tr><td> Platform Name</td><td>"+System.getProperty("os.name")+"</td></tr>" +
					"<tr><td> Browser Name</td><td>"+CheckBrowserName()+"</td></tr>" +
					"<tr><td> Version</td><td>"+CheckBrowserVersion()+"</td></tr>" +
					"<tr><td> Url</td><td>" +executionUrl+ "</td></tr>" +
					"<tr><td> Release</td><td>" +releaseName+ "</td></tr></table></td>" +
					"<td id=\"piChart\"><canvas id='can' width='160' height='160'></canvas></td>" +
					"<td><table id='passFailCountTable'>" +
					"<tr><td class='right'>&#10004;</td><td class='totalText'> Passed:</td><td class='count'>" +passed+ "</td></tr>" +
					"<tr><td class='wrong'>&#10006;</td><td class='totalText'> Failed: </td><td class='count'>" +failed+ "</td></tr>" +
					"<tr ><td class='skipped'>&#8252;</td><td class='totalText'>Skipped:</td><td class='count'>" +skipped+ "</td></tr>" +
					"<tr ><td class='right'></td><td class='totalText';>Total:</td><td class='count'>" +testCases+ "</td></tr></table></td>" +
					"<td></td></tr></table>");
		}catch(Exception e){
			logger.info("Failed to write excection summary sue to exception "+e.getMessage());
		}
	}


	private void testSetTemplate(String passed, String failed, String testCases) {
		try{
			EndTime = getDateFormat();   
			String executionUrl = exeURL;
			
			long hours=0, minutes=0;
			if(StartTime!=null && EndTime!=null)
			{LocalDateTime LocalStartTime =StartTime.toInstant()
                     .atZone(ZoneId.systemDefault())
                     .toLocalDateTime();
			LocalDateTime LocalEndTime=EndTime.toInstant()
                     .atZone(ZoneId.systemDefault())
                     .toLocalDateTime();
		     hours = Duration.between(LocalStartTime, LocalEndTime).toHours();
		     minutes = Duration.between(LocalStartTime, LocalEndTime).toMinutes();
		     if(minutes > 60)
		     {
		    	 minutes %= 60;
		     }
			}
			
			writeSummary("<table id='piechart' style='border-collapse: collapse;' width='100%'>"
					+ "<br style = \"line-height:1;\"><br>"
					+ "<tr><td bgcolor=#ffffff width='25%' align='left'><p>"
					+ "<b><font color='green'>Passed:</font></b>"+passed+"</p>"
					+ "<p><b><font color='red'>&nbspFailed:</font></b>"+failed+"</p>"
					+ "<p><b><font color='#004994'>&nbsp&nbspTotal:</font></b>"+testCases+"</p>"
					+ "</td><td width='20%' align='left' bgcolor=#ffffff>"
					+ "<br style = \"line-height:1;\"><br>"
					+ "<canvas id='can' width='150' height='150'>"
					+ "</canvas></td>"
					+ "<td bgcolor=#ffffff width='55%' align='left'>"
					+ "<h2 class=exesum>Execution Summary :</h2><table class=exeSumTable><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Start Time : </b>"+StartTime+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>End Time : </b>"+EndTime+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Time Taken : </b>"+hours+"<b>:</b>"+minutes+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Platform : </b>"+System.getProperty("os.name")+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Browser : </b>"+CheckBrowserName()+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Version : </b>"+CheckBrowserVersion()+"</td></tr><tr class=b0>"
					+ "<td style=\"display:inline\"><b>Url : </b>"+executionUrl+"</td></tr><tr class=b0>"
					+ "</tr></table></td></tr></table>");
			//writeSummary("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<table align=center  width=100%>");
		}catch(Exception e){
			logger.info("Failed to write excection summary sue to exception "+e.getMessage());
		}
	}

	private void writeSummary(String lines) {
		try {
			bufWriter1.write(lines);
		} catch (IOException e) {
			logger.error(
					"Exception caught while writing details in HTML : "+ e);
		}
	}

	public void mergeDetailsAndSummaryFile() throws IOException {
		BufferedReader inputStream = new BufferedReader(new FileReader(BufferfilePath));
		String count;
		while ((count = inputStream.readLine()) != null) {
			bufWriter1.write(count);
		}
		bufWriter1.write("</table></body></html>");
		flushBufferFile1();
		closeBufferFile1();
		inputStream.close();
	}

	public void closeBufferFile2() throws IOException{
		try{
			bufWriter2.close();
		}catch(Exception e){
			logger.error("Exception occured while closing the reporting file. Exception : "+e);
		}
	}
	public void closeBufferFile1() throws IOException{
		try{
			bufWriter1.close();
		}catch(Exception e){
			logger.error("Exception occured while closing the reporting file. Exception : "+e);
		}
	}
	public void flushBufferFile1() throws IOException{
		try{
			bufWriter1.flush();
		}catch(Exception e){
			logger.error("Exception occured while closing the reporting file. Exception : "+e);
		}
	}

	public void AddClassAndMethodNameToList(String PF){
		if(PF.equals("pass")){
			if(testMethod!=null){
				String UniqueMethodName = ClassName+"."+testMethod;
				PassedList.add(UniqueMethodName);
			}
		}else if (PF.equals("fail")){
			if(testMethod!=null){
				FailedClassList.add(ClassName);
				String UniqueMethodName = ClassName+"."+testMethod;
				PassedList.add(UniqueMethodName);
				FailedList.add(UniqueMethodName);
			}
		}else if (PF.equals("skipped")){
			if(testMethod!=null){
				String UniqueMethodName = ClassName+"."+testMethod;
				PassedList.add(UniqueMethodName);
				skippedList.add(UniqueMethodName);
			}
		}
	}

	public void pass(String Step, String Expected, String Actual) {
		if(reportingFormat.contains("Complex")){
			AddClassAndMethodNameToList("pass");
			String colorcode = "#47d147";
			logPass(Step, Expected, Actual);
		}else if(reportingFormat.contains("Simple")){
			logPass_SR(Step,Actual);
		}
		/*try{
			extentLogger.log(LogStatus.PASS, Step,Actual+takeScreenShot(PassPath));
		}catch(Exception e){
			logger.info("Was not logged in extent report");
		}*/
	}

	public void fail(String Step, String Expected, String Actual) {
		FailFlag=true;
		if(reportingFormat.contains("Complex")){
			AddClassAndMethodNameToList("fail");
			String colorcode = "#ff3333";
			logFail(Step, Expected, Actual);
		}else if(reportingFormat.contains("Simple")){
			logFail_SR(Step, Actual);
		}
		/*try{
			extentLogger.log(LogStatus.FAIL,Step,Actual+takeScreenShot(FailPath));
		}catch(Exception e){
			logger.info("Was not logged in extent report");
		}*/
	}
	
	public void hold(String Step, String Expected, String Actual) {
		if(reportingFormat.contains("Complex")){
			AddClassAndMethodNameToList("pass");
			String colorcode = "#F393D3";
			logHold(Step, Expected, Actual, colorcode); 
		}else if(reportingFormat.contains("Simple")){
			logHold_SR(Actual);
		}
		/*try{
			extentLogger.log(LogStatus.PASS, Step,Actual+takeScreenShot(PassPath));
		}catch(Exception e){
			logger.info("Was not logged in extent report");
		}*/
	}

	public void info(String Step,String Expected,String Actual){
		if(reportingFormat.contains("Complex")){
			AddClassAndMethodNameToList("pass");
			String colorcode = "#47d147";
			logPass(Step, Expected, Actual);
		}else if(reportingFormat.contains("Simple")){
			logPass_SR(Step, Actual);
		}
		//extentLogger.log(LogStatus.INFO, Step,Actual+takeScreenShot(PassPath));
	}

	public void error(String Step,String Expected,String Actual){
		FailFlag=true;
		if(reportingFormat.contains("Complex")){
			AddClassAndMethodNameToList("fail");
			String colorcode = "#ff3333";
			logFail(Step, Expected, Actual);
		}else if(reportingFormat.contains("Simple")){
			logFail_SR(Step,Actual);
		}
		//extentLogger.log(LogStatus.ERROR, Step,Actual+takeScreenShot(FailPath));
	}

	public void skip(String Step,String Actual){
		System.out.println("Skipped Test:"+Actual);
		//extentLogger.log(LogStatus.SKIP, Step,Actual+takeScreenShot(FailPath));
	}

	/*private String takeScreenShot(String Path) {
		Date now = new Date();
		DateFormat shortDf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		String currentDate = shortDf.format(now).toString().replace("/", "");
		currentDate = currentDate.toString().replace(" ", "_");
		currentDate = currentDate.toString().replace(":", "_");
		String path = Path+"FreshDirect"+currentDate+".jpg";
		File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshotFile, new File(path),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("Failed to take Screenshot due to exception "+e.getMessage());
		}
	//	String image = extentLogger.addScreenCapture(path);
		return image;
	}*/

	/*public void closeExtentReports(){
		extent.flush();
		extent.close();
	}*/

	public void VerifyAfterTest(ITestResult result){
		if(reportingFormat.contains("Complex")){
		if(FailFlag){
			result.setStatus(ITestResult.FAILURE);
			FailFlag=false;
		}
		testMethod=null;
		}else if(reportingFormat.contains("Simple")){
			closeLoggingListAfterMethod();
		}
		//extent.endTest(extentLogger);
	}

	public void VerifyAfterClass(){
		if(reportingFormat.contains("Complex")){
		ClassName=null;
		}else if(reportingFormat.contains("Simple")){
			closeLoggingListAfterClass();
		}
	}

	//Class logs
	public void createClassReport(String className){
//		writeDetails("<html><head><style>");
//		writeDetails(stylingTemplate());
//		writeDetails("</style></head><body>");
		testCaseClassDetailsTemplateNew(className);
	}

	private void testCaseClassDetailsTemplateNew(String testClassName) {

		writeDetails("<tr><td  colspan='7'><p class='className' id='"+testClassName+"'><b>" +testClassName+ "</b></p></td></tr>");
		writeDetails("<tr class='detailsReport'><td width='2%'></td><td width='2%%'></td><td width='2%'> </td><td width='20%'>" +
				"<b>Step</b></td><td width='25%'><b>Expected</b></td><td width='25%'><b>Actual</b></td><td width='12%'><b>Time</b></td></tr>");
	}

	private void testCaseClassDetailsTemplate(String testClassName) {
		writeDetails("<table width=100%>");
		writeDetails("<th class=\"beforeTest\" id="+testClassName+" colspan=7>"+testClassName+"</th>");
		writeDetails("<tr class=d0><td width=12%><b>Time</b></td><td width=20%><b>Step</b></td>"
				+ "<td width=30%><b>Expected</b></td><td wisth=30%><b>Actual</b></td><td width=8%><b>Result</b></td></tr>");
	}

	public void InitiateClassLevelReport(String Classname){
		System.out.println("======================================="+Classname+"=======================================");
		if(reportingFormat.contains("Complex")){
			testMethod=null;
			bufferList.add("1");
			ClassName = Classname;
			ClassList.add(ClassName);
			createClassReport(Classname);	
		}else if(reportingFormat.contains("Simple")){
			addClassNameInSimpleReport(Classname);
		}
		//CreateFilesExtent();
	}

	public String CheckBrowserName() {
		Capabilities caps = ((RemoteWebDriver) webDriver).getCapabilities();
		String browserName = caps.getBrowserName();
		return browserName;
	}

	public String CheckBrowserVersion() {
		Capabilities caps = ((RemoteWebDriver) webDriver).getCapabilities();
		String browserVersion = caps.getVersion();
		return browserVersion;
	}	


	/*//Extent report functions are currently not used 
	public void CreateFilesExtent(){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		extent = new ExtentReports (System.getProperty("user.dir") +"/test-output/extentReport/FreshDirect"+timestamp.getTime()+".html", true);
		//extent.addSystemInfo("Environment","Environment Name")
		extent
		.addSystemInfo("Browser Name", BrowserName)
		.addSystemInfo("Platform", System.getProperty("os.name"));
		//loading the external xml file (i.e., extent-config.xml) which was placed under the base directory
		//You could find the xml file below. Create xml file in your project and copy past the code mentioned below
		extent.loadConfig(new File(System.getProperty("user.dir")+"/extent-config.xml"));
	}
	 */

	//Simple Reporting 
	public void createSimpleTemplate(){
		writeDetails("<html><head><title bgcolor = \"#bbbbbb\">FreshDirect Reporting</title></head><body>");
		writeDetails("<h1>FreshDirect Reporting</h1>");
		writeDetails("<style>");
		writeDetails(stylingTemplateForSimpleReport());
		writeDetails("</style></head>");
	}

	private String stylingTemplateForSimpleReport() {
		String str = "body{background-color: #ffffff;  border: 2px solid grey; border-radius: 5px}";
		str = str + "h1 { align=center; text-align: center;color: #000000; font-family:  Candara,Calibri;font-size: 28px; }";
		str += "button { align=center; text-align: left;background-color: #ffffff; color: #004080; font-family:  Candara,Calibri;font-size: 24px; }";
		str += "h3 {text-align=right;color: #004080;font-family: Candara, Calibri; font-size: 20px; }";
		str += "h4 {text-align=right;font-family: Candara, Calibri; font-size: 18px; }";
		str += "#leftsection { position: absolute; left: 0; top: 0; width: 50%; }";
		str += "li#pass {color:#008000;}";
		str += "li#fail {color:#ff0000;}";
		str += "li#hold {color:#F393D3;}";
		str += "li#pass span {color:#008000; font-size: 16px;}";
		str += "li#fail span {color:#ff0000; font-size: 16px;}";
		str += "li#hold span {color:#F393D3; font-size: 16px;}";
		str += "ul.test_steps{style='list-style-type:disc'}";
		str += "form{align:\"center\";}";
		str +=".collapsible {background-color: #e6f5ff;color:#000080;cursor: "
				+ "pointer; padding: 16px;width: 100%;border: none;text-align: left;outline: none;font-size: 20px;font-weight:bold;}";
		str +=".active, .collapsible:hover {background-color: #b3d9ff;}";
		str +=".collapsible:after {content: '+';color: white;font-weight: bold;float: left; margin-left: 5px;font-size: 20px; color:black}";
		str	+= ".active:after {content: '-';font-size: 20px; color:black}";
		str +=".content {padding: 0 18px;display: none;overflow: hidden;background-color: #fcfcfc;}";
		return str;
	}

	public void CreateSimpleFormatFile(){
		try{
			String name = "ExecutionReport";
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			BufDirectory = new File(String.valueOf("./test-output/SimpleReport"));
			if(!BufDirectory.exists())
				BufDirectory.mkdir();
			BufferfilePath = "./test-output/SimpleReport/" + name + timestamp.getTime()+ ".html";
			openBufferFile(BufferfilePath);
		}catch(Exception e){
			logger.error("Fail to CreateSimpleFormatFile due to exception "+e.getMessage());
		}
	}

	public void addClassNameInSimpleReport(String className){
		writeDetails("<hr>");
		writeDetails("<button class=\"collapsible\">&nbsp"+className+"</button>");
		writeDetails("<div class=\"content\">");
		writeDetails("<ul @class='test_steps'>");
	}

	public void addMethodNameInSimpleReport(String methodName){
		writeDetails("<h3>&nbsp&nbsp"+methodName+"</h3>");
		writeDetails("<ul @class='test_steps'>");
	}
	
	public void closeLoggingListAfterClass(){
		writeDetails("</ul>");
		writeDetails("</div>");
	}
	public void closeLoggingListAfterMethod(){
		writeDetails("</ul>");
	}

	public void logPass_SR(String Step,String Actual){
		writeDetails("<li id='pass'><span>"+Actual+"</span></li>");
		logger.info(Step+" : "+Actual);	
	}

	public void logFail_SR(String step,String Actual){
		writeDetails("<li id='fail'><span>"+Actual+"</span></li>");
		logger.error(step+" : "+Actual);	
	}
	
	public void logHold_SR(String Actual){
		writeDetails("<li id='hold'><span>"+"On Hold scenario : "+Actual+"</span></li>");
	}

	public void endTheHTML(){
		writeDetails(writeScriptForCollapse());
		writeDetails("</body></html>");
	}
	
	public String writeScriptForCollapse(){
		String script = "<script>var coll = document.getElementsByClassName(\"collapsible\");var i;";
		script +="for (i = 0; i < coll.length; i++) {";
		script += "coll[i].addEventListener(\"click\", function() {";
		script += "this.classList.toggle(\"active\");";
		script += "var content = this.nextElementSibling;";
		script += "if (content.style.display === \"block\") {";
		script += "content.style.display = \"none\";} else {";
	    script += "content.style.display = \"block\";}});}</script>";
	    return script;
	}


	public void InitiateMethodLevelReportWithDescription(Method method,String testDescription){
		System.out.println("\n\n------------------------------------"+method.getName()+"------------------------------------");
		testDescription = testDescription.replace('.', ' ');
		testDescription = testDescription.replace('/','|');
		if(reportingFormat.contains("Complex")){
			bufferList.add("0");
			UID methodId = new UID();
			String uniqueMethodName = method.getName()+"/"+testDescription+"/"+methodId;
			verifyTestCase(uniqueMethodName);
			//extentLogger = extent.startTest(method.getName(),classname);
		}else if(reportingFormat.contains("Simple")){
			addMethodNameInSimpleReport(testDescription);
		}
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {

	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {

	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {

	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		ITestNGMethod iTestNGMethod = iTestResult.getMethod();
		Method method = iTestNGMethod.getConstructorOrMethod().getMethod();
		Test test = method.getAnnotation(Test.class);
		String testDescription = test.description();
		if(testDescription.length() > 0) {
			testDescription = testDescription.replace('.', ' ');
			testDescription = testDescription.replace('/', '|');
		} else{
			testDescription = method.getName();
		}

		if(reportingFormat.contains("Complex")){
			bufferList.add("0");
			UID methodId = new UID();
			String uniqueMethodName = method.getName()+"/"+testDescription+"/"+methodId;
			verifyTestCase(uniqueMethodName);
		}else if(reportingFormat.contains("Simple")){
			addMethodNameInSimpleReport(testDescription);
		}
		AddClassAndMethodNameToList("skipped");
		iTestResult.setStatus(ITestResult.SKIP);
		testMethod=null;
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

	}

	@Override
	public void onStart(ITestContext iTestContext) {

	}

	@Override
	public void onFinish(ITestContext iTestContext) {

	}

	private String timeBetweenSteps(){

		if(firstStep) {
			lastStepExecutionTime = getDateFormat();
			firstStep = false;
		}

		Date currentExecutionTime = getDateFormat();

		LocalDateTime LocalStartTime =lastStepExecutionTime.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		LocalDateTime LocalEndTime=currentExecutionTime.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		lastStepExecutionTime = currentExecutionTime;

		long  millis = Duration.between(LocalStartTime, LocalEndTime).toMillis();
		 long seconds = millis / 1000;
		 String timeWithStyle = timeWithStyle =  "<p style='text-align:right;background-color:#adebad'> " + seconds + "s</p>";

		if(seconds > 10)
			timeWithStyle =  "<p style='text-align:right; background-color:#ffad99'> " + seconds + "s</p>";
		else if(seconds > 5)
		 	timeWithStyle =  "<p style='text-align:right;background-color:#ffff99'> " + seconds + "s</p>";

		 return timeWithStyle;
	}
}