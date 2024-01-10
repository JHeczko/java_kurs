package org.chatbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection implements IDatabaseConnection {
    private Connection connection;

    // TODO: Implementacja połączenia z bazą danych
    public DatabaseConnection(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public void addReservation(String customerName, String reservationTime, int numberOfGuests) throws SQLException {
        // TODO: Implementacja metody dodającej rezerwację do bazy danych
        //  Użyj try with resource lub zamknij statement
        String sql = "INSERT ...";
        // ...
    }

    @Override
    public void deleteReservation(int reservationId) throws SQLException {
        // TODO: Implementacja metody usuwającej rezerwację z bazy danych
        //  Użyj try with resource lub zamknij statement
        String sql = "DELETE ...";
        // ...
    }

    @Override
    public ResultSet listReservations() throws SQLException {
        // TODO: Implementacja metody zwracającej listę rezerwacji z bazy danych
        //  Nie zamykaj w tym miejscu ResultSet.
        String sql = "SELECT ...";
        // ...
        return null;
    }

    // TODO: Metoda do zamknięcia połączenia z bazą danych
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
