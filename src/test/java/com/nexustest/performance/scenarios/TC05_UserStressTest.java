package com.nexustest.performance.scenarios;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.testng.annotations.Test;
import java.util.HashMap;

/**
 * TC05: Kullanıcı Stresi Testi (User Stress Test)
 * Amaç: Kullanıcı login operasyonlarının yüksek yük altında davranışını test etmek
 * - 100 eşzamanlı kullanıcı
 * - 10 saniye ramp-up süresi
 * - Her kullanıcı 5 kez login denemesi yapıyor
 */
public class TC05_UserStressTest extends BasePerformanceTest {

    @Override
    protected String getTestName() {
        return "User API Stress Test";
    }

    @Override
    public void execute() {
        System.out.println("Executing User API stress test...");

        ThreadGroup loginThreadGroup = createThreadGroup(
                "User Login Stress",
                100,  // High number of threads
                10,   // Ramp-up period
                5    // Loop count - her kullanıcı 5 kez deneyecek
        );

        HashMap<String, String> loginParams = new HashMap<>();
        loginParams.put("username", "test");
        loginParams.put("password", "test123");

        HTTPSamplerProxy loginSampler = jmeterEngine.createHttpSampler(
                "User Login",
                BASE_URL,
                "/v2/user/login",
                "GET",
                loginParams
        );

        HeaderManager headerManager = jmeterEngine.createHeaderManager();
        executeTest(loginThreadGroup, loginSampler, headerManager);
    }

    @Test(description = "Execute User API Stress Test")
    public void runUserStressTest() {
        setup();
        execute();
        generateReport();
    }

    @Override
    public void generateReport() {
        System.out.println("Generating User API stress test report...");
    }
}