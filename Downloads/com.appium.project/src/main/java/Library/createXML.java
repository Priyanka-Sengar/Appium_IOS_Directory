package Library;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

public class createXML {

	private static Writer bufWriter1;
	private static Logger logger=Logger.getLogger("HAC");
	public static LinkedHashMap<String, String> testCaseName;
	public static String ParameterName = "";
	public static String ParameterValue = "";
	static ReadConfigFile read =  new ReadConfigFile();
	private static String BrowserNames = read.readProperties("browserName");
	private static String BrowserVersion = read.readProperties("Version");
	private static String Parallel = read.readProperties("Parallel");
	private static String GroupName = read.readProperties("groupName");

	public void openDetailsFile(String filePath) {
		try {
			Writer fileWriter = new FileWriter(filePath);
			bufWriter1 = new BufferedWriter(fileWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeClass() throws IOException {
		for (String key : testCaseName.keySet()) {
			bufWriter1.write("\t\t\t\t<class name=\"TestCases." + testCaseName.get(key) + "\"/>\n");
		}
	}

	public void createParameters(String name, String value) throws IOException {
		bufWriter1.write("\t\t<parameter name=\"" + name + "\"  value=\"" + value + "\"/>\n");
	}

	public void assignValues(LinkedHashMap<String, String> testCaseName, String ParameterName, String ParameterValue) {
		this.testCaseName = testCaseName;
		this.ParameterName = ParameterName;
		this.ParameterValue = ParameterValue;
	}

	public void writeDetails(LinkedHashMap<String, String> testCaseName, String ParameterName, String ParameterValue)
			throws IOException {
		assignValues(testCaseName, ParameterName, ParameterValue);

		String filePath = System.getProperty("user.dir") + "/src/test/resources/testng.xml";
		new createXML().openDetailsFile(filePath);
		bufWriter1.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bufWriter1.write("<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >\n");
		writeSuite();
		closeBufferFile();
	}

	public void writeClasses() throws IOException {
		bufWriter1.write("\t\t\t<classes>\n");
		writeClass();
		bufWriter1.write("\t\t\t</classes>\n");
	}

	/*public String getExecutionType(){
		if(Parallel.contains("Y")){
			return "tests";
		}else{
			return "false";
		}
	}*/

	public void writeSuite() throws IOException {
		//String parallel = getExecutionType();
		bufWriter1.write("\t<suite name=\"FreshDirectWebSuite\" time-out=\"1800000\" parallel=\"false\">\n");
		writeTest();
		bufWriter1.write("\t</suite>");
	}

	public void writeTest(){
		String[] Browsers=BrowserNames.split(",");
		String[] Version=BrowserVersion.split(",");
		for(int i=0;i<Browsers.length;i++){
			try {
				bufWriter1.write("\t\t<test name=\"FreshDirect_Test"+(i+1)+"\" preserve-order=\"true\">\n");
				createParameters("browserName",Browsers[i]);
				createParameters("version",Version[i]);
				writeGroups();
				writeClasses();
				bufWriter1.write("\t\t</test>\n");
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}

	public void closeBufferFile() throws IOException {
		try {
			bufWriter1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeGroups(){
		String Group = GroupName;
		if(!Group.equalsIgnoreCase("All")){
			try{
				bufWriter1.write("\t\t\t<groups>\n");
				writeDefineAndRun(Group);
				bufWriter1.write("\t\t\t</groups>\n");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}		
	}

	public void writeDefineAndRun(String grpName) throws IOException {
		if(grpName.contains(",")){
			String[] grpNames = grpName.split(",");
			bufWriter1.write("\t\t\t\t<define name=\"defineAll\">\n");
			for (String groupName:grpNames){	
				bufWriter1.write("\t\t\t\t\t<include name=\""+groupName+"\"/>\n");
			}
			bufWriter1.write("\t\t\t\t</define>\n");
			bufWriter1.write("\t\t\t\t<run>\n");
			bufWriter1.write("\t\t\t\t\t<include name=\"defineAll\"/>\n");
			bufWriter1.write("\t\t\t\t</run>\n");
		}
		else{
			bufWriter1.write("\t\t\t\t<run>\n");
			bufWriter1.write("\t\t\t\t\t<include name=\""+grpName+"\"/>\n");
			bufWriter1.write("\t\t\t\t</run>\n");
		}
	}
}