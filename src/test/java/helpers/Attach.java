package helpers;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


import java.net.MalformedURLException;
import java.net.URL;


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
        String videoHtml = "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                + getVideoUrl()
                + "' type='video/mp4'></video></body></html>";
        Allure.addAttachment("Video", "text/html", videoHtml, ".html");
    }

    public static URL getVideoUrl() {
        String videoUrl = "https://selenoid.autotests.cloud/video/" + sessionId() + ".mp4";
        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
