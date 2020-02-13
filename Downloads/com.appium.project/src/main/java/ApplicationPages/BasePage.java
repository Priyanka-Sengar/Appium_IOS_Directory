package ApplicationPages;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Priority;
import org.junit.AfterClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.markuputils.Markup;
//import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Library.Utility;
import Reporting.ReportNG;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class BasePage  {
	private String pageValidation;

	public BasePage(String pageObject){
		this.pageValidation=pageObject;	
	}	

	public  ExtentTest test;
	//	public static ExtentHtmlReporter htmlReporter;
	public  ExtentReports extend;
	public ITestResult testResults;
	String file =System.getProperty("user.dir")+"/test-output/ExtentReportResults.html";



	@BeforeSuite
	public void setup() throws InterruptedException, MalformedURLException {

		//		setUp();

		try
		{
			test=extend.startTest("Desired Capablilty","Setting the proeprties");
			DesiredCapabilities dc=new DesiredCapabilities();
			dc.setCapability(MobileCapabilityType.DEVICE_NAME,Utility.getData.DeviceName);
			dc.setCapability(MobileCapabilityType.PLATFORM_VERSION,Utility.getData.PlatformVersion);
			dc.setCapability("noReset",Utility.constant.desiredCapability_true());
			dc.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT ,Utility.getData.CommandTimeOut);
			dc.setCapability(MobileCapabilityType.PLATFORM_NAME, Utility.getData.PlatformName);
			dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, Utility.getData.AppPackage);
			dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, Utility.getData.AppActivity);
			dc.setCapability("autoGrantPermissions", Utility.constant.desiredCapability_true());
			dc.setCapability("noSign", Utility.constant.desiredCapability_true());
			Utility.driver=new AndroidDriver<>(new URL(Utility.constant.AppiumUrl),dc);
			//			driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
			Utility.waitImplicit(10);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	//	@AfterTest
	//	public void endExtend()
	//	{
	//		extend.flush();
	//		extend.close();
	//	}

	@BeforeTest
	public void ExtendReportSetUp()
	{

		//		htmlReporter = new ExtentHtmlReporter(file);
		extend= new ExtentReports (System.getProperty("user.dir")+"/test-output/ExtentReportResults.html");
		extend.addSystemInfo("Envioremnt", "QA");
	}


	//	@BeforeTest
	//	public void extendReport()
	//	{
	//		htmlReporter = new ExtentHtmlReporter(file);
	//		extend= new ExtentReports ();
	//		extend.attachReporter(htmlReporter);
	//		htmlReporter.config().setReportName("Regression");
	//		htmlReporter.config().setTheme(Theme.STANDARD);
	//		htmlReporter.config().setDocumentTitle("EXTENDS REPORTS");
	//
	//	}

	@AfterMethod
	public void teardownReport(ITestResult testResults) throws InterruptedException{
		try {

			if(testResults.getStatus()==ITestResult.FAILURE)
			{
				test.log(LogStatus.FAIL, Utility.constant.TestCasesFailed+testResults.getName());
				test.log(LogStatus.FAIL, testResults.getThrowable());
				String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
				test.log(LogStatus.FAIL,test.addScreencast(screenShot));

			}else if(testResults.getStatus()==ITestResult.SUCCESS)
			{
				test.log(LogStatus.PASS, Utility.constant.TestCasesPassed+testResults.getName());
				test.log(LogStatus.PASS, testResults.getThrowable());
				String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
				test.log(LogStatus.PASS, test.addScreencast(screenShot));
			}
			else
			{
				test.log(LogStatus.SKIP, Utility.constant.TestCasesPassed+testResults.getName());;
				test.log(LogStatus.SKIP, testResults.getThrowable());
				String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
				test.log(LogStatus.SKIP,test.addScreencast(screenShot));
			}

		}catch(Exception e)
		{
			extend.flush();
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
	}


	//	@AfterMethod
	//	public void checkResult(ITestResult testResults)
	//	{
	//		if(testResults.getStatus()==ITestResult.FAILURE)
	//		{
	//			test.log(Status.FAIL, "Test Case is Falied becuse of becasue of below problem");
	//			test.log(Status.FAIL, testResults.getThrowable());
	//			test.log(Status.SKIP, Utility.constant.TestCasesPassed+testResults.getName());;
	//			test.log(Status.SKIP, testResults.getThrowable());
	//			String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
	//			try {
	//				test.log(Status.SKIP,(Markup) test.addScreenCaptureFromPath(screenShot));
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//
	//		}else if(testResults.getStatus()==ITestResult.SUCCESS)
	//		{
	//			test.log(Status.PASS, "TestCases is Passed");
	//			test.log(Status.SKIP, Utility.constant.TestCasesPassed+testResults.getName());;
	//			test.log(Status.SKIP, testResults.getThrowable());
	//			String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
	//			try {
	//				test.log(Status.SKIP,(Markup) test.addScreencastFromPath(screenShot));
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//		else
	//		{
	//			test.log(Status.SKIP, Utility.constant.TestCasesPassed+testResults.getName());;
	//			test.log(Status.SKIP, testResults.getThrowable());
	//			String screenShot=Utility.getScreenShot(Utility.driver, testResults.getName());
	//			try {
	//				test.log(Status.SKIP,(Markup) test.addScreencastFromPath(screenShot));
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//
	//	}

}








