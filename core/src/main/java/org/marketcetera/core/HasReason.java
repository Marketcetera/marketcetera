package org.marketcetera.core;

/* $License$ */

/**
 * Indicates the implementor has a human-readable reason justifying a specific action.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasReason
{
    /**
     * Get the reason value.
     *
     * @return a <code>String</code> value
     */
    String getReason();
    /**
     * Set the reason value.
     *
     * @param inReason a <code>String</code> value
     */
    void setReason(String inReason);
}
