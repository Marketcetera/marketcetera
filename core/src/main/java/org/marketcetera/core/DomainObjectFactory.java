package org.marketcetera.core;

/* $License$ */

/**
 * Create a new <code>Clazz</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DomainObjectFactory<Clazz extends DomainObject>
{
    /**
     * Create a new <code>Clazz</code> value.
     *
     * @param inObject a <code>Clazz</code>value
     * @return a <code>Clazz</code> value
     */
    Clazz create(Clazz inObject);
}
