package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Represents an object with a name and description.
 *
 * @version $Id$
 * @since $Release$
 */
public interface NamedObject
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription();
}
