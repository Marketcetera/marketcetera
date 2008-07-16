package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.Messages.*;


import java.sql.Blob;
import java.sql.Clob;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/* $License$ */
/**
 * Class that plugs-in hibernate specific functionality.
 *
 * This class uses reflection to invoke hibernate functions
 * so as to not have compile time dependency on hibernate.
 * The intent is that by not having a compile time dependency
 * on hibernate, our code cannot accidently have dependency
 * on hibernate classes... ie. it helps us keep our depend only
 * on JPA.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
class HibernateVendor extends JPAVendor {
    private Method createBlob;
    private Method createClob;

    public HibernateVendor() throws PersistSetupException {
        //Do reflection to not have compile time dependency on hibernate.
        try {
            Class<?> hibernateClass = Class.forName(HIBERNATE_CLASS);
            createBlob = hibernateClass.getDeclaredMethod("createBlob",byte[].class);
            createClob = hibernateClass.getDeclaredMethod("createClob",String.class);
        } catch (ClassNotFoundException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        } catch (NoSuchMethodException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        }
    }

    public Blob initBlob() throws PersistenceException {
        try {
            return (Blob) createBlob.invoke(null,new byte[0]);
        } catch (IllegalAccessException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        } catch (InvocationTargetException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        }
    }

    public Clob initClob() throws PersistenceException {
        try {
            return (Clob) createClob.invoke(null,"");
        } catch (IllegalAccessException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        } catch (InvocationTargetException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        }
    }
    private static final String HIBERNATE_CLASS = "org.hibernate.Hibernate";
}
