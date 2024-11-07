package com.nexustest.api.tests;

import com.nexustest.api.models.Order;
import com.nexustest.api.services.StoreService;
import com.nexustest.utils.ExtentReportManager;
import com.nexustest.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.util.Map;
import static org.testng.Assert.*;

@Listeners(com.nexustest.utils.TestListener.class)
public class StoreTest {
    private StoreService storeService;
    private Long savedOrderId;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        System.out.println("Initializing PetService...");
        storeService = new StoreService();
    }

    @Test(priority = 1, description = "TC01 - Place a new order and verify its details")
    public void TC01_testPlaceOrder() {
        ExtentReportManager.getTest().info("Starting test: Place new order");
        assertNotNull(storeService, "StoreService should not be null");

        ExtentReportManager.getTest().info("Loading order test data from JSON");
        Order order = TestDataReader.getTestData("store-data.json", "validOrder", Order.class);

        ExtentReportManager.getTest().info("Creating new order for petId: " + order.getPetId());
        Response createResponse = storeService.placeOrder(order);
        ExtentReportManager.getTest().info("Place order API response code: " + createResponse.getStatusCode());
        assertEquals(createResponse.getStatusCode(), 200, "Order creation failed");

        Order createdOrder = createResponse.as(Order.class);
        savedOrderId = createdOrder.getId();
        ExtentReportManager.getTest().info("Created order with ID: " + savedOrderId);

        ExtentReportManager.getTest().info("Verifying created order details");
        Response getResponse = storeService.getOrderById(savedOrderId);
        ExtentReportManager.getTest().info("Get order API response code: " + getResponse.getStatusCode());
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get order by ID");

        Order retrievedOrder = getResponse.as(Order.class);
        ExtentReportManager.getTest().info("Verifying order details");
        assertEquals(retrievedOrder.getPetId(), order.getPetId(), "Pet ID does not match");
        assertEquals(retrievedOrder.getQuantity(), order.getQuantity(), "Quantity does not match");
        assertEquals(retrievedOrder.getStatus(), order.getStatus(), "Order status does not match");
    }

    @Test(priority = 2, description = "TC02 - Get store inventory",
            dependsOnMethods = "TC01_testPlaceOrder")
    public void TC02_testGetInventory() {
        ExtentReportManager.getTest().info("Starting test: Get store inventory");
        assertNotNull(storeService, "StoreService should not be null");

        ExtentReportManager.getTest().info("Requesting store inventory");
        Response response = storeService.getInventory();
        ExtentReportManager.getTest().info("Get inventory API response code: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), 200, "Failed to get inventory");

        Map<String, Integer> inventory = response.as(Map.class);
        ExtentReportManager.getTest().info("Retrieved inventory data");
        assertNotNull(inventory, "Inventory should not be null");
        assertTrue(inventory.size() > 0, "Inventory should not be empty");

        ExtentReportManager.getTest().info("Inventory status counts:");
        inventory.forEach((status, count) ->
                ExtentReportManager.getTest().info(String.format("Status: %s, Count: %d", status, count)));
    }

    @Test(priority = 3, description = "TC03 - Get order by invalid ID")
    public void TC03_testGetInvalidOrder() {
        ExtentReportManager.getTest().info("Starting test: Get invalid order");
        assertNotNull(storeService, "StoreService should not be null");

        Long invalidOrderId = 999999999L;
        ExtentReportManager.getTest().info("Attempting to get order with invalid ID: " + invalidOrderId);
        Response response = storeService.getOrderById(invalidOrderId);
        ExtentReportManager.getTest().info("Get invalid order API response code: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), 404, "Should return 404 for non-existent order");
    }

    @Test(priority = 4, description = "TC04 - Delete order",
            dependsOnMethods = "TC01_testPlaceOrder")
    public void TC04_testDeleteOrder() {
        ExtentReportManager.getTest().info("Starting test: Delete order");
        assertNotNull(storeService, "StoreService should not be null");
        assertNotNull(savedOrderId, "SavedOrderId should not be null");

        ExtentReportManager.getTest().info("Deleting order with ID: " + savedOrderId);
        Response deleteResponse = storeService.deleteOrder(savedOrderId);
        ExtentReportManager.getTest().info("Delete order API response code: " + deleteResponse.getStatusCode());
        assertEquals(deleteResponse.getStatusCode(), 200, "Failed to delete order");

        ExtentReportManager.getTest().info("Verifying order deletion");
        Response getResponse = storeService.getOrderById(savedOrderId);
        ExtentReportManager.getTest().info("Get deleted order API response code: " + getResponse.getStatusCode());
        assertEquals(getResponse.getStatusCode(), 404, "Order should not exist after deletion");
    }
}