package org.marketcetera.core;

/* $License$ */

/**
 * Create a mutable <code>Clazz</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableDomainObjectFactory<Clazz extends DomainObject,MutableClazz extends MutableDomainObject<Clazz>>
        extends DomainObjectFactory<Clazz>
{
    /**
     * Create a mutable <code>Clazz</code> value.
     *
     * @return a <code>MutableClazz</code> value
     */
    MutableClazz create();
}
