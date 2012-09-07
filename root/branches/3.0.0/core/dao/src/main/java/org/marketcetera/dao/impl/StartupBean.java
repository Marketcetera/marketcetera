package org.marketcetera.dao.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.path.EntityPathBase;
import org.marketcetera.dao.domain.PersistentPermission;
import org.marketcetera.dao.domain.PersistentRole;
import org.marketcetera.dao.domain.PersistentUser;


/**
 * @version $Id$
 * @date 7/14/12 3:38 AM
 */

public class StartupBean {
// ------------------------------ FIELDS ------------------------------

    private EntityManager entityManager;

// --------------------- GETTER / SETTER METHODS ---------------------

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

// -------------------------- OTHER METHODS --------------------------

    public void activate() {
        Object deserialized;
//        if (true) {
//            try {
//                JAXBContext context = JAXBContext.newInstance(PersistentPermission.class);
//                Marshaller m = context.createMarshaller();
//                m.marshal(new PersistentPermission(), System.out);
//            } catch (JAXBException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//            return;
//        }
        try {
            InputStream inputStream = getClass().getResourceAsStream("/initialdata.xml");
            JAXBContext context = JAXBContext.newInstance(PersistentPermission.class, PersistentRole.class, PersistentUser.class);
            Unmarshaller m = context.createUnmarshaller();
            deserialized = m.unmarshal(new InputStreamReader(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Exception loading initialdata.xml to database", e);
        }

        Collection<Object> c = null;
        if (deserialized instanceof Collection) {
            c = (Collection<Object>) deserialized;
        } else {
            c = new LinkedList<Object>();
            c.add(deserialized);
        }

        // find if there are any of the first object in the database
        Object first = c.iterator().next();
        JPAQuery query = new JPAQuery(entityManager);
        EntityPathBase<?> object = new EntityPathBase<Object>(first.getClass(), "object");
        long count = query.from(object).count();

        if (count == 0) {
            for (Object o : (Collection) deserialized) {
                doSave(o);
            }
        }
    }

    private void doSave(Object deserialized) {
        entityManager.persist(deserialized);
    }
}
