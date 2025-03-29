package org.proiect;

import org.proiect.clientManipulation.ClientThread;
import org.proiect.persistence.connection.DatabaseConnection;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.model.LocationEntity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int PORT = 0000;
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT); Socket socket = new Socket(); DatabaseConnection databaseConnection = new DatabaseConnection("persistenceUnit")){

            EntityDao<LocationEntity> locationDao = new EntityDao<>(databaseConnection);
            LocationMap locationMap = new LocationMap();
            locationMap.loadFromConnection(locationDao);

            while(true) {
                Socket received = serverSocket.accept();
                System.out.println(received.getInetAddress().getHostAddress());
                /* Open a thread for each received client */
                new ClientThread(received, locationMap, locationDao, databaseConnection).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
