package org.simplestore.service;

import org.junit.jupiter.api.Test;
import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartConcurrencyTest {
    private final Inventory inventory = new Inventory();

    @Test
    void addAndRemoveItemsConcurrently() throws InterruptedException {
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        inventory.addProduct(new Product(1, "Test Product", 10.0));
        ArrayList<Thread> threadList = new ArrayList<>();
        // Prepare tests with 10 threads. Next:

        // TODO Add 100 items concurrently
        for(int i = 0; i < 10; i++){
            threadList.add(new Thread(){
                public void run(){
                    shoppingCart.addItem(1,10);
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    shoppingCart.removeItem(1,5);
                }
            });
        }
        for(Thread thread : threadList){
            thread.start();
        }
        for(Thread thread : threadList){
            thread.join();
        }
        // TODO Remove 50 items concurrently
        // TODO Await for threads termination, eg. join

        // Check if the final quantity is as expected
        try{
            assertEquals(50, shoppingCart.getItemQuantity(1));
        } catch(ProductNotFoundException e){
            e.printStackTrace();
        }
    }

    @Test
    void calculateTotalCostConcurrently() throws InterruptedException, ProductNotFoundException {
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        inventory.addProduct(new Product(1, "Test Product", 10.0));
        ArrayList<Thread> threadList = new ArrayList<>();
        // Prepare tests with 10 threads. Next:

        // TODO Add 100 items concurrently
        for(int i = 0; i < 10; i++){
            threadList.add(new Thread(()-> {
                shoppingCart.addItem(1,10);
            }));
        }
        for(Thread thread : threadList){
            thread.start();
        }
        for(Thread thread : threadList){
            thread.join();
        }
        // TODO Add 100 items concurrently
        // TODO Await for threads termination, eg. join

        // Check if the total cost calculation is correct
        assertEquals(1000.0, shoppingCart.calculateTotalCost());
    }

    // Note for presenter: Discuss the importance of concurrency testing in a multi-threaded environment.
}
