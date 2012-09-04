package org.marketcetera.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.security.User;
import org.marketcetera.dao.domain.PersistentUser;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @version $Id$
 * @date 6/29/12 12:40 AM
 */

public class UserDaoImpl implements UserDao {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAll() {
        return entityManager.createNamedQuery("findAllUsers").getResultList();
    }

//    @Override
//    public User loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = getByName(username);
//        if(user == null) {
//            throw new UsernameNotFoundException(username);
//        }
//        return user;
//    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
