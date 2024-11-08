package com.nexustest.performance.scenarios;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.testng.annotations.Test;
import java.util.HashMap;

public class TC04_PetStoreSpikeTest extends BasePerformanceTest {

    @Override
    protected String getTestName() {
        return "Pet Store Spike Test";
    }

    @Override
    public void execute() {
        System.out.println("Executing Pet Store spike test...");

        // Sudden spike of users searching for available pets
        ThreadGroup spikeThreadGroup = createThreadGroup(
                "Pet Search Spike",
                200,  // Very high number of threads
                1,    // Quick ramp-up
                1     // Single iteration
        );

        HashMap<String, String> params = new HashMap<>();
        params.put("status", "available");

        HTTPSamplerProxy getPetsSampler = jmeterEngine.createHttpSampler(
                "Get Available Pets",
                BASE_URL,
                "/v2/pet/findByStatus",
                "GET",
                params
        );

        HeaderManager headerManager = jmeterEngine.createHeaderManager();
        executeTest(spikeThreadGroup, getPetsSampler, headerManager);
    }

    @Test(description = "Execute Pet Store Spike Test")
    public void runSpikeTest() {
        setup();
        execute();
        generateReport();
    }

    @Override
    public void generateReport() {
        System.out.println("Generating spike test report...");
    }
}