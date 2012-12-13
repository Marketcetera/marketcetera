package org.marketcetera.marketdata.webservices;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.TradeEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class WebServicesEventFactory
{
    public WebServicesEvent create(Event inEvent)
    {
        if(inEvent instanceof AskEvent) {
            return new WebServicesAskEvent((AskEvent)inEvent);
        } else if(inEvent instanceof TradeEvent) {
            return new WebServicesTradeEvent((TradeEvent)inEvent);
        } else if(inEvent instanceof MarketstatEvent) {
            return new WebServicesMarketstatEvent((MarketstatEvent)inEvent);
        }
        return new WebServicesEvent(inEvent);
    }
}
