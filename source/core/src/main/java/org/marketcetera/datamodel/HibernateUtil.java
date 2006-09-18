package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * Entrypoint to Hibernate - enables creation of a Hibernate session
 * that should be used for storing data in the db.
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class HibernateUtil {
    private static final String URL_PREFIX = "jdbc:mysql://";

    public HibernateUtil() {
    }

    private static SessionFactory sessionFactory;

    public static SessionFactory initialize() {
        return initialize(null);
    }

    public static SessionFactory initialize(String connectionSuffix)
    {
        if(sessionFactory != null) return sessionFactory;

        try {
            Configuration configuration = new AnnotationConfiguration().configure();
            if(connectionSuffix != null) {
                // reset the db
                configuration.setProperty(Environment.URL, URL_PREFIX+connectionSuffix);
            }
            configuration.setInterceptor(new AccessTimeModificationInterceptor());
            sessionFactory = configuration.buildSessionFactory();
            return sessionFactory;
        } catch (Throwable ex) {
            LoggerAdapter.error(MessageKey.HIBERNATE_CREATION_ERR.getLocalizedMessage(), ex, HibernateUtil.class);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        if(sessionFactory == null) {
            initialize();
        }
        return sessionFactory;
    }
}
