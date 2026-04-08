package tests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import base.BaseTest;
import pages.onboarding;
import pages.login;
import pages.HomePage;

public class LoginTest extends BaseTest {

    @Test
    public void navigateToEmailLogin() {
        // Create test in report
        ExtentTest test = extent.createTest("Login Test");

        try {
            test.info("App is launched successfully");

            onboarding onboard = new onboarding(driver);
            onboard.clickLoginSignup();
            test.info("Clicked Login/Signup button");

            login loginPage = new login(driver);
            loginPage.loginWithEmail();
            test.info("Entered email & password");

            test.pass("Login completed successfully");

        } catch (Exception e) {
            test.fail("Test Failed : " + e.getMessage());
            throw e;
        }
    }

    @Test(dependsOnMethods = "navigateToEmailLogin")
    public void playNewMusicReleaseSong() {
        ExtentTest test = extent.createTest("Play New Music Release Song");

        try {
            test.info("Navigating to HomePage and playing song: Eji-Ogbe");

            HomePage homePage = new HomePage(driver);
            homePage.playSongWithPopupHandling("Eji-Ogbe");

            test.pass("Song started playing successfully (popup handled)");

        } catch (Exception e) {
            test.fail("Test Failed: " + e.getMessage());
            throw e;
        }
    }
}
