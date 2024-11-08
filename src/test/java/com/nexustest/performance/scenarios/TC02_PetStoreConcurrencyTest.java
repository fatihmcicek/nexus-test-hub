package com.nexustest.performance.scenarios;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.testng.annotations.Test;
import com.nexustest.performance.config.JMeterEngine;

public class TC02_PetStoreConcurrencyTest implements TestScenario {
    private JMeterEngine jmeterEngine;
    private static final String BASE_URL = "petstore.swagger.io";
    private static final String TEST_NAME = "TC02_AddNewPets_ConcurrencyTest";

    @Override
    public void setup() {
        System.out.println("Setting up JMeter test...");
        jmeterEngine = new JMeterEngine();
    }

    @Override
    public void execute() {
        System.out.println("Executing TC02 - Concurrent Pet Addition Test");

        int loops = 0;
        ThreadGroup threadGroup = jmeterEngine.createThreadGroup(
                "Concurrent Pet Creation",
                50,   // Yüksek eşzamanlı kullanıcı
                2,     // Hızlı ramp-up
                loops);

        String petData = """
                {
                    "id": 0,
                    "category": {"id": 1, "name": "Dogs"},
                    "name": "TestDog",
                    "photoUrls": ["http://example.com/photo.jpg"],
                    "tags": [{"id": 1, "name": "test"}],
                    "status": "available"
                }""";

        HTTPSamplerProxy httpSampler = jmeterEngine.createHttpSampler(
                "Add New Pet",
                BASE_URL,
                "/v2/pet",
                "POST",
                null
        );
        httpSampler.setPostBodyRaw(true);
        httpSampler.addNonEncodedArgument("", petData, "");

        HeaderManager headerManager = jmeterEngine.createHeaderManager();
        jmeterEngine.setupTestPlan(TEST_NAME, threadGroup, httpSampler, headerManager);
    }

    @Override
    public void generateReport() {
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Test Name: " + TEST_NAME);
        System.out.println("Report Location: target/jmeter/reports/");
        System.out.println("=============================\n");
    }

    @Test(description = "TC02 - Concurrent Pet Addition Test")
    public void runConcurrencyTest() throws InterruptedException {
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