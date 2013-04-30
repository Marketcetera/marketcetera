package org.marketcetera.marketdata.webservices;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.trade.ConvertibleSecurity;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Constructs <code>WebServicesEvent</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class WebServicesEventFactory
{
    /**
     * Creates web services capable events from the given input event.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>WebServicesEvent</code> value
     */
    public WebServicesEvent create(Event inEvent)
    {
        if(inEvent instanceof AskEvent) {
            return new WebServicesAskEvent((AskEvent)inEvent);
        } else if(inEvent instanceof TradeEvent) {
            TradeEvent event = (TradeEvent)inEvent;
            Instrument instrument = event.getInstrument();
            if(instrument instanceof ConvertibleSecurity) {
                return new WebServicesConvertibleSecurityTradeEvent(event);
            } else {
                return new WebServicesTradeEvent((TradeEvent)inEvent);
            }
        } else if(inEvent instanceof MarketstatEvent) {
            MarketstatEvent event = (MarketstatEvent)inEvent;
            Instrument instrument = event.getInstrument();
            if(instrument instanceof ConvertibleSecurity) {
                return new WebServicesConvertibleSecurityMarketstatEvent(event);
            } else {
                return new WebServicesMarketstatEvent(event);
            }
        }
        return new WebServicesEvent(inEvent);
    }
}
