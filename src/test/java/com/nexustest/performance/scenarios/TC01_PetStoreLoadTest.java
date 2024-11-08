package com.nexustest.performance.scenarios;

import com.nexustest.performance.config.JMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

public class TC01_PetStoreLoadTest implements TestScenario {
    private JMeterEngine jmeterEngine;
    private static final String BASE_URL = "petstore.swagger.io";
    private static final String TEST_NAME = "Pet Store Load Test";

    @Override
    public void setup() {
        System.out.println("Setting up JMeter test...");
        jmeterEngine = new JMeterEngine();
    }

    @Override
    public void execute() {
        System.out.println("Executing performance test...");

        ThreadGroup threadGroup = jmeterEngine.createThreadGroup(
                "Pet Store Users",
                25,
                5
        );

        Map<String, String> params = new HashMap<>();
        params.put("status", "available");

        HTTPSamplerProxy httpSampler = jmeterEngine.createHttpSampler(
                "Get Available Pets",
                BASE_URL,
                "/v2/pet/findByStatus",
                "GET",
                params
        );

        HeaderManager headerManager = jmeterEngine.createHeaderManager();
        jmeterEngine.setupTestPlan(TEST_NAME, threadGroup, httpSampler, headerManager);
    }

    @Override
    public void generateReport() {
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Test Name: " + TEST_NAME);
        System.out.println("Report Location: target/jmeter/results/");
        System.out.println("To view the report, open index.html in your browser from the report directory");
        System.out.println("=============================\n");
    }

    @Test(description = "Execute Pet Store Load Test")
    public void runLoadTest() throws InterruptedException {
        try {
            setup();
            execute();
            jmeterEngine.runTest();
            Thread.sleep(1000);
            generateReport();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}