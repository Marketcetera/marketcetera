package org.marketcetera.core.systemmodel;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Represents an object with a name.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NamedObject.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: NamedObject.java 82316 2012-03-21 21:13:27Z colin $")
public interface NamedObject
{
    /**
     * Gets the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
}
