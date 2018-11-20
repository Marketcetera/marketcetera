package org.marketcetera.trade.dao;

import org.hibernate.StaleObjectStateException;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.service.impl.DatabaseIdService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Constructs unique identifiers backed by the database.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseIDFactory.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@Service
@ClassVersion("$Id: DatabaseIDFactory.java 17266 2017-04-28 14:58:00Z colin $")
public class DatabaseIDFactory
        extends DBBackedIDFactory
{
    /**
     * Create a new DatabaseIDFactory instance.
     */
    public DatabaseIDFactory()
    {
        this(""); //$NON-NLS-1$
    }
    /**
     * Create a new DatabaseIDFactory instance.
     *
     * @param inPrefix a <code>String</code> value
     */
    public DatabaseIDFactory(String inPrefix)
    {
        super(inPrefix);
    }
    /**
     * Get the cacheQuantity value.
     *
     * @return a <code>long</code> value
     */
    public long getCacheQuantity()
    {
        return mCacheQuantity;
    }
    /**
     * Sets the cacheQuantity value.
     *
     * @param a <code>long</code> value
     */
    public void setCacheQuantity(long inCacheQuantity)
    {
        mCacheQuantity = inCacheQuantity;
    }
    /**
     * Get the databaseIdManager value.
     *
     * @return a <code>DatabaseIdService</code> value
     */
    public DatabaseIdService getDatabaseIdManager()
    {
        return databaseIdManager;
    }
    /**
     * Sets the databaseIdManager value.
     *
     * @param inDatabaseIdManager a <code>DatabaseIdService</code> value
     */
    public void setDatabaseIdManager(DatabaseIdService inDatabaseIdManager)
    {
        databaseIdManager = inDatabaseIdManager;
    }
    /**
     * Get the retryInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getRetryInterval()
    {
        return retryInterval;
    }
    /**
     * Sets the retryInterval value.
     *
     * @param inRetryInterval a <code>long</code> value
     */
    public void setRetryInterval(long inRetryInterval)
    {
        retryInterval = inRetryInterval;
    }
    /**
     * Get the retryLimit value.
     *
     * @return a <code>int</code> value
     */
    public int getRetryLimit()
    {
        return retryLimit;
    }
    /**
     * Sets the retryLimit value.
     *
     * @param inRetryLimit a <code>int</code> value
     */
    public void setRetryLimit(int inRetryLimit)
    {
        retryLimit = inRetryLimit;
    }
    /**
     * Helper function intended to be overwritten by subclasses.
     * This is where the real request for IDs happens
     */
    protected void performIDRequest()
    {
        int executionCount = 0;
        boolean success = false;
        StaleObjectStateException soe = null;
        while(!success && executionCount++ <= retryLimit) {
            try {
                PersistentDatabaseID id = databaseIdManager.allocateIdBlock(mCacheQuantity);
                long nextID = id.getNextAllowedId();
                long upTo = nextID + mCacheQuantity;
                SLF4JLoggerProxy.trace(this,
                                       "Allocated {} up to {} leaving {}",
                                       nextID,
                                       upTo,
                                       id.getNextAllowedId());
                setMaxAllowedID(upTo);
                setNextID(nextID);
                success = true;
            } catch (StaleObjectStateException e) {
                soe = e;
                SLF4JLoggerProxy.debug(this,
                                      "Stale object state detected");
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        if(!success) {
            if(soe == null) {
                throw new IllegalArgumentException("Unable to allocate ids after " + retryLimit + " tries");
            } else {
                throw soe;
            }
        }
    }
    /**
     * interval in ms to wait before retrying an id request
     */
    private long retryInterval = 100;
    /**
     * number of times to retry before giving up
     */
    private int retryLimit = 10;
    /**
     * manages access to database id values
     */
    @Autowired
    private DatabaseIdService databaseIdManager;
    /**
     * default number of ids to allocate at once
     */
    private long mCacheQuantity = 10000;
}
