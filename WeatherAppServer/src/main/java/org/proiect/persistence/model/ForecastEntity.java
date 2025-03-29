package org.proiect.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.proiect.persistence.model.enums.WeatherState;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "app_forecast", schema = "public")
public class ForecastEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id", nullable = false)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long forecastedLocationId;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "high", nullable = false)
    private Integer high;
    @Column(name = "low", nullable = false)
    private Integer low;

    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private WeatherState state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForecastEntity that = (ForecastEntity) o;

        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }

        return this.forecastedLocationId != null && that.forecastedLocationId != null
                && this.forecastedLocationId.equals(that.forecastedLocationId)
                && this.forecastDate != null && that.forecastDate != null
                && this.forecastDate.equals(that.forecastDate);
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);

        if (id == null) {
            result = 31 * result + (forecastedLocationId != null ? forecastedLocationId.hashCode() : 0);
            result = 31 * result + (forecastDate != null ? forecastDate.hashCode() : 0);
        }

        result = 31 * result + (high != null ? high.hashCode() : 0);
        result = 31 * result + (low != null ? low.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }
}