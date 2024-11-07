package com.nexustest.api.tests;

import com.nexustest.api.models.Order;
import com.nexustest.api.services.StoreService;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import static org.testng.Assert.*;

public class StoreTest {
    private StoreService storeService;
    private Long savedOrderId;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        System.out.println("Initializing StoreService...");
        storeService = new StoreService();
    }

    @Test(priority = 1, description = "TC01 - Place a new order and verify its details")
    public void TC01_testPlaceOrder() {
        System.out.println("Running Store TC01...");
        assertNotNull(storeService, "StoreService should not be null");

        Order order = createTestOrder();
        Response createResponse = storeService.placeOrder(order);
        assertEquals(createResponse.getStatusCode(), 200, "Order creation failed");

        Order createdOrder = createResponse.as(Order.class);
        savedOrderId = createdOrder.getId();
        assertNotNull(savedOrderId, "Order ID should not be null");

        Response getResponse = storeService.getOrderById(savedOrderId);
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get order by ID");

        Order retrievedOrder = getResponse.as(Order.class);
        assertEquals(retrievedOrder.getPetId(), order.getPetId(), "Pet ID does not match");
        assertEquals(retrievedOrder.getQuantity(), order.getQuantity(), "Quantity does not match");
        assertEquals(retrievedOrder.getStatus(), order.getStatus(), "Order status does not match");
    }

    @Test(priority = 2, description = "TC02 - Get store inventory",
            dependsOnMethods = "TC01_testPlaceOrder")
    public void TC02_testGetInventory() {
        System.out.println("Running Store TC02...");
        assertNotNull(storeService, "StoreService should not be null");

        Response response = storeService.getInventory();
        assertEquals(response.getStatusCode(), 200, "Failed to get inventory");

        Map<String, Integer> inventory = response.as(Map.class);
        assertNotNull(inventory, "Inventory should not be null");
        assertTrue(inventory.size() > 0, "Inventory should not be empty");

        inventory.forEach((status, count) ->
                System.out.println("Status: " + status + ", Count: " + count));
    }

    @Test(priority = 3, description = "TC03 - Get order by invalid ID")
    public void TC03_testGetInvalidOrder() {
        System.out.println("Running Store TC03...");
        assertNotNull(storeService, "StoreService should not be null");

        Response response = storeService.getOrderById(999999999L);
        assertEquals(response.getStatusCode(), 404, "Should return 404 for non-existent order");
    }

    @Test(priority = 4, description = "TC04 - Delete order",
            dependsOnMethods = "TC01_testPlaceOrder")
    public void TC04_testDeleteOrder() {
        System.out.println("Running Store TC04...");
        assertNotNull(storeService, "StoreService should not be null");
        assertNotNull(savedOrderId, "SavedOrderId should not be null");

        Response deleteResponse = storeService.deleteOrder(savedOrderId);
        assertEquals(deleteResponse.getStatusCode(), 200, "Failed to delete order");

        Response getResponse = storeService.getOrderById(savedOrderId);
        assertEquals(getResponse.getStatusCode(), 404, "Order should not exist after deletion");
    }

    private Order createTestOrder() {
        return Order.builder()
                .petId(1L)
                .quantity(1)
                .shipDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .status("placed")
                .complete(false)
                .build();
    }
}