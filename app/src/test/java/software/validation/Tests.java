package software.validation;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

//Ensure random order of testing
@TestMethodOrder(MethodOrderer.Random.class)
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
         * Testing the the URL sensitivity as seen in exploratory testing instabilities
         */
        public void testURL() {
                Response response = given()
                                .when()
                                .get("/todos/")
                                .then()
                                .extract().response();

                assertEquals(404, response.statusCode());
        }
        @Test
        /*
         * Testing posting with a badly written JSON file
         */
        public void testMalformedJSON() {
                String badrequest = "{ \"title\": \"Staples, \"doneStatus\": false, \"description\": \"bad request\" ";

                Response response = given()
                                .header("Content-Type", "application/json")
                                .body(badrequest)
                                .when()
                                .post("/todos")
                                .then()
                                .extract().response();

                assertEquals(400, response.statusCode());
        }


        @Test
        /*
         * Testing posting with a badly written XML 
         */
        public void testMalformedXML() {
                String badrequest = "<todo><title>Staples tag<doneStatus>false</doneStatus></todo>";

                Response response = given()
                                .header("Content-Type", "application/xml")
                                .body(badrequest)
                                .when()
                                .post("/todos")
                                .then()
                                .extract().response();

                assertEquals(400, response.statusCode());
        }

        @Test
        public void testDeleteInvalidTodo() {
        String request = "{\"title\": \"Staple paperwork\", \"doneStatus\": false, \"description\": \"Waiting deletion :(\" }";

        // Create a new todo
        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post("/todos")
                .then()
                .extract().response();

        String id = createResponse.jsonPath().getString("id");

        // Delete the todo
        Response deleteResponse = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/todos/" + id)
                .then()
                .extract().response();

        assertEquals(200, deleteResponse.statusCode());

        // Try to delete it again
        Response deleteAgainResponse = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/todos/" + id)
                .then()
                .extract().response();

        assertEquals(404, deleteAgainResponse.statusCode());
        }

        @Test
        public void testGetTodosAsJSON() {
         Response response = given()
            .accept("application/json")
            .when()
            .get("/todos")
            .then()
            .extract().response();

    assertEquals(200, response.statusCode());
    assertFalse(response.jsonPath().getList("todos").isEmpty());
}

        @Test
        public void testGetTodosAsXML() {
        Response response = given()
            .accept("application/xml")
            .when()
            .get("/todos")
            .then()
            .extract().response();

    assertEquals(200, response.statusCode());
    assertNotNull(response.xmlPath().getString("todos.todo[0].id"));
    assertFalse(response.xmlPath().getString("todos.todo[0].id").isEmpty());

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
         * non-boolean doneStatus, and an ID are invalid
         */
        public void testPostTodosInvalid() {

                String request1 = "{ \"title\": \"Invalid Todo\", \"doneStatus\": \"false\", \"description\": \"Boolean as a string\" }";
                String request2 = "{ \"title\": \" \", \"doneStatus\": false, \"description\": \"No title\" }";
                String request3 = "{ \"title\": null, \"doneStatus\": false, \"description\": \"Null title\" }";
                String request4 = "{ \"id\": 3, \"title\": \"No ID\", \"doneStatus\": false, \"description\": \"With an ID\" }";
        
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

                Response response4 = given()
                                .header("Content-Type", "application/json")
                                .body(request4)
                                .when()
                                .post("/todos")
                                .then()
                                .extract().response();

                

                assertEquals(400, response1.statusCode());
                assertEquals("[Failed Validation: doneStatus should be BOOLEAN]", response1.jsonPath().getString("errorMessages"));
                assertEquals(400, response2.statusCode());
                assertEquals("[Failed Validation: title : can not be empty]", response2.jsonPath().getString("errorMessages"));
                assertEquals(400, response3.statusCode());
                assertEquals("[title : field is mandatory]",response3.jsonPath().getString("errorMessages"));
                assertEquals(400, response4.statusCode());
                assertEquals("[Invalid Creation: Failed Validation: Not allowed to create with id]", response4.jsonPath().getString("errorMessages"));


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
                assertEquals("1", response.jsonPath().getString("todos[0].id"));
        }

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
        public void testPostwithInvalidID() {
                String request = "{\"title\": \"Buy Paper\", \"doneStatus\": false, \"description\": \"Going to store\" }";
                Response response = given()
                                .header("Content-Type", "application/json")
                                .body(request)
                                .when()
                                .post("/todos/500")
                                .then()
                                .extract().response();

                assertEquals(404, response.statusCode());
                assertEquals("[No such todo entity instance with GUID or ID 500 found]",response.jsonPath().getString("errorMessages"));

        }

        @Test
        /*
         * Testing put todos with ID
         */
        public void testPutwithIDInvalid() {
                String request = "{\"id\": 108, \"title\": \"Invalid ID test\", \"doneStatus\": false, \"description\": \"Filing paperwork\" }";
                Response response = given()
                                .header("Content-Type", "application/json")
                                .body(request)
                                .when()
                                .put("/todos/108")
                                .then()
                                .extract().response();

                assertEquals(404, response.statusCode());
                assertEquals("[Invalid GUID for 108 entity todo]",response.jsonPath().getString("errorMessages"));
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
                                .delete("/todos/" + id)
                                .then()
                                .extract().response();

                assertEquals(201, response.statusCode());
                assertEquals(200, response2.statusCode());

        }

        @Test
        public void testPostTodosTasksof() {
                // Create taskof project id = 1 for todo of id = 1
                
                String tasksof = "{ \"id\": \"1\" }";

                Response response = given()
                                .header("Content-Type", "application/json")
                                .body(tasksof)
                                .when()
                                .post("/todos/1/tasksof")
                                .then()
                                .extract().response();

                assertEquals(201, response.statusCode());
        }

        @Test
        public void testGetTodosIdTasksof() {
                // Create taskof project id = 1 for todo of id = 1 
                String tasksof = "{ \"id\": \"1\" }";
                Response response1 = given()
                                .header("Content-Type", "application/json")
                                .body(tasksof)
                                .when()
                                .post("/todos/1/tasksof")
                                .then()
                                .extract().response();

                assertEquals(201, response1.statusCode());

                Response response2 = given()
                                .when()
                                .get("/todos/1/tasksof")
                                .then()
                                .extract().response();

                assertEquals(200, response2.statusCode());
                assertNotNull(response2.jsonPath().getList("projects[0].tasks"));
        }

        @Test
        public void testDeleteTodosIdTasksof() {
                // Create taskof project id = 1 for todo of id = 1 
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