package org.marketcetera.trade.modules;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.OrderIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Converts FIX messages to trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TradeMessageConverterModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected TradeMessagePackage onReceiveData(Object inData,
                                                DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasFIXMessage)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasFIXMessage.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        HasFIXMessage hasFixMessage = (HasFIXMessage)inData;
        Message fixTradeMessage = hasFixMessage.getMessage();
        ServerFixSession serverFixSession;
        try {
            serverFixSession = brokerService.getServerFixSession(FIXMessageUtil.getReversedSessionId(FIXMessageUtil.getSessionId(fixTradeMessage)));
        } catch (FieldNotFound e) {
            throw new ReceiveDataException(e);
        }
        if(serverFixSession == null) {
            throw new ReceiveDataException(new RuntimeException("Message rejected because the session is unknown for: " + fixTradeMessage)); // TODO
        }
        SLF4JLoggerProxy.debug(this,
                               "Received {} for {}",
                               fixTradeMessage,
                               serverFixSession);
        try {
            TradeMessage tradeMessage = tradeService.convertResponse(hasFixMessage,
                                                                     serverFixSession);
            SLF4JLoggerProxy.debug(this,
                                   "Converted {} to {}",
                                   fixTradeMessage,
                                   tradeMessage);
            return new TradeMessagePackage(serverFixSession,
                                           tradeMessage);
        } catch (OrderIntercepted e) {
            SLF4JLoggerProxy.info(this,
                                  "{} not re-emitted because it was intercepted",
                                  fixTradeMessage);
            return null;
        } catch (Exception e) {
            throw new ReceiveDataException(e);
        }
    }
    /**
     * Create a new MessageConverterModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected TradeMessageConverterModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to trade services
     */
    @Autowired
    private TradeService tradeService;
}
