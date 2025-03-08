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

        test.info("Pet added successfully, but backend did not validate status properly.");

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Backend accepted an invalid status ('invalid'). Ideally, this should have been rejected with a 400 or similar error.");

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Empty name should have been rejected, but API accepted it.");

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Null status should have been rejected, but API accepted it. Check backend validation.");

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Name length exceeding 255 characters should have been rejected by the API.");

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Empty request body was accepted by the API, which is incorrect. API should validate that fields are provided.");
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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Pet with non-existent category should have been rejected. API does not validate category existence.");
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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Invalid photo URL accepted. This should have been validated.");
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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Invalid tag ID accepted. This should have been rejected.");
    }

    @Test
    void addNewPetWithDuplicateName() {
        test = extent.createTest("Add New Pet with Duplicate Name Test");

        Category category = new Category();
        category.setId(1L);
        category.setName("Rabbit");

        Pet pet = new Pet();
        pet.setCategory(category);
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

        Response responseDuplicate = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .post("/pet");

        test.info("Verifying response status code for the second request");
        responseDuplicate.then().statusCode(200);

        test.info("Duplicate pet name accepted. API should have validated uniqueness of pet names.");
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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Negative category ID accepted. API should validate category IDs.");
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

        test.info("Sold status accepted. If sold is a valid status, this is correct.");
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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Special characters in name accepted. API should validate name for special characters.");
    }

}
