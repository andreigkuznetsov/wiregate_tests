package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationPage {
    public SelenideElement firstName = $("input[name='firstName']");
    public SelenideElement lastName = $("input[name='lastName']");
    public SelenideElement phoneNumber = $("input[name='phoneNumber']");
    public SelenideElement emailAddress = $("input[name='emailAddress']");
    public SelenideElement password = $("input[name='password']");
    public SelenideElement confirmPassword = $("input[name='confirmPassword']");
    public SelenideElement isAgree = $("input[name='isAgree']");
    public SelenideElement registerButton = $x("//button[contains(text(), \"S'inscrire\")]");

    public void shouldBeOnSuccessPage() {
        String currentUrl = webdriver().driver().url();
        assertThat(currentUrl)
                .withFailMessage("Ожидался переход на страницу /success, а url: %s", currentUrl)
                .contains("/success");
    }
}