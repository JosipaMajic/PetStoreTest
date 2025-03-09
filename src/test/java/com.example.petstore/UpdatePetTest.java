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

public class UpdatePetTest {

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
        Long petId = 123L;

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

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Verifying response content type");
        response.then().contentType(ContentType.JSON);

        test.pass("Pet updated successfully");
    }

    @Test
    void updateNonExistingPet() {
        test = extent.createTest("Update Non-Existing Pet Test");
        Long petId = -1L;

        Category category = new Category();
        category.setId(1L);
        category.setName("Mammal");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("rabbit");

        Pet pet = new Pet();
        pet.setId(-1L); // Invalid ID
        pet.setCategory(category);
        pet.setName("MiffyUpdated");
        pet.setPhotoUrls(List.of());
        pet.setTags(List.of(tag));
        pet.setStatus("sold");

        test.info("Updating a non-existing pet with ID: " + petId);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("api_key", ConfigReader.getApiKey())
                .body(pet)
                .when()
                .put("/pet");

        test.info("Verifying response status code");
        response.then().statusCode(200);
        test.pass("Pet updated with invalid ID");
    }
}
