package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;

@Story("Форма регистрации")
public class RegistrationWithPageObjectTests extends TestBase {

    @Test
    @DisplayName("Успешная регистрация")
    void successfulRegistrationTest() {
        step("Открыть страницу регистрации", () ->
                registrationPage.openPage());
        step("Заполнить форму регистрации", () -> {
            registrationPage
                    .setFirstName("Alex")
                    .setLastName("Egorov")
                    .setEmail("alex@egorov.com")
                    .setGender("Other")
                    .setUserNumber("1234567890")
                    .setDateOfBirth("30", "July", "2008");
            $("#subjectsInput").setValue("Math").pressEnter();
            $("#hobbiesWrapper").$(byText("Sports")).click();
            $("#uploadPicture").uploadFromClasspath("img/1.png");
            $("#currentAddress").setValue("Some address 1");
            $("#state").click();
            $("#stateCity-wrapper").$(byText("NCR")).click();
            $("#city").click();
            $("#stateCity-wrapper").$(byText("Delhi")).click();
        });
        step("Отправить форму", () -> {
            $("#submit").click();
        });
        step("Проверить результаты формы регистрации", () -> {
            step("Проверить появление компонента с результатами регистрации", () -> {
                $(".modal-dialog").should(appear);
                $("#example-modal-sizes-title-lg").shouldHave(text("Thanks for submitting the form"));
            });
            registrationPage.checkResult("Student Name", "Alex Egorov")
                    .checkResult("Student Email", "alex@egorov.com");
        });
    }

    @Test
    @DisplayName("Неуспешная регистрация")
    void brokenRegistrationTest() {
        step("Открыть страницу регистрации", () ->
                registrationPage.openPage());

        step("Заполнить форму регистрации", () -> {
            registrationPage
                    .setFirstName("Alex")
                    .setLastName("Egorov")
                    .setGender("Other")
                    .setUserNumber("1234567890");
        });
        step("Отправить форму", () -> {
            $("#submit").click();
        });

        step("Проверить результаты формы регистрации", () -> {
            step("Проверить появление компонента с результатами регистрации", () -> {
                $(".modal-dialog").should(appear);
                $("#example-modal-sizes-title-lg").shouldHave(text("Thanks for submitting the form"));
            });

            registrationPage.checkResult("Student Name", "Alex Egorov")
                    .checkResult("Student Email", "alex111@egorov.com");
        });
    }
}