package com.nexustest.api.services;

import com.nexustest.api.base.BaseRequest;
import com.nexustest.api.models.Pet;
import com.nexustest.core.constants.ApiEndpoints;
import io.restassured.response.Response;

public class PetService extends BaseRequest {

    public Response createPet(Pet pet) {
        return setup()
                .body(pet)
                .when()
                .post(ApiEndpoints.PET);
    }

    public Response getPetById(Long petId) {
        return setup()
                .pathParam("petId", petId)
                .when()
                .get(ApiEndpoints.PET_BY_ID);
    }

    public Response getPetsByStatus(String status) {
        return setup()
                .queryParam("status", status)
                .when()
                .get(ApiEndpoints.PET_BY_STATUS);
    }

    public Response updatePet(Pet pet) {
        return setup()
                .body(pet)
                .when()
                .put(ApiEndpoints.PET);
    }

    public Response deletePet(Long petId) {
        return setup()
                .pathParam("petId", petId)
                .when()
                .delete(ApiEndpoints.PET_BY_ID);
    }
}