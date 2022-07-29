package com.redsource.distributed.system.acceptance;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
        plugin = {"pretty", "html:build/cucumber"},
        publish = false,
        glue = "com.redsource.distributed.system.acceptance")
public class AcceptanceTest {
}
