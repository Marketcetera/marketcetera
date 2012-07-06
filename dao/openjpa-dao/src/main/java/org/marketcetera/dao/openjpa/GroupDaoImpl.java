package org.marketcetera.dao.openjpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.marketcetera.core.systemmodel.Group;
import org.marketcetera.dao.GroupDao;
import org.marketcetera.dao.impl.PersistentGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/29/12 12:39 AM
 */

public class GroupDaoImpl implements GroupDao {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(GroupDaoImpl.class);
    private EntityManager entityManager;


    @Override
    public Group getByName(String inName) {
        return (Group) entityManager.createNamedQuery("findGroupByName").getSingleResult();
    }

    @Override
    public void add(Group inData) {
        entityManager.persist(inData);

    }

    @Override
    public void save(Group inData) {
        entityManager.merge(inData);

    }

    @Override
    public Group getById(long inId) {
        return entityManager.find(PersistentGroup.class, inId);
    }

    @Override
    public List<Group> getAll() {
        return entityManager.createNamedQuery("findAllGroups").getResultList();
    }

    @Override
    public void delete(Group inData) {
        entityManager.remove(inData);

    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
