package com.nexustest.api.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import com.nexustest.core.config.ConfigManager;

public class BaseRequest {
    protected RequestSpecification setup() {
        RestAssured.baseURI = ConfigManager.getInstance().getBaseUrl();

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    protected RequestSpecification setup(String token) {
        return setup()
                .header("Authorization", "Bearer " + token);
    }
}