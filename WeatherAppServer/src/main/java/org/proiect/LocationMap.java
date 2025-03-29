package org.proiect;

import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.model.LocationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationMap {
    private static final int GEOHASHING_LENGHT = 8;
    private static final Double EARTH_RADIUS_KM = 6378.137; //equatorial
    private static final int LOCATION_MAP_SIZE = 1<<GEOHASHING_LENGHT;

    private ArrayList<LocationEntity>[][] locations;

    public LocationMap() {
        locations = (ArrayList<LocationEntity>[][]) new ArrayList[LOCATION_MAP_SIZE][LOCATION_MAP_SIZE];
        for(int i = 0; i < 1<<GEOHASHING_LENGHT; i++) {
            for(int j = 0; j < 1<<GEOHASHING_LENGHT; j++) {
                locations[i][j] = new ArrayList<>();
            }
        }
    }

    public void loadFromConnection(EntityDao<LocationEntity> locationDao) {
        try {
            List<LocationEntity> locations = locationDao.findAll(LocationEntity.class);
            for(LocationEntity location : locations) {
                addLocation(location);
            }
            System.out.printf("%d locations loaded!\n", locations.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double getHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        if(Math.abs(lat1) > 90.0d || Math.abs(lon1) > 180.0d || Math.abs(lat2) > 90.0d || Math.abs(lon2) > 180.0d) return Double.NaN;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private static char[] getGeoHash(double lat, double lon) {
        if(Math.abs(lat) > 90.0d || Math.abs(lon) > 180.0d) return null;
        char[] geoHash = new char[2];
        char powerOfTwo = 128;
        double maxLat = 180.0d;
        double maxLon = 360.0d;
        double latCpy = lat + 90.0d;
        double lonCpy = lon + 180.0d;
        for (int i = 0; i < GEOHASHING_LENGHT; i++) {
            if(latCpy > maxLat/2) {
                latCpy -= maxLat/2;
                geoHash[0] += powerOfTwo;
            }
            if(lonCpy > maxLon/2) {
                lonCpy -= maxLon/2;
                geoHash[1] += powerOfTwo;
            }
            maxLat /=2;
            maxLon /=2;
            powerOfTwo >>=1;
        }
        return geoHash;
    }


    public void addLocation(LocationEntity location) {
        char[] geoHash = getGeoHash(location.getLatitude(), location.getLongitude());
        if(geoHash == null) {
            System.err.printf("Invalid coordinate provided! %s", location.getName());
            return;
        }
        locations[geoHash[0]][geoHash[1]].add(location);
    }

    public Optional<LocationEntity> findClosestLocation(Double lat, Double lon) {
        char[] geoHash = getGeoHash(lat, lon);
        Optional<LocationEntity> closest = Optional.empty();
        if(geoHash==null) {
            System.err.println("Invalid coordinate provided!");
            return closest;
        }
        double closestDistance = Double.MAX_VALUE;

        for(LocationEntity sameSquareLoc : locations[geoHash[0]][geoHash[1]]) {
            double distance = getHaversineDistance(lat,lon,sameSquareLoc.getLatitude(),sameSquareLoc.getLongitude());
            if(distance < closestDistance) {
                closest = Optional.of(sameSquareLoc);
                closestDistance = distance;
            }
        }
        boolean wasJustFound = false;
        int searchRadius = 1;
        do {
            wasJustFound = false;
            for(int i = -searchRadius; i <= searchRadius; i++) {
                for(int j = -searchRadius; j <= searchRadius; j++) {
                    int searchY = ((int) geoHash[0]) + i;
                    int searchX = ((int) geoHash[1]) + j;
                    if(searchX < 0 || searchX >= LOCATION_MAP_SIZE || searchY < 0 || searchY >= LOCATION_MAP_SIZE) continue;
                    for(LocationEntity currLoc : locations[searchY][searchX]) {
                        double distance = getHaversineDistance(lat,lon, currLoc.getLatitude(), currLoc.getLongitude());
                        if(distance < closestDistance) {
                            wasJustFound = true;
                            closest = Optional.of(currLoc);
                            closestDistance = distance;
                        }
                    }
                }
            }
            searchRadius++;
        } while(wasJustFound || (closest.isEmpty() && searchRadius < LOCATION_MAP_SIZE));
        return closest;
    }
}
