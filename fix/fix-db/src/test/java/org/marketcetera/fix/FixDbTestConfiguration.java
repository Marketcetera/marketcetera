package org.marketcetera.fix;

import org.marketcetera.brokers.service.FixSessionProvider;
import org.marketcetera.fix.dao.PersistentFixSessionFactory;
import org.marketcetera.fix.dao.PersistentFixSessionProvider;
import org.marketcetera.fix.impl.SimpleActiveFixSessionFactory;
import org.marketcetera.fix.store.HibernateMessageStoreConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides FIX test configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class FixDbTestConfiguration
{
    /**
     * Get the Hibernate message store configuration value.
     *
     * @return a <code>HibernateMessageStoreConfiguration</code> value
     */
    @Bean
    public HibernateMessageStoreConfiguration getMessageStoreConfiguration()
    {
        HibernateMessageStoreConfiguration messageStoreConfiguration = new HibernateMessageStoreConfiguration();
        messageStoreConfiguration.getMessageTypeBlacklist().clear();
        return messageStoreConfiguration;
    }
    /**
     * Get the active FIX session factory value.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    @Bean
    public MutableActiveFixSessionFactory getActiveFixSessionFactory()
    {
        return new SimpleActiveFixSessionFactory();
    }
    /**
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    @Bean
    public MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
    /**
     * Get the FIX session provider value.
     *
     * @return a <code>FixSessionProvider</code> value
     */
    @Bean
    public FixSessionProvider getFixSessionProvider()
    {
        return new PersistentFixSessionProvider();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    @Bean
    public FixSessionFactory getFixSessionFactory()
    {
        return new PersistentFixSessionFactory();
    }
}
