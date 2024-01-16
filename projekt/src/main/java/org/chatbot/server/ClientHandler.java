package org.chatbot.server;

import org.chatbot.database.DatabaseConnection;
import org.chatbot.logic.ChatbotLogic;
import org.chatbot.response.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatbotLogic chatbotLogic;

    public ClientHandler(Socket socket) throws IOException, SQLException {
        this.clientSocket = socket;
        this.chatbotLogic = new ChatbotLogic(new DatabaseConnection("jdbc:mysql://localhost/chatbot_db", "chatbot-app", "fjbh124555b&jkkj@@232"));
    }

    @Override
    public void run(){
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            //  Wysyłanie wiadomości powitalnej od razu po nawiązaniu połączenia
            Response greeting = chatbotLogic.processInput("");
            out.println(greeting.getMessage());

            // TODO: Implementacja wysyłania wiadomości powitalnej i odbioru odpowiedzi od klienta
            //  oraz odbiór i przetwarzanie wiadomości od klienta
            String inputLine = null;
             while ((inputLine = in.readLine()) != "exit") {
                 System.out.println("Cos");
                 System.out.println(inputLine);
                 Response response = chatbotLogic.processInput(inputLine);
                 out.println(response.getMessage());
             }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            chatbotLogic.exit();
        }
    }
}
