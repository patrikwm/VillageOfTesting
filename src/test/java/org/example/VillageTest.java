package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class VillageTest {
    private Village village;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        village = new Village();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void testAddWorker_Success() {
        // Given
        String name = "John Smith";
        String occupation = "miner";

        // When
        village.AddWorker(name, occupation);

        // Then
        assertEquals(1, village.getWorkers().size());
        assertTrue(village.getWorkers().stream().anyMatch(worker ->
                worker.getName().equals(name) && worker.getOccupation().equals(occupation)));
    }

    @Test
    void testAddWorker_OccupationNotFound() {
        // Given
        String name = "Sofia Lee";
        String occupation = "doctor";

        // When
        village.AddWorker(name, occupation);

        // Then
        assertEquals(0, village.getWorkers().size());
    }

    @Test
    void testWorkersPerformTasks() {
        // Given: Add workers
        village.AddWorker("Betty Lundsten", "farmer");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");

        // When: Simulate passing of day
        village.Day();

        // Then: Assert
        assertEquals(12, village.getFood()); // Each farmer gathers 5 food per day. Expected: 10 + 5 - 3 = 12
        assertEquals(1, village.getWood()); // Each lumberjack gathers 1 wood per day
        assertEquals(1, village.getMetal()); // Each miner gathers 1 metal per day
    }

    @Test
    void testCannotAddWorkerIfNoRoom() {
        // Given
        village.AddWorker("Betty Lundsten", "farmer");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");
        village.AddWorker("Bob Williams", "builder");
        village.AddWorker("Jesse Yield", "farmer");
        village.AddWorker("Jack Timber", "lumberjack");

        // When
        village.AddWorker("James Fox", "miner"); // Attempt to add another worker

        // Then
        assertEquals(6, village.getWorkers().size()); // Ensure only 6 workers are present
    }

    @Test
    void testContinueToNextDayWithoutWorkers() {
        // When
        village.Day(); // Attempt to continue to the next day without any workers

        // Then
        assertTrue(village.isGameOver()); // Village should be marked as game over
    }

    @Test
    void testContinueToNextDayWithSufficientFood() {
        // Given
        village.AddWorker("Bob Williams", "builder");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");

        // When
        village.Day(); // Continue to the next day

        // Then
        assertTrue(outputStreamCaptor.toString().contains("eats")); // Verify that workers eat when food is available
    }

    @Test
    void testContinueToNextDayWithInsufficientFood() {
        // Given
        village.AddWorker("Bob Williams", "builder");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");
        village.setFood(0); // No food available

        // When
        village.Day(); // Continue to the next day

        // Then
        assertTrue(outputStreamCaptor.toString().contains("No food left")); // Verify the correct message is printed
    }

    @Test
    void testWorkersWithoutFoodLongEnough() {
        // Given
        village.AddWorker("Bob Williams", "builder");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");

        // When: Increment the number of days. Everyone should be dead after 9 days.
        for (int i = 0; i < 9; i++) {
            village.Day();
        }

        // Then
        assertTrue(village.isGameOver()); // Village should be marked as game over
    }

    @Test
    public void testAddHouseProject() {
        // Given
        village.setWood(6);
        village.setMetal(1);

        // When
        village.AddProject("House");

        // Then
        assertEquals(1, village.getProjects().size());
        assertEquals("House", village.getProjects().get(0).getName());
    }

    @Test
    public void testWoodmillBuildingCompletion() {
        // Given
        // Add workers to gather resources
        village.AddWorker("Betty Lundsten", "farmer");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");
        village.AddWorker("Bob Williams", "builder");

        // Add wood and metal for building a Woodmill
        village.setWood(6);
        village.setMetal(2);

        // Add Woodmill project
        village.AddProject("Woodmill");
        village.Build("Bob Williams");

        // When
        while (!village.getProjects().isEmpty()) {
            village.Day();
        }

        // Then
        // Verify Woodmill is completed
        assertEquals(0, village.getProjects().size());
        assertEquals(4, village.getBuildings().size());
        assertEquals("Woodmill", village.getBuildings().get(village.getBuildings().size()-1).getName());

        // Verify the effect of Woodmill on wood production
        assertEquals(2, village.getWoodPerDay()); // Wood production should increase by 1
    }

    @Test
    public void testAddProjectWithoutEnoughMaterial() {
        // Given
        village.setWood(3);
        village.setMetal(5);

        // When: Try to add a project that requires more wood and metal than available
        village.AddProject("Quarry");

        // Then: Verify that the project is not added due to insufficient material
        assertEquals(0, village.getProjects().size());
    }

    @Test
    public void testCreateCastleAndWinGame() {
        // When
        // Add workers to gather resources
        village.AddWorker("Betty Lundsten", "farmer");
        village.AddWorker("Adam Wood", "lumberjack");
        village.AddWorker("John Smith", "miner");
        village.AddWorker("Jack Timber", "lumberjack");
        village.AddWorker("Gil Braggin", "miner");

        // Simulate days passing to gather resources
        for (int i = 0; i < 22; i++) {
            village.Day();
        }

        // Add Bob the builder and a Farm Project
        village.AddWorker("Bob Williams", "builder");
        village.AddProject("Farm");
        village.Build("Bob Williams");

        // Simulate days passing to gather resources
        for (int i = 0; i < 6; i++) {
            village.Day();
        }

        // Build the Castle
        village.AddProject("Castle");
        village.Build("Bob Williams");

        // Simulate days passing until the Castle is built
        while (!village.getProjects().isEmpty()) {
            village.Day();
        }

        // Then
        // Verify that the Castle is built and the game is won
        assertTrue(village.isGameOver());
        assertEquals("Castle", village.getBuildings().get(4).getName());
    }
}