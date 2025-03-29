package org.proiect.persistence.dao;

import org.proiect.persistence.connection.Connection;
import org.proiect.persistence.connection.DatabaseConnection;
import org.proiect.persistence.model.RoleEntity;
import org.proiect.persistence.model.enums.Role;

public class RoleDao extends EntityDao<RoleEntity> {
    public RoleDao(Connection connection) {
        super(connection);
    }
    public RoleEntity findRoleByCode(String code) {
        try {
            return ((DatabaseConnection) this.getConnection()).getDbConnAbs().executeQueryTransaction(
                    entityManager -> (RoleEntity) entityManager.createNativeQuery(
                                    "SELECT * FROM app_role WHERE role = (CAST(? AS role))", RoleEntity.class)
                            .setParameter(1, code) // No "param" prefix needed
                            .getSingleResult(),
                    RoleEntity.class
            );
        } catch (Exception e) {
            return null; // Handle the case where no result is found
        }
    }


}

