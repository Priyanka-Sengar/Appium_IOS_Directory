package TestCases;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Global.GlobalTest;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class MyAccountTest extends GlobalTest {
	
	@BeforeClass(alwaysRun = true)

	public void setup() throws InterruptedException, MalformedURLException {

		//		setUp();

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
			driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

	}
	@Test
	public void editAccount()
	{
		myaccount().editAccount();
	}
}
