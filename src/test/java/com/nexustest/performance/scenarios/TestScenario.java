package com.nexustest.performance.scenarios;

public interface TestScenario {
    void setup();
    void execute();
    void generateReport();
}