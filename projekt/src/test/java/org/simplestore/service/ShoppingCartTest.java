package org.simplestore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShoppingCartTest {
    private ShoppingCart shoppingCart;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.addProduct(new Product(1, "Test Product", 10.0));
        inventory.addProduct(new Product(2, "Another Product", 20.0));
        shoppingCart = new ShoppingCart(inventory);
    }

    @Test
    void addItem_ShouldAddProductToCart() {
        shoppingCart.addItem(1, 1);
        assertEquals(1, shoppingCart.getItemQuantity(1), "Cart should have 1 item of product ID 1");
    }

    @Test
    void removeItem_ShouldRemoveProductFromCart() {
        shoppingCart.addItem(1, 2);
        shoppingCart.removeItem(1, 1);
        assertEquals(1, shoppingCart.getItemQuantity(1), "Cart should have 1 item of product ID 1 after removal");
    }

    @Test
    void clearCart_ShouldEmptyTheCart() {
        shoppingCart.addItem(1, 1);
        shoppingCart.clearCart();
        assertEquals(0, shoppingCart.getItemQuantity(1), "Cart should be empty after clearCart is called");
    }

    @Test
    void calculateTotalCost_ShouldReturnCorrectTotal() throws ProductNotFoundException {
        shoppingCart.addItem(1, 2); // 20.0 total
        shoppingCart.addItem(2, 1); // 20.0 + 20.0 = 40.0 total
        assertEquals(40.0, shoppingCart.calculateTotalCost(), "Total cost should be correct");
    }

    @Test
    void calculateTotalCost_WithInvalidProductId_ShouldThrowException() {
        shoppingCart.addItem(999, 1);
        assertThrows(ProductNotFoundException.class, shoppingCart::calculateTotalCost);
    }

    @Test
    void getItemQuantity_ShouldReturnCorrectQuantity() {
        shoppingCart.addItem(1, 3);
        shoppingCart.addItem(2, 2);
        assertEquals(3, shoppingCart.getItemQuantity(1), "Quantity for product ID 1 should be correct");
        assertEquals(2, shoppingCart.getItemQuantity(2), "Quantity for product ID 2 should be correct");
    }

    // Note for presenter: Discuss the importance of comprehensive testing for different scenarios.
}
