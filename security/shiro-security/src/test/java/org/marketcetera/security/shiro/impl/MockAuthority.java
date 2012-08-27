package org.marketcetera.security.shiro.impl;

import org.marketcetera.dao.impl.PersistentAuthority;

/* $License$ */

/**
 * Provides a test implementation of an <code>Authority</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockAuthority
        extends PersistentAuthority
{
    /**
     * Create a new MockAuthority instance.
     */
    public MockAuthority()
    {
        setAuthority("authority-" + System.nanoTime());
        setId(System.nanoTime());
    }
    private static final long serialVersionUID = 1L;
}
