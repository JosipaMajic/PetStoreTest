package com.example.petstore;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.ExtentReportUtil;

import static io.restassured.RestAssured.given;

public class GetPetTest {

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
    void getPetById() {
        test = extent.createTest("Get Pet By ID Test");
        Long petId = 1L;

        test.info("Retrieving pet details by ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            test.info("Response with pet ID " + petId + " returned status code " + statusCode);
        } else {
            test.fail("Expected status code 200 for valid pet ID, but got " + statusCode);
        }

        test.info("Verifying response content type");
        String contentType = response.getHeader("Content-Type");
        if (contentType.contains("application/json")) {
            test.info("Response content type is JSON: " + contentType);
        } else {
            test.fail("Expected content type application/json, but got " + contentType);
        }

        test.info("Response body for pet ID: " + response.getBody().asPrettyString());
        test.pass("Pet retrieved successfully with ID: " + petId);
    }

    @Test
    void getPetByInvalidId() {
        test = extent.createTest("Get Pet by Invalid ID Test");
        Long petId = -1L;

        test.info("Retrieving pet details by invalid ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            test.info("Response with invalid ID returned status code " + statusCode);
        } else {
            test.fail("Expected status code 404 for invalid ID, but got " + statusCode);
        }
        test.info("Response body for invalid ID: " + response.getBody().asPrettyString());
        test.info("Invalid ID was rejected as expected. It should not exist.");
    }

    @Test
    void getPetByStringId() {
        test = extent.createTest("Get Pet by String ID Test");
        String petId = "invalidStringId";

        test.info("Retrieving pet details by string ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            test.info("Response with string ID returned status code " + statusCode);
        } else {
            test.fail("Expected status code 404 for string ID, but got " + statusCode);
        }

        test.info("Response body for string ID: " + response.getBody().asPrettyString());
        test.info("String ID was rejected as expected. It should be a long value.");
    }

    @Test
    void getPetByNoId() {
        test = extent.createTest("Get Pet by No ID Test");

        test.info("Retrieving pet details without an ID");
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet");

        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            test.info("Response with no ID returned status code " + statusCode);
        } else {
            test.fail("Expected status code 405 for missing ID, but got " + statusCode);
        }

        test.info("Response body for missing ID: " + response.getBody().asPrettyString());
        test.info("No ID was rejected as expected. The API should require an ID.");
    }

    @Test
    void getPetByNonExistentId() {
        test = extent.createTest("Get Pet by Non-Existent ID Test");
        int petId = 999999;

        test.info("Retrieving pet details by non-existent ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            test.info("Expected 404 status code for non-existent pet ID, test passed.");
        } else {
            test.fail("Expected status code 404 for non-existent pet ID, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        test.info("Response body for non-existent pet ID: " + responseBody);

        if (statusCode != 404) {
            test.fail("Unexpected error code: " + statusCode + ". Response: " + responseBody);
        }
    }
}
