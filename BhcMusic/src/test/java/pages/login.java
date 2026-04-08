package pages;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class login {

    private AndroidDriver driver;

    // Constructor
    public login(AndroidDriver driver) {
        this.driver = driver;
    }

    public void loginWithEmail() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Click Continue With Email
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Continue With Email")
        )).click();

// <<<<<<< HEAD
//         System.out.println("Clicked Continue With your email address");
// =======
        System.out.println("Clicked Continue With Email Address");
>>>>>>> main

     // Enter Email
        WebElement emailField = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("(//android.widget.EditText)[1]")
            )
        );
        emailField.click();
        emailField.sendKeys("anshika.siliconstream@gmail.com");

        // Enter Password
        WebElement passwordField = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("(//android.widget.EditText)[2]")
            )
        );
        passwordField.click();
        passwordField.sendKeys("123456"); 
//        click login button
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.xpath("//android.widget.Button[@content-desc='LOGIN']")
        )).click();
        // Clicked Login button

     // Wait for Home screen
        wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//android.widget.ScrollView")
        ));
        System.out.println("Login Successful");
    }
}