package org.marketcetera.strategyengine.client;

import org.marketcetera.core.ClientFactory;
import org.marketcetera.core.ClientParameters;
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
public interface SEClientFactory<ParameterClazz extends ClientParameters>
        extends ClientFactory<SEClient,ParameterClazz>
{
    /**
     * Creates an <code>SEClient</code> instance.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return an <code>SEClient</code> value
     */
    public SEClient create(ParameterClazz inParameters);
}
