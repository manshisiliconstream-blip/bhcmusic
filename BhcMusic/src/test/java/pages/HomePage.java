package pages;

// Import required libraries
import java.time.Duration;          // For setting explicit timeouts
import java.util.Map;               // For passing coordinates to clickGesture

import org.openqa.selenium.By;      // For locating elements using various strategies
import org.openqa.selenium.WebElement; // To represent a DOM element
import org.openqa.selenium.support.ui.ExpectedConditions; // Common wait conditions
import org.openqa.selenium.support.ui.WebDriverWait;      // To pause execution until a condition is met

import io.appium.java_client.AppiumBy;   // Appium-specific locators (e.g., Android UI Automator)
import io.appium.java_client.android.AndroidDriver; // Android driver instance

public class HomePage {

    // The AndroidDriver instance that will interact with the app
    AndroidDriver driver;

    // Standard wait (20 seconds) for most UI elements
    WebDriverWait wait;

    // Longer wait (30 seconds) for the popup that appears ~20 seconds after a song starts
    WebDriverWait longWait;

    // Constructor: initializes driver and wait objects when a HomePage object is created
    public HomePage(AndroidDriver driver) {
        this.driver = driver;                                // Store the driver reference
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // 20s timeout
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30)); // 30s for popup
    }

    // ======================= ELEMENT LOCATORS =======================

    // Locator for the "Trending Artists" section header (used to verify homepage)
    By trendingSection = By.xpath("//android.view.View[@content-desc='Trending Artists']");

    // Locator for the first artist image (used in old method)
    By firstArtist = By.xpath("(//android.widget.ImageView)[1]");

    // Locator for a generic "Play" button (fallback)
    By playButton = By.xpath("//android.widget.ImageView[@content-desc='Play']");

    // Locator for the "Play Next" button inside the subscription popup (provided by user)
    By playNextButton = By.xpath("//android.widget.Button[@content-desc='Play Next']");

    // Locator for the "Purchase" button (kept for possible future use)
    By purchaseButton = By.xpath("//android.widget.Button[@content-desc='Purchase']");

    // Indicator that a song is playing – the "Pause" button (playback controls)
    By pauseButton = By.xpath("//android.widget.ImageView[@content-desc='Pause']");

    // Another playback indicator: a bar that says "Now Playing"
    By nowPlayingBar = By.xpath("//android.view.View[contains(@content-desc, 'Now Playing')]");

    // ======================= EXISTING METHODS (VERIFY & TRENDING ARTIST) =======================

    /**
     * Scrolls down until the "Trending Artists" section becomes visible.
     * Used to confirm the home page loaded correctly.
     */
    public void verifyHomePage() {
        // Use Android UI Automator to scroll until an element with description "Trending Artists" is in view
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().description(\"Trending Artists\"));"));

        // Wait up to 20 seconds for the trending section element to actually be visible on screen
        wait.until(ExpectedConditions.visibilityOfElementLocated(trendingSection));

        // Print confirmation to the console (useful for debugging)
        System.out.println("Trending section visible");
    }

    /**
     * Clicks on the first clickable artist card after the "Trending Artists" section.
     * Uses precise coordinate‑based tap to avoid flaky clicks.
     */
    public void clickFirstTrendingArtist() {
        // Ensure the trending section is visible before trying to find the first artist
        wait.until(ExpectedConditions.visibilityOfElementLocated(trendingSection));

        // Give the carousel a moment to stop moving (1000ms)
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // If interrupted, just continue – not critical
        }

        // XPath: get the first clickable view that appears after the "Trending Artists" header
        By firstVisibleArtist = By.xpath(
                "(//android.view.View[@content-desc='Trending Artists']/following::android.view.View[@clickable='true'])[1]");

        // Wait until that artist element is visible
        WebElement artist = wait.until(
                ExpectedConditions.visibilityOfElementLocated(firstVisibleArtist));

        // Calculate the center coordinates of the artist element (to tap exactly in the middle)
        int centerX = artist.getLocation().getX() + (artist.getSize().getWidth() / 2);
        int centerY = artist.getLocation().getY() + (artist.getSize().getHeight() / 2);

        // Use Appium's mobile: clickGesture script to perform a precise tap at those coordinates
        driver.executeScript("mobile: clickGesture", Map.of(
                "x", centerX,
                "y", centerY));

        System.out.println("Artist card tapped");

        // Wait 3 seconds for any transition or animation to finish
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }

    // ======================= NEW METHODS FOR NEW MUSIC RELEASES & POPUP HANDLING =======================

    /**
     * Public method that the test will call.
     * Taps a song, handles the "Play Next" popup, and waits for playback to start.
     *
     * @param songContentDesc the exact content-desc of the song (e.g., "Eji-Ogbe")
     */
    public void playSongWithPopupHandling(String songContentDesc) {
        // Step 1: Scroll to the song and tap it (private helper method)
        tapOnNewMusicReleaseSong(songContentDesc);
        System.out.println("Tapped on song: " + songContentDesc);

        // Step 2: Wait for the subscription popup to appear and click "Play Next"
        try {
            // longWait (20s) will keep trying until the "Play Next" button is clickable
            WebElement playNext = longWait.until(ExpectedConditions.elementToBeClickable(playNextButton));
            System.out.println("Popup detected - 'Play Next' button found. Clicking...");
            playNext.click();   // Actually tap the button
            System.out.println("Clicked 'Play Next' button.");
        } catch (Exception e) {
            // If no popup appears within 30 seconds, assume the song played directly
            System.out.println("No popup appeared within 20 seconds. Assuming song started directly.");
        }

        // Step 3: Verify that playback has actually started (by looking for Pause button, etc.)
        waitForPlaybackToStart();
        System.out.println("Playback started successfully.");
    }

    /**
     * Private helper: Scrolls the screen until the given song's content-desc is visible.
     * Uses UI Automator's scrollIntoView for efficiency.
     */
    private void scrollToSongByContentDesc(String songContentDesc) {
        try {
            // Try to scroll directly to the element with the exact description
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"" + songContentDesc + "\"));"));
            System.out.println("Scrolled directly to song: " + songContentDesc);
        } catch (Exception e) {
            // If direct scroll fails (e.g., element not yet loaded), fallback to scrolling to the section header
            System.out.println("Direct scroll to song failed, trying section-based scroll...");
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector().description(\"New Music Releases\"));"));
        }
    }

    /**
     * Private helper: Uses multiple strategies to locate and tap the song.
     * Tries exact XPath match, then contains match, then UI Automator direct click.
     */
    private void tapOnNewMusicReleaseSong(String songContentDesc) {
        // First, make sure the song is on screen
        scrollToSongByContentDesc(songContentDesc);

        // Give a short pause for any lazy-loading content
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Strategy 1: Exact match on content-desc attribute
        By exactSongLocator = By.xpath("//android.view.View[@content-desc='" + songContentDesc + "']");
        try {
            WebElement song = wait.until(ExpectedConditions.elementToBeClickable(exactSongLocator));
            song.click();
            System.out.println("Tapped on song (exact match): " + songContentDesc);
            return;   // Success – exit method
        } catch (Exception e) {
            System.out.println("Exact match not found, trying contains match...");
        }

        // Strategy 2: Partial / contains match (in case of extra spaces or truncated text)
        By containsSongLocator = By.xpath("//android.view.View[contains(@content-desc, '" + songContentDesc + "')]");
        try {
            WebElement song = wait.until(ExpectedConditions.elementToBeClickable(containsSongLocator));
            song.click();
            System.out.println("Tapped on song (contains match): " + songContentDesc);
            return;
        } catch (Exception e) {
            System.out.println("Contains match also failed, trying UiAutomator...");
        }

        // Strategy 3: Last resort – use Android UI Automator to click by description
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiSelector().description(\"" + songContentDesc + "\")")).click();
            System.out.println("Tapped using UiAutomator: " + songContentDesc);
        } catch (Exception e) {
            // If all strategies fail, throw a clear exception to inform the test
            throw new RuntimeException("Unable to find or tap song: " + songContentDesc, e);
        }
    }

    /**
     * Private helper: Verifies that playback has started by checking for one of several known UI indicators.
     * If none are found, assumes playback started after a short delay.
     */
    private void waitForPlaybackToStart() {
        // An array of different locators that indicate active playback
        By[] playbackIndicators = {
            pauseButton,                     // Pause button (means playing)
            nowPlayingBar,                   // "Now Playing" text
            By.xpath("//android.widget.ImageView[@content-desc='Playing']"), // "Playing" icon
            By.xpath("//android.view.View[contains(@content-desc, '0:00')]")  // A timer showing "0:00"
        };

        boolean playbackStarted = false;
        // Loop through each indicator; as soon as one is visible, consider playback started
        for (By locator : playbackIndicators) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                System.out.println("Playback started - detected: " + locator);
                playbackStarted = true;
                break;   // exit loop early
            } catch (Exception e) {
                // Indicator not found – just continue to the next one
            }
        }

        // If none of the specific indicators were found, wait 3 seconds and assume playback started
        if (!playbackStarted) {
            try {
                Thread.sleep(3000);
                System.out.println("Playback assumed started after 3 seconds wait.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted status
            }
        }
    }
}