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
    static void beforeAll() {

        Configuration.baseUrl = System.getProperty("baseUrl", "https://demoqa.com");


        String browser = System.getProperty("browser", "chrome");
        String browserVersion = System.getProperty("browserVersion", "100.0");
        Configuration.browser = browser;
        Configuration.browserVersion = browserVersion;


        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");


        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));


        String remote = System.getProperty("remote", "");
        if (remote != null && !remote.isEmpty()) {
            Configuration.remote = remote;
        }


        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;
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