package com.nexustest.performance.reports;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.visualizers.SamplingStatCalculator;
import org.apache.jorphan.collections.HashTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class JMeterReportGenerator {
    private static final String RESULTS_DIR = "target/jmeter/results/";
    private static final String REPORTS_DIR = "target/jmeter/reports/";
    private static final Map<String, SamplingStatCalculator> statisticsMap = new HashMap<>();

    public static void setupResultCollector(HashTree testPlanTree, String testName) {
        try {
            // Create directories
            new File(RESULTS_DIR).mkdirs();
            new File(REPORTS_DIR).mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String resultFile = RESULTS_DIR + testName + "_" + timestamp + ".jtl";

            // Create custom result collector
            CustomResultCollector resultCollector = new CustomResultCollector(testName);
            resultCollector.setFilename(resultFile);

            // Configure what to save
            SampleSaveConfiguration saveConfig = new SampleSaveConfiguration();
            saveConfig.setAsXml(false); // Save as CSV instead of XML
            saveConfig.setFieldNames(true);
            saveConfig.setTimestamp(true);
            saveConfig.setTime(true);
            saveConfig.setLabel(true);
            saveConfig.setBytes(true);
            saveConfig.setLatency(true);
            saveConfig.setThreadCounts(true);
            saveConfig.setDataType(true);
            saveConfig.setEncoding(true);
            saveConfig.setSampleCount(true);
            saveConfig.setSuccess(true);

            resultCollector.setSaveConfig(saveConfig);

            // Add result collector to test plan
            testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

            System.out.println("Results will be saved to: " + resultFile);

        } catch (Exception e) {
            throw new RuntimeException("Error setting up result collector", e);
        }
    }

    public static void generateReport(String testName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportDir = REPORTS_DIR + testName + "_" + timestamp + "/";
            new File(reportDir).mkdirs();

            // Generate HTML report
            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Performance Test Report</title>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
            html.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            html.append("th { background-color: #4CAF50; color: white; }");
            html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
            html.append("h1, h2 { color: #333; }");
            html.append("</style></head><body>");

            html.append("<h1>Performance Test Report - ").append(testName).append("</h1>");
            html.append("<p>Generated on: ").append(timestamp).append("</p>");

            if (!statisticsMap.isEmpty()) {
                html.append("<h2>Test Results Summary</h2>");
                html.append("<table>");
                html.append("<tr>");
                html.append("<th>Label</th>");
                html.append("<th>Samples</th>");
                html.append("<th>Average (ms)</th>");
                html.append("<th>Min (ms)</th>");
                html.append("<th>Max (ms)</th>");
                html.append("<th>90% Line (ms)</th>");
                html.append("<th>Errors</th>");
                html.append("<th>Throughput</th>");
                html.append("</tr>");

                for (Map.Entry<String, SamplingStatCalculator> entry : statisticsMap.entrySet()) {
                    SamplingStatCalculator stat = entry.getValue();
                    html.append("<tr>");
                    html.append("<td>").append(entry.getKey()).append("</td>");
                    html.append("<td>").append(stat.getCount()).append("</td>");
                    html.append("<td>").append(String.format("%.2f", stat.getMean())).append("</td>");
                    html.append("<td>").append(String.format("%.2f", stat.getMin())).append("</td>");
                    html.append("<td>").append(String.format("%.2f", stat.getMax())).append("</td>");
                    html.append("<td>").append(String.format("%.2f", stat.getPercentPoint(0.90))).append("</td>");
                    html.append("<td>").append(stat.getErrorCount()).append("</td>");
                    html.append("<td>").append(String.format("%.2f", stat.getRate())).append("/sec</td>");
                    html.append("</tr>");
                }
                html.append("</table>");

                // Add summary description
                html.append("<div style='margin-top: 20px;'>");
                html.append("<h2>Metrics Description</h2>");
                html.append("<ul>");
                html.append("<li><strong>Samples:</strong> Total number of requests sent</li>");
                html.append("<li><strong>Average:</strong> Average response time in milliseconds</li>");
                html.append("<li><strong>Min/Max:</strong> Minimum and maximum response times</li>");
                html.append("<li><strong>90% Line:</strong> 90% of requests were completed within this time</li>");
                html.append("<li><strong>Errors:</strong> Number of failed requests</li>");
                html.append("<li><strong>Throughput:</strong> Number of requests processed per second</li>");
                html.append("</ul>");
                html.append("</div>");
            }

            html.append("</body></html>");

            // Write HTML report
            String reportFile = reportDir + "index.html";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                writer.write(html.toString());
            }

            System.out.println("\nTest Report generated successfully!");
            System.out.println("Report location: " + reportFile);

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    // Custom ResultCollector to gather statistics
    private static class CustomResultCollector extends ResultCollector {

        public CustomResultCollector(String testName) {
            super();
        }

        @Override
        public void sampleOccurred(SampleEvent event) {
            super.sampleOccurred(event);

            SampleResult result = event.getResult();
            String label = result.getSampleLabel();

            // Get or create calculator for this label
            SamplingStatCalculator calculator = statisticsMap.computeIfAbsent(label,
                    SamplingStatCalculator::new);

            // Add sample result
            calculator.addSample(result);
        }
    }
}