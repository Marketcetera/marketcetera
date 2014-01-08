package org.marketcetera.core;

@ClassVersion("$Id$")
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
     * Initializes the id factory object.
     * 
     * @throws NoMoreIDsException if the factory cannot be initialized
     */
    @Override
    public final void init()
            throws NoMoreIDsException
    {
        try {
            grabIDs();
        } catch (NoMoreIDsException e) {
            Messages.ERROR_DB_ID_FACTORY_INIT.info(this, e.getMessage());
            throw e;
        }
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
     * Helper function intended to be overwritten by subclasses.
     * This is where the real request for IDs happens
     */
    protected void performIDRequest()
            throws Exception
    {
        PersistentDatabaseID id = PersistentDatabaseID.getPersistentID();
        long nextID = id.getNextAllowedId();
        long upTo = nextID + mCacheQuantity;
        id.setNextAllowedId(upTo);
        setMaxAllowedID(upTo);
        setNextID(nextID);
        PersistentDatabaseID.save(id);
    }
    /**
     * default number of ids to allocate at once
     */
    private long mCacheQuantity = 1000;
}
