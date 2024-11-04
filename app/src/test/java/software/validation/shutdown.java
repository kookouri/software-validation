package software.validation;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;

public class shutdown {

    public static void shutdownMethod() {
                try {
                        @SuppressWarnings("unused")
                        Response response = given()
                                        .get("http://localhost:4567/shutdown");

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
    
}
