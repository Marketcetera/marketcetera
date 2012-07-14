package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.marketcetera.dao.UserDao;
import org.marketcetera.core.systemmodel.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:40 AM
 */

public class UserDaoImpl implements UserDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private EntityManager entityManager;


    @Override
    public User getByName(String inUsername) {
        return (User) entityManager.createNamedQuery("findUserByUsername").getSingleResult();
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
    public User getById(long inId) {
        return entityManager.find(PersistentUser.class, inId);
    }

    @Override
    public List<User> getAll() {
        return entityManager.createNamedQuery("findAllUsers").getResultList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByName(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
