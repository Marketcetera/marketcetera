package org.marketcetera.webservices.systemmodel;

import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Provides a test <code>SystemObject</code> implementation.
 *
 * @version $Id: MockSystemObject.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public abstract class MockSystemObject
        implements SystemObject
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.SystemObject#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param a <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
    }
    /**
     * Create a new MockSystemObject instance.
     */
    protected MockSystemObject()
    {
    }
    /**
     * id value
     */
    private long id;
}
