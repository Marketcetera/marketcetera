package org.marketcetera.trade.config;

import java.util.List;

import org.marketcetera.dataflow.config.DataFlowProvider;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.trade.TradeConstants;
import org.marketcetera.trade.modules.TradeMessageBroadcastModuleFactory;
import org.marketcetera.trade.modules.TradeMessageConverterModuleFactory;
import org.marketcetera.trade.modules.TradeMessagePersistenceModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides the typical incoming data flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StandardIncomingDataFlowProvider
        implements DataFlowProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getDataFlow(org.marketcetera.module.ModuleManager)
     */
    @Override
    public DataRequest[] getDataFlow(ModuleManager inModuleManager)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               fixDataRequest));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessageConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessagePersistenceModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TradeMessageBroadcastModuleFactory.INSTANCE_URN));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getName()
     */
    @Override
    public String getName()
    {
        return TradeConstants.incomingDataFlowName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "Handles incoming data from brokers";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#receiveDataFlowId(org.marketcetera.module.DataFlowID)
     */
    @Override
    public void receiveDataFlowId(DataFlowID inDataFlowId)
    {
        SLF4JLoggerProxy.info(this,
                              "{} assigned data flow ID {}",
                              getName(),
                              inDataFlowId);
        dataFlowId = inDataFlowId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StandardIncomingDataFlowProvider [name=").append(getName()).append(", dataFlowId=")
                .append(dataFlowId).append("]");
        return builder.toString();
    }
    /**
     * holds the data flow ID assigned to this data flow
     */
    private DataFlowID dataFlowId;
}
