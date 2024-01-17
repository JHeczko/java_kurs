package org.stockmarket;

import org.stockmarket.model.Stock;
import org.stockmarket.model.StockType;

public class StockMarketDemo {
    public static void main(String[] args) {
        // Create a stock market instance
        StockMarket market = new StockMarket();

        // Add some stocks to the market
        market.addStock(new Stock("AAPL", StockType.TECHNOLOGY, 150.0));
        market.addStock(new Stock("MSFT", StockType.TECHNOLOGY, 200.0));
        market.addStock(new Stock("JPM", StockType.FINANCE, 100.0));
        market.addStock(new Stock("PFE", StockType.HEALTHCARE, 50.0));

        // AAPL stock description
        System.out.println("Apple sector description:");
        market.getStock("AAPL").ifPresent(stock -> System.out.println(stock.getTypeDescription()));
        // Przy poprawnej implementacji, drukuje:
        //  Technology and innovation sector

        // List stocks of a specific type
        System.out.println("\nTechnology Stocks:");
        market.listStocksByType(StockType.TECHNOLOGY).forEach(System.out::println);
        // Przy poprawnej implementacji, drukuje:
        //  AAPL - Technology and innovation sector - $150.0
        //  MSFT - Technology and innovation sector - $200.0

        // Update the price of a stock
        System.out.println("\nUpdating price of AAPL...");
        market.updateStockPrice("AAPL", 155.0);

        // Display updated stock
        System.out.println("Updated Stock:");
        market.getStocks().stream()
                .filter(stock -> stock.getSymbol().equals("AAPL"))
                .findFirst()
                .ifPresent(System.out::println);
        // Przy poprawnej implementacji, drukuje:
        //  Stock{symbol='AAPL', type=Technology, description=Technology and innovation sector, price=155.0}
    }
}
