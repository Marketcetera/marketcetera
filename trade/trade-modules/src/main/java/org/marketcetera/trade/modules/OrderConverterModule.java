package org.marketcetera.trade.modules;

import org.marketcetera.brokers.Broker;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
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
    protected Message onReceiveData(Object inData,
                                    DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof Order)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(Messages.WRONG_DATA_TYPE,
                                                                  Order.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        Order order = (Order)inData;
        Broker broker = tradeService.selectBroker(order);
        SLF4JLoggerProxy.debug(this,
                               "{} targeted to {}",
                               order,
                               broker);
        Message convertedOrder = tradeService.convertOrder(order,
                                                           broker);
        FIXMessageUtil.setSessionId(convertedOrder,
                                    broker.getSessionId());
        SLF4JLoggerProxy.debug(this,
                               "{} converted to {}",
                               order,
                               convertedOrder);
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
     * provides access to trade services
     */
    @Autowired
    private TradeService tradeService;
}
