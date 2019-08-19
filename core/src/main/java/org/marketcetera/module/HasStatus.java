package org.marketcetera.module;

/* $License$ */

/**
 * Indicates that the implementor has status attributes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasStatus
{
    /**
     * Get the failed value.
     *
     * @return a <code>boolean</code> value
     */
    boolean getFailed();
    /**
     * Get the error message value.
     *
     * @return a <code>String</code> value
     */
    String getErrorMessage();
}
