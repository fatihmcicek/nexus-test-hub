package com.nexustest.api.tests;

import com.nexustest.api.models.User;
import com.nexustest.api.services.UserService;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

public class UserTest {
    private UserService userService;
    private String testUsername;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        System.out.println("Initializing UserService...");
        userService = new UserService();
        testUsername = "testuser" + System.currentTimeMillis();
    }

    @Test(priority = 1, description = "TC01 - Create a new user and verify")
    public void TC01_testCreateUser() {
        System.out.println("Running User TC01...");
        assertNotNull(userService, "UserService should not be null");

        User user = createTestUser(testUsername);
        Response createResponse = userService.createUser(user);
        assertEquals(createResponse.getStatusCode(), 200, "User creation failed");

        // Verify user creation
        Response getResponse = userService.getUserByUsername(testUsername);
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get user");

        User createdUser = getResponse.as(User.class);
        assertEquals(createdUser.getUsername(), user.getUsername(), "Username does not match");
        assertEquals(createdUser.getEmail(), user.getEmail(), "Email does not match");
    }

    @Test(priority = 2, description = "TC02 - Create multiple users with list")
    public void TC02_testCreateUsersWithList() {
        System.out.println("Running User TC02...");
        User user1 = createTestUser("testuser1" + System.currentTimeMillis());
        User user2 = createTestUser("testuser2" + System.currentTimeMillis());

        Response response = userService.createUsersWithList(Arrays.asList(user1, user2));
        assertEquals(response.getStatusCode(), 200, "Failed to create users with list");
    }

    @Test(priority = 3, description = "TC03 - Update user information",
            dependsOnMethods = "TC01_testCreateUser")
    public void TC03_testUpdateUser() {
        System.out.println("Running User TC03...");

        User updatedUser = createTestUser(testUsername);
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setEmail("updated@email.com");

        Response updateResponse = userService.updateUser(testUsername, updatedUser);
        assertEquals(updateResponse.getStatusCode(), 200, "User update failed");

    }

    @Test(priority = 4, description = "TC04 - User login and logout")
    public void TC04_testLoginLogout() {
        System.out.println("Running User TC04...");

        Response loginResponse = userService.loginUser(testUsername, "Test123!");
        assertEquals(loginResponse.getStatusCode(), 200, "Login failed");
        String sessionToken = loginResponse.getHeader("X-Expires-After");
        assertNotNull(sessionToken, "Session token should not be null");

        Response logoutResponse = userService.logoutUser();
        assertEquals(logoutResponse.getStatusCode(), 200, "Logout failed");
    }

    @Test(priority = 5, description = "TC05 - Delete user",
            dependsOnMethods = {"TC01_testCreateUser", "TC03_testUpdateUser"})
    public void TC05_testDeleteUser() {
        System.out.println("Running User TC05...");

        Response deleteResponse = userService.deleteUser(testUsername);
        assertEquals(deleteResponse.getStatusCode(), 200, "User deletion failed");
    }

    private User createTestUser(String username) {
        return User.builder()
                .username(username)
                .firstName("Test")
                .lastName("User")
                .email(username + "@test.com")
                .password("Test123!")
                .phone("+1234567890")
                .userStatus(0)
                .build();
    }
}