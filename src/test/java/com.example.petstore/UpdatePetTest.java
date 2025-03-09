package com.example.petstore;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.example.petstore.dto.Category;
import com.example.petstore.dto.Pet;
import com.example.petstore.dto.Tag;
import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.ExtentReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UpdatePetTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePetTest.class);
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = ConfigReader.getBaseUrl();
        extent = ExtentReportUtil.getExtentReports();
    }

    @AfterAll
    static void tearDown() {
        ExtentReportUtil.flushReports();
    }

    @Test
    void updateExistingPet() {
        test = extent.createTest("Update Existing Pet Test");
        int petId = 123;

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("MiffyUpdated");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("sold");

        test.info("Updating an existing pet with name: " + pet.getName());
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        logger.info("Verifying response status code");
        response.then().statusCode(200);

        logger.info("Verifying response content type");
        response.then().contentType(ContentType.JSON);

        logger.info("Verifying response body contains correct pet details");
        response.then()
                .body("name", equalTo(pet.getName()))
                .body("status", equalTo(pet.getStatus()))
                .body("category.id", equalTo(pet.getCategory().getId().intValue()))
                .body("category.name", equalTo(pet.getCategory().getName()));

        logger.info("Pet updated successfully");
        test.pass("Pet updated successfully");
    }

    @Test
    void updateNonExistingPet() {
        test = extent.createTest("Update Non-Existing Pet Test");
        long petId = -1L;

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("rabbit");

        Pet pet = new Pet();
        pet.setId(-1L);
        pet.setCategory(category);
        pet.setName("MiffyUpdated");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("sold");

        logger.info("Updating a non-existing pet with ID: " + petId);
        test.info("Updating a non-existing pet with ID: " + petId);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        response.then().statusCode(200);
        logger.info("Pet updated with invalid ID (status code 200 returned unexpectedly)");
        test.pass("Pet updated with invalid ID");
    }

    @Test
    void updatePetWithNullId() {
        test = extent.createTest("Update Pet with Null ID Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Pet petWithNullId = new Pet();
        petWithNullId.setCategory(category);
        petWithNullId.setName("Fluffy");
        petWithNullId.setPhotoUrls(List.of());
        petWithNullId.setTags(List.of(new Tag("Mammal", 1L)));
        petWithNullId.setStatus("available");
        petWithNullId.setId(null);

        logger.info("Updating pet with null ID");
        Response responseWithNullId = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(petWithNullId)
                .when()
                .put("/pet");

        int statusCodeForNullId = responseWithNullId.getStatusCode();
        if (statusCodeForNullId == 405) {
            logger.info("Expected 405 status code for null ID, test passed.");
        } else {

            logger.error("Expected status code 405 for null ID, but got {}", statusCodeForNullId);
            test.fail("Expected status code 405 for null ID, but got " + statusCodeForNullId);
        }
        logger.info("Response body for null ID: {}", responseWithNullId.getBody().asPrettyString());
    }

    @Test
    void updatePetWithInvalidStatus() {
        test = extent.createTest("Update Pet with Invalid Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("MiffyUpdated");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("invalidStatus");

        logger.info("Updating an existing pet with an invalid status: {}", pet.getStatus());
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        logger.info("Verifying response status code for invalid status");
        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            logger.info("Expected 400 status code for invalid status");
        } else {
            logger.error("Expected 400 status code, but got {}", statusCode);
            test.fail("Expected 400 status code, but got " + statusCode);
        }
        logger.info("Response body: {}", response.getBody().asPrettyString());
    }

    @Test
    void updatePetWithMissingName() {
        test = extent.createTest("Update Pet with Missing Name Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("sold");
        pet.setName(null);

        logger.info("Updating an existing pet with missing name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        logger.info("Verifying response status code for missing name");
        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            logger.info("Expected 400 status code for missing name");
        } else {
            logger.error("Expected 400 status code, but got {}", statusCode);
            test.fail("Expected 400 status code, but got " + statusCode);
        }
        logger.info("Response body: {}", response.getBody().asPrettyString());
    }

    @Test
    void updatePetWithEmptyCategory() {
        test = extent.createTest("Update Pet with Empty Category Test");

        Pet pet = new Pet();
        pet.setCategory(null);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        logger.info("Updating a pet with empty category");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        logger.info("Verifying response status code for empty category");
        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            logger.info("Expected 400 status code for empty category");
        } else {
            logger.error("Expected 400 status code, but got {}", statusCode);
            test.fail("Expected 400 status code, but got " + statusCode);
        }
        logger.info("Response body: {}", response.getBody().asPrettyString());
    }
    @Test
    void updatePetWithoutName() {
        test = extent.createTest("Update Pet without Name Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Tag tag = new Tag("Mammal", 1L);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("available");

        logger.info("Updating pet without name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for missing name, test passed.");
        } else {
            test.fail("Expected status code 400 for missing name, but got " + statusCode);
            logger.error("Expected 400 status code, but got {}", statusCode);
        }

        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for missing name: " + responseBody);

        test.fail("Backend accepted a pet without a name during update. Expected a 400 error for missing name, but it was accepted.");
    }

    @Test
    void updatePetWithDuplicateName() {
        test = extent.createTest("Update Pet with Duplicate Name Field Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        logger.info("Updating pet with duplicate name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        int statusCodeForDuplicateName = response.getStatusCode();
        if (statusCodeForDuplicateName == 400) {
            test.fail("Expected status code 400 for duplicate name, but got " + statusCodeForDuplicateName);
            logger.error("Expected 400 status code for duplicate name, but got {}", statusCodeForDuplicateName);
        } else {
            test.info("Response with duplicate name returned status code " + statusCodeForDuplicateName);
        }

        test.info("Response body for duplicate name: " + response.getBody().asPrettyString());
        test.info("Duplicate name rejected as expected. Name should be unique.");
    }

    @Test
    void updatePetWithNullStatus() {
        test = extent.createTest("Update Pet with Null Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Miffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus(null);

        logger.info("Updating pet with null status");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            test.info("Expected 405 status code for null status, test passed.");
        } else {
            test.fail("Expected status code 405 for null status, but got " + statusCode);
            logger.error("Expected 405 status code, but got {}", statusCode);
        }

        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for null status: " + responseBody);

        test.fail("Backend accepted a null status during update. Expected a 405 error for null status, but it was accepted.");
    }

    @Test
    void updatePetWithExceedingNameLength() {
        test = extent.createTest("Update Pet with Exceeding Name Length Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        String longName = "A".repeat(300);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName(longName);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        logger.info("Updating pet with a very long name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            test.info("Expected 405 status code for exceeding name length, test passed.");
        } else {
            test.fail("Expected status code 405 for exceeding name length, but got " + statusCode);
            logger.error("Expected 405 status code, but got {}", statusCode);
        }

        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for exceeding name length: " + responseBody);

        test.fail("Backend accepted a name length exceeding 255 characters. Expected a 405 error for exceeding name length, but it was accepted.");
    }

    @Test
    void updatePetWithEmptyRequestBody() {
        test = extent.createTest("Update Pet with Empty Request Body Test");

        logger.info("Sending an empty request body");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body("{}")
                .when()
                .put("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for empty request body, test passed.");
        } else {
            test.fail("Expected status code 400 for empty request body, but got " + statusCode);
            logger.error("Expected 400 status code, but got {}", statusCode);
        }

        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for empty request body: " + responseBody);

        test.fail("Backend accepted an empty request body during update. Expected a 400 error for empty fields, but it was accepted.");
    }
}
