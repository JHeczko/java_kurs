package org.simplestore.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InventoryLoaderTest {
    private Path tempFilePath;
    private Inventory inventory;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary file
        tempFilePath = Files.createTempFile("testInventory", ".txt");

        // Write some test data to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath.toFile()))) {
            writer.write("1, Test Product, 10.0\n");
            writer.write("2, Another Product, 20.0\n");
        }

        inventory = new Inventory();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete the temporary file
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    void loadInventory_ShouldLoadProductsCorrectly() throws ProductNotFoundException {
        // Load the inventory from the temporary file
        InventoryLoader.loadInventory(tempFilePath.toString(), inventory);

        // Check if the products are loaded correctly
        assertNotNull(inventory.getProduct(1), "Product with ID 1 should exist");
        assertNotNull(inventory.getProduct(2), "Product with ID 2 should exist");

        // Check the details of the loaded products
        Product product1 = inventory.getProduct(1);
        assertEquals("Test Product", product1.getName(), "Name of product 1 should be 'Test Product'");
        assertEquals(10.0, product1.getPrice(), "Price of product 1 should be 10.0");

        Product product2 = inventory.getProduct(2);
        assertEquals("Another Product", product2.getName(), "Name of product 2 should be 'Another Product'");
        assertEquals(20.0, product2.getPrice(), "Price of product 2 should be 20.0");
    }

    @Test
    void loadInventory_WithInvalidData_ShouldHandleErrors() {
        // Write invalid data to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFilePath.toFile()))) {
            writer.write("invalid data\n");
        } catch (IOException e) {
            fail("Failed to write invalid data to temporary file");
        }

        // Load the inventory from the temporary file
        // TODO Should handle java.lang.NumberFormatException: For input string: "invalid data"
        InventoryLoader.loadInventory(tempFilePath.toString(), inventory);

        // The inventory should be empty as the data is invalid
        assertThrows(Exception.class, () -> inventory.getProduct(1), "Should throw an exception as the product ID 1 does not exist");
    }

    // Note for presenter: Discuss the importance of testing file I/O operations and handling invalid data scenarios.
}
