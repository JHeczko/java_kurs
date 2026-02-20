package org.simplestore.model;

import java.util.Locale;

public class Product {
    private final int id;
    private final String name;
    private final double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId(){
        return this.id;
    }
    public double getPrice(){
        return this.price;
    }
    public String getName(){
        return this.name;
    }
    @Override
    public String toString(){
        String formatedString = String.format(Locale.US,"Product{id=%d, name='%s', price=%.1f}", getId(),getName(),getPrice(), getPrice());
        return formatedString;
    }
    //String expectedString = "Product{id=2, name='Another Product', price=20.0}";

    // Getters and toString() method, see: src/test/java/org/simplestore/model/ProductTest.java
    // TODO: Implement getters for id, name, and price
    // TODO: Override toString() method for Product representation
}
