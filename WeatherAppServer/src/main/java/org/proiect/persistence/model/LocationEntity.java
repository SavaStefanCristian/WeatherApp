package org.proiect.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "app_location", schema = "public")
public class LocationEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "name")
    private String name;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationEntity that = (LocationEntity) o;

        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }

        if (this.name != null && that.name != null && this.name.equals(that.name)) {
            return true;
        }

        return this.latitude.equals(that.latitude) && this.longitude.equals(that.longitude);
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);

        if (id == null) {
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
            result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        }

        return result;
    }

}