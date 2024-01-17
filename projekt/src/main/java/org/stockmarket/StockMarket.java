package org.stockmarket;

import org.stockmarket.model.Stock;
import org.stockmarket.model.StockType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockMarket {
    private final List<Stock> stocks;

    public StockMarket() {
        stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }


    // TODO zaimplementuj metodę, która zwraca opisy wszystkich akcje danego typu, razem z opisem, np.
    //  "AAPL - Technology and innovation sector - $150.0",
    //  "MSFT - Technology and innovation sector - $200.0",
    //  ...
    public List<String> listStocksByType(StockType type) {
        // TODO
        ArrayList<String> listOfStrings = new ArrayList<>();

        for(Stock s : stocks){
            if(s.getType() == type){
                String toList = "";
                toList += s.getSymbol();
                toList += " - ";
                toList += s.getTypeDescription();
                toList += " - ";
                toList += s.getPrice();
                listOfStrings.add(toList);
            }
        }

        return listOfStrings;
    }

    // TODO zaimplementuj metodę, która ustawia nową cenę danej akcji
    public void updateStockPrice(String symbol, double newPrice) {
        // TODO
        for(Stock s : stocks){
            if(s.getSymbol() == symbol){
                s.setPrice(newPrice);
            }
        }
    }

    // TODO zaimplentuj metodę, która zwraca daną akcję.
    //  Użyj optional na wypadek, gdyby nie było akcji o danym symbolu.
    public Optional<Stock> getStock(String symbol) {
        Stock toReturn = null;
        for(Stock s : stocks){
            if(s.getSymbol() == symbol){
                toReturn = s;
            }
        }
        Optional<Stock> optionalStock = Optional.ofNullable(toReturn);
        return optionalStock;
    }

    // TODO zaimplentuj metodę, która zwraca wszystkie akcje
    public List<Stock> getStocks() {
        // TODO
        return this.stocks;
    }
}
