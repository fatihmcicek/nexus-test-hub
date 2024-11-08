package com.nexustest.performance.scenarios;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.testng.annotations.Test;

public class TC03_StoreStressTest extends BasePerformanceTest {

    @Override
    protected String getTestName() {
        return "Store API Load Test";
    }

    @Override
    public void execute() {
        System.out.println("Executing Store API performance test...");

        // Store Inventory Test
        ThreadGroup inventoryThreadGroup = createThreadGroup(
                "Store Inventory Users",
                30, // threads
                5,  // ramp-up
                2   // loop count
        );

        HTTPSamplerProxy getInventorySampler = jmeterEngine.createHttpSampler(
                "Get Store Inventory",
                BASE_URL,
                "/v2/store/inventory",
                "GET",
                null
        );

        HeaderManager headerManager = jmeterEngine.createHeaderManager();
        executeTest(inventoryThreadGroup, getInventorySampler, headerManager);
    }

    @Test(description = "Execute Store API Load Test")
    public void runStoreLoadTest() {
        setup();
        execute();
        generateReport();
    }

    @Override
    public void generateReport() {
        System.out.println("Generating Store API performance test report...");
    }
}