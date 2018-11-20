package org.marketcetera.core;

/* $License$ */

/**
 * Indicates that the implementing object can provide a mutable view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMutableView<Clazz>
{
    /**
     * Get a mutable view of this object.
     *
     * @return a <code>Clazz</code> value
     */
    Clazz getMutableView();
}
