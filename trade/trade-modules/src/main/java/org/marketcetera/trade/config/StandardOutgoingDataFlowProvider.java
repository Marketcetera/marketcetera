package org.marketcetera.trade.config;

import java.util.List;

import org.marketcetera.dataflow.config.DataFlowProvider;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.fix.FixDataRequest;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.trade.TradeConstants;
import org.marketcetera.trade.modules.OrderConverterModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessageCachingModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessagePersistenceModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides the typical Outgoing data flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StandardOutgoingDataFlowProvider
        implements DataFlowProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getDataFlow(org.marketcetera.module.ModuleManager)
     */
    @Override
    public DataRequest[] getDataFlow(ModuleManager inModuleManager)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        ModuleURN headwaterUrn = HeadwaterModule.createHeadwaterModule(TradeConstants.outgoingDataFlowName,
                                                                       inModuleManager);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TransactionModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OrderConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessageCachingModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(OutgoingMessagePersistenceModuleFactory.INSTANCE_URN));
        FixDataRequest fixDataRequest = new FixDataRequest();
        fixDataRequest.setIncludeAdmin(false);
        fixDataRequest.setIncludeApp(true);
        fixDataRequest.getMessageWhiteList().clear();
        fixDataRequest.getMessageBlackList().clear();
        dataRequestBuilder.add(new DataRequest(FixInitiatorModuleFactory.INSTANCE_URN,
                                               fixDataRequest));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getName()
     */
    @Override
    public String getName()
    {
        return TradeConstants.outgoingDataFlowName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.config.DataFlowProvider#getDescription()
     */
    @Override
    public String getDescription()
    {
        return "Handles outgoing messages to brokers";
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
        builder.append("StandardOutgoingDataFlowProvider [name=").append(getName()).append(", dataFlowId=")
                .append(dataFlowId).append("]");
        return builder.toString();
    }
    /**
     * holds the data flow ID assigned to this data flow
     */
    private DataFlowID dataFlowId;
}
