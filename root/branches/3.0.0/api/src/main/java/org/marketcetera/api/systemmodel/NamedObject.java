package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Represents an object with a name and description.
 *
 * @version $Id: NamedObject.java 82316 2012-03-21 21:13:27Z colin $
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
