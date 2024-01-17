package org.stockmarket.model;

public class Stock {
    private final String symbol;
    private final StockType type;
    private double price;

    public Stock(String symbol, StockType type, double price) {
        this.symbol = symbol;
        this.type = type;
        this.price = price;
    }

    // Getters and setters
    public String getSymbol() {
        return symbol;
    }

    public StockType getType() {
        return type;
    }

    public String getTypeDescription() {
        return type.getDescription();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", type=" + type.getName() +
                ", description=" + type.getDescription() +
                ", price=" + price +
                '}';
    }
}
