package org.marketcetera.core.systemmodel;

/* $License$ */

/**
 * Provides a test <code>Authority</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockAuthority
        extends MockVersionedObject
        implements MutableAuthority
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return authority;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return getAuthority();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableAuthority#setAuthority(java.lang.String)
     */
    @Override
    public void setAuthority(String inAuthority)
    {
        authority = inAuthority;
    }
    /**
     * authority value
     */
    private String authority;
    private static final long serialVersionUID = 1L;
}
