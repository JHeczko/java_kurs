package org.chatbot.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDatabaseConnection {
    void addReservation(String customerName, String reservationTime, int numberOfGuests) throws SQLException;

    void deleteReservation(int reservationId) throws SQLException;

    ResultSet listReservations() throws SQLException;

    void closeConnection() throws SQLException;
}
