package software.validation;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:4567";
    }
   

    @Test
    public void serverInitiation() {
        Response response = given()
                .when()
                .get("/")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());

    }

    @Test
    /*
     * Test head todos
     */
    public void testHeadTodos() {
        Response response = given()
                .when()
                .head("/todos")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().isEmpty());

    }

    @Test
    /*
     * Testing the get todos
     */
    public void testGetTodos() {
        Response response = given()
                .when()
                .get("/todos")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());

    }

    @Test
    /*
     * Testing the post todo for both xml and json
     */
    public void testPostTodos() {
        String request1 = "{\"title\": \"Staple Paperwork\", \"doneStatus\": false, \"description\": \"Filing paperwork\" }";
        String request2 = "<todo>" + "<title> Staple Paperwork</title>" + "<doneStatus>false</doneStatus>"
                + "<description> Filing paperwork </description>" + "</todo>";
        Response response1 = given()
                .header("Content-Type", "application/json")
                .body(request1)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        Response response2 = given()
                .header("Content-Type", "application/xml")
                .body(request2)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        assertEquals(201, response1.statusCode());
        assertNotNull(response1.jsonPath().getString("id"));
        assertEquals("Staple Paperwork", response1.jsonPath().getString("title"));
        assertEquals("false", response1.jsonPath().getString("doneStatus"));
        assertEquals("Filing paperwork", response1.jsonPath().getString("description"));

        assertEquals(201, response2.statusCode());
        assertNotNull(response2.jsonPath().getString("id"));
        assertEquals("Staple Paperwork", response2.jsonPath().getString("title"));
        assertEquals("false", response2.jsonPath().getString("doneStatus"));
        assertEquals("Filing paperwork", response2.jsonPath().getString("description"));
    }

    @Test
    /*
     * Testing to see if a empty string title, null title,
     * and a non-boolean doneStatus are invalid
     */
    public void testPostTodosInvalid() {
        // testing with a false rather than false boolean
        String request1 = "{ \"title\": \"Test Todo\", \"doneStatus\": \"false\", \"description\": \"A test todo\" }";
        String request2 = "{ \"title\": \" \", \"doneStatus\": \"false\", \"description\": \"A test todo\" }";
        String request3 = "{ \"title\": null, \"doneStatus\": \"false\", \"description\": \"A test todo\" }";

        Response response1 = given()
                .header("Content-Type", "application/json")
                .body(request1)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        Response response2 = given()
                .header("Content-Type", "application/json")
                .body(request2)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        Response response3 = given()
                .header("Content-Type", "application/json")
                .body(request3)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        assertEquals(400, response1.statusCode());
        assertEquals(400, response2.statusCode());
        assertEquals(400, response3.statusCode());
    }

    // with IDs //
    @Test
    /*
     * Testing Get todos with ID
     */
    public void testGetTodoswithID() {
        Response response = given()
                .when()
                .get("/todos/1")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertEquals("1", response.jsonPath().getString("todos[0].id"));    }

    @Test
    /*
     * Testing Head todos with ID
     */
    public void testTodosHeadwithID() {
        Response response = given()
                .when()
                .head("/todos/1")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().isEmpty());
    }

    @Test
    /*
     * Testing put todos with ID
     */
    public void testPutwithID() {
        String request = "{ \"title\": \"Staple Paperwork\", \"doneStatus\": false, \"description\": \"Filing paperwork\" }";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .put("/todos/1")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertEquals("false", response.jsonPath().getString("doneStatus"));
    }

    @Test
    /*
     * Testing post todos with ID which is not valid
     */
    public void testPostwithID() {
        String request = "{\"title\": \"Buy Paper\", \"doneStatus\": false, \"description\": \"Going to store\" }";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("/todos/500")
                .then()
                .extract().response();

        assertEquals(404, response.statusCode());
       
    }

    @Test
    /*
     * Testing put todos with ID
     */
    public void testPutwithIDInvalid() {
        String request = "{\"id\": 108, \"title\": \"WEEE\", \"doneStatus\": false, \"description\": \"Filing paperwork\" }";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .put("/todos/id/108")
                .then()
                .extract().response();

        assertEquals(404, response.statusCode());
    }

    @Test
    /*
     * Delete todos with ID
     */
    public void testDeletewithID() {
        String request = "{\"title\": \"To Delete\", \"doneStatus\": false, \"description\": \"Waiting deletion :(\" }";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("/todos")
                .then()
                .extract().response();


        String id = response.jsonPath().getString("id");

        Response response2 = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/todos/"+ id)
                .then()
                .extract().response();


        assertEquals(201, response.statusCode());
        assertEquals(200, response2.statusCode());

    }

@Test
public void testPostTodosTasksof() {
    // Create a new todo for category relationship
    String tasksof = "{ \"id\": \"1\" }";
    Response categoryResponse = given()
            .header("Content-Type", "application/json")
            .body(tasksof)
            .when()
            .post("/todos/1/tasksof")
            .then()
            .extract().response();

    assertEquals(201, categoryResponse.statusCode());
}
@Test
    public void testGetTodosIdTasksof() {
        // Create a new todo for category relationship
    String tasksof = "{ \"id\": \"1\" }";
    Response categoryResponse = given()
            .header("Content-Type", "application/json")
            .body(tasksof)
            .when()
            .post("/todos/1/tasksof")
            .then()
            .extract().response();

    assertEquals(201, categoryResponse.statusCode());

        Response response = given()
                .when()
                .get("/todos/1/tasksof")
                .then()
                .extract().response();
    
        assertEquals(200, response.statusCode());
        assertNotNull( response.jsonPath().getList("projects[0].tasks"));    
    }
@Test
public void testDeleteTodosIdTasksof() {
    // Create a new todo for category relationship
    String tasksof = "{ \"id\": \"1\" }";
    Response categoryResponse = given()
            .header("Content-Type", "application/json")
            .body(tasksof)
            .when()
            .post("/todos/1/tasksof")
            .then()
            .extract().response();

    assertEquals(201, categoryResponse.statusCode());
    Response response = given()
            .header("Content-Type", "application/json")
            .when()
            .delete("/todos/1/tasksof/1")
            .then()
            .extract().response();

    assertEquals(200, response.statusCode());
}


@Test
public void testPostTodosCategories() {
    // Create a new todo for category relationship
    String category = "{ \"id\": \"1\" }";
    Response categoryResponse = given()
            .header("Content-Type", "application/json")
            .body(category)
            .when()
            .post("/todos/1/categories")
            .then()
            .extract().response();

    assertEquals(201, categoryResponse.statusCode());
}
@Test
    public void testGetTodosIdCategories() {
        Response response = given()
                .when()
                .get("/todos/1/categories")
                .then()
                .extract().response();
    
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().getList("categories"));
    }
@Test
public void testDeleteTodosIdCategories() {
     // Create a new todo for category relationship
     String category = "{ \"id\": \"1\" }";
     Response categoryResponse = given()
             .header("Content-Type", "application/json")
             .body(category)
             .when()
             .post("/todos/1/categories")
             .then()
             .extract().response();
 
     assertEquals(201, categoryResponse.statusCode());
    Response response = given()
            .header("Content-Type", "application/json")
            .when()
            .delete("/todos/1/categories/1")
            .then()
            .extract().response();

    assertEquals(200, response.statusCode());
}



}