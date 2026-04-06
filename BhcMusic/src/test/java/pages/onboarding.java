package pages;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.appium.java_client.AppiumBy;

public class onboarding {

    private WebDriver driver;

    public onboarding(WebDriver driver) {
        this.driver = driver;
    }

    public void clickLoginSignup() {

        System.out.println("Waiting for Login/Signup button...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Go with Login/Signup")
        )).click();

        System.out.println("Clicked Login/Signup");
    }

    // Handle Notification Permission Popup
    public void handleNotificationPopup() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement allowBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            AppiumBy.id("com.android.permissioncontroller:id/permission_allow_button")
                    )
            );

            allowBtn.click();
            System.out.println("Notification Permission Allowed");

        } catch (Exception e) {
            System.out.println("Notification popup not displayed");
        }
    }
}