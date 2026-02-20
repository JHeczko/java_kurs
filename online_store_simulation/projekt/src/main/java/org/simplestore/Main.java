package org.simplestore;

import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;
import org.simplestore.service.ShoppingCart;
import org.simplestore.util.InventoryLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        InventoryLoader.loadInventory("src/main/resources/inventory.txt", inventory);
        // TODO: Change to proper path (example file is in project resources)
        // Implement example usage of application components
        // TODO: Create shopping carts, add products,
        //  clear cart, handle exceptions, etc.
        ShoppingCart shoppingCart1 = new ShoppingCart(inventory);
        shoppingCart1.addItem(2,3);
        shoppingCart1.addItem(2,3);
        shoppingCart1.addItem(4,3);
        try {
            System.out.println("Quantity of id 4: " + shoppingCart1.getItemQuantity(4));
            System.out.println("Quantity of id 4: " + shoppingCart1.getItemQuantity(2));
            shoppingCart1.removeItem(4,4);
            System.out.println(inventory.getProduct(5).toString());
            System.out.println("Quantity of id 4 after delete: " + shoppingCart1.getItemQuantity(4));
            System.out.println("Before clear summary price: " + shoppingCart1.calculateTotalCost());
            shoppingCart1.clearCart();
            System.out.println("After clear summary price: " + shoppingCart1.calculateTotalCost() + "\n");

        }catch(ProductNotFoundException e){
            e.printStackTrace();
        }
        System.out.println("First print before removing item id 4: ");
        inventory.printInventory();
        inventory.removeProduct(4);
        System.out.println("After removeing number id 4:");
        inventory.printInventory();
        // TODO: Add product to inventory, list all products
        // TODO: Remove product from inventory, list all products
    }
}

