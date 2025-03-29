package org.proiect.clientManipulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.persistence.exceptions.JSONException;
import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.model.CurrentWeatherEntity;
import org.proiect.persistence.model.ForecastEntity;
import org.proiect.persistence.model.LocationEntity;
import org.proiect.persistence.model.enums.WeatherState;

import java.time.LocalDate;
import java.util.HashSet;

public class LoadJsonHandler {

    public String start(SerializableMessage jsonMessage, EntityDao<LocationEntity> locationDao, EntityDao<CurrentWeatherEntity> currentWeatherDao, EntityDao<ForecastEntity> forecastDao) {
        String errorMessages = "";
        ObjectMapper objectMapper = new ObjectMapper();

        HashSet<LocationEntity> locationEntities = new HashSet<>();
        HashSet<CurrentWeatherEntity> currentWeatherEntities = new HashSet<>();
        HashSet<ForecastEntity> forecastEntities = new HashSet<>();

        try {
            JsonNode root = objectMapper.readTree(jsonMessage.message);

            JsonNode locationNode = root.get("locationData");
            if(locationNode != null)
                for(JsonNode location : locationNode) {
                    LocationEntity locationEntity = new LocationEntity();
                    locationEntity.setName(location.get("name").asText());
                    locationEntity.setLatitude(location.get("latitude").asDouble());
                    locationEntity.setLongitude(location.get("longitude").asDouble());

                    locationEntities.add(locationEntity);
                }
            for(LocationEntity locationEntity : locationEntities) {
                LocationEntity alreadyExistingLocation = locationDao.findFirstByParams(LocationEntity.class, new ParameterPair("name", locationEntity.getName()));
                if(alreadyExistingLocation != null) {
                    errorMessages += "Location with name " + locationEntity.getName() + " already exists.\n";
                    continue;
                }
                locationDao.save(locationEntity);
            }

            JsonNode currentWeatherNode = root.get("currentWeatherData");
            if(currentWeatherNode != null)
                for(JsonNode currentWeather : currentWeatherNode) {
                    CurrentWeatherEntity currentWeatherEntity = new CurrentWeatherEntity();
                    String locationName = currentWeather.get("locationName").asText();
                    LocationEntity locationData = locationDao.findFirstByParams(LocationEntity.class, new ParameterPair("name", locationName));
                    if (locationData == null) {
                        throw new JSONException("Could not find location with name: " + locationName);
                    }
                    currentWeatherEntity.setLocationId(locationData.getId());
                    currentWeatherEntity.setTemperature(currentWeather.get("temperature").asInt());
                    currentWeatherEntity.setState(WeatherState.valueOf(currentWeather.get("state").asText()));

                    currentWeatherEntities.add(currentWeatherEntity);
                }
            for(CurrentWeatherEntity currentWeatherEntity : currentWeatherEntities) {
                CurrentWeatherEntity alreadyExistingCurrentWeather = currentWeatherDao.findFirstByParams(CurrentWeatherEntity.class, new ParameterPair("locationId", currentWeatherEntity.getLocationId()));
                if(alreadyExistingCurrentWeather != null) {
                    currentWeatherEntity.setId(alreadyExistingCurrentWeather.getId());
                    currentWeatherDao.update(currentWeatherEntity);
                    continue;
                }
                currentWeatherDao.save(currentWeatherEntity);
            }

            JsonNode forecastNode = root.get("forecastData");
            if(forecastNode != null)
                for(JsonNode forecast : forecastNode) {
                    ForecastEntity forecastEntity = new ForecastEntity();
                    String locationName = forecast.get("locationName").asText();
                    LocationEntity locationData = locationDao.findFirstByParams(LocationEntity.class, new ParameterPair("name", locationName));
                    if (locationData == null) {
                        throw new JSONException("Could not find location with name: " + locationName);
                    }
                    forecastEntity.setForecastedLocationId(locationData.getId());
                    forecastEntity.setForecastDate(LocalDate.parse(forecast.get("date").asText()));
                    forecastEntity.setHigh(forecast.get("high").asInt());
                    forecastEntity.setLow(forecast.get("low").asInt());
                    forecastEntity.setState(WeatherState.valueOf((forecast.get("state").asText())));

                    forecastEntities.add(forecastEntity);
                }
            for (ForecastEntity forecastEntity : forecastEntities) {
                ForecastEntity alreadyExistingForecast = forecastDao.findFirstByParams(ForecastEntity.class, new ParameterPair("forecastedLocationId", forecastEntity.getForecastedLocationId()), new ParameterPair("forecastDate", forecastEntity.getForecastDate()));
                if(alreadyExistingForecast != null) {
                    forecastEntity.setId(alreadyExistingForecast.getId());
                    forecastDao.update(forecastEntity);
                    continue;
                }
                forecastDao.save(forecastEntity);
            }

        } catch (JsonProcessingException | IllegalArgumentException e) {
            return errorMessages + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return errorMessages;
    }
}
