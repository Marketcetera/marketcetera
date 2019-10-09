package org.marketcetera.fix;

/* $License$ */

/**
 * Constructs {@link FixAdminClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AdminClientFactory.java 17879 2019-08-19 17:30:03Z colin $
 * @since $Release$
 */
public interface FixAdminClientFactory<ParameterClazz>
{
    /**
     * Create a new {@link FixAdminClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>FixAdminClient</code> value
     */
    FixAdminClient create(ParameterClazz inParameterClazz);
}
