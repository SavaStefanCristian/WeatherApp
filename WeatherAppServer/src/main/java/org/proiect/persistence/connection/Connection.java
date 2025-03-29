package org.proiect.persistence.connection;

import org.proiect.persistence.model.PersistableEntity;

import java.io.Closeable;
import java.util.List;

public abstract class Connection implements Closeable {
    public abstract <T extends PersistableEntity> void save(T entity) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> T findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> T findById(Class<T> entityType, Long id) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> void update (T entity) throws Exception;
    public abstract <T extends PersistableEntity> void delete (T entity) throws Exception;
}
//TODO: ADD UPDATE AND DELETE IN ALL CONNECTION RELATED CLASSES (CONS + DAO)
