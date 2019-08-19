package org.marketcetera.trade.modules;

import org.marketcetera.admin.HasUser;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;
import quickfix.SessionID;

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
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected HasFIXMessage onReceiveData(Object inData,
                                          DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasOrder)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasOrder.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        if(!(inData instanceof HasUser)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasUser.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        Order order = ((HasOrder)inData).getOrder();
        ServerFixSession broker = tradeService.selectServerFixSession(order);
        SLF4JLoggerProxy.debug(this,
                               "{} targeted to {}",
                               order,
                               broker);
        Message convertedOrder = tradeService.convertOrder(order,
                                                           broker);
        FIXMessageUtil.setSessionId(convertedOrder,
                                    new SessionID(broker.getActiveFixSession().getFixSession().getSessionId()));
        SLF4JLoggerProxy.debug(this,
                               "{} converted to {}",
                               order,
                               convertedOrder);
        return new OwnedMessage(((HasUser)inData).getUser(),
                                new BrokerID(broker.getActiveFixSession().getFixSession().getBrokerId()),
                                new SessionID(broker.getActiveFixSession().getFixSession().getSessionId()),
                                convertedOrder);
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
