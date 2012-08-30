package org.marketcetera.api.systemmodel;

/* $License$ */

/**
 * Provides a mutable view of a <code>NamedObject</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
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
}
