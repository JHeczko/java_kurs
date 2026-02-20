package org.simplestore.util;

import org.simplestore.model.Inventory;
import org.simplestore.model.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InventoryLoader {

    public static void loadInventory(String filePath, Inventory inventory) {

        // TODO: Refactor this method to use try-with-resource for better resource management.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            // TODO: Implement error handling for file reading and parsing,
            //  eg. by handling NumberFormatException
            //  and writing to System.err.println(...)
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                inventory.addProduct(new Product(id, name, price));
            }
        }catch(NumberFormatException e){
            System.err.println(e.getMessage());
        } catch(IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
