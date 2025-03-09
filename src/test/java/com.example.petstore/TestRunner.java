package com.example.petstore;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        CreatePetTest.class,
        GetPetTest.class,
        UpdatePetTest.class
})
public class TestRunner {
}
