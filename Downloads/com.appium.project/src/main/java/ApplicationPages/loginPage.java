package ApplicationPages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import com.google.common.collect.ImmutableMap;

import Library.Utility;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class loginPage extends BasePage{


	public static String pageObject =Utility.getLocator.hacSymbolId;
	public loginPage() {
		//		//validate home page with pageObject
		super(pageObject);
	}

	@Test(priority=1)
	public void credentials()
	{
//test=extend.createTest("LOGIN PAGE","ENTERING CREDENTIALS");
		try {
			test=extend.startTest("LOGIN PAGE","ENTERING CREDENTIALS");

			//LOGIN
			if(!Utility.isElementPresent(Utility.getLocator.myAccountId))
			{
				Utility.setValue(Utility.getLocator.usernameId, Utility.getData.Username);
				Utility.setValue(Utility.getLocator.passwordId, Utility.getData.Password);
				Utility.getWebElement(Utility.getLocator.loginId).click();
				Utility.waitImplicit(30);

			}
			else
			{
				System.out.println(Utility.constant.NotFoundLocator);
			}

		}catch(Exception e)
		{
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}


	}

	@Test(priority=2)
	public void credentialsFail()
	{
		try {

			//LOGIN
			if(Utility.isElementPresent(Utility.getLocator.myAccountId))
			{
				Utility.getWebElement(Utility.getLocator.homeID).click();
				Utility.waitImplicit(30);
				Utility.getWebElement(Utility.getLocator.logoutId).click();
				Utility.waitImplicit(30);
			}

			else
			{
				System.out.println(Utility.constant.NotFoundLocator);
			}
			Utility.setValue(Utility.getLocator.usernameId, Utility.getData.UsernameFail);
			Utility.setValue(Utility.getLocator.passwordId, Utility.getData.PasswordFail);
			Utility.getWebElement(Utility.getLocator.loginId).click();
			Utility.waitImplicit(30);

		}catch(Exception e)
		{
			System.out.println(Utility.constant.FailedException+e.getMessage());
		}


	}
}
