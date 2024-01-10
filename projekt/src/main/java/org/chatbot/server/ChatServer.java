package org.chatbot.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ChatServer {
    private ServerSocket serverSocket;

    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer(1234); // Use any free port
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
