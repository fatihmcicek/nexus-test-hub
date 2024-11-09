package com.nexustest.api.tests;

import com.nexustest.api.models.Pet;
import com.nexustest.api.services.PetService;
import com.nexustest.utils.ExtentReportManager;
import com.nexustest.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.annotations.*;

import java.util.List;

import static org.testng.Assert.*;

@Listeners(com.nexustest.utils.TestListener.class)
public class PetTest {
    private PetService petService;
    private Long savedPetId;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        System.out.println("Initializing PetService...");
        petService = new PetService();
    }

    @DataProvider(name = "petStatuses")
    public Object[][] getPetStatuses() {
        ExtentReportManager.getTest().info("Setting up data provider for pet statuses");
        return new Object[][]{
                {"available"},
                {"pending"},
                {"sold"}
        };
    }

    @Test(priority = 1, description = "TC01 - Create a new pet and verify its details")
    public void TC01_testCreateAndGetPet() {
        ExtentReportManager.getTest().info("Starting test: Create new pet");
        assertNotNull(petService, "PetService should not be null");

        ExtentReportManager.getTest().info("Loading test data from JSON file");
        Pet pet = TestDataReader.getTestData("pet-data.json", "validPet", Pet.class);

        ExtentReportManager.getTest().info("Creating new pet with name: " + pet.getName());
        Response createResponse = petService.createPet(pet);
        ExtentReportManager.getTest().info("Create pet API response code: " + createResponse.getStatusCode());
        assertEquals(createResponse.getStatusCode(), 200, "Pet creation failed");

        Pet createdPet = createResponse.as(Pet.class);
        savedPetId = createdPet.getId();
        ExtentReportManager.getTest().info("Created pet with ID: " + savedPetId);

        ExtentReportManager.getTest().info("Verifying created pet details");
        Response getResponse = petService.getPetById(savedPetId);
        ExtentReportManager.getTest().info("Get pet API response code: " + getResponse.getStatusCode());
        assertEquals(getResponse.getStatusCode(), 200, "Failed to get pet by ID");

        Pet retrievedPet = getResponse.as(Pet.class);
        ExtentReportManager.getTest().info("Verifying pet name and status");
        assertEquals(retrievedPet.getName(), pet.getName(), "Pet name does not match");
        assertEquals(retrievedPet.getStatus(), pet.getStatus(), "Pet status does not match");
    }

    @Test(priority = 2, description = "TC02 - Update existing pet's information",
            dependsOnMethods = "TC01_testCreateAndGetPet")
    public void TC02_testUpdatePet() {
        ExtentReportManager.getTest().info("Starting test: Update existing pet");
        assertNotNull(petService, "PetService should not be null");
        assertNotNull(savedPetId, "SavedPetId should not be null");

        ExtentReportManager.getTest().info("Getting existing pet with ID: " + savedPetId);
        Response getResponse = petService.getPetById(savedPetId);
        Pet existingPet = getResponse.as(Pet.class);

        ExtentReportManager.getTest().info("Loading update data from JSON");
        Pet updateData = TestDataReader.getTestData("pet-data.json", "updatePet", Pet.class);
        existingPet.setName(updateData.getName());
        existingPet.setStatus(updateData.getStatus());

        ExtentReportManager.getTest().info("Updating pet with new name: " + updateData.getName() + " and status: " + updateData.getStatus());
        Response updateResponse = petService.updatePet(existingPet);
        ExtentReportManager.getTest().info("Update pet API response code: " + updateResponse.getStatusCode());
        assertEquals(updateResponse.getStatusCode(), 200, "Pet update failed");

        ExtentReportManager.getTest().info("Verifying updated pet details");
        Response verifyResponse = petService.getPetById(savedPetId);
        Pet updatedPet = verifyResponse.as(Pet.class);
        assertEquals(updatedPet.getName(), updateData.getName(), "Pet name update failed");
        assertEquals(updatedPet.getStatus(), updateData.getStatus(), "Pet status update failed");
    }

    @Test(priority = 3, description = "TC03 - Find pets by status",
            dataProvider = "petStatuses")
    public void TC03_testFindPetsByStatus(String status) {
        ExtentReportManager.getTest().info("Starting test: Find pets by status: " + status);
        assertNotNull(petService, "PetService should not be null");

        ExtentReportManager.getTest().info("Searching for pets with status: " + status);
        Response response = petService.getPetsByStatus(status);
        ExtentReportManager.getTest().info("Find pets API response code: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), 200, "Failed to get pets by status");

        List<Pet> pets = response.jsonPath().getList("", Pet.class);
        ExtentReportManager.getTest().info("Found " + pets.size() + " pets with status: " + status);
        assertNotNull(pets, "Pet list should not be null");

        ExtentReportManager.getTest().info("Verifying status of each pet in the response");
        for (Pet pet : pets) {
            assertEquals(pet.getStatus(), status,
                    String.format("Pet with ID %d has incorrect status %s", pet.getId(), pet.getStatus()));
        }
    }

    @Test(priority = 4, description = "TC04 - Delete a pet",
            dependsOnMethods = {"TC01_testCreateAndGetPet", "TC02_testUpdatePet"})
    public void TC04_testDeletePet() {
        ExtentReportManager.getTest().info("Starting test: Delete pet");
        assertNotNull(petService, "PetService should not be null");
        assertNotNull(savedPetId, "SavedPetId should not be null");

        ExtentReportManager.getTest().info("Deleting pet with ID: " + savedPetId);
        Response deleteResponse = petService.deletePet(savedPetId);
        ExtentReportManager.getTest().info("Delete pet API response code: " + deleteResponse.getStatusCode());
        assertEquals(deleteResponse.getStatusCode(), 200, "Failed to delete pet");

        ExtentReportManager.getTest().info("Verifying pet deletion");
        Response getResponse = petService.getPetById(savedPetId);
        ExtentReportManager.getTest().info("Get deleted pet API response code: " + getResponse.getStatusCode());
        assertEquals(getResponse.getStatusCode(), 404, "Pet should not exist after deletion");
    }

    @Test(priority = 5, description = "TC05 - Attempt to get non-existent pet")
    public void TC05_testGetNonExistentPet() {
        ExtentReportManager.getTest().info("Starting test: Get non-existent pet");
        assertNotNull(petService, "PetService should not be null");

        Long nonExistentId = 999999999L;
        ExtentReportManager.getTest().info("Attempting to get pet with non-existent ID: " + nonExistentId);
        Response response = petService.getPetById(nonExistentId);
        ExtentReportManager.getTest().info("Get non-existent pet API response code: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), 404, "Should return 404 for non-existent pet");
    }

    @Test(priority = 6, description = "TC06 - Create pet with invalid status")
    public void TC06_testCreatePetWithInvalidStatus() {
        ExtentReportManager.getTest().info("Starting test: Create pet with invalid status");
        assertNotNull(petService, "PetService should not be null");

        ExtentReportManager.getTest().info("Loading invalid pet data from JSON");
        Pet invalidPet = TestDataReader.getTestData("pet-data.json", "invalidPet", Pet.class);

        ExtentReportManager.getTest().info("Attempting to create pet with invalid status: " + invalidPet.getStatus());
        Response response = petService.createPet(invalidPet);
        ExtentReportManager.getTest().info("Create invalid pet API response code: " + response.getStatusCode());
    }
}