package org.simplestore.model;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    // TODO: Remember of synchronization logic!
    // There is several methods to achieve this.
    private final Map<Integer, Product> products = new HashMap<>();

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public Product getProduct(int id) throws ProductNotFoundException {
        Product product = products.get(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return product;
    }

    // See file: src/test/java/org/simplestore/model/InventoryTest.java
    // TODO: Implement a method to list all products
    // TODO: Implement a method to remove a product by id
}
