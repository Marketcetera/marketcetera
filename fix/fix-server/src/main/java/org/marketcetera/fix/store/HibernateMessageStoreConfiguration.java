package org.marketcetera.fix.store;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

import com.google.common.collect.Sets;

import quickfix.field.MsgType;

/* $License$ */

/**
 * Provides a bridge to Spring configuration for the {@link HibernateMessageStore}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HibernateMessageStoreConfiguration
{
    /**
     * Get the instance value.
     *
     * @return a <code>HibernateMessageStoreConfiguration</code> value
     * @throws IllegalArgumentException if the configuration instance has not been initialized
     */
    public static HibernateMessageStoreConfiguration getInstance()
    {
        waitForReady();
        Validate.notNull(instance);
        return instance;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        synchronized(ready) {
            ready.set(true);
            ready.notifyAll();
        }
    }
    /**
     * Create a new HibernateMessageStoreConfiguration instance.
     */
    public HibernateMessageStoreConfiguration()
    {
        instance = this;
    }
    /**
     * Get the messageDao value.
     *
     * @return a <code>MessageStoreMessageDao</code> value
     */
    public MessageStoreMessageDao getMessageDao()
    {
        waitForReady();
        return messageDao;
    }
    /**
     * Get the sessionDao value.
     *
     * @return a <code>MessageStoreSessionDao</code> value
     */
    public MessageStoreSessionDao getSessionDao()
    {
        waitForReady();
        return sessionDao;
    }
    /**
     * Get the transactionManager value.
     *
     * @return a <code>PlatformTransactionManager</code> value
     */
    public PlatformTransactionManager getTransactionManager()
    {
        waitForReady();
        return transactionManager;
    }
    /**
     * Get the messageTypeBlacklist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getMessageTypeBlacklist()
    {
        waitForReady();
        return messageTypeBlacklist;
    }
    /**
     * Sets the messageTypeBlacklist value.
     *
     * @param inMessageTypeBlacklist a <code>Set&lt;String&gt;</code> value
     */
    public void setMessageTypeBlacklist(Set<String> inMessageTypeBlacklist)
    {
        messageTypeBlacklist = inMessageTypeBlacklist;
    }
    /**
     * Get the messageTypeWhitelist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getMessageTypeWhitelist()
    {
        waitForReady();
        return messageTypeWhitelist;
    }
    /**
     * Sets the messageTypeWhitelist value.
     *
     * @param inMessageTypeWhitelist a <code>Set&lt;String&gt;</code> value
     */
    public void setMessageTypeWhitelist(Set<String> inMessageTypeWhitelist)
    {
        messageTypeWhitelist = inMessageTypeWhitelist;
    }
    /**
     * Wait until the object is ready to be used.
     * 
     * @throws RuntimeException if the object is interrupted while waiting
     */
    private static void waitForReady()
    {
        try {
            synchronized(ready) {
                while(!ready.get()) {
                    SLF4JLoggerProxy.info(HibernateMessageStoreConfiguration.class,
                                         "Waiting for the config to be ready");
                    ready.wait(1000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * provides access to the message data store
     */
    @Autowired
    private MessageStoreMessageDao messageDao;
    /**
     * provides access to the session data store
     */
    @Autowired
    private MessageStoreSessionDao sessionDao;
    /**
     * transaction manager value
     */
    @Autowired
    private PlatformTransactionManager transactionManager;
    /**
     * static instance
     */
    private static HibernateMessageStoreConfiguration instance;
    /**
     * message types listed here will not be stored, others will
     */
    private Set<String> messageTypeBlacklist = Sets.newHashSet(MsgType.MARKET_DATA_INCREMENTAL_REFRESH,MsgType.MARKET_DATA_REQUEST,MsgType.MARKET_DATA_REQUEST_REJECT,MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
    /**
     * message types listed here will be stored, others will not
     */
    private Set<String> messageTypeWhitelist = Sets.newHashSet();
    /**
     * indicates that this object is ready to use
     */
    private final static AtomicBoolean ready = new AtomicBoolean(false);
}
