package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an object with a name.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NamedObject.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: NamedObject.java 82384 2012-07-20 19:09:59Z colin $")
public interface NamedObject
{
    /**
     * Gets the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
}
