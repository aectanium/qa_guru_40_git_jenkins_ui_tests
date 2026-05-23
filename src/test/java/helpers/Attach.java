package helpers;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class Attach {

    public static String getSessionId() {
        try {
            WebDriver driver = getWebDriver();
            if (driver != null) {
                return ((org.openqa.selenium.remote.RemoteWebDriver) driver).getSessionId().toString();
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Error getting session ID: " + e.getMessage());
        }
        return null;
    }

    public static void screenshotAs() {
        try {
            byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Last screenshot", "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            System.err.println("DEBUG: Error taking screenshot: " + e.getMessage());
        }
    }

    public static void pageSource() {
        try {
            String pageSource = getWebDriver().getPageSource();
            Allure.addAttachment("Page source", "text/plain", pageSource, ".txt");
        } catch (Exception e) {
            System.err.println("DEBUG: Error getting page source: " + e.getMessage());
        }
    }

    public static void attachAsText(String attachName, String message) {
        Allure.addAttachment(attachName, "text/plain", message, ".txt");
    }

    public static void browserConsoleLogs() {
        try {
            LogEntries logEntries = getWebDriver().manage().logs().get(BROWSER);
            attachAsText(
                    "Browser console logs",
                    String.join("\n", logEntries.getAll().stream().map(Object::toString).toList())
            );
        } catch (Exception e) {
            attachAsText("Browser console logs", "No logs available: " + e.getMessage());
        }
    }

    public static void addVideo() {
        try {
            String currentSessionId = getSessionId();
            if (currentSessionId == null || currentSessionId.isEmpty()) {
                System.err.println("DEBUG: No session ID available for video");
                attachAsText("Video Info", "Video not available: No session ID (local run?)");
                return;
            }

            URL videoUrl = getVideoUrl(currentSessionId);
            if (videoUrl == null) {
                System.err.println("DEBUG: Failed to construct video URL");
                attachAsText("Video Info", "Video not available: Failed to construct URL");
                return;
            }

            String videoUrlString = videoUrl.toString();
            System.err.println("DEBUG: Video URL: " + videoUrlString);

            String videoHtml = "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                    + videoUrlString
                    + "' type='video/mp4'></video></body></html>";

            Allure.addAttachment("Video", "text/html", videoHtml, ".html");
            System.err.println("DEBUG: Video attachment added to Allure");

        } catch (Exception e) {
            System.err.println("DEBUG: Error adding video: " + e.getMessage());
            attachAsText("Video Error", "Failed to add video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static URL getVideoUrl(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            System.err.println("DEBUG: Session ID is null or empty in getVideoUrl");
            return null;
        }

        String videoUrl = "https://selenoid.autotests.cloud/video/" + sessionId + ".mp4";
        System.err.println("DEBUG: Constructed video URL: " + videoUrl);

        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            System.err.println("DEBUG: Malformed video URL: " + videoUrl);
            e.printStackTrace();
        }
        return null;
    }
}
