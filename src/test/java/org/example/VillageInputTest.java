package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStream;

import static org.mockito.Mockito.*;

class VillageInputTest {

    private DatabaseConnection databaseConnection;
    private Village village;
    private VillageInput villageInput;
    private final InputStream originalSystemIn = System.in;
    private ByteArrayInputStream testIn;

    @BeforeEach
    void setUp() {
        databaseConnection = mock(DatabaseConnection.class);
        village = mock(Village.class);

        // No need to replace System.in anymore, so these parts are removed
    }

    @AfterEach
    void restoreSystemIn() {
        // Restore original System.in after each test
        System.setIn(originalSystemIn);
    }

    @Test
    void testSave() {
        // Set up mock behavior
        ArrayList<String> villages = new ArrayList<>();
        villages.add("existing_village");
        when(databaseConnection.GetTownNames()).thenReturn(villages);
        when(databaseConnection.SaveVillage(any(Village.class), anyString())).thenReturn(true);

        // Simulate input
        String input = "new_village\ny\n";
        testIn = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(testIn);

        // Inject the mock Scanner
        villageInput = new VillageInput(village, databaseConnection, scanner);

        // Call method
        villageInput.Save();

        // Verify interactions
        verify(databaseConnection, times(1)).GetTownNames();
        verify(databaseConnection, times(1)).SaveVillage(any(Village.class), eq("new_village"));
    }

    @Test
    void testLoad() {
        // Set up mock behavior
        ArrayList<String> villages = new ArrayList<>();
        villages.add("existing_village");
        when(databaseConnection.GetTownNames()).thenReturn(villages);
        when(databaseConnection.LoadVillage(anyString())).thenReturn(new Village());

        // Simulate input
        String input = "existing_village\n";
        testIn = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(testIn);

        // Inject the mock Scanner
        villageInput = new VillageInput(village, databaseConnection, scanner);

        // Call method
        villageInput.Load();

        // Verify interactions
        verify(databaseConnection, times(1)).GetTownNames();
        verify(databaseConnection, times(1)).LoadVillage(eq("existing_village"));
    }
}
