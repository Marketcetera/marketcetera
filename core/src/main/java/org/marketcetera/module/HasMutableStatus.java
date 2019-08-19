package org.marketcetera.module;

/* $License$ */

/**
 * Indicates that the implementor has mutable status attributes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMutableStatus
        extends HasStatus
{
    /**
     * Set the error message value.
     *
     * @param inMessage a <code>String</code> value
     */
    void setErrorMessage(String inMessage);
    /**
     * Set the failed value.
     *
     * @param inFailed a <code>boolean</code> value
     */
    void setFailed(boolean inFailed);
}
