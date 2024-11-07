package com.nexustest.api.tests;

import com.nexustest.api.models.Pet;
import com.nexustest.api.services.PetService;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import java.util.Collections;
import java.util.List;
import static org.testng.Assert.*;

public class PetTest {
    private PetService petService;
    private Long savedPetId;

    @BeforeClass(alwaysRun = true)  // alwaysRun ekledik
    public void setup() {
        System.out.println("Initializing PetService...");
        petService = new PetService();
    }

    @DataProvider(name = "petStatuses")
    public Object[][] getPetStatuses() {
        return new Object[][] {
                {"available"},
                {"pending"},
                {"sold"}
        };
    }

    @Test(priority = 1, description = "TC01 - Create a new pet and verify its details")
    public void TC01_testCreateAndGetPet() {
        System.out.println("Running TC01...");
        assertNotNull(petService, "PetService should not be null");

        Pet pet = createTestPet("Buddy", "available");

        Response createResponse = petService.createPet(pet);
        assertEquals(createResponse.getStatusCode(), 200, "Pet creation failed");

        Pet createdPet = createResponse.as(Pet.class);
        savedPetId = createdPet.getId();
        assertNotNull(savedPetId, "Pet ID should not be null");

        Response getResponse = petService.getPetById(savedPetId);
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get pet by ID");

        Pet retrievedPet = getResponse.as(Pet.class);
        assertEquals(retrievedPet.getName(), pet.getName(), "Pet name does not match");
        assertEquals(retrievedPet.getStatus(), pet.getStatus(), "Pet status does not match");
    }

    @Test(priority = 2, description = "TC02 - Update existing pet's information",
            dependsOnMethods = "TC01_testCreateAndGetPet")
    public void TC02_testUpdatePet() {
        System.out.println("Running TC02...");
        assertNotNull(petService, "PetService should not be null");
        assertNotNull(savedPetId, "SavedPetId should not be null");

        // Get existing pet
        Response getResponse = petService.getPetById(savedPetId);
        Pet existingPet = getResponse.as(Pet.class);

        // Update pet details
        existingPet.setName("Buddy Updated");
        existingPet.setStatus("sold");

        // Update pet
        Response updateResponse = petService.updatePet(existingPet);
        assertEquals(updateResponse.getStatusCode(), 200, "Pet update failed");

        // Verify updates
        Response verifyResponse = petService.getPetById(savedPetId);
        Pet updatedPet = verifyResponse.as(Pet.class);
        assertEquals(updatedPet.getName(), "Buddy Updated", "Pet name update failed");
        assertEquals(updatedPet.getStatus(), "sold", "Pet status update failed");
    }

    @Test(priority = 3, description = "TC03 - Find pets by status",
            dataProvider = "petStatuses")
    public void TC03_testFindPetsByStatus(String status) {
        System.out.println("Running TC03 with status: " + status);
        assertNotNull(petService, "PetService should not be null");

        Response response = petService.getPetsByStatus(status);
        assertEquals(response.getStatusCode(), 200, "Failed to get pets by status");

        List<Pet> pets = response.jsonPath().getList("", Pet.class);
        assertNotNull(pets, "Pet list should not be null");

        // Verify all returned pets have the correct status
        for (Pet pet : pets) {
            assertEquals(pet.getStatus(), status,
                    String.format("Pet with ID %d has incorrect status %s", pet.getId(), pet.getStatus()));
        }
    }

    @Test(priority = 4, description = "TC04 - Delete a pet",
            dependsOnMethods = {"TC01_testCreateAndGetPet", "TC02_testUpdatePet"})
    public void TC04_testDeletePet() {
        System.out.println("Running TC04...");
        assertNotNull(petService, "PetService should not be null");
        assertNotNull(savedPetId, "SavedPetId should not be null");

        Response deleteResponse = petService.deletePet(savedPetId);
        assertEquals(deleteResponse.getStatusCode(), 200, "Failed to delete pet");

        // Verify pet is deleted
        Response getResponse = petService.getPetById(savedPetId);
        assertEquals(getResponse.getStatusCode(), 404, "Pet should not exist after deletion");
    }

    @Test(priority = 5, description = "TC05 - Attempt to get non-existent pet")
    public void TC05_testGetNonExistentPet() {
        System.out.println("Running TC05...");
        assertNotNull(petService, "PetService should not be null");

        Response response = petService.getPetById(999999999L);
        assertEquals(response.getStatusCode(), 404, "Should return 404 for non-existent pet");
    }

    @Test(priority = 6, description = "TC06 - Create pet with invalid status")
    public void TC06_testCreatePetWithInvalidStatus() {
        System.out.println("Running TC06...");
        assertNotNull(petService, "PetService should not be null");
    }

    // Helper method to create test pet
    private Pet createTestPet(String name, String status) {
        Pet.Category category = Pet.Category.builder()
                .id(1L)
                .name("Dogs")
                .build();

        Pet.Tag tag = Pet.Tag.builder()
                .id(1L)
                .name("friendly")
                .build();

        return Pet.builder()
                .name(name)
                .status(status)
                .category(category)
                .photoUrls(Collections.singletonList("http://example.com/photo.jpg"))
                .tags(Collections.singletonList(tag))
                .build();
    }
}