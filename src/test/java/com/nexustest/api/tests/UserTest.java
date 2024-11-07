package com.nexustest.api.tests;

import com.nexustest.api.models.User;
import com.nexustest.api.services.UserService;
import com.nexustest.utils.ExtentReportManager;
import com.nexustest.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.util.Arrays;
import static org.testng.Assert.*;

@Listeners(com.nexustest.utils.TestListener.class)
public class UserTest {
    private UserService userService;
    private String testUsername;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        System.out.println("Initializing PetService...");
        userService = new UserService();
        testUsername = "testuser" + System.currentTimeMillis();
    }

    @Test(priority = 1, description = "TC01 - Create a new user and verify")
    public void TC01_testCreateUser() {
        ExtentReportManager.getTest().info("Starting test: Create new user");
        assertNotNull(userService, "UserService should not be null");

        ExtentReportManager.getTest().info("Loading user test data from JSON");
        User userData = TestDataReader.getTestData("user-data.json", "validUser", User.class);
        User user = createTestUser(testUsername, userData);

        ExtentReportManager.getTest().info("Creating new user with username: " + user.getUsername());
        Response createResponse = userService.createUser(user);
        ExtentReportManager.getTest().info("Create user API response code: " + createResponse.getStatusCode());
        assertEquals(createResponse.getStatusCode(), 200, "User creation failed");

        ExtentReportManager.getTest().info("Verifying created user details");
        Response getResponse = userService.getUserByUsername(testUsername);
        ExtentReportManager.getTest().info("Get user API response code: " + getResponse.getStatusCode());
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get user");

        User createdUser = getResponse.as(User.class);
        ExtentReportManager.getTest().info("Verifying user details");
        assertEquals(createdUser.getUsername(), user.getUsername(), "Username does not match");
        assertEquals(createdUser.getEmail(), user.getEmail(), "Email does not match");
    }

    @Test(priority = 2, description = "TC02 - Create multiple users with list")
    public void TC02_testCreateUsersWithList() {
        ExtentReportManager.getTest().info("Starting test: Create multiple users");
        ExtentReportManager.getTest().info("Loading user test data from JSON");
        User userData = TestDataReader.getTestData("user-data.json", "validUser", User.class);

        String user1Username = "testuser1" + System.currentTimeMillis();
        String user2Username = "testuser2" + System.currentTimeMillis();

        User user1 = createTestUser(user1Username, userData);
        User user2 = createTestUser(user2Username, userData);

        ExtentReportManager.getTest().info("Creating multiple users: " + user1Username + ", " + user2Username);
        Response response = userService.createUsersWithList(Arrays.asList(user1, user2));
        ExtentReportManager.getTest().info("Create users API response code: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), 200, "Failed to create users with list");
    }

    @Test(priority = 3, description = "TC03 - Update user information",
            dependsOnMethods = "TC01_testCreateUser")
    public void TC03_testUpdateUser() {
        ExtentReportManager.getTest().info("Starting test: Update user");

        ExtentReportManager.getTest().info("Loading update data from JSON");
        User updateData = TestDataReader.getTestData("user-data.json", "updateUser", User.class);

        ExtentReportManager.getTest().info("Creating updated user object");
        User updatedUser = createTestUser(testUsername, updateData);

        ExtentReportManager.getTest().info("Updating user: " + testUsername);
        Response updateResponse = userService.updateUser(testUsername, updatedUser);
        ExtentReportManager.getTest().info("Update user API response code: " + updateResponse.getStatusCode());
        assertEquals(updateResponse.getStatusCode(), 200, "User update failed");
    }

    @Test(priority = 4, description = "TC04 - User login and logout")
    public void TC04_testLoginLogout() {
        ExtentReportManager.getTest().info("Starting test: User login/logout");

        ExtentReportManager.getTest().info("Attempting to login with username: " + testUsername);
        Response loginResponse = userService.loginUser(testUsername, "Test123!");
        ExtentReportManager.getTest().info("Login API response code: " + loginResponse.getStatusCode());
        assertEquals(loginResponse.getStatusCode(), 200, "Login failed");

        String sessionToken = loginResponse.getHeader("X-Expires-After");
        ExtentReportManager.getTest().info("Session token received: " + sessionToken);

        ExtentReportManager.getTest().info("Attempting to logout");
        Response logoutResponse = userService.logoutUser();
        ExtentReportManager.getTest().info("Logout API response code: " + logoutResponse.getStatusCode());
        assertEquals(logoutResponse.getStatusCode(), 200, "Logout failed");
    }

    @Test(priority = 5, description = "TC05 - Delete user",
            dependsOnMethods = {"TC01_testCreateUser", "TC03_testUpdateUser"})
    public void TC05_testDeleteUser() {
        ExtentReportManager.getTest().info("Starting test: Delete user");

        ExtentReportManager.getTest().info("Deleting user: " + testUsername);
        Response deleteResponse = userService.deleteUser(testUsername);
        ExtentReportManager.getTest().info("Delete user API response code: " + deleteResponse.getStatusCode());
        assertEquals(deleteResponse.getStatusCode(), 200, "User deletion failed");

        ExtentReportManager.getTest().info("Verifying user deletion");
        Response getResponse = userService.getUserByUsername(testUsername);
        ExtentReportManager.getTest().info("Get deleted user API response code: " + getResponse.getStatusCode());
    }

    private User createTestUser(String username, User baseData) {
        return User.builder()
                .username(username)
                .firstName(baseData.getFirstName())
                .lastName(baseData.getLastName())
                .email(username + baseData.getEmail())
                .password(baseData.getPassword())
                .phone(baseData.getPhone())
                .userStatus(baseData.getUserStatus())
                .build();
    }
}