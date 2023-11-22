package org.simplestore;

import org.simplestore.model.Inventory;
import org.simplestore.util.InventoryLoader;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        InventoryLoader.loadInventory("path/to/inventory.txt", inventory);  // TODO: Change to proper path (example file is in project resources)

        // Implement example usage of application components

        // TODO: Create shopping carts, add products,
        //  clear cart, handle exceptions, etc.

        // TODO: Add product to inventory, list all products
        // TODO: Remove product from inventory, list all products
    }
}

