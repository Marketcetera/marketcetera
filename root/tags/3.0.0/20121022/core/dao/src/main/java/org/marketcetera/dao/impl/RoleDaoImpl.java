package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.MutableRole;
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
    public MutableRole getByName(String inName) {
        return (MutableRole)entityManager.createNamedQuery("PersistentRole.findByName").setParameter("name",
                                                                                                     inName).getSingleResult();
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
    public MutableRole getById(long inId) {
        return (MutableRole)entityManager.find(PersistentRole.class,
                                               inId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MutableRole> getAll() {
        return entityManager.createNamedQuery("PersistentRole.findAll").getResultList();
    }

    @Override
    public void delete(Role inData) {
        entityManager.remove(entityManager.merge(inData));

    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
