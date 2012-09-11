package org.marketcetera.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.marketcetera.api.dao.MutableUser;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.security.User;
import org.marketcetera.dao.domain.PersistentUser;

/**
 * @version $Id$
 * @date 6/29/12 12:40 AM
 */

public class UserDaoImpl implements UserDao {
    private EntityManager entityManager;


    @Override
    public MutableUser getByName(String inUsername) {
        MutableUser user = (MutableUser)entityManager.createNamedQuery("PersistentUser.findByName").setParameter("name",
                                                                                                                 inUsername).getSingleResult();
        user.setPermissions(getPermissionsByUserId(user.getId()));
        return user;
    }

    @Override
    public void add(User inData) {
        entityManager.persist(inData);
    }

    @Override
    public void save(User inData) {
        entityManager.merge(inData);

    }

    @Override
    public void delete(User inData) {
        entityManager.remove(inData);

    }

    @Override
    public MutableUser getById(long inId) {
        return (MutableUser)entityManager.find(PersistentUser.class,
                                               inId);
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<MutableUser> getAll() {
        List<MutableUser> users = entityManager.createNamedQuery("PersistentUser.findAll").getResultList();
        for(MutableUser user : users) {
            user.setPermissions(getPermissionsByUserId(user.getId()));
        }
        return users;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<Permission> getPermissionsByUserId(long inUserId)
    {
        Set<Permission> permissions = new HashSet<Permission>();
        List results = entityManager.createNamedQuery("findPermissionsByUserId").setParameter(1,
                                                                                              inUserId).getResultList();
        if(results != null) {
            permissions.addAll(results);
        }
        return permissions;
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
