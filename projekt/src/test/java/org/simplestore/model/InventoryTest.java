package org.simplestore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        product1 = new Product(1, "Product1", 100.0);
        product2 = new Product(2, "Product2", 200.0);
        inventory.addProduct(product1);
        inventory.addProduct(product2);
    }

    @Test
    void addAndGetProduct() throws ProductNotFoundException {
        assertEquals(product1, inventory.getProduct(1), "Product1 should be retrieved correctly");
        assertEquals(product2, inventory.getProduct(2), "Product2 should be retrieved correctly");
    }

    @Test
    void getProductNotFound() {
        assertThrows(ProductNotFoundException.class, () -> inventory.getProduct(3), "Should throw ProductNotFoundException for non-existing product");
    }

    @Test
    void listAllProducts() {
        Collection<Product> products = inventory.listAllProducts();
        assertTrue(products.contains(product1), "Product list should contain Product1");
        assertTrue(products.contains(product2), "Product list should contain Product2");
        assertEquals(2, products.size(), "Product list should contain two products");
    }

    @Test
    void removeProduct() {
        inventory.removeProduct(1);
        assertThrows(ProductNotFoundException.class, () -> inventory.getProduct(1), "Product1 should be removed and not found");
    }

    // Note for presenter: Explain the importance of testing each functionality of the Inventory class.
    // Discuss how these tests ensure the correctness of adding, retrieving, listing, and removing products.
}
