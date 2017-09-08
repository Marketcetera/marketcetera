package org.marketcetera.test;

import java.util.Collection;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.modules.fix.FixInitiatorModuleFactory;
import org.marketcetera.persist.TransactionModuleFactory;
import org.marketcetera.trade.config.DataFlowProvider;
import org.marketcetera.trade.config.StandardIncomingDataFlowProvider;
import org.marketcetera.trade.config.StandardOutgoingDataFlowProvider;
import org.marketcetera.trade.config.StandardReportInjectionDataFlowProvider;
import org.marketcetera.trade.modules.OrderConverterModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessageCachingModuleFactory;
import org.marketcetera.trade.modules.OutgoingMessagePersistenceModuleFactory;
import org.marketcetera.trade.modules.TradeMessageBroadcastModuleFactory;
import org.marketcetera.trade.modules.TradeMessageConverterModuleFactory;
import org.marketcetera.trade.modules.TradeMessagePersistenceModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class IntegrationTestConfiguration
{
    /**
     * Get the module manager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    @Bean
    public ModuleManager getModuleManager()
    {
        ModuleManager moduleManager = new ModuleManager();
        moduleManager.init();
        ModuleManager.startModulesIfNecessary(moduleManager,
                                              TransactionModuleFactory.INSTANCE_URN,
                                              TradeMessageConverterModuleFactory.INSTANCE_URN,
                                              TradeMessagePersistenceModuleFactory.INSTANCE_URN,
                                              TradeMessageBroadcastModuleFactory.INSTANCE_URN,
                                              OrderConverterModuleFactory.INSTANCE_URN,
                                              OutgoingMessageCachingModuleFactory.INSTANCE_URN,
                                              OutgoingMessagePersistenceModuleFactory.INSTANCE_URN,
                                              FixInitiatorModuleFactory.INSTANCE_URN);
        for(DataFlowProvider dataFlowProvider : dataFlowProviders) {
            SLF4JLoggerProxy.info(this,
                                  "Starting {}",
                                  dataFlowProvider);
            try {
                dataFlowProvider.receiveDataFlowId(moduleManager.createDataFlow(dataFlowProvider.getDataFlow(moduleManager)));
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Unable to start data flow: " + dataFlowProvider.getName(),
                                                 e);
            }
        }
        return moduleManager;
    }
    /**
     * Create the standard incoming data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getIncomingDataFlow()
    {
        return new StandardIncomingDataFlowProvider();
    }
    /**
     * Create the standard outgoing data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getOutgoingDataFlow()
    {
        return new StandardOutgoingDataFlowProvider();
    }
    /**
     * Create the standard report injection data flow.
     *
     * @return a <code>DataFlowProvider</code> value
     */
    @Bean
    public DataFlowProvider getInjectionDataFlow()
    {
        return new StandardReportInjectionDataFlowProvider();
    }
    /**
     * provides data flows
     */
    @Autowired(required=false)
    private Collection<DataFlowProvider> dataFlowProviders = Lists.newArrayList();
}
