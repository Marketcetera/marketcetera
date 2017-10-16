package org.marketcetera.dataflow.modules;

import java.util.Collection;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.dataflow.client.DataBroadcaster;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Redirects data to data receivers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class DataFlowReceiverModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected Object onReceiveData(Object inData,
                                   DataEmitterSupport inDataSupport)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received {}",
                               inData);
        for(DataBroadcaster dataBroadcaster : dataBroadcasters) {
            try {
                dataBroadcaster.receiveData(inData);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Publishing data",
                                                 e);
            }
        }
        return inData;
    }
    /**
     * Create a new DataFlowReceiverModule instance.
     *
     * @param inInstanceUrn
     */
    DataFlowReceiverModule(ModuleURN inInstanceUrn)
    {
        super(inInstanceUrn,
              true);
    }
    /**
     * publishers of data
     */
    @Autowired(required=false)
    private Collection<DataBroadcaster> dataBroadcasters = Lists.newArrayList();
}
