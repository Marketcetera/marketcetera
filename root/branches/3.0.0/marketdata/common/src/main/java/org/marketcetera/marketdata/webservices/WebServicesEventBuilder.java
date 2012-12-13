package org.marketcetera.marketdata.webservices;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.TradeEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class WebServicesEventBuilder
{
    public WebServicesEvent create(Event inEvent)
    {
        if(inEvent instanceof AskEvent) {
            if(inEvent instanceof EquityEvent) {
                return new WebServicesEquityAskEvent((AskEvent)inEvent);
            }
        } else if(inEvent instanceof TradeEvent) {
            if(inEvent instanceof EquityEvent) {
                return new WebServicesEquityTradeEvent((TradeEvent)inEvent);
            }
        }
        return new WebServicesEvent(inEvent);
    }
}
