package com.example.petstore;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExtentReportUtil;

import static io.restassured.RestAssured.given;

public class GetPetTest {
    private static final Logger logger = LoggerFactory.getLogger(GetPetTest.class);

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

        logger.info("Retrieving pet details by ID: {}", petId);
        test.info("Retrieving pet details by ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            logger.info("Response with pet ID {} returned status code {}", petId, statusCode);
            test.info("Response with pet ID " + petId + " returned status code " + statusCode);
        } else {
            logger.error("Expected status code 200 for valid pet ID, but got {}", statusCode);
            test.fail("Expected status code 200 for valid pet ID, but got " + statusCode);
        }

        logger.info("Verifying response content type");
        test.info("Verifying response content type");
        String contentType = response.getHeader("Content-Type");
        if (contentType.contains("application/json")) {
            logger.info("Response content type is JSON: {}", contentType);
            test.info("Response content type is JSON: " + contentType);
        } else {
            logger.error("Expected content type application/json, but got {}", contentType);
            test.fail("Expected content type application/json, but got " + contentType);
        }

        logger.info("Response body for pet ID: {}", response.getBody().asPrettyString());
        test.info("Response body for pet ID: " + response.getBody().asPrettyString());
        logger.info("Pet retrieved successfully with ID: {}", petId);
        test.pass("Pet retrieved successfully with ID: " + petId);
    }

    @Test
    void getPetByInvalidId() {
        test = extent.createTest("Get Pet by Invalid ID Test");
        long petId = -1L;

        logger.info("Retrieving pet details by invalid ID: {}", petId);
        test.info("Retrieving pet details by invalid ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            logger.info("Response with invalid ID returned status code {}", statusCode);
            test.info("Response with invalid ID returned status code " + statusCode);
        } else {
            logger.error("Expected status code 404 for invalid ID, but got {}", statusCode);
            test.fail("Expected status code 404 for invalid ID, but got " + statusCode);
        }
        logger.info("Response body for invalid ID: {}", response.getBody().asPrettyString());
        test.info("Response body for invalid ID: " + response.getBody().asPrettyString());
        logger.info("Invalid ID was rejected as expected. It should not exist.");
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

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            logger.info("Response with string ID returned status code {}", statusCode);
            test.info("Response with string ID returned status code " + statusCode);
        } else {
            logger.error("Expected status code 404 for string ID, but got {}", statusCode);
            test.fail("Expected status code 404 for string ID, but got " + statusCode);
        }

        logger.info("Response body for string ID: {}", response.getBody().asPrettyString());
        test.info("Response body for string ID: " + response.getBody().asPrettyString());
        logger.info("String ID was rejected as expected. It should be a long value.");
        test.info("String ID was rejected as expected. It should be a long value.");
    }

    @Test
    void getPetByNoId() {
        test = extent.createTest("Get Pet by No ID Test");

        logger.info("Retrieving pet details without an ID");
        test.info("Retrieving pet details without an ID");
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet");

        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 405) {
            logger.info("Response with no ID returned status code {}", statusCode);
            test.info("Response with no ID returned status code " + statusCode);
        } else {
            logger.error("Expected status code 405 for missing ID, but got {}", statusCode);
            test.fail("Expected status code 405 for missing ID, but got " + statusCode);
        }

        logger.info("Response body for missing ID: {}", response.getBody().asPrettyString());
        test.info("Response body for missing ID: " + response.getBody().asPrettyString());
        logger.info("No ID was rejected as expected. The API should require an ID.");
        test.info("No ID was rejected as expected. The API should require an ID.");
    }

    @Test
    void getPetByNonExistentId() {
        test = extent.createTest("Get Pet by Non-Existent ID Test");
        int petId = 999999;

        logger.info("Retrieving pet details by non-existent ID: {}", petId);
        test.info("Retrieving pet details by non-existent ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            logger.info("Expected 404 status code for non-existent pet ID, test passed.");
            test.info("Expected 404 status code for non-existent pet ID, test passed.");
        } else {
            logger.error("Expected status code 404 for non-existent pet ID, but got {}", statusCode);
            test.fail("Expected status code 404 for non-existent pet ID, but got " + statusCode);
        }
        String responseBody = response.getBody().asPrettyString();
        logger.info("Response body for non-existent pet ID: {}", responseBody);
        test.info("Response body for non-existent pet ID: " + responseBody);

        if (statusCode != 404) {
            logger.error("Unexpected error code: {}. Response: {}", statusCode, responseBody);
            test.fail("Unexpected error code: " + statusCode + ". Response: " + responseBody);
        }
    }
    @Test
    void getPetByDecimalId() {
        test = extent.createTest("Get Pet by Decimal ID Test");
        double decimalId = 123.45;

        logger.info("Retrieving pet details by decimal ID: {}", decimalId);
        test.info("Retrieving pet details by decimal ID: " + decimalId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", decimalId);

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            logger.info("Response with decimal ID returned status code {}", statusCode);
            test.info("Response with decimal ID returned status code " + statusCode);
        } else {
            logger.error("Expected status code 404 for decimal ID, but got {}", statusCode);
            test.fail("Expected status code 404 for decimal ID, but got " + statusCode);
        }

        logger.info("Response body for decimal ID: {}", response.getBody().asPrettyString());
        test.info("Response body for decimal ID: " + response.getBody().asPrettyString());
        logger.info("Decimal ID was rejected as expected. It should be an integer.");
        test.info("Decimal ID was rejected as expected. It should be an integer.");
    }
    @Test
    void getPetByLargeId() {
        test = extent.createTest("Get Pet by Large ID Test");
        long largeId = 4654546546464561443L;
        logger.info("Retrieving pet details by large ID: " + largeId);
        test.info("Retrieving pet details by large ID: " + largeId);

        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", largeId);

        logger.info("Verifying response status code");
        test.info("Verifying response status code");
        int statusCode = response.getStatusCode();
        if (statusCode == 404) {
            logger.info("Response with large ID returned status code {}", statusCode);
            test.info("Response with large ID returned status code " + statusCode);
        } else {
            logger.error("Expected status code 404 for large ID, but got {}", statusCode);
            test.fail("Expected status code 404 for large ID, but got " + statusCode);
        }

        logger.info("Response body for large ID: {}", response.getBody().asPrettyString());
        test.info("Response body for large ID: " + response.getBody().asPrettyString());
        logger.info("Large ID was rejected as expected. It should be a valid integer.");
        test.info("Large ID was rejected as expected. It should be a valid integer.");
    }
}
