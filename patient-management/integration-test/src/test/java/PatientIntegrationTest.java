import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost:8083";
    }

    @Test
    public void shouldReturnAllPatientsOnSuccessfulLogin() {
        String payLoad = """
                {
                    "email": "test1@test1.com",
                    "password": "password12"
                }
                """;

        String token = given()
                .contentType("application/json")
                .body(payLoad)
                .when()
                .post("api/user/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

            given()
                    .header("Authorization", "Bearer "+token)
                    .when().get("api/patients")
                    .then()
                    .statusCode(200)
                    .body("patients", notNullValue());
    }
}
