package Library;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigFile{
	public static WebDriver webDriver;
	//public int oneMinuteWait = 60;
	protected Logger logger=Logger.getLogger("HAC");
	public static String filePath, BufferfilePath;
	public static String testMethod;
	static ReadConfigFile read =  new ReadConfigFile();
	public static String BrowserName = read.readProperties("DeviceName");
	public static String BrowserStack = read.readProperties("BrowserStack");
	public static String version = read.readProperties("Version");
	public static String reportingFormat = read.readProperties("ReportingFormat");
	public static String exeURL = read.readProperties("URL");
	public static String releaseName = read.readProperties("releaseName");
	//public Data getData = new Data();
	
	/*
	 * System Informations
	 */
	private final static String operatingSystem = System.getProperty("os.name");
	//public static String 
	public ConfigFile(){
		PropertyConfigurator.configure("Log4j.properties");
	}

	//Data for Browser Stack
	public static final String USERNAME = read.readProperties("UserName");
	public static final String AUTOMATE_KEY = read.readProperties("AutomateKey");
	public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
	public static final String ProjName = read.readProperties("ProjectName");
	public static final String Platform = read.readProperties("Platform");

	/**
	 * @throws IOException 
	 * @Function: To open the browser
	 */
	public void openMain(String browser, String version) throws IOException
	{

		try{
			if(BrowserStack.contains("Y")){
				BrowserStackExecution(browser,version);
			}else{
				switch(browser) {
				case "Chrome" :
					ChromeExecution();
					break;
				case "Firefox" :
					FirefoxExecution();
					break;
				case "IE" :
					IEExecution();
					break;
				case "Safari" :
					SafariExecution();
					break;
				default :
					System.out.println("Invalid BrowserName. Check the config properties file. "
							+ "(Browser Format: Chrome, Firefox, IE and Safari)");
				}

			}
		}catch(Exception e){
			logger.error("Failed due to "+e.getMessage());
		}
	}
	
	public void ChromeExecution(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		if (isMac())
			System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/Jars/chromedriver");
		else
			System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/Jars/chromedriver.exe");
//		System.setProperty("webdriver.chrome.driver","/Users/FD/Desktop/XBTFW/SprintestCrossBrowserFramework/SeleniumGrid/chromedriver");
		webDriver = new ChromeDriver(options);
		
		if (isMac())
			webDriver.manage().window().fullscreen();
		else
			webDriver.manage().window().maximize();
	}
	
	public void FirefoxExecution(){
		try{
			System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"/Jars/geckodriver.exe");
			ProfilesIni profile = new ProfilesIni(); 
			FirefoxProfile myprofile = profile.getProfile("default");
			webDriver = new FirefoxDriver();
//			webDriver = new FirefoxDriver(myprofile);
			webDriver.manage().window().maximize();
		}
		catch(Exception e){
			logger.error("FF failed to launch due to exception "+e);
		}
	}
	
	public void IEExecution(){
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"/Jars/IEDriverServer.exe");
		webDriver = new InternetExplorerDriver();
		webDriver.manage().window().maximize();
		logger.info("Browser opened successfully");
	}
	
	public void SafariExecution(){
		SafariOptions options = new SafariOptions();
//		options.setUseCleanSession(true);
		webDriver = new SafariDriver(options);
		webDriver.manage().window().maximize();
	}
	
	
	

	public void BrowserStackExecution(String browser, String version){
		String lowercaseBrowser = browser.toLowerCase();
		msgBox("You are now executing scripts on Browser Stack");
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("browser", lowercaseBrowser);
		caps.setCapability("version", version);
		caps.setCapability("platform", Platform);
		caps.setCapability("browserstack.debug", "true");
		caps.setCapability("build", ProjName);
		caps.setCapability("browserstack.local", "true");
		try {
			webDriver = new RemoteWebDriver(new URL(URL), caps);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("Failed to start execution on browser stack due to exception "+e.getMessage());
		}
	}

	public static void msgBox(String msg) {
		javax.swing.JOptionPane.showConfirmDialog((java.awt.Component)
				null, msg, "Alert", javax.swing.JOptionPane.DEFAULT_OPTION);
	}
	
	/*
	 * Is it a Mac OS ?
	 * 	Return: true / false
	 * @arafatmamun
	 */
	public static boolean isMac() {

		return (operatingSystem.toLowerCase().indexOf("mac") >= 0);

	}
}