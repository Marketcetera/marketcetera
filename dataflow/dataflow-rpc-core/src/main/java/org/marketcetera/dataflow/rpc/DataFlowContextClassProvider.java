package org.marketcetera.dataflow.rpc;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides context classes for marshalling and unmarshalling data flow client messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class DataFlowContextClassProvider
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
    public static final DataFlowContextClassProvider INSTANCE = new DataFlowContextClassProvider();
}
