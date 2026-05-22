package tests;
import helpers.Attach;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;
import pages.RegistrationPage;

import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    RegistrationPage registrationPage = new RegistrationPage();

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeAll
    static void setupSelenideConfig() {
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.baseUrl = System.getProperty("baseUrl", "https://demoqa.com");
        Configuration.pageLoadStrategy = "eager";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        String remoteUrl = System.getProperty("remoteUrl");
        String environment = System.getProperty("environment", "demo");

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            Configuration.browserVersion = System.getProperty("browserVersion", "128.0");
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
            System.out.println("Using local WebDriver - letting Selenium Manager determine version");
            Configuration.browserVersion = "";
        }

        System.out.println("=== Configuration ===");
        System.out.println("Browser: " + Configuration.browser);
        System.out.println("Version: " + Configuration.browserVersion);
        System.out.println("Remote: " + Configuration.remote);
        System.out.println("========================");
    }

    @AfterEach
    void addAttachments() {
        Attach.screenshotAs();
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();
        closeWebDriver();
    }
}
