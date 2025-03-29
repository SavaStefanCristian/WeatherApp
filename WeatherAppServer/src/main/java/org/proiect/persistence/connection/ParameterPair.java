package org.proiect.persistence.connection;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParameterPair<T,W> {
    private T name;
    private W value;

    public ParameterPair(T name, W value) {
        this.name = name;
        this.value = value;
    }

}
