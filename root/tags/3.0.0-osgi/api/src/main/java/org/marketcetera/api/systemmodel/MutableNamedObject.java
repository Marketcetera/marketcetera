package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Provides a mutable view of a <code>NamedObject</code>.
 *
 * @version $Id$
 * @since $Release$
 */
public interface MutableNamedObject
        extends NamedObject
{
    /**
     * Set the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName);
    /**
     * Set the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription);
}
