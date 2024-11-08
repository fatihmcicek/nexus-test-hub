package com.nexustest.performance.scenarios;

import com.nexustest.performance.config.JMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;

public abstract class BasePerformanceTest implements TestScenario {
    protected JMeterEngine jmeterEngine;
    protected static final String BASE_URL = "petstore.swagger.io";

    @Override
    public void setup() {
        System.out.println("Setting up JMeter test: " + getTestName());
        jmeterEngine = new JMeterEngine();
    }

    protected abstract String getTestName();

    protected ThreadGroup createThreadGroup(String name, int numThreads, int rampUp, int loops) {
        return jmeterEngine.createThreadGroup(name, numThreads, rampUp, loops);
    }

    protected ThreadGroup createThreadGroup(String name, int numThreads, int rampUp) {
        return createThreadGroup(name, numThreads, rampUp, 1); // Varsayılan olarak 1 loop
    }

    protected void executeTest(ThreadGroup threadGroup, HTTPSamplerProxy httpSampler, HeaderManager headerManager) {
        jmeterEngine.setupTestPlan(getTestName(), threadGroup, httpSampler, headerManager);
        jmeterEngine.runTest();
    }
}