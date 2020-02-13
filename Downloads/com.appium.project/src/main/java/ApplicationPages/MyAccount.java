package ApplicationPages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import Library.Utility;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class MyAccount extends BasePage {
	public static String pageObject =Utility.getLocator.myAccountId;

	public MyAccount() {
		super(pageObject);
		// TODO Auto-generated constructor stub
	}

	@Test(priority=1)
	public void editAccount()
	{
		
		try {
			test=extend.startTest("MYACCOUNT","ENTERING CREDENTIALS");
			if(!Utility.isElementPresent(Utility.getLocator.myAccountId))
			{
				Utility.setValue(Utility.getLocator.usernameId, Utility.getData.Username);
				Utility.setValue(Utility.getLocator.passwordId, Utility.getData.Password);
				Utility.getWebElement(Utility.getLocator.loginId).click();
				Utility.waitImplicit(25);
				Utility.waitImplicit(30);
			}
			else
			{
				System.out.println(Utility.constant.NotFoundLocator);
			}
				Utility.getWebElement(Utility.getLocator.myAccountId).click();
				Utility.waitImplicit(15);
				Utility.getWebElement(Utility.getLocator.editAccountId).click();
				Utility.waitImplicit(30);
				Date objDate = new Date();
				Utility.setValue(Utility.getLocator.addressId, Utility.getData.Address+objDate.toString());
				Utility.setValue(Utility.getLocator.editAccountId,"TEST"+objDate.toString() );
				Utility.getWebElement(Utility.getLocator.doneId).click();
				Utility.waitImplicit(15);
				Utility.getWebElement(Utility.getLocator.homeID).click();
				Utility.getWebElement(Utility.getLocator.logoutId).click();
				Utility.waitImplicit(30);

		}catch(Exception e)

		{
			System.out.println(Utility.constant.NotFoundLocator);
			Utility.waitImplicit(10);
			Utility.getWebElement(Utility.getLocator.doneId).click();
			Utility.waitImplicit(25);
			Utility.getWebElement(Utility.getLocator.homeID).click();
			Utility.waitImplicit(15);
			if(!Utility.isElementPresent(Utility.getLocator.logoutId)) {
				Utility.getWebElement(Utility.getLocator.homeID).click();	
				Utility.getWebElement(Utility.getLocator.logoutId).click();
			}
			Utility.getWebElement(Utility.getLocator.logoutId).click();
			Utility.waitImplicit(30);
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}
	}

}
