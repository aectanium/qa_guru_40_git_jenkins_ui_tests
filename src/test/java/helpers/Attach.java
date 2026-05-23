package helpers;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.sessionId;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.openqa.selenium.logging.LogType.BROWSER;
import org.openqa.selenium.logging.LogEntries;

public class Attach {
    public static void screenshotAs() {
        byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Last screenshot", "image/png", new java.io.ByteArrayInputStream(screenshot), "png");
    }

    public static void pageSource() {
        String pageSource = getWebDriver().getPageSource();
        Allure.addAttachment("Page source", "text/plain", pageSource, ".txt");
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
            String currentSessionId = String.valueOf(sessionId());
            if (currentSessionId == null || currentSessionId.isEmpty()) {
                System.err.println("DEBUG: No session ID available for video");
                return;
            }

            String videoUrl = getVideoUrl().toString();
            System.err.println("DEBUG: Video URL: " + videoUrl);

            String videoHtml = "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                    + videoUrl
                    + "' type='video/mp4'></video></body></html>";

            Allure.addAttachment("Video", "text/html", videoHtml, ".html");
            System.err.println("DEBUG: Video attachment added to Allure");

        } catch (Exception e) {
            System.err.println("DEBUG: Error adding video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static URL getVideoUrl() {
        String currentSessionId = String.valueOf(sessionId());
        if (currentSessionId == null || currentSessionId.isEmpty()) {
            System.err.println("DEBUG: Session ID is null or empty in getVideoUrl");
            return null;
        }

        String videoUrl = "https://selenoid.autotests.cloud/video/" + currentSessionId + ".mp4";
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
