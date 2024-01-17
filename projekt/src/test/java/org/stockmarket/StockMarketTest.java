package org.stockmarket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stockmarket.model.Stock;
import org.stockmarket.model.StockType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StockMarketTest {
    private StockMarket market;

    @BeforeEach
    public void setUp() {
        market = new StockMarket();
        market.addStock(new Stock("AAPL", StockType.TECHNOLOGY, 150.0));
        market.addStock(new Stock("JPM", StockType.FINANCE, 100.0));
        market.addStock(new Stock("PFE", StockType.HEALTHCARE, 50.0));
    }

    @Test
    public void testAddStock() {
        Stock newStock = new Stock("TSLA", StockType.TECHNOLOGY, 700.0);
        market.addStock(newStock);
        assertTrue(market.getStocks().contains(newStock));
    }

    @Test
    public void testListStocksByType() {
        List<String> technologyStocks = market.listStocksByType(StockType.TECHNOLOGY);
        assertFalse(technologyStocks.isEmpty());
        assertTrue(technologyStocks.stream().anyMatch(stock -> stock.contains("AAPL")));
        assertTrue(technologyStocks.stream().allMatch(stock -> stock.contains(StockType.TECHNOLOGY.getDescription())));
    }

    @Test
    public void testUpdateStockPrice() {
        String symbol = "AAPL";
        double newPrice = 155.0;
        market.updateStockPrice(symbol, newPrice);
        Stock updatedStock = market.getStocks().stream()
                .filter(stock -> stock.getSymbol().equals(symbol))
                .findFirst()
                .orElse(null);
        assertNotNull(updatedStock);
        assertEquals(newPrice, updatedStock.getPrice());
    }
}
