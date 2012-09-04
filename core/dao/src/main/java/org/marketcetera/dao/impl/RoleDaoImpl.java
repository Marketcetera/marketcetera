package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.RoleDao;
import org.marketcetera.dao.domain.PersistentRole;

/**
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class RoleDaoImpl implements RoleDao {
    private EntityManager entityManager;


    @Override
    public Role getByName(String inName) {
        return (Role) entityManager.createNamedQuery("findRoleByName").getSingleResult();
    }

    @Override
    public void add(Role inData) {
        entityManager.persist(inData);

    }

    @Override
    public void save(Role inData) {
        entityManager.merge(inData);

    }

    @Override
    public Role getById(long inId) {
        return entityManager.find(PersistentRole.class, inId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getAll() {
        return entityManager.createNamedQuery("findAllRoles").getResultList();
    }

    @Override
    public void delete(Role inData) {
        entityManager.remove(inData);

    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
