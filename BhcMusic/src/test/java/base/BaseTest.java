package base;

import java.net.URL;
import java.time.Duration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import utils.ExtentManager;

public class BaseTest {

    public static AndroidDriver driver;
    public static ExtentReports extent;   // added

    @BeforeSuite
    public void startReport() {        // added
        extent = ExtentManager.getInstance();
    }

    @BeforeClass
    public void setUp() {
        try {

            UiAutomator2Options options = new UiAutomator2Options();

            options.setPlatformName("Android");
            options.setDeviceName("moto g45 5G");
            options.setUdid("ZN4222MGHC");

            options.setAppPackage("com.bhcmusic.app");
            options.setAppActivity("com.bhcmusic.app.MainActivity");
            options.setAppWaitActivity("*");

            options.setAutomationName("UiAutomator2");

            options.setNoReset(false);
            options.setAutoGrantPermissions(true);
            options.setAppWaitForLaunch(true);

            options.setNewCommandTimeout(Duration.ofSeconds(300));

            driver = new AndroidDriver(
                    new URL("http://127.0.0.1:4723"),
                    options
            );

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            System.out.println("Driver Created: " + driver);
            System.out.println("Current Package: " + driver.getCurrentPackage());
            System.out.println("Current Activity: " + driver.currentActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void endReport() {     // added
        extent.flush();
    }
}