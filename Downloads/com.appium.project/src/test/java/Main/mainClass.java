package Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.TestNG;
import org.w3c.dom.Document;

import Library.ReadConfigFile;
import Library.createXML;

public class mainClass {
	static Document xml = null;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			LinkedHashMap<String, String> configData = new ReadConfigFile().getDataFromPropertiesFile();

			Object[] keySet = configData.keySet().toArray();

			LinkedHashMap<String, String> TestCaseName = new LinkedHashMap<String, String>();
			for (Object key : keySet) {
				String value = configData.get(key);
				if (value.contains("/")) {
					String[] parameterValue = value.split("/");
					if (parameterValue[1].equalsIgnoreCase("Y")) {
						TestCaseName.put(key.toString(), parameterValue[0]);
					}
				}
			}
			new createXML().writeDetails(TestCaseName, "", "");

			runTestNgXML();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runTestNgXML() {

		// Create object of TestNG Class
		TestNG runner=new TestNG();

		// Create a list of String 
		List<String> suitefiles=new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add(System.getProperty("user.dir")+"/src/test/resources/testng.xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}

}