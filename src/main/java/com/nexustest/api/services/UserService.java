package com.nexustest.api.services;

import com.nexustest.api.base.BaseRequest;
import com.nexustest.api.models.User;
import com.nexustest.core.constants.ApiEndpoints;
import io.restassured.response.Response;
import java.util.List;

public class UserService extends BaseRequest {

    public Response createUser(User user) {
        return setup()
                .body(user)
                .when()
                .post(ApiEndpoints.USER);
    }

    public Response createUsersWithList(List<User> users) {
        return setup()
                .body(users)
                .when()
                .post(ApiEndpoints.USER + "/createWithList");
    }

    public Response getUserByUsername(String username) {
        return setup()
                .pathParam("username", username)
                .when()
                .get(ApiEndpoints.USER_BY_USERNAME);
    }

    public Response updateUser(String username, User user) {
        return setup()
                .pathParam("username", username)
                .body(user)
                .when()
                .put(ApiEndpoints.USER_BY_USERNAME);
    }

    public Response deleteUser(String username) {
        return setup()
                .pathParam("username", username)
                .when()
                .delete(ApiEndpoints.USER_BY_USERNAME);
    }

    public Response loginUser(String username, String password) {
        return setup()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(ApiEndpoints.USER_LOGIN);
    }

    public Response logoutUser() {
        return setup()
                .when()
                .get(ApiEndpoints.USER_LOGOUT);
    }
}