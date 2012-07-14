package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.dao.AuthorityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:38 AM
 */

public class AuthorityDaoImpl implements AuthorityDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AuthorityDaoImpl.class);
    private EntityManager entityManager;


    @Override
    public void add(Authority inData) {
        entityManager.persist(inData);
    }

    @Override
    public void save(Authority inData) {
        entityManager.merge(inData);
    }

    @Override
    public Authority getByName(String inName) {
        return (Authority) entityManager.createNamedQuery("findUserByName").setParameter("name", inName).getSingleResult();
    }

    @Override
    public Authority getById(long inId) {
        return entityManager.find(PersistentAuthority.class, inId);
    }

    @Override
    public List<Authority> getAll() {
        return entityManager.createNamedQuery("findAllAuthorities").getResultList();
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
