package org.marketcetera.quickfix;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.marketcetera.fix.IncomingMessageFactory;
import org.marketcetera.fix.SessionNameProvider;
import org.marketcetera.fix.dao.IncomingMessageDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides configuration for {@link RecordingLog} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RecordingLogConfiguration
        implements ApplicationContextAware
{
    /**
     * Create a new RecordingLogFactoryConfiguration instance.
     */
    public RecordingLogConfiguration()
    {
        Validate.isTrue(instance == null);
        instance = this;
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(dataSource);
        Validate.notNull(incomingMessageFactory);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the incomingMessageDao value.
     *
     * @return an <code>IncomingMessageDao</code> value
     */
    public IncomingMessageDao getIncomingMessageDao()
    {
        return incomingMessageDao;
    }
    /**
     * Sets the incomingMessageDao value.
     *
     * @param inIncomingMessageDao an <code>IncomingMessageDao</code> value
     */
    public void setIncomingMessageDao(IncomingMessageDao inIncomingMessageDao)
    {
        incomingMessageDao = inIncomingMessageDao;
    }
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * Get the dataSource value.
     *
     * @return a <code>DataSource</code> value
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }
    /**
     * Sets the dataSource value.
     *
     * @param inDataSource a <code>DataSource</code> value
     */
    public void setDataSource(DataSource inDataSource)
    {
        dataSource = inDataSource;
    }
    /**
     * Get the incomingMessageFactory value.
     *
     * @return a <code>IncomingMessageFactory</code> value
     */
    public IncomingMessageFactory getIncomingMessageFactory()
    {
        return incomingMessageFactory;
    }
    /**
     * Sets the incomingMessageFactory value.
     *
     * @param inIncomingMessageFactory a <code>IncomingMessageFactory</code> value
     */
    public void setIncomingMessageFactory(IncomingMessageFactory inIncomingMessageFactory)
    {
        incomingMessageFactory = inIncomingMessageFactory;
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>SessionSettings</code> value
     */
    public SessionSettings getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    public void setSessionSettings(SessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
    }
    /**
     * Get the sessionNameProvider value.
     *
     * @return a <code>SessionNameProvider</code> value
     */
    public SessionNameProvider getSessionNameProvider()
    {
        return sessionNameProvider;
    }
    /**
     * Sets the sessionNameProvider value.
     *
     * @param inSessionNameProvider a <code>SessionNameProvider</code> value
     */
    public void setSessionNameProvider(SessionNameProvider inSessionNameProvider)
    {
        sessionNameProvider = inSessionNameProvider;
    }
    /**
     * describes the FIX session settings
     */
    private SessionSettings sessionSettings;
    /**
     * data source to use for database connections
     */
    private DataSource dataSource;
    /**
     * constructs incoming message objects
     */
    @Autowired
    private IncomingMessageFactory incomingMessageFactory;
    /**
     * application context value
     */
    private ApplicationContext applicationContext;
    /**
     * provides data store access to incoming message values
     */
    @Autowired
    private IncomingMessageDao incomingMessageDao;
    /**
     * optional service that gives human-readable names for sessions
     */
    @Autowired(required=false)
    private SessionNameProvider sessionNameProvider;
    /**
     * provides access to the instance created by Spring
     */
    public static RecordingLogConfiguration instance;
}
