package org.marketcetera.security.shiro.impl;

import org.marketcetera.dao.impl.PersistentUser;

/* $License$ */

/**
 * Test implementation of a <code>User</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockUser
        extends PersistentUser
{
    /**
     * Create a new MockUser instance.
     *
     */
    public MockUser()
    {
        super();
    }
    /**
     * Create a new MockUser instance.
     *
     * @param inName a <code>String</code> value
     * @param inPassword a <code>String</code> value
     */
    public MockUser(String inName,
                    String inPassword)
    {
        super(inName,
              inPassword);
    }
    private static final long serialVersionUID = 1L;
}
