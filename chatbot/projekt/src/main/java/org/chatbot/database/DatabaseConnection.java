package org.chatbot.database;

import java.sql.*;

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
        String sql = "INSERT INTO reservations (customer_name, reservation_time, number_of_guests) VALUES ('" + customerName + "', '" + reservationTime + "', '" + numberOfGuests + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        if((!statement.isClosed()) && statement != null){
            statement.close();
        }
    }

    @Override
    public void deleteReservation(int reservationId) throws SQLException {
        // TODO: Implementacja metody usuwającej rezerwację z bazy danych
        //  Użyj try with resource lub zamknij statement
        String sql = "DELETE FROM reservations WHERE id='"+reservationId+"'";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
        if((!stm.isClosed()) && stm != null){
            stm.close();
        }
    }

    @Override
    public ResultSet listReservations() throws SQLException {
        // TODO: Implementacja metody zwracającej listę rezerwacji z bazy danych
        //  Nie zamykaj w tym miejscu ResultSet.
        String sql = "SELECT id,customer_name,reservation_time,number_of_guests FROM reservations";
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        return resultSet;
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
