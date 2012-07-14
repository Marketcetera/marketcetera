package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.Messages.*;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage0P;


import java.sql.Blob;
import java.sql.Clob;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
@ClassVersion("$Id$") //$NON-NLS-1$
class HibernateVendor extends JPAVendor {
    private Method createBlob;
    private Method createClob;

    public HibernateVendor() throws PersistSetupException {
        //Do reflection to not have compile time dependency on hibernate.
        try {
            Class<?> hibernateClass = Class.forName(HIBERNATE_CLASS);
            createBlob = hibernateClass.getDeclaredMethod("createBlob",byte[].class); //$NON-NLS-1$
            createClob = hibernateClass.getDeclaredMethod("createClob",String.class); //$NON-NLS-1$
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
            return (Clob) createClob.invoke(null,""); //$NON-NLS-1$
        } catch (IllegalAccessException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        } catch (InvocationTargetException e) {
            throw new PersistSetupException(e, HIBERNATE_INTEGRATION_ISSUE);
        }
    }

    public I18NBoundMessage getEntityExistsMessage(
            javax.persistence.EntityExistsException exception) {
        Matcher m = EEE_MSG_CLASS_NAME_PATTERN.matcher(exception.getMessage());
        //if its a save operation, return a custom message
        if(m.matches() & m.groupCount() == 1) {
            return new I18NBoundMessage1P(ENTITY_EXISTS_INSERT_ERROR,
                            EntityRemoteServer.getEntityName(m.group(1)));
        }
        //otherwise return a generic message
        return new I18NBoundMessage0P(ENTITY_EXISTS_GENERIC_ERROR);
    }

    private static final Pattern EEE_MSG_CLASS_NAME_PATTERN =
            Pattern.compile(".*: \\[([\\p{L}\\p{N}_$\\.]*)\\]"); //$NON-NLS-1$
    private static final String HIBERNATE_CLASS = "org.hibernate.Hibernate"; //$NON-NLS-1$
}
