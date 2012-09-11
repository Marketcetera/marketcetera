package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionDao;
import org.marketcetera.dao.domain.PersistentPermission;

/**
 * @version $Id$
 * @date 6/29/12 12:38 AM
 */

public class PermissionDaoImpl implements PermissionDao {
    private EntityManager entityManager;


    @Override
    public void add(Permission inData) {
        entityManager.persist(inData);
    }

    @Override
    public void save(Permission inData) {
        entityManager.merge(inData);
    }

    @Override
    public Permission getByName(String inName) {
        return (Permission) entityManager.createNamedQuery("PersistentPermission.findByName").setParameter("name", inName).getSingleResult();
    }

    @Override
    public Permission getById(long inId) {
        return entityManager.find(PersistentPermission.class, inId);
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<Permission> getAll() {
        return entityManager.createNamedQuery("PersistentPermission.findAll").getResultList();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.PermissionDao#delete(org.marketcetera.core.systemmodel.Permission)
     */
    @Override
    public void delete(Permission inPermission)
    {
        entityManager.remove(inPermission);
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
