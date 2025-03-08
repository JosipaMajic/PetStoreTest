package com.example.petstore;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
        Long petId = 123L;

        test.info("Retrieving pet details by ID: " + petId);
        Response response = given()
                .header("api_key", ConfigReader.getApiKey())
                .when()
                .get("/pet/{petId}", petId);

        test.info("Verifying response status code");
        response.then().statusCode(200);

        test.info("Verifying response content type");
        response.then().contentType(ContentType.JSON);

        test.pass("Pet retrieved successfully");
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
        response.then().statusCode(404);

        test.pass("Pet not found with invalid ID");
    }
}
