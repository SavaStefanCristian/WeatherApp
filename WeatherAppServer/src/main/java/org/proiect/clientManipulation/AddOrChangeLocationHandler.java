package org.proiect.clientManipulation;

import org.proiect.LocationMap;
import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.model.LocationEntity;
import org.proiect.persistence.model.UserEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class AddOrChangeLocationHandler {
    UserEntity user;
    EntityDao<UserEntity> userDao;
    EntityDao<LocationEntity> locationDao;
    LocationMap locationMap;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public void start(UserEntity user, LocationMap locationMap, EntityDao<UserEntity> userDao, EntityDao<LocationEntity> locationDao, ObjectInputStream in, ObjectOutputStream out) {
        this.user = user;
        this.userDao = userDao;
        this.locationDao = locationDao;
        this.locationMap = (LocationMap) locationMap;
        this.in = in;
        this.out = out;

        SerializableMessage startMessage = new SerializableMessage("");
        while(true) {
            try {
                startMessage.message += "\nTell us where you are (City name):\n  1. Use coordinates instead.\n  0. Return to main menu\n";
                this.out.writeObject(startMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                startMessage = new SerializableMessage("");
                SerializableMessage locationName = (SerializableMessage) this.in.readObject();
                if (locationName.equals("0")) {
                    return;
                }
                if (locationName.equals("1")) {
                    if(setLocationByCoordinates()) {
                        return;
                    }
                    else {
                        continue;
                    }
                }
                LocationEntity location = locationDao.findFirstByParams(LocationEntity.class, new ParameterPair("name", locationName.message));
                if(location == null) {
                    startMessage.message += "\nSadly, we couldn't find the location with name " + locationName + "\nPlease try again.";
                    continue;
                }
                user.setLastLocation(location);
                userDao.update(user);
                return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private Boolean setLocationByCoordinates() throws Exception {
        SerializableMessage startMessage = new SerializableMessage("");
        while(true) {
            startMessage.message += "\nTell us where you are:\n0. Use location name instead.\nLatitude: ";
            this.out.writeObject(startMessage);
            this.out.writeObject(new SerializableMessage("OK"));
            startMessage = new SerializableMessage("");
            Double latitude = 0.0;
            try {
                latitude = Double.parseDouble(((SerializableMessage) this.in.readObject()).message);
            } catch (NumberFormatException e) {
                startMessage.message += "\nPlease try again.";
                continue;
            }
            if(latitude<-90.0 || latitude > 90.0) {
                startMessage.message += "\nInvalid latitude!\nPlease try again.";
                continue;
            }
            if(latitude.equals(0.0)) {
                SerializableMessage confirmMessage = new SerializableMessage("\nDo you want to use location name instead? (yes/no)\n");
                this.out.writeObject(confirmMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                SerializableMessage yesNo = (SerializableMessage) this.in.readObject();
                if(yesNo.message.equalsIgnoreCase("yes")) {
                    return false;
                }
            }
            SerializableMessage requestLongitudeMessage = new SerializableMessage("Longitude: ");
            this.out.writeObject(requestLongitudeMessage);
            this.out.writeObject(new SerializableMessage("OK"));
            Double longitude = 0.0;
            try {
                longitude = Double.parseDouble(((SerializableMessage) this.in.readObject()).message);
            } catch (NumberFormatException e) {
                startMessage.message += "\nPlease try again.";
                continue;
            }
            if(longitude<-180.0 || longitude > 180.0) {
                startMessage.message += "\nInvalid longitude!\nPlease try again.";
                continue;
            }
            if(longitude.equals(0.0)) {
                SerializableMessage confirmMessage = new SerializableMessage("\nDo you want to use location name instead? (yes/no)\n");
                this.out.writeObject(confirmMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                SerializableMessage yesNo = (SerializableMessage) this.in.readObject();
                if(yesNo.message.equalsIgnoreCase("yes")) {
                    return false;
                }
            }
            Optional<LocationEntity> foundLocation = locationMap.findClosestLocation(latitude,longitude);
            if(foundLocation.isPresent()) {
                double distance = LocationMap.getHaversineDistance(latitude,longitude,foundLocation.get().getLatitude(),foundLocation.get().getLongitude());
                SerializableMessage confirmMessage = new SerializableMessage("\n");
                confirmMessage.message += "The closest location we found is '%s' at %.2f km away.\n Latitude : %.2f\n Longitude : %.2f\n ".formatted(foundLocation.get().getName(),distance, foundLocation.get().getLatitude(),foundLocation.get().getLongitude());
                confirmMessage.message += "Do you want to change your location to this? (yes/no)\n";
                this.out.writeObject(confirmMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                SerializableMessage yesNo = (SerializableMessage) this.in.readObject();
                if(yesNo.message.equalsIgnoreCase("no")) {
                    continue;
                }
                this.user.setLastLocation(foundLocation.get());
                userDao.update(user);
                return true;
            }
            else {
                startMessage.message += "\nSomething went wrong while trying to find where you are... Please try again.";
                continue;
            }
        }
    }
}
