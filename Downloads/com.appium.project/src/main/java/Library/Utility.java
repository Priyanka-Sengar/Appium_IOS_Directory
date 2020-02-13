package Library;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.ExtentReports;

//import com.relevantcodes.extentreports.ExtentReports;
//import com.relevantcodes.extentreports.ExtentTest;

import Assert.AppData;

import Assert.String_Constant;
import Assert.UILOCATOR;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class Utility  {

	public static AppData getData = new AppData();
	public static String_Constant constant=new String_Constant();
	public static UILOCATOR getLocator = new UILOCATOR (); 
	
	public static AppiumDriver<MobileElement> driver=null;
	//	public static ExtentTest test;
	//
	ExtentReports extend;
	ITestResult testResults;
	public static String file =System.getProperty(Utility.constant.UserDirectory)+Utility.constant.ExtendPath;


	//	//Driver
	//	public static WebDriver getWebDriver(){
	//		return webdriver;
	//	}

	// LOCATOR

	public static WebElement getWebElement(String actualLocator) {
		WebElement element = null;
		try {
			if (actualLocator.startsWith("/")) {
				element = driver.findElement(By.xpath(actualLocator));
			} else {
				try {
					element = driver.findElement(By.id(actualLocator));
				} catch (Exception e) {
					try {
						element = driver.findElement(By.name(actualLocator));
					} catch (Exception e1) {
						try {
							element = driver.findElement(By.cssSelector(actualLocator));
						} catch (Exception e2) {
							try {
								element = driver.findElement(By.className(actualLocator));
								//								} catch (Exception e3) {
								//									try {
								//										element = driver.findElement(By.linkText(actualLocator));
								//									} catch (Exception e4) {
								//										try {
								//											element = driver.findElement(By.partialLinkText(actualLocator));
							}catch (Exception e5) {									
								//										logger.info("Error while finding the Element. Exception: " +e5);
								//FrameworkLogger.error("Get Element","User Should be able to find element "+actualLocator,"Error while finding the Element. Exception: " +e4);
							}
						}
					}
					//						}
					//					}
				}
			}

		} catch (Exception e) {
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
		return element;
	}




	//SENDKEYS
	public static void setValue(String sElementName, Object sValue) {
		WebElement locator = getWebElement(sElementName);
		try {
			if(locator!=null){
				locator.clear();
				//if(BrowserName.equalsIgnoreCase("Safari")) 
				locator.sendKeys(sValue.toString().trim());
				System.out.println("User should be able to enter value in field:"+sValue+"Entered value of '"+sValue+" in the field");
			}else {
				System.out.println( "User should be able to set value:" + sValue+ "User is not able to enter value:" + sValue + " in field as field is not available");
			}
		} catch (Exception e) {
			System.out.println("User should be able to set value:"+sValue+ "User is not able to enter value:"+sValue+" in field due to exception:"+e.getMessage());
		}
	}


	//ISSELECTED

	public static Boolean isElementSelected(String sElement) {
		boolean flag = false;
		try{
			if (getWebElement(sElement)!= null) {
				WebElement element = getWebElement(sElement);
				if (element.isSelected()) {
					flag = true;
				} else {
					flag = false;
				}
			} else {
				flag = false;
			}
		}catch(Exception e){
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
		return flag;
	}

	// IMPLICITWAIT
	public static void waitImplicit(int seconds){
		try{
			driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);


		}catch(Exception e){
			System.out.println("Locator  is not found within "+seconds+" seconds. Exception:"+e.toString());
		}
	}

	// JAVSCRIPTEXECUTOR


	public static void scrollToView(String sLocator){
		try{	

			if(getWebElement(sLocator) != null){
				((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", getWebElement(sLocator));
				//Point coordinates = getWebElement(sLocator).getLocation();
				//((JavascriptExecutor)webDriver).executeScript("window.scrollBy("+coordinates.x+","+coordinates.y+")", "");
			}else{
				System.out.println( "Should scroll to the element"+sLocator+"Failed to scroll the page due to null");
			}
		}catch(Exception e){
			System.out.println("Should scroll to the element "+sLocator+ "Failed to scroll the page due to exception "+e.toString());
		}
	}

	//DesiredCapbilty


	public void swipe(String actualLocator ,String moveToLocator,int wait)
	{
		TouchAction ta = new TouchAction(driver);
		ta.press(getWebElement(actualLocator)).waitAction(wait).moveTo(getWebElement(moveToLocator)).release().perform();
	}


	public void scrollIntoView(String text)
	{

		MobileElement element = driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().resourceId(\"com.android.vending:id/tab_recycler_view\")).getChildByText("
						+ "new UiSelector().className(\"android.widget.TextView\"), \"Games We Are Playing\")"));

		//Perform the action on the element
		element.click();
	}
	public static void desiredSetup() 
	{
		try
		{

			DesiredCapabilities dc=new DesiredCapabilities();
			dc.setCapability(MobileCapabilityType.DEVICE_NAME,"52007920cac2665d");
			dc.setCapability(MobileCapabilityType.PLATFORM_VERSION,"9");
			dc.setCapability("noReset","true");
			dc.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT ,"25");
			dc.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
			dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.csi.vanguard");
			dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.csi.vanguard.ui.SplashScreen");
			dc.setCapability("autoGrantPermissions", "true");
			dc.setCapability("noSign", "true");
			driver=new AndroidDriver<>(new URL("http://127.0.0.1:4727/wd/hub"),dc);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}catch(Exception e)
		{
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
	}
	//SCREENSHOT
	public static void screenShot(String png)
	{
		try
		{
			TakesScreenshot screenshot =(TakesScreenshot)driver;
			File src=screenshot.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src,new File( System.getProperty(Utility.constant.UserDirectory)+"/test-output/"+png+".png"));

		}catch(Exception e) {

		}
	}

	// IS DISPLAYED

	public static Boolean isElementPresent(String Element){
		boolean flag = false ;
		try{			
			if (getWebElement(Element) != null) {
				WebElement element = getWebElement(Element);
				if (element.isDisplayed()) {
					flag = true;
				}
				else {
					flag = false;
				}
			}
		}catch(Exception e){
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
		return flag;
	}
	//EXPLICT WAIT 

	public static void explictWait(String Loctor)
	{
		try
		{
			WebDriverWait wait = new WebDriverWait(Utility.driver, 45);
			WebElement messageElement = wait.until( ExpectedConditions.presenceOfElementLocated(By.id(Loctor)) );	
		}catch(Exception e)
		{
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
	}

	//SCROLL IN MOBILE 

	public static void scrollTouch(WebElement data,int a,int b)
	{

		TouchActions action = new TouchActions(driver);
		action.scroll(data,a,b);
		action.perform();
	}

	public static String getScreenShot(WebDriver driver,String screenshotName)
	{
		String dataName =new SimpleDateFormat("yyyyMMddmmss").format(new Date());
		TakesScreenshot ts=(TakesScreenshot)driver;
		File Source =ts.getScreenshotAs(OutputType.FILE);
		String des=System.getProperty(Utility.constant.UserDirectory)+"/TESTCASES_IMAGES/"+screenshotName+dataName+".png";
		File finalDes=new File(des);
		try {
			FileUtils.copyFile(Source, finalDes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return des;
	}


	public void hideKeyboard(){
		driver.hideKeyboard();
	}

	public static void swipe(int startx,int starty,int duration, int endx ,int endy)
	{
		TouchAction action = new TouchAction(driver);
		action.press(startx, starty).waitAction(duration).moveTo(endx, endy).release().perform();
	}
	
	


}
