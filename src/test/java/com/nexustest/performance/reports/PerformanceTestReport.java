package com.nexustest.performance.reports;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

public class PerformanceTestReport {
    private static final String REPORT_DIR = "target/performance-reports/";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final Map<String, TestMetrics> testMetrics = new TreeMap<>();

    public void addMetrics(String testName, TestMetrics metrics) {
        testMetrics.put(testName, metrics);
    }

    public void generateHtmlReport() {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            File reportDir = new File(REPORT_DIR);
            reportDir.mkdirs();

            String reportPath = REPORT_DIR + "performance_report_" + timestamp + ".html";
            try (PrintWriter writer = new PrintWriter(new FileWriter(reportPath))) {
                writeHtmlHeader(writer);
                writeTestResults(writer);
                writeHtmlFooter(writer);
            }

            System.out.println("Performance report generated: " + reportPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHtmlHeader(PrintWriter writer) {
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Performance Test Report</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        writer.println("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
        writer.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        writer.println("th { background-color: #4CAF50; color: white; }");
        writer.println("tr:nth-child(even) { background-color: #f2f2f2; }");
        writer.println(".pass { color: green; }");
        writer.println(".fail { color: red; }");
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("<h1>Performance Test Report</h1>");
        writer.println("<p>Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>");
    }

    private void writeTestResults(PrintWriter writer) {
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<th>Test Name</th>");
        writer.println("<th>Samples</th>");
        writer.println("<th>Average Response Time (ms)</th>");
        writer.println("<th>90% Response Time (ms)</th>");
        writer.println("<th>Error Rate (%)</th>");
        writer.println("<th>Throughput (req/sec)</th>");
        writer.println("<th>Status</th>");
        writer.println("</tr>");

        for (Map.Entry<String, TestMetrics> entry : testMetrics.entrySet()) {
            TestMetrics metrics = entry.getValue();
            writer.println("<tr>");
            writer.println("<td>" + entry.getKey() + "</td>");
            writer.println("<td>" + metrics.getSamples() + "</td>");
            writer.println("<td>" + String.format("%.2f", metrics.getAverageResponseTime()) + "</td>");
            writer.println("<td>" + String.format("%.2f", metrics.getPercentile90()) + "</td>");
            writer.println("<td>" + String.format("%.2f", metrics.getErrorRate()) + "</td>");
            writer.println("<td>" + String.format("%.2f", metrics.getThroughput()) + "</td>");
            writer.println("<td class=\"" + (metrics.isPassed() ? "pass" : "fail") + "\">" +
                    (metrics.isPassed() ? "PASS" : "FAIL") + "</td>");
            writer.println("</tr>");
        }
        writer.println("</table>");
    }

    private void writeHtmlFooter(PrintWriter writer) {
        writer.println("</body>");
        writer.println("</html>");
    }

    public static class TestMetrics {
        private final int samples;
        private final double averageResponseTime;
        private final double percentile90;
        private final double errorRate;
        private final double throughput;
        private final boolean passed;

        public TestMetrics(int samples, double averageResponseTime, double percentile90,
                           double errorRate, double throughput, boolean passed) {
            this.samples = samples;
            this.averageResponseTime = averageResponseTime;
            this.percentile90 = percentile90;
            this.errorRate = errorRate;
            this.throughput = throughput;
            this.passed = passed;
        }

        // Getters
        public int getSamples() { return samples; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public double getPercentile90() { return percentile90; }
        public double getErrorRate() { return errorRate; }
        public double getThroughput() { return throughput; }
        public boolean isPassed() { return passed; }
    }
}