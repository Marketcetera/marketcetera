package org.marketcetera.strategyengine.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>SAClient</code> instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface SAClientFactory<ParameterClazz>
{
    /**
     * Creates an <code>SAClient</code> instance.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return an <code>SAClient</code> value
     */
    public SAClient<ParameterClazz> create(ParameterClazz inParameters);
}
