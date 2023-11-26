package org.simplestore;

import org.simplestore.model.Inventory;
import org.simplestore.model.Product;
import org.simplestore.model.ProductNotFoundException;
import org.simplestore.service.ShoppingCart;
import org.simplestore.util.InventoryLoader;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        InventoryLoader.loadInventory("src/main/resources/inventory.txt", inventory);  // TODO: Change to proper path (example file is in project resources)
        try{System.out.println(inventory.getProduct(5).toString());
        }   catch (ProductNotFoundException e){
            e.printStackTrace();
        }
        // Implement example usage of application components
        ShoppingCart shoppingCart1 = new ShoppingCart(inventory);
        // TODO: Create shopping carts, add products,
        //  clear cart, handle exceptions, etc.
        shoppingCart1.addItem(2,3);
        shoppingCart1.addItem(2,3);
        shoppingCart1.addItem(4,3);
        try {
            System.out.println(shoppingCart1.getItemQuantity(4));
            System.out.println(shoppingCart1.getItemQuantity(2));
        }catch(ProductNotFoundException e){
            e.printStackTrace();
        }
        // TODO: Add product to inventory, list all products
        // TODO: Remove product from inventory, list all products
    }
}

