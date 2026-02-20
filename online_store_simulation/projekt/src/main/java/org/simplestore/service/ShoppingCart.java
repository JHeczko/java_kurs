package org.simplestore.service;

import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    // TODO Finish implementation. eg. add needed constructor

    // Remember of synchronization logic!
    // It could be achieved in many ways.
    private final Inventory inventory;
    private final Map<Integer, Integer> cartItems = new HashMap<>();

    public ShoppingCart(Inventory inventory){
        this.inventory = inventory;
    }

    public synchronized void addItem(int productId, int quantity) {
        cartItems.merge(productId, quantity, Integer::sum);  // Equivalent of lambda (a, b) -> Integer.sum(a, b)
    }
    public synchronized void removeItem(int id, int quantity){
        cartItems.merge(id, quantity, (oldQuantity, newQuantity) -> {return (oldQuantity - newQuantity);});
        if(cartItems.get(id) <= 0){
            cartItems.remove(id);
        }

    }
    public synchronized double calculateTotalCost() throws ProductNotFoundException {
        double prizeTotal = 0;
        for(Integer id : cartItems.keySet()){
            try{
                inventory.getProduct(id);
            }catch(Exception e){
                throw new ProductNotFoundException("Not found a product in calculateTotalCost method");
            }
            prizeTotal += (cartItems.get(id) * inventory.getProduct(id).getPrice());
        }
        return prizeTotal;
    }

    public synchronized int getItemQuantity(int id) throws ProductNotFoundException{
        try{
            int quantityItem = cartItems.get(id);
            return quantityItem;
        } catch(Exception e){
            return 0;
        }
    }

    public synchronized void clearCart(){
        cartItems.clear();
    }

    public boolean isEmpty(){
        return cartItems.isEmpty();
    }


    // See file: src/test/java/org/simplestore/service/ShoppingCartTest.java
    // TODO: Implement a method to remove a product from the cart
    // TODO: Implement a method to calculate the total price of the cart
    // TODO: Implement a method to clear the cart

}
