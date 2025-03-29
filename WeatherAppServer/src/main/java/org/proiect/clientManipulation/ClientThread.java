package org.proiect.clientManipulation;

import org.proiect.LocationMap;
import org.proiect.persistence.connection.DatabaseConnection;
import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.dao.RoleDao;
import org.proiect.persistence.model.*;
import org.proiect.persistence.model.enums.Role;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientThread extends Thread {
    private Socket client;
    private LocationMap locationMap;
    private EntityDao<LocationEntity> locationDao;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private DatabaseConnection databaseConnection;
    private UserEntity user;
    public ClientThread(Socket client, LocationMap locationMap, EntityDao<LocationEntity> locationDao, DatabaseConnection databaseConnection) {
        this.client = client;
        this.locationMap = locationMap;
        this.locationDao = locationDao;
        try {
            this.in = new ObjectInputStream(client.getInputStream());
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.databaseConnection = databaseConnection;
        this.user = null;
    }

    @Override
    public void run() {
        try {
            EntityDao<UserEntity> userDao = new EntityDao<UserEntity>(databaseConnection);
            EntityDao<CurrentWeatherEntity> currentWeatherDao = new EntityDao<CurrentWeatherEntity>(databaseConnection);
            EntityDao<ForecastEntity> forecastDao = new EntityDao<ForecastEntity>(databaseConnection);

            LoginOrRegisterHandler loginOrRegisterHandler = new LoginOrRegisterHandler();

            Optional<Long> userId = loginOrRegisterHandler.start(userDao, in, out);
            if (userId.isEmpty()) {
                SerializableMessage endMessage = new SerializableMessage("\nGoodbye.\nSee you soon!\n");
                this.out.writeObject(endMessage);
                this.client.close();
                return;
            }

            userDao = new EntityDao<UserEntity>(databaseConnection);
            this.user = userDao.findById(UserEntity.class, userId.get());
            Boolean isAdmin = false;
            Collection<RoleEntity> roles = user.getRoles();
            if(roles == null || roles.isEmpty()) {
                EntityDao<UserRole> userRoleDao = new EntityDao<UserRole>(databaseConnection);
                List<UserRole> userRoleList = userRoleDao.findAllByParams(UserRole.class, new ParameterPair("user", user));
                roles = new ArrayList<RoleEntity>();
                for(UserRole userRole : userRoleList) {
                    roles.add(userRole.getRole());
                }
                if(roles.isEmpty()) {
                    SerializableMessage endMessage = new SerializableMessage("\nThis account is corrupted. No role data. Please try again later\n");
                    this.out.writeObject(endMessage);
                    this.client.close();
                    return;
                }
            }

            if(roles.stream()
                    .anyMatch(role -> role.getRole().equals(Role.ADMIN))) {
                isAdmin = true;
            }

            SerializableMessage startMessage = new SerializableMessage("\nHello, %s.\n".formatted(this.user.getUsername()));

            while(true) {

                startMessage.message += "\n  1. See weather\n  2. Change or set location\n";
                if(isAdmin) {
                    startMessage.message += "  3. Load data to server via data.json\n";
                }
                startMessage.message +="  0. Exit\n";
                this.out.writeObject(startMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                startMessage= new SerializableMessage("");

                SerializableMessage response = (SerializableMessage) this.in.readObject();

                if(response.equals("1")) {
                    if(this.user.getLastLocation() == null) {
                        startMessage.message += "\nPlease set your location first!";
                    }
                    else {
                        WeatherDataHandler weatherDataHandler = new WeatherDataHandler();
                        String lastMessage = weatherDataHandler.start(this.user.getLastLocation(), currentWeatherDao, forecastDao, this.in, this.out);
                        startMessage.message += lastMessage;
                    }
                    continue;
                }

                if(response.equals("2")) {
                    AddOrChangeLocationHandler addOrChangeLocationHandler = new AddOrChangeLocationHandler();
                    addOrChangeLocationHandler.start(this.user, this.locationMap, userDao, this.locationDao,this.in,this.out);
                    continue;
                }

                if(response.equals("3") && isAdmin) {
                    SerializableMessage jsonRequestMessage = new SerializableMessage("Sending JSON file...\n");
                    this.out.writeObject(jsonRequestMessage);
                    this.out.writeObject(new SerializableMessage("JSON"));
                    SerializableMessage jsonResponse = (SerializableMessage) this.in.readObject();
                    LoadJsonHandler loadJsonHandler = new LoadJsonHandler();
                    SerializableMessage loadResult = new SerializableMessage(loadJsonHandler.start(jsonResponse,locationDao,currentWeatherDao,forecastDao));
                    if(loadResult.equals("")) {
                        loadResult.message += "\nLoaded all data successfully\n";
                    }
                    this.out.writeObject(loadResult);
                    continue;

                }

                if(response.equals("0")) {
                    SerializableMessage endMessage = new SerializableMessage("\nGoodbye, %s.\nSee you soon!\n".formatted(this.user.getUsername()));
                    this.out.writeObject(endMessage);
                    this.client.close();
                    return;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
