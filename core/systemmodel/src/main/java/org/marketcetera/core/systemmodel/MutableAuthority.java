package org.marketcetera.core.systemmodel;

/* $License$ */

/**
 * Provides a mutable view of an <code>Authority</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableAuthority
        extends Authority
{
    /**
     * Sets the authority value.
     *
     * @param inAuthority a <code>String</code> value
     */
    public void setAuthority(String inAuthority);
}
