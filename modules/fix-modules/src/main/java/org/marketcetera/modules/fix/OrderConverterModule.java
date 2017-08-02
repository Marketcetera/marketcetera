package org.marketcetera.modules.fix;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.Order;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;

/* $License$ */

/**
 * Converts orders to FIX messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class OrderConverterModule
        extends AbstractDataReemitterModule
        implements DataEmitter,DataReceiver
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected Object onReceiveData(Object inData,
                                   DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof Order)) {
            throw new DataFlowException(new RuntimeException("Data flow requires objects of type Order, not " + inData.getClass().getSimpleName()));
        }
        Order order = (Order)inData;
        Broker broker = brokerService.selectBroker(order);
        Message convertedOrder = brokerService.convertOrder(order,
                                                            broker);
        return convertedOrder;
    }
    /**
     * Create a new MessageConverterModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected OrderConverterModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}
