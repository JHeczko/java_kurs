package org.chatbot.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInputReader;

    public ChatClient(String address, int port) throws Exception {
        // TODO Zainicjuj połączenie z serwerem chatu
        this.socket = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(),true);
        this.userInputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void send(String message) {
        out.println(message);
    }

    public String receive() throws Exception {
        return in.readLine();
    }

    public void close() throws Exception {
        in.close();
        out.close();
        socket.close();
        userInputReader.close();
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient("localhost", 1235);

        System.out.println("Connected to chatbot. Type your messages:");
        System.out.println(client.receive());
        // TODO Zaimplementuj pętlę do komunikacji z serwerem
        //  która wczytuje input z konsoli, przesyła do serwera i odbiera odpowiedź

        String userInput;
        while (!(userInput = client.userInputReader.readLine()).equals("exit")){
            client.send(userInput);
            System.out.println("Chatbot says: " + client.receive());
        }
        client.send("exit");
        client.close();

    }
}
