package org.marketcetera.admin;

/* $License$ */

/**
 * Constructs {@link AdminClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AdminClientFactory<ParameterClazz>
{
    /**
     * Create a new {@link AdminClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>AdminClient</code> value
     */
    AdminClient create(ParameterClazz inParameterClazz);
}
