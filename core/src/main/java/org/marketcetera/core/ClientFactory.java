package org.marketcetera.core;

/* $License$ */

/**
 * Create a client of the specified type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClientFactory<ClientClazz extends BaseClient,ParameterClazz extends ClientParameters>
{
    /**
     * Create a client using the given parameters.
     *
     * @param inParameters a <code>ParameterClazz</code>value
     * @return a <code>ClientClazz</code> value
     */
    ClientClazz create(ParameterClazz inParameters);
}
