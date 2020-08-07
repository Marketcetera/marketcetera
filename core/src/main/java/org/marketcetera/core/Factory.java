package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates objects of the given type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface Factory<Clazz>
{
    /**
     * Create a new object of type <code>Clazz</code>.
     *
     * @return a <code>Clazz</code> value
     */
    Clazz create();
    /**
     * Create a new object of type <code>Clazz</code>.
     *
     * @param inObject a <code>Clazz</code> value
     * @return a <code>Clazz</code> value
     */
    Clazz create(Clazz inObject);
}
