package org.proiect;

import org.proiect.clientManipulation.SerializableMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public void start(){
        try {
            this.socket = new Socket("localhost", 0000);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            while(true) {
                try {
                    SerializableMessage messageFromServer = (SerializableMessage) in.readObject();
                    System.out.print(messageFromServer);

                    SerializableMessage contextFromServer = (SerializableMessage) in.readObject();
                    if(contextFromServer.equals("JSON")) {
                        String jsonFromFile  = Files.readString(Path.of("\\src\\main\\resources\\data.json"));
                        SerializableMessage jsonMessage = new SerializableMessage(jsonFromFile);
                        out.writeObject(jsonMessage);
                        SerializableMessage resultFromServer = (SerializableMessage) in.readObject();
                        System.out.println(resultFromServer);
                        continue;
                    }

                    SerializableMessage message = new SerializableMessage(scanner.nextLine());

                    this.out.writeObject(message);
                } catch (EOFException | SocketException e) {
                    System.out.println("Connection to server closed.");
                    break; // Exit the loop if the server has closed the connection
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}