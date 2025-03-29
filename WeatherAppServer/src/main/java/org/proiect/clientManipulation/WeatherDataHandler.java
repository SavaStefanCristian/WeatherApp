package org.proiect.clientManipulation;

import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.model.CurrentWeatherEntity;
import org.proiect.persistence.model.ForecastEntity;
import org.proiect.persistence.model.LocationEntity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherDataHandler {
    LocationEntity location;
    EntityDao<CurrentWeatherEntity> currentWeatherDao;
    EntityDao<ForecastEntity> forecastDao;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public String start(LocationEntity location, EntityDao<CurrentWeatherEntity> currentWeatherDao, EntityDao<ForecastEntity> forecastDao, ObjectInputStream in, ObjectOutputStream out) {
        this.location = location;
        this.currentWeatherDao = currentWeatherDao;
        this.forecastDao = forecastDao;
        this.in = in;
        this.out = out;


        try {
            CurrentWeatherEntity currentWeather = currentWeatherDao.findFirstByParams(CurrentWeatherEntity.class, new ParameterPair("locationId", location.getId()));
            List<ForecastEntity> forecastList = forecastDao.findAllByParams(ForecastEntity.class, new ParameterPair("forecastedLocationId", location.getId()))
                    .stream().sorted((first, second) -> first.getForecastDate().compareTo(second.getForecastDate())).collect(Collectors.toList());

            SerializableMessage weatherData = new SerializableMessage("\n");

            if (currentWeather == null && forecastList.isEmpty()) {
                return "\nNo weather data found for %s\n".formatted(location.getName());
            }
            weatherData.message += "\nWeather data for %s :\n".formatted(location.getName());

            if (currentWeather == null)
                weatherData = new SerializableMessage("No current weather data found for %s\n".formatted(location.getName()));
            else
                weatherData.message += "Currently %d Celsius, %s\n".formatted(currentWeather.getTemperature(), currentWeather.getState());
            Integer index = 0;
            if (forecastList.isEmpty())
                weatherData= new SerializableMessage("No forecast data found for %s\n".formatted(location.getName()));
            else
                for (; index < forecastList.size(); index++) {
                    if (index == 3) break;
                    weatherData.message += "%s High: %d, Low: %d, %s\n".formatted(forecastList.get(index).getForecastDate(), forecastList.get(index).getHigh(), forecastList.get(index).getLow(), forecastList.get(index).getState());
                }
            while (true) {
                weatherData.message += "\n  1. Print more forecast data\n  0. Return to main menu\n";
                this.out.writeObject(weatherData);
                this.out.writeObject(new SerializableMessage("OK"));
                weatherData = new SerializableMessage("");
                SerializableMessage response = (SerializableMessage) this.in.readObject();
                if (response.equals("0")) {
                    break;
                }
                if (response.equals("1")) {
                    if (index == forecastList.size()) {
                        weatherData = new SerializableMessage("\nNo more forecast data available\n");
                        continue;
                    }
                    Integer startIndex = index;
                    for (; index < forecastList.size(); index++) {
                        if (index == startIndex + 3) break;
                        weatherData.message += "%s High: %d, Low: %d, %s\n".formatted(forecastList.get(index).getForecastDate(), forecastList.get(index).getHigh(), forecastList.get(index).getLow(), forecastList.get(index).getState());
                    }
                    continue;
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return "";
    }

}
