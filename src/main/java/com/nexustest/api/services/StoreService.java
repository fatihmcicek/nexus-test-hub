package com.nexustest.api.services;

import com.nexustest.api.base.BaseRequest;
import com.nexustest.api.models.Order;
import com.nexustest.core.constants.ApiEndpoints;
import io.restassured.response.Response;

public class StoreService extends BaseRequest {

    public Response placeOrder(Order order) {
        return setup()
                .body(order)
                .when()
                .post(ApiEndpoints.STORE_ORDER);
    }

    public Response getOrderById(Long orderId) {
        return setup()
                .pathParam("orderId", orderId)
                .when()
                .get(ApiEndpoints.STORE_ORDER_BY_ID);
    }

    public Response deleteOrder(Long orderId) {
        return setup()
                .pathParam("orderId", orderId)
                .when()
                .delete(ApiEndpoints.STORE_ORDER_BY_ID);
    }

    public Response getInventory() {
        return setup()
                .when()
                .get(ApiEndpoints.STORE_INVENTORY);
    }
}