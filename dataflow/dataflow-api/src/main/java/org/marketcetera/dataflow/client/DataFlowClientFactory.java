package org.marketcetera.dataflow.client;

import org.marketcetera.core.ClientFactory;
import org.marketcetera.core.ClientParameters;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>DataFlowClient</code> instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SEClientFactory.java 17245 2016-09-03 01:25:42Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: SEClientFactory.java 17245 2016-09-03 01:25:42Z colin $")
public interface DataFlowClientFactory<ParameterClazz extends ClientParameters>
        extends ClientFactory<DataFlowClient,ParameterClazz>
{
    /**
     * Creates a <code>DataFlowClient</code> instance.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return an <code>DataFlowClient</code> value
     */
    public DataFlowClient create(ParameterClazz inParameters);
}
