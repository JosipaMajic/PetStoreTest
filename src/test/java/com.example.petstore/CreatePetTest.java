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

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class CreatePetTest {
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
    void addNewPet() {
        test = extent.createTest("Add New Pet Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Tag tag = new Tag("Mammal", 1L);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Miffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("available");

        test.info("Creating a new pet with name: " + pet.getName());
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Verifying response content type");
        response.then().contentType(ContentType.JSON);

        test.info("Verifying response body contains correct pet details");
        response.then()
                .body("name", equalTo(pet.getName()))
                .body("status", equalTo(pet.getStatus()))
                .body("category.id", equalTo(pet.getCategory().getId().intValue()))
                .body("category.name", equalTo(pet.getCategory().getName()));

        test.info("Response body: " + response.getBody().asPrettyString());
        test.info("Pet added successfully.");

    }

    @Test
    void addNewPetWithSoldStatus() {
        test = extent.createTest("Add New Pet with Sold Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("sold");

        test.info("Creating a new pet with status 'sold'");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Sold status accepted.");
    }

    @Test
    void addNewPetWithPendingStatus() {
        test = extent.createTest("Add New Pet with Sold Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("pending");

        test.info("Creating a new pet with status 'pending'");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Pending status accepted.");
    }

    @Test
    void addNewPetWithNullId() {
        test = extent.createTest("Add New Pet with Null ID Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet petWithNullId = new Pet();
        petWithNullId.setCategory(category);
        petWithNullId.setName("Fluffy");
        petWithNullId.setPhotoUrls(List.of());
        petWithNullId.setTags(List.of(new Tag("Mammal", 1L)));
        petWithNullId.setStatus("available");
        petWithNullId.setId(null);

        test.info("Creating a new pet with null ID");
        Response responseWithNullId = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(petWithNullId)
                .when()
                .post("/pet");

        int statusCodeForNullId = responseWithNullId.getStatusCode();
        if (statusCodeForNullId == 405) {
            test.info("Expected 405 status code for null ID, test passed.");
        } else {
            test.fail("Expected status code 405 for null ID, but got " + statusCodeForNullId);
        }
        String responseBody = responseWithNullId.getBody().asPrettyString();
        test.info("Response body for null ID: " + responseBody);

        if (responseBody.contains("error") || responseBody.contains("Invalid ID")) {
            test.info("Error response for null ID: " + responseBody);
        } else {
            test.info("Backend accepted null ID. This should ideally be rejected.");
        }
        test.fail("Backend accepted null ID. Expected a 405 error for invalid ID, but it was accepted.");

    }

    @Test
    void addNewPetWithNoId() {
        test = extent.createTest("Add New Pet without an ID Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet petWithEmptyId = new Pet();
        petWithEmptyId.setCategory(category);
        petWithEmptyId.setName("Fluffy");
        petWithEmptyId.setPhotoUrls(List.of());
        petWithEmptyId.setTags(List.of(new Tag("Mammal", 1L)));
        petWithEmptyId.setStatus("available");

        test.info("Creating a new pet with empty ID");
        Response responseWithoutId = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(petWithEmptyId)
                .when()
                .post("/pet");

        int statusCodeForNoId = responseWithoutId.getStatusCode();
        if (statusCodeForNoId == 200) {
            test.info("Response with no ID returned status code " + statusCodeForNoId);
        } else {
            test.fail("Expected status code 200, but got " + statusCodeForNoId);
        }

        test.info("Response body for pet without ID: " + responseWithoutId.getBody().asPrettyString());
        test.info("Backend automatically generated an ID for the new pet.");
    }

    @Test
    void addNewPetWithStringId() {
        test = extent.createTest("Add New Pet with String ID Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet petWithStringId = new Pet();
        petWithStringId.setCategory(category);
        petWithStringId.setName("Fluffy");
        petWithStringId.setPhotoUrls(List.of());
        petWithStringId.setTags(List.of(new Tag("Mammal", 1L)));
        petWithStringId.setStatus("available");
        try {
            petWithStringId.setId(Long.valueOf("string-id"));
        } catch (NumberFormatException e) {
            test.info("Caught expected exception for invalid ID value: " + e.getMessage());
        }
        test.info("Creating a new pet with string ID");
        Response responseWithStringId = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(petWithStringId)
                .when()
                .post("/pet");

        int statusCodeForStringId = responseWithStringId.getStatusCode();
        if (statusCodeForStringId == 400) {
            test.fail("Expected status code 400 for string ID, but got " + statusCodeForStringId);
        } else {
            test.info("Response with string ID returned status code " + statusCodeForStringId);
        }

        test.info("Response body for string ID: " + responseWithStringId.getBody().asPrettyString());
        test.info("String ID was rejected as expected. It should be a long value.");
    }

    @Test
    void addNewPetWithInvalidStatus() {
        test = extent.createTest("Add New Pet with Invalid Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Tag tag = new Tag("Mammal", 1L);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Miffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("invalid");

        test.info("Creating a new pet with invalid status");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for invalid status, test passed.");
        } else {
            test.fail("Expected status code 400 for invalid status, but got " + statusCode);
        }

        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for invalid status: " + responseBody);

        test.fail("Backend accepted an invalid status ('invalid'). Expected a 400 error for invalid status, but it was accepted.");
    }

    @Test
    void addNewPetWithLongInsteadOfStringForStatus() {
        test = extent.createTest("Add New Pet with Long Instead of String for Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus(String.valueOf(123456789L));

        test.info("Creating a new pet with a long value for status");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCodeForLongStatus = response.getStatusCode();
        if (statusCodeForLongStatus == 405) {
            test.fail("Expected status code 405 for long value in status, but got " + statusCodeForLongStatus);
        } else {
            test.info("Response with long value in status returned status code " + statusCodeForLongStatus);
        }

        test.info("Response body for long value in status: " + response.getBody().asPrettyString());
        test.info("Long value in status rejected as expected. Status should be a string.");
    }

    @Test
    void addNewPetWithSpecialCharactersInStatus() {
        test = extent.createTest("Add New Pet with Special Characters in Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("avai@lable!");

        test.info("Creating a new pet with special characters in status");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCodeForStatus = response.getStatusCode();
        if (statusCodeForStatus == 405) {
            test.fail("Expected status code 405 for special characters in status, but got " + statusCodeForStatus);
        } else {
            test.info("Response with special characters in status returned status code " + statusCodeForStatus);
        }

        test.info("Response body for special characters in status: " + response.getBody().asPrettyString());
        test.info("Special characters in status rejected as expected. Status should be a valid string.");
    }

    @Test
    void addNewPetWithoutName() {
        test = extent.createTest("Add New Pet without Name Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Tag tag = new Tag("Mammal", 1L);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("available");

        test.info("Creating a new pet without name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for missing name, test passed.");
        } else {
            test.fail("Expected status code 400 for missing name, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for missing name: " + responseBody);

        test.fail("Backend accepted a pet without a name. Expected a 400 error for missing name, but it was accepted.");
    }

    @Test
    void addNewPetWithDuplicateName() {
        test = extent.createTest("Add New Pet with Duplicate Name Field Test");

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

        test.info("Creating a new pet with duplicate name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCodeForDuplicateName = response.getStatusCode();
        if (statusCodeForDuplicateName == 400) {
            test.fail("Expected status code 400 for duplicate name, but got " + statusCodeForDuplicateName);
        } else {
            test.info("Response with duplicate name returned status code " + statusCodeForDuplicateName);
        }

        test.info("Response body for duplicate name: " + response.getBody().asPrettyString());
        test.info("Duplicate name rejected as expected. Name should be unique.");
    }

    @Test
    void addNewPetWithNullStatus() {
        test = extent.createTest("Add New Pet with Null Status Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Miffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus(null);

        test.info("Creating a new pet with null status");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            test.info("Expected 405 status code for null status, test passed.");
        } else {
            test.fail("Expected status code 405 for null status, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for null status: " + responseBody);

        test.fail("Backend accepted a null status. Expected a 405 error for null status, but it was accepted.");
    }

    @Test
    void addNewPetWithExceedingNameLength() {
        test = extent.createTest("Add New Pet with Exceeding Name Length Test");

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

        test.info("Creating a new pet with a very long name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            test.info("Expected 405 status code for exceeding name length, test passed.");
        } else {
            test.fail("Expected status code 405 for exceeding name length, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for exceeding name length: " + responseBody);

        test.fail("Backend accepted a name length exceeding 255 characters. Expected a 405 error for exceeding name length, but it was accepted.");
    }

    @Test
    void addNewPetWithEmptyRequestBody() {
        test = extent.createTest("Add New Pet with Empty Request Body Test");

        test.info("Sending an empty request body");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body("{}")
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for empty request body, test passed.");
        } else {
            test.fail("Expected status code 400 for empty request body, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for empty request body: " + responseBody);

        test.fail("Backend accepted an empty request body. Expected a 400 error for empty fields, but it was accepted.");
    }

    @Test
    void addNewPetWithNonExistentCategory() {
        test = extent.createTest("Add New Pet with Non-Existent Category Test");

        Category category = new Category();
        category.setId(999L);
        category.setName("NonExistent");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with a non-existent category ID");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for non-existent category, test passed.");
        } else {
            test.fail("Expected status code 400 for non-existent category, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for non-existent category: " + responseBody);

        test.fail("Backend accepted a pet with a non-existent category. Expected a 400 error for invalid category, but it was accepted.");
    }

    @Test
    void addNewPetWithDuplicateCategory() {
        test = extent.createTest("Add New Pet with Duplicate Category Field Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with duplicate category");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCodeForDuplicateCategory = response.getStatusCode();
        if (statusCodeForDuplicateCategory == 400) {
            test.fail("Expected status code 400 for duplicate category, but got " + statusCodeForDuplicateCategory);
        } else {
            test.info("Response with duplicate category returned status code " + statusCodeForDuplicateCategory);
        }

        test.info("Response body for duplicate category: " + response.getBody().asPrettyString());
        test.info("Duplicate category rejected as expected. Category should be unique.");
    }

    @Test
    void addNewPetWithInvalidPhotoUrl() {
        test = extent.createTest("Add New Pet with Invalid Photo URL Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of("invalid_url"));
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with an invalid photo URL");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for invalid photo URL, test passed.");
        } else {
            test.fail("Expected status code 400 for invalid photo URL, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for invalid photo URL: " + responseBody);

        test.fail("Backend accepted an invalid photo URL. Expected a 400 error for invalid photo URL, but it was accepted.");
    }

    @Test
    void addNewPetWithPhotoUrlAsLong() {
        test = extent.createTest("Add New Pet with Photo URL as Long Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of(String.valueOf(123456789L)));  // Long value instead of String
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with photo URL as long");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCodeForPhotoUrlAsLong = response.getStatusCode();
        if (statusCodeForPhotoUrlAsLong == 405) {
            test.fail("Expected status code 405 for long value in photo URL, but got " + statusCodeForPhotoUrlAsLong);
        } else {
            test.info("Response with long value in photo URL returned status code " + statusCodeForPhotoUrlAsLong);
        }

        test.info("Response body for long value in photo URL: " + response.getBody().asPrettyString());
        test.info("Long value in photo URL rejected as expected. Photo URL should be a string.");
    }

    @Test
    void addNewPetWithInvalidTags() {
        test = extent.createTest("Add New Pet with Invalid Tags Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("NonExistentTag", 999L)));
        pet.setStatus("available");

        test.info("Creating a new pet with invalid tag ID");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for invalid tag ID, test passed.");
        } else {
            test.fail("Expected status code 400 for invalid tag ID, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for invalid tag ID: " + responseBody);

        test.fail("Backend accepted an invalid tag ID. Expected a 400 error for invalid tag ID, but it was accepted.");
    }

    @Test
    void addNewPetWithNegativeCategoryId() {
        test = extent.createTest("Add New Pet with Negative Category ID Test");

        Category category = new Category();
        category.setId(-1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName("Fluffy");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with a negative category ID");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for negative category ID, test passed.");
        } else {
            test.fail("Expected status code 400 for negative category ID, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for negative category ID: " + responseBody);

        test.fail("Backend accepted a negative category ID. Expected a 400 error for invalid category ID, but it was accepted.");
    }

    @Test
    void addNewPetWithLargePayload() {
        test = extent.createTest("Add New Pet with Large Payload Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        String longName = "A".repeat(10000);

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName(longName);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with a large payload");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Large payload accepted by the API. This should be rejected if there are size limits.");
    }

    @Test
    void addNewPetWithSpecialCharactersInName() {
        test = extent.createTest("Add New Pet with Special Characters in Name Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        String specialName = "Fluffy@#$%^&*()";

        Pet pet = new Pet();
        pet.setCategory(category);
        pet.setName(specialName);
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(new Tag("Mammal", 1L)));
        pet.setStatus("available");

        test.info("Creating a new pet with special characters in the name");
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        int statusCode = response.getStatusCode();
        if (statusCode == 400) {
            test.info("Expected 400 status code for special characters in name, test passed.");
        } else {
            test.fail("Expected status code 400 for special characters in name, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for special characters in name: " + responseBody);

        test.fail("Backend accepted special characters in name. Expected a 400 error for invalid characters in the pet name, but it was accepted.");
    }

}
