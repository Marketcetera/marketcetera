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
    public PersistentPermission getByName(String inName) {
        return (PersistentPermission)entityManager.createNamedQuery("PersistentPermission.findByName").setParameter("name", inName).getSingleResult();
    }

    @Override
    public PersistentPermission getById(long inId) {
        return entityManager.find(PersistentPermission.class,
                                  inId); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.PermissionDao#isInUseByRole(long)
     */
    @Override
    public boolean isInUseByRole(long inId)
    {
        Number result = (Number)entityManager.createNamedQuery("PersistentPermission.isPermissionInUseByRole").setParameter(1,
                                                                                                                            inId).getSingleResult();
        return result.intValue() != 0;
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
        entityManager.remove(entityManager.merge(inPermission));
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
