import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;


public class AuthIntegrationTest {

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost:8083";
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        String payLoad = """
                {
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;
        Response response = given()
                .contentType("application/json")
                .body(payLoad)
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        System.out.println("Generated token: " + response.jsonPath().getString("token"));
    }
}
