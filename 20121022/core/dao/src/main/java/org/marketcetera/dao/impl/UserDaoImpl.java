package org.marketcetera.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.jpa.JPATypedQueryVisitor;
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
        entityManager.remove(entityManager.merge(inData));

    }

    @Override
    public MutableUser getById(long inId) {
        return (MutableUser)entityManager.find(PersistentUser.class,
                                               inId);
    }

    @Override
    public List<MutableUser> getAll(SearchCondition<MutableUser> sc) {
        JPATypedQueryVisitor visitor = new JPATypedQueryVisitor(entityManager, PersistentUser.class);

        if (sc == null) {
            return getAll();
        }

        sc.accept(visitor);
        TypedQuery typedQuery = visitor.getQuery();
        return typedQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<MutableUser> getAll() {
        List<MutableUser> users = entityManager.createNamedQuery("PersistentUser.findAll").getResultList();
        return users;
    }
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
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
