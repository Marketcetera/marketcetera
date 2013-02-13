package org.marketcetera.persist;

/* $License$ */

/**
 * Provides services for managing <code>NDEntityBase</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NDEntityService<Clazz extends NDEntityBase>
        extends NDEntityRepository<Clazz>, EntityService<Clazz>
{
}
