package com.nexustest.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class TestDataReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode getTestData(String fileName) {
        try (InputStream is = TestDataReader.class
                .getClassLoader()
                .getResourceAsStream("test-data/" + fileName)) {

            if (is == null) {
                throw new RuntimeException("Test data file not found: " + fileName);
            }

            return objectMapper.readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("Error reading test data file: " + fileName, e);
        }
    }

    public static <T> T getTestData(String fileName, String node, Class<T> valueType) {
        try {
            JsonNode jsonNode = getTestData(fileName);
            return objectMapper.treeToValue(jsonNode.get(node), valueType);
        } catch (IOException e) {
            throw new RuntimeException("Error converting test data for node: " + node, e);
        }
    }
}