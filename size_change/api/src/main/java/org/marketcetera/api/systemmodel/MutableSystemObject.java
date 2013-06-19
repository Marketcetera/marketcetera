package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Provides a mutable view of a <code>SystemObject<code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableSystemObject
        extends SystemObject
{
    /**
     * Sets the id value.
     *
     * @param inId a <code>long</code> value
     */
    public void setId(long inId);
}
