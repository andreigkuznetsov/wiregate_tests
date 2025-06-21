package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import util.DatabaseHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistrationApiTest {

    String uniqueEmail;
    long userId = -1;

    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost:8000";
    }

    @BeforeEach
    void generateEmail() {
        uniqueEmail = "apiuser" + System.currentTimeMillis() + "@example.com";
        userId = -1;
    }

    @Test
    @DisplayName("Позитивный API тест: регистрация пользователя с проверкой в БД")
    void registrationApiPositiveTest() throws Exception {
        String payload = """
                {
                  "first_name": "Jean",
                  "last_name": "Dujardin",
                  "phone_number": "12345679",
                  "email": "%s",
                  "password": "Abcd1234@",
                  "is_agree": true
                }
                """.formatted(uniqueEmail);

        Number idNum = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201)
                .body("first_name", equalTo("Jean"))
                .body("last_name", equalTo("Dujardin"))
                .body("phone_number", equalTo("12345679"))
                .body("email", equalTo(uniqueEmail))
                .body("is_agree", equalTo(true))
                .body("id", notNullValue())
                .extract().path("id");

        userId = idNum.longValue();

        Thread.sleep(500);

        Assertions.assertTrue(
                DatabaseHelper.checkUserInDbById(
                        userId,
                        "Jean",
                        "Dujardin",
                        "12345679",
                        uniqueEmail,
                        "Abcd1234@"
                ),
                "User fields in DB do not match expected values"
        );
    }

    @Test
    @DisplayName("Негативный API тест: регистрация пользователей с одинаковыми email")
    void registrationApiNegativeDuplicateEmailTest() {
        String payload = """
                {
                  "first_name": "Jean",
                  "last_name": "Dujardin",
                  "phone_number": "12345679",
                  "email": "%s",
                  "password": "Abcd1234@",
                  "is_agree": true
                }
                """.formatted(uniqueEmail);

        Number idNum = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201)
                .extract().path("id");
        userId = idNum.longValue();

        given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/users/create")
                .then()
                .statusCode(anyOf(is(400), is(409)));
    }

    @AfterEach
    void cleanup() throws Exception {
        if (userId != -1) {
            DatabaseHelper.deleteUserById(userId);
        }
    }
}