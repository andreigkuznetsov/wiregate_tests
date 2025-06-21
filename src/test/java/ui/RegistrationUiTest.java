package ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import pages.RegistrationPage;
import util.DatabaseHelper;

import static com.codeborne.selenide.Selenide.open;

public class RegistrationUiTest {
    RegistrationPage regPage = new RegistrationPage();
    String testEmail = "uiuser" + System.currentTimeMillis() + "@example.com";
    long userId = -1;

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "http://localhost:3000";
    }

    @Test
    @DisplayName("Позитивный UI тест: регистрация пользователя с проверкой в БД")
    void registrationViaUi_savedToDb() throws Exception {
        open("/");

        regPage.firstName.setValue("Pierre");
        regPage.lastName.setValue("Dupont");
        regPage.phoneNumber.setValue("12345678");
        regPage.emailAddress.setValue(testEmail);
        regPage.password.setValue("Abcd1234@");
        regPage.confirmPassword.setValue("Abcd1234@");
        regPage.isAgree.setSelected(true);
        regPage.registerButton.shouldBe(Condition.enabled).click();
        regPage.shouldBeOnSuccessPage();

        Thread.sleep(500);

        userId = DatabaseHelper.findIdByEmail(testEmail);

        Assertions.assertTrue(
                DatabaseHelper.checkUserInDbById(
                        userId,
                        "Pierre",
                        "Dupont",
                        "12345678",
                        testEmail,
                        "Abcd1234@"
                ),
                "User fields in DB do not match expected values"
        );
    }

    @AfterEach
    void cleanup() throws Exception {
        if (userId != -1) {
            DatabaseHelper.deleteUserById(userId);
        }
    }
}