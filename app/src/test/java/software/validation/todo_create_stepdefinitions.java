package software.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class todo_create_stepdefinitions {

    private Response response;
    private RequestSpecification request;
    private String todo2Id;
    private String todo1Id;
    private static final String BASE_URL = "http://localhost:4567";

    @Given("the API is responsive and contains only default todo objects")
    public void theApiIsResponsive() {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");
        clearTodos(); // Step 1: Clear all existing todos
        postDefaultTodos(); // Step 2: Add default todos
    }

    private void clearTodos() {
        // Clear all existing todos
        Response getTodosResponse = RestAssured.get("/todos");
        List<Map<String, Object>> todos = getTodosResponse.jsonPath().getList("todos");
        for (Map<String, Object> todo : todos) {
            String todoId = todo.get("id").toString();
            RestAssured.delete("/todos/" + todoId);
        }
    }

    private void postDefaultTodos() {
        // Creating and logging todos with IDs
        Map<String, Object> todo1 = Map.of(
                "title", "Staple Paperwork",
                "doneStatus", false,
                "description", "Filing paperwork");
        Response response1 = request.body(todo1).post("/todos");
        assertEquals(201, response1.getStatusCode(), "Failed to create default todo 1");
        todo1Id = response1.jsonPath().getString("id");

        Map<String, Object> todo2 = Map.of(
                "title", "File Paperwork",
                "doneStatus", false,
                "description", "Filing paperwork");
        Response response2 = request.body(todo2).post("/todos");
        assertEquals(201, response2.getStatusCode(), "Failed to create default todo 2");
        todo2Id = response2.jsonPath().getString("id");

    }

    /////////// Create todo steps ///////

    @When("a new todo is created with the title {string}, the doneStatus {string} and the description {string}")
    public void createTodoWithAllFields(String title, String doneStatus, String description) {
        Boolean doneStatusBoolean = Boolean.parseBoolean(doneStatus);

        Map<String, Object> todoData = Map.of(
                "title", title,
                "doneStatus", doneStatusBoolean,
                "description", description);
        response = request.body(todoData).post("/todos");
        assertEquals(201, response.getStatusCode(), "Expected a successful creation with status code 201");
        assertEquals(description, response.jsonPath().getString("description"),
                "Description in response does not match");
    }

    @Then("a new todo exists in the database with the title {string}, the doneStatus {string} and the description {string}")
    public void verifyTodoExistsInDatabase(String title, String doneStatus, String description) {
        Boolean expectedDoneStatus = Boolean.parseBoolean(doneStatus);

        Response getTodosResponse = RestAssured.get("/todos");
        boolean todoExists = getTodosResponse.jsonPath().getList("todos.title").contains(title);
        assertTrue(todoExists, "Todo with title '" + title + "' was not found in the database");

        Boolean actualDoneStatus = getTodosResponse.jsonPath()
                .getBoolean("todos.find { it.title == '" + title + "' }.doneStatus");
        String actualDescription = getTodosResponse.jsonPath()
                .getString("todos.find { it.title == '" + title + "' }.description");

        assertEquals(expectedDoneStatus, actualDoneStatus, "The doneStatus does not match for title '" + title + "'");
        assertEquals(description, actualDescription, "The description does not match for title '" + title + "'");
    }

    @When("a new todo is created with the title {string}")
    public void createTodoWithTitleOnly(String title) {
        Map<String, Object> todoData = Map.of("title", title);
        response = request.body(todoData).post("/todos");
    }

    @Then("a new todo exists in the database with the title {string} and the default status")
    public void verifyTodoExistsWithDefaultStatus(String title) {
        Response getTodosResponse = RestAssured.get("/todos");
        boolean todoExists = getTodosResponse.jsonPath().getList("todos.title").contains(title);
        assertTrue(todoExists, "Todo with title '" + title + "' was not found in the database");
        Boolean defaultStatus = false;

        String todoStatusString = getTodosResponse.jsonPath()
                .getString("todos.find { it.title == '" + title + "' }.doneStatus");
        Boolean todoStatus = Boolean.parseBoolean(todoStatusString);
        assertEquals(defaultStatus, todoStatus, "The todo does not have the default status");
    }

    @When("a new todo is created with the title {string} and the done status {string} and the description {string}")
    public void createTodoWithInvalidFields(String title, String doneStatus, String description) {
        Boolean doneStatusBoolean = Boolean.parseBoolean(doneStatus);
        Map<String, Object> todoData = Map.of(
                "title", title,
                "doneStatus", doneStatusBoolean,
                "description", description);

        response = request.body(todoData).post("/todos");
    }

    @Then("a todo will not be created")
    public void verifyTodoNotCreated() {
        assertEquals(400, response.getStatusCode(), "Expected status code 400 for invalid creation");
    }

    ///////// Update Todo Steps//////////
    
    @When("a todo of id {string} with the title {string}, the doneStatus {string} and the description {string} with the name {string}, doneStatus {string} and description {string}")
    public void updateTodoWithDetails(String id, String title, String doneStatus, String description, String newTitle,
            String newDoneStatus, String newDescription) {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");
        Boolean newDoneStatusBoolean = Boolean.parseBoolean(newDoneStatus);
        Map<String, Object> updatedTodoData = Map.of(
                "title", newTitle,
                "doneStatus", newDoneStatusBoolean,
                "description", newDescription);
        todo2Id = String.valueOf(todo2Id);
        System.out.println("Updating Todo with ID: " + todo2Id);

        response = request.body(updatedTodoData).put("/todos/" + todo2Id);
        assertEquals(200, response.getStatusCode(), "Expected status code 200 for successful update");
    }

    // Update the description of a todo to an empty value
    @When("a todo of id {string} with the title {string}, the doneStatus {string} and the description {string} with the name {string}, doneStatus {string} and an empty description {string}")
    public void updateTodoWithEmptyDescription(String id, String title, String doneStatus, String description,
            String newTitle, String newDoneStatus, String newDescription) {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");
        Boolean newDoneStatusBoolean = Boolean.parseBoolean(newDoneStatus);
        Map<String, Object> updatedTodoData = Map.of(
                "title", newTitle,
                "doneStatus", newDoneStatusBoolean,
                "description", newDescription);
        todo2Id = String.valueOf(todo2Id);
        System.out.println("Updating Todo with ID: " + todo2Id);

        response = request.body(updatedTodoData).put("/todos/" + todo2Id);
        assertEquals(200, response.getStatusCode(), "Expected status code 200 for successful update");
    }

    @When("a todo of id {string} with an empty title {string}, the doneStatus {string} and the description {string} with the name {string}, doneStatus {string} and description {string}")
    public void updateodoWithInvalidFields(String id, String title, String doneStatus, String description,
            String newTitle, String newDoneStatus, String newDescription) {
        Boolean newDoneStatusBoolean = Boolean.parseBoolean(newDoneStatus);
        Map<String, Object> updatedTodoData = new HashMap<>();
        updatedTodoData.put("title", newTitle);
        updatedTodoData.put("doneStatus", newDoneStatusBoolean);
        updatedTodoData.put("description", ""); // Setting description to an empty string

        response = request.body(updatedTodoData).put("/todos/" + todo1Id);
    }

    @Then("the todo with id {string} will exist in the database with the title {string}, doneStatus {string} and description {string}")
    public void verifyLastTodoUpdated(String id, String newTitle, String newDoneStatus, String newDescription) {
        RestAssured.baseURI = BASE_URL;
        todo2Id = String.valueOf(todo2Id);
        Response responseToDo = RestAssured.get("/todos/" + todo2Id);

        assertEquals(newTitle, responseToDo.jsonPath().getString("todos[0].title"), "Title does not match");
        assertEquals(newDoneStatus, responseToDo.jsonPath().getString("todos[0].doneStatus"),
                "doneStatus does not match");
        assertEquals(newDescription, responseToDo.jsonPath().getString("todos[0].description"),
                "Description does not match");
    }

    @Then("a todo will not be updated")
    public void a_todo_will_not_be_updated() {
        assertEquals(400, response.getStatusCode(),
                "Expected a status code of 400, indicating the update was not successful.");
    }

    /////////// Delete Todo Steps //////////

    @When("a todo of id {string} with the title {string}, the doneStatus {string} and the description {string} is deleted from the system")
    public void deleteTodoWithId(String id, String title, String doneStatus, String description) {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");

        todo2Id = String.valueOf(todo2Id);

        response = request.delete("/todos/" + todo2Id);
        assertEquals(200, response.getStatusCode(), "Expected status code 200 for successful deletion");

    }

    @When("a todo of id {string} with the title {string}, the doneStatus {string} and the description {string} is deleted from the system associated with the category {string}")
    public void deleteTodoWithCategory(String id, String title, String doneStatus, String description,
            String categoryid) {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");
        request.body("{\"id\": \"" + todo2Id + "\", \"title\": \"" + title + "\", \"doneStatus\": " + doneStatus
                + ", \"description\": \"" + description + "\"}");
        response = request.post("/todos/" + todo2Id + "/categories/1");
        todo2Id = String.valueOf(todo2Id);

        response = request.delete("/todos/" + todo2Id);
        assertEquals(200, response.getStatusCode(), "Expected status code 200 for successful deletion");
    }

    @When("a todo with an invalid {string} with the title {string}, the doneStatus {string} and the description {string} is deleted from the system")
    public void deleteTodoWithInvalidId(String id, String title, String doneStatus, String description) {
        response = request.delete("/todos/" + id);
    }

    @Then("the todo with id {string} will be deleted from the database")
    public void verifyTodoDeletedFromDatabase(String id) {
        RestAssured.baseURI = BASE_URL;
        request = RestAssured.given().contentType("application/json");
        todo2Id = String.valueOf(todo2Id);

        Response getTodoResponse = RestAssured.get("/todos/" + todo2Id);
        assertEquals(404, getTodoResponse.getStatusCode(), "Expected status code 404 as the todo should be deleted.");
    }

    @Then("the todo with title {string} will be deleted from the database")
    public void verifyTodoDeletedByTitle(String title) {
        Response getTodosResponse = RestAssured.get("/todos");
        List<Map<String, Object>> todos = getTodosResponse.jsonPath().getList("todos");
        boolean todoExists = todos.stream().anyMatch(todo -> title.equals(todo.get("title")));
        assertFalse(todoExists, "Todo with title '" + title + "' should be deleted from the database.");
    }


    /// Apply to all of the feature files ///
    @Then("the status code {int} will be received")
    public void verifyStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode(),
                "Expected status code " + statusCode + " but received " + response.getStatusCode());
    }

    @Then("the error message {string} will be raised")
    public void verifyErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = response.jsonPath().getString("errorMessages[0]");
        assertEquals(expectedErrorMessage, actualErrorMessage, "Error message does not match");
    }

}
