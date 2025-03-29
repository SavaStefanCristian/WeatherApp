package org.proiect.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.proiect.persistence.model.enums.WeatherState;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "app_current_weather", schema = "public")
public class CurrentWeatherEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "current_weather_id", nullable = false)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "temperature", nullable = false)
    private Integer temperature;

    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private WeatherState state;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentWeatherEntity that = (CurrentWeatherEntity) o;

        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }
        return this.locationId != null && that.locationId != null && this.locationId.equals(that.locationId);
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);

        if (id == null && locationId != null) {
            result = 31 * result + locationId.hashCode();
        }

        result = 31 * result + (temperature != null ? temperature.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }


}