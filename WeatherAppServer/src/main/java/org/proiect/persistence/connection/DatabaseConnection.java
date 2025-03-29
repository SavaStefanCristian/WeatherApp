package org.proiect.persistence.connection;

import lombok.Getter;
import org.proiect.persistence.model.CurrentWeatherEntity;
import org.proiect.persistence.model.ForecastEntity;
import org.proiect.persistence.model.PersistableEntity;
import org.proiect.persistence.model.RoleEntity;

import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseConnection extends Connection {
    @Getter
    private DatabaseConnectionAbstract dbConnAbs;

    public DatabaseConnection(String persistenceUnit) {
        this.dbConnAbs = new DatabaseConnectionAbstract(persistenceUnit);
    }

//    @Override
//    public <T extends PersistableEntity> void save(T entity) throws Exception {
//        this.dbConnAbs.executeTransaction(entityManager -> entityManager.persist(entity));
//    }
@Override
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        if (entity instanceof RoleEntity) {
            this.dbConnAbs.executeTransaction(entityManager -> {
                entityManager.createNativeQuery("INSERT INTO app_role (role) VALUES (CAST(? AS role))")
                        .setParameter(1, ((RoleEntity) entity).getRole().getCode())
                        .executeUpdate();
            });
        } else if (entity instanceof CurrentWeatherEntity) {
            this.dbConnAbs.executeTransaction(entityManager -> {
                entityManager.createNativeQuery(
                                "INSERT INTO app_current_weather (location_id, temperature, state) " +
                                        "VALUES (?, ?, CAST(? AS weather_state))")
                        //.setParameter(1, ((CurrentWeatherEntity) entity).getLocation().getId()) // Location ID
                        .setParameter(1, ((CurrentWeatherEntity) entity).getLocationId()) // Location ID
                        .setParameter(2, ((CurrentWeatherEntity) entity).getTemperature()) // Temperature value
                        .setParameter(3, ((CurrentWeatherEntity) entity).getState().name()) // Enum state as String
                        .executeUpdate();
            });
        }else if (entity instanceof ForecastEntity) {
            this.dbConnAbs.executeTransaction(entityManager -> {
                entityManager.createNativeQuery(
                                "INSERT INTO app_forecast (location_id, forecast_date, high, low, state) " +
                                        "VALUES (?, ?, ?, ?, CAST(? AS weather_state))")
                        //.setParameter(1, ((ForecastEntity) entity).getLocation().getId()) // Location ID
                        .setParameter(1, ((ForecastEntity) entity).getForecastedLocationId()) // Location ID
                        .setParameter(2, ((ForecastEntity) entity).getForecastDate()) // Forecast Date
                        .setParameter(3, ((ForecastEntity) entity).getHigh()) // High temperature
                        .setParameter(4, ((ForecastEntity) entity).getLow()) // Low temperature
                        .setParameter(5, ((ForecastEntity) entity).getState().name()) // Enum state as String
                        .executeUpdate();
            });
        } else {
            this.dbConnAbs.executeTransaction(entityManager -> entityManager.persist(entity));
        }
    }

    @Override
    public <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception {
        String abstractQuery = "SELECT e FROM %s e".formatted(entityType.getSimpleName());

        TypedQuery<T> query = this.dbConnAbs
                .executeQueryTransaction(entityManager -> entityManager
                        .createQuery(abstractQuery, entityType), TypedQuery.class);

        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> T findById(Class<T> entityType, Long id){
        T entity = this.dbConnAbs.executeQueryTransaction(entityManager ->
                entityManager.find(entityType, id), entityType);
        return entity;
    }

    @Override
    public <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery.setMaxResults(1);
        }, TypedQuery.class);

        // Retrieve the single result or return null if no result
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair... params) throws Exception {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction and get the results as a list
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery;
        }, TypedQuery.class);

        // Return the full result list
        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> void update(T entity) throws Exception {
        dbConnAbs.executeTransaction(entityManager -> entityManager.merge(entity));
    }

    @Override
    public <T extends PersistableEntity> void delete(T entity) throws Exception {
        T managedEntity = dbConnAbs.getEntityManager().merge(entity);
        dbConnAbs.executeTransaction(entityManager -> entityManager.remove(managedEntity));
    }

    @Override
    public void close() throws IOException {
        this.dbConnAbs.close();
    }
}