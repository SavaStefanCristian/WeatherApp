package org.proiect.persistence.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        return (role == null) ? null : role.getCode();
    }

    @Override
    public Role convertToEntityAttribute(String code) {
        if(code == null) return null;
        return Stream.of(Role.values()).filter(role -> role.getCode().equals(code)).findFirst().orElse(null);
    }
}
