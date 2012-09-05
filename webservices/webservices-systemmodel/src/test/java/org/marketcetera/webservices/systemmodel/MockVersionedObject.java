package org.marketcetera.webservices.systemmodel;

import org.marketcetera.api.systemmodel.VersionedObject;

/* $License$ */

/**
 * Provides a test <code>VersionedObject</code> implementation.
 *
 * @version $Id: MockVersionedObject.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public abstract class MockVersionedObject
        extends MockSystemObject
        implements VersionedObject
{
    /**
     * Set the version value.
     *
     * @param inVersion an <code>int</code> value
     */
    public void setVersion(int inVersion)
    {
        version = inVersion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.VersionedObject#getVersion()
     */
    @Override
    public int getVersion()
    {
        return version;
    }
    /**
     * Create a new MockVersionedObject instance.
     */
    protected MockVersionedObject()
    {
    }
    /**
     * test version value
     */
    private int version;
}
