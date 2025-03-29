package org.proiect.persistence.dao;


import lombok.Getter;
import org.proiect.persistence.connection.Connection;
import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.model.PersistableEntity;

import java.util.List;

public class EntityDao<T extends PersistableEntity> {
    @Getter
    private final Connection connection;
    public EntityDao(Connection connection) {
        this.connection = connection;
    }

    public void save(T object) throws Exception {
        connection.save(object);
    }

    public T findById(Class<T> entityType, Long id) throws Exception{
        return connection.findById(entityType, id);
    }


    public List<T> findAll(Class<T> entityType) throws Exception{
        return connection.findAll(entityType);
    }

    public T findFirstByParams(Class<T> entityType, ParameterPair... params) throws Exception{
        return connection.findFirstByParams(entityType, params);
    }

    public List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception {
        return connection.findAllByParams(entityType, params);
    }

    public void update(T object) throws Exception {
        connection.update(object);
    }
    public void delete(T object) throws Exception {
        connection.delete(object);
    }
}
