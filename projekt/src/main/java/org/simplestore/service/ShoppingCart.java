package org.simplestore.service;

import org.simplestore.model.Inventory;
import org.simplestore.model.Product;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    // TODO Finish implementation. eg. add needed constructor

    // Remember of synchronization logic!
    // It could be achieved in many ways.
    private final Inventory inventory;
    private final Map<Integer, Integer> cartItems = new HashMap<>();

    public void addItem(int productId, int quantity) {
        cartItems.merge(productId, quantity, Integer::sum);  // Equivalent of lambda (a, b) -> Integer.sum(a, b)
    }

    // See file: src/test/java/org/simplestore/service/ShoppingCartTest.java
    // TODO: Implement a method to remove a product from the cart
    // TODO: Implement a method to calculate the total price of the cart
    // TODO: Implement a method to clear the cart

}
