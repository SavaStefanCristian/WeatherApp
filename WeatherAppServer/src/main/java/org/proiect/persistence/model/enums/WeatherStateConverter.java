package org.proiect.persistence.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class WeatherStateConverter implements AttributeConverter<WeatherState, String> {
    @Override
    public String convertToDatabaseColumn(WeatherState state) {
        return (state == null) ? null : state.getCode();
    }

    @Override
    public WeatherState convertToEntityAttribute(String code) {
        if(code == null) return null;
        return Stream.of(WeatherState.values()).filter(state -> state.getCode().equals(code)).findFirst().orElse(null);
    }
}