package org.marketcetera.dataflow.client.rpc;

import org.marketcetera.dataflow.client.XmlValue;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides context classes for marshalling and unmarshalling data flow client messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: StrategyAgentClientContextClassProvider.java 17233 2016-09-01 20:31:07Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: StrategyAgentClientContextClassProvider.java 17233 2016-09-01 20:31:07Z colin $")
public class DataFlowClientContextClassProvider
        implements ContextClassProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.ContextClassProvider#getContextClasses()
     */
    @Override
    public Class<?>[] getContextClasses()
    {
        return CLASSES;
    }
    /**
     * class list to return
     */
    private static final Class<?>[] CLASSES = new Class<?>[] { ModuleInfo.class, XmlValue.class };
    /**
     * instance value
     */
    public static final DataFlowClientContextClassProvider INSTANCE = new DataFlowClientContextClassProvider();
}
