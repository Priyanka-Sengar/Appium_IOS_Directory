package Library;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

public class ReadConfigFile {
  public static LinkedHashMap<String, String> readMap= new LinkedHashMap<String, String>();
	public LinkedHashMap<String, String> getDataFromPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out

			Object[] keySet = prop.keySet().toArray();

			for (int i = 0; i < keySet.length; i++) {
				readMap.put((String) keySet[i], prop.getProperty((String) keySet[i]));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
        return readMap;
	}
	
	public String readProperties(String skey){
		 readMap= getDataFromPropertiesFile();
		 String keyValue =readMap.get(skey);
		 return keyValue;
	}
}