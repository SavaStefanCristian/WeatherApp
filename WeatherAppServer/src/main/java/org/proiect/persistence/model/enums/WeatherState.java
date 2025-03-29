package org.proiect.persistence.model.enums;

public enum WeatherState {
    CLEAR("CLEAR"),
    CLOUDY("CLOUDY"),
    RAINING("RAINING"),
    SNOWING("SNOWING");

    private String code;
    private WeatherState(String code) {
        this.code = code;
    }
    public  String getCode() {
        return code;
    }
}
