package org.simplestore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    // TODO: Remember of synchronization logic!
    // There is several methods to achieve this.
    private final Map<Integer, Product> products = new HashMap<>();

    public void addProduct(Product product) {
        synchronized (products) {
            products.put(product.getId(), product);
        }
    }

    public Product getProduct(int id) throws ProductNotFoundException {
        Product product = products.get(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return product;
    }

    public ArrayList<Product> listAllProducts(){
        ArrayList<Product> list = new ArrayList<>();
        int i = 0;
        for(Integer it : products.keySet()){
            list.add(i, products.get(it));
            i++;
        }
        return list;
    }
    public void removeProduct(Integer key){
        synchronized (products) {
            products.remove(key);
        }
    }
    public void printInventory(){
        for(Integer i : products.keySet()){
            System.out.println(i + ") " + products.get(i).toString());
        }
    }
    // See file: src/test/java/org/simplestore/model/InventoryTest.java
    // TODO: Implement a method to list all products
    // TODO: Implement a method to remove a product by id
}
