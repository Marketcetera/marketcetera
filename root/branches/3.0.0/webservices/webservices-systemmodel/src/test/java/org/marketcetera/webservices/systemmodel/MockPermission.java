package org.marketcetera.webservices.systemmodel;

/* $License$ */

/**
 * Provides a test <code>Permission</code> implementation.
 *
 * @version $Id: MockPermission.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public class MockPermission
        extends MockVersionedObject
        implements MutablePermission
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GrantedPermission#getPermission()
     */
    @Override
    public String getPermission()
    {
        return permission;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return getPermission();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutablePermission#setPermission(java.lang.String)
     */
    @Override
    public void setPermission(String inPermission)
    {
        permission = inPermission;
    }
    /**
     * permission value
     */
    private String permission;
    private static final long serialVersionUID = 1L;
}
