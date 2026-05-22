package tests;
import helpers.Attach;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.remote.DesiredCapabilities;
import pages.RegistrationPage;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@ExtendWith(TestBase.FailureHandler.class)
public class TestBase {

    RegistrationPage registrationPage = new RegistrationPage();

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeAll
    static void setupSelenideConfig() {

        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserVersion = System.getProperty("browserVersion", "126.0");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.baseUrl = System.getProperty("baseUrl", "https://demoqa.com");
        Configuration.pageLoadStrategy = "eager";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        String environment = System.getProperty("environment", "demo");
        String remoteUrl = System.getProperty("remoteUrl");

        System.out.println("Environment: " + environment);
        System.out.println("Browser: " + Configuration.browser + " " + Configuration.browserVersion);
        System.out.println("Headless: " + Configuration.headless);

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            System.out.println("Using remote WebDriver: " + remoteUrl);

            DesiredCapabilities capabilities = new DesiredCapabilities();

            boolean enableVideo = Boolean.parseBoolean(System.getProperty("enableVideo", "false"));
            Map<String, Object> selenoidOptions = Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", enableVideo,
                    "name", "UI Tests - " + environment,
                    "build", System.getProperty("buildVersion", "local")
            );

            capabilities.setCapability("selenoid:options", selenoidOptions);
            Configuration.browserCapabilities = capabilities;
            Configuration.remote = remoteUrl;
        } else {
            System.out.println("Using local WebDriver");
        }
    }

    @AfterEach
    void addAttachments() {
        System.out.println("=== @AfterEach started ===");
        try {
            System.out.println("Taking screenshot...");
            Attach.screenshotAs();
            System.out.println("Screenshot taken");

            System.out.println("Getting page source...");
            Attach.pageSource();
            System.out.println("Page source taken");

            System.out.println("Getting browser console logs...");
            Attach.browserConsoleLogs();
            System.out.println("Browser logs taken");

            if (Boolean.parseBoolean(System.getProperty("enableVideo", "false"))) {
                System.out.println("Adding video...");
                Attach.addVideo();
                System.out.println("Video added");
            }
        } catch (Exception e) {
            System.err.println("Error during attachment: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Closing WebDriver...");
            closeWebDriver();
            System.out.println("=== @AfterEach finished ===");
        }
    }

    static class FailureHandler implements TestExecutionExceptionHandler {
        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            System.out.println("=== FailureHandler started - Test Failed! ===");

            try {
                System.out.println("Failure: Taking screenshot...");
                Attach.screenshotAs();
                System.out.println("Failure: Screenshot taken");

                System.out.println("Failure: Getting page source...");
                Attach.pageSource();
                System.out.println("Failure: Page source taken");

                System.out.println("Failure: Getting browser console logs...");
                Attach.browserConsoleLogs();
                System.out.println("Failure: Browser logs taken");

                if (Boolean.parseBoolean(System.getProperty("enableVideo", "false"))) {
                    System.out.println("Failure: Adding video...");
                    Attach.addVideo();
                    System.out.println("Failure: Video added");
                }
            } catch (Exception e) {
                System.err.println("Error during attachment on failure: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("=== FailureHandler finished - Re-throwing exception ===");

            throw throwable;
        }
    }
}
