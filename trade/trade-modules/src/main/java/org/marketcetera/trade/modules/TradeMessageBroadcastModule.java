package org.marketcetera.trade.modules;

import java.util.Collection;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.AbstractDataReemitterModule;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageBroadcaster;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Facilitates the broadcast of trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TradeMessageBroadcastModule
        extends AbstractDataReemitterModule
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataReemitterModule#onReceiveData(java.lang.Object, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    protected HasTradeMessage onReceiveData(Object inData,
                                            DataEmitterSupport inDataSupport)
    {
        if(!(inData instanceof HasTradeMessage)) {
            throw new ReceiveDataException(new I18NBoundMessage2P(org.marketcetera.module.Messages.WRONG_DATA_TYPE,
                                                                  HasTradeMessage.class.getSimpleName(),
                                                                  inData.getClass().getSimpleName()));
        }
        TradeMessage tradeMessage = ((HasTradeMessage)inData).getTradeMessage();
        if(tradeMessageBroadcasters == null || tradeMessageBroadcasters.isEmpty()) {
            Messages.NO_TRADE_MESSAGE_PUBLISHER.warn(this,
                                                     tradeMessage);
        } else {
            for(TradeMessageBroadcaster tradeMessageBroadcaster : tradeMessageBroadcasters) {
                try {
                    tradeMessageBroadcaster.reportTradeMessage(tradeMessage);
                } catch (Exception e) {
                    PlatformServices.handleException(this,
                                                     "Error publishing trade message",
                                                     e);
                }
            }
        }
        return (HasTradeMessage)inData;
    }
    /**
     * Create a new MessageConverterModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    protected TradeMessageBroadcastModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * provides access to trade services
     */
    @Autowired(required=false)
    private Collection<TradeMessageBroadcaster> tradeMessageBroadcasters = Lists.newArrayList();
}
