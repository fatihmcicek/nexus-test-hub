package com.nexustest.performance.config;

import com.nexustest.performance.reports.JMeterReportGenerator;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import java.io.File;
import java.util.Map;

public class JMeterEngine {
    private StandardJMeterEngine jmeter;
    private ListedHashTree testPlanTree;

    public JMeterEngine() {
        File jmeterHome = new File(System.getProperty("user.dir") + "/src/test/resources/jmeter");
        if (!jmeterHome.exists()) {
            jmeterHome.mkdirs();
        }

        File jmeterProperties = new File(jmeterHome.getPath() + "/bin/jmeter.properties");
        if (!jmeterProperties.exists()) {
            try {
                jmeterProperties.getParentFile().mkdirs();
                jmeterProperties.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("Could not create jmeter.properties", e);
            }
        }

        JMeterUtils.setJMeterHome(jmeterHome.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        JMeterUtils.initLocale();

        jmeter = new StandardJMeterEngine();
        testPlanTree = new ListedHashTree();
    }

    public ThreadGroup createThreadGroup(String name, int numThreads, int rampUp, int loops) {
        LoopController loopController = new LoopController();
        loopController.setLoops(loops);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopController.class.getName());

        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName(name);
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroup.class.getName());

        return threadGroup;
    }

    public ThreadGroup createThreadGroup(String name, int numThreads, int rampUp) {
        return createThreadGroup(name, numThreads, rampUp, 1);
    }

    public HTTPSamplerProxy createHttpSampler(String name, String domain, String path,
                                              String method, Map<String, String> params) {
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setDomain(domain);
        httpSampler.setPort(443);
        httpSampler.setProtocol("https");
        httpSampler.setPath(path);
        httpSampler.setMethod(method);
        httpSampler.setName(name);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HTTPSamplerProxy.class.getName());

        if (params != null) {
            params.forEach((key, value) -> {
                HTTPArgument arg = new HTTPArgument();
                arg.setName(key);
                arg.setValue(value);
                arg.setMetaData("=");
                httpSampler.getArguments().addArgument(arg);
            });
        }

        return httpSampler;
    }

    public HeaderManager createHeaderManager() {
        HeaderManager headerManager = new HeaderManager();
        headerManager.add(new Header("Content-Type", "application/json"));
        headerManager.add(new Header("Accept", "application/json"));
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderManager.class.getName());
        return headerManager;
    }

    public void setupTestPlan(String testName, ThreadGroup threadGroup,
                              HTTPSamplerProxy httpSampler, HeaderManager headerManager) {
        TestPlan testPlan = new TestPlan(testName);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlan.class.getName());

        ListedHashTree testPlanTree = new ListedHashTree();
        testPlanTree.add(testPlan);

        ListedHashTree threadGroupHashTree = new ListedHashTree();
        threadGroupHashTree.add(threadGroup);
        threadGroupHashTree.add(httpSampler);
        threadGroupHashTree.add(headerManager);

        testPlanTree.add(testPlan, threadGroupHashTree);

        // Add result collector
        JMeterReportGenerator.setupResultCollector(testPlanTree, testName);

        this.testPlanTree = testPlanTree;
    }


    public void runTest() {
        try {
            jmeter.configure(testPlanTree);
            jmeter.run();
        } catch (Exception e) {
            throw new RuntimeException("Error running JMeter test", e);
        }
    }
}