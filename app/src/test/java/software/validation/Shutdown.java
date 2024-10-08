package software.validation;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterAll;
import static io.restassured.RestAssured.*;

public class Shutdown {

    /*
     * GET for shutdown of application
     */
    @AfterAll
    public static void shutdown() throws InterruptedException {
        Response response = given()
            .when()
            .get("http://localhost:4567/shutdown");

        }
            
    }

