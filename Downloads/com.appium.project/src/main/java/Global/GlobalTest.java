package Global;

import java.net.MalformedURLException;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import ApplicationPages.MyAccount;
import ApplicationPages.loginPage;
import Library.Utility;
@Listeners(Reporting.ReportNG.class)
public class GlobalTest extends Utility {

	
	private static loginPage login = null;
	private static MyAccount myaccount= null;
//	@BeforeTest(alwaysRun = true) 
//	public void setUp() throws MalformedURLException, InterruptedException{
//		setup();
//	}
//	@AfterClass(alwaysRun = true) 
//	public void teardown(ITestResult testResults) throws InterruptedException{
//		
//		if(testResults.getStatus()==ITestResult.FAILURE)
//		{
//			screenShot("EXECUTION_DATA");
//		
//			test.log(Status.FAIL, "Test Case is Falied becuse of becasue of below problem");
//			test.log(Status.FAIL, testResults.getThrowable());
//
//		}else if(testResults.getStatus()==ITestResult.SUCCESS)
//		{
//			test.log(Status.PASS, "TestCases is Passed");
//		}
//		else
//		{
//			test.log(Status.SKIP, testResults.getThrowable());
//		}
//	}
	
	public loginPage login(){
		if(login==null)
			login =new loginPage();
		return login;
	}
	
	public MyAccount myaccount(){
		if(myaccount==null)
			myaccount =new MyAccount();
		return myaccount;
	}
}
