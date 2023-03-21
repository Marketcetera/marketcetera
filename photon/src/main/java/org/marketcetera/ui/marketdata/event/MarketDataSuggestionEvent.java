package org.marketcetera.ui.marketdata.event;

import org.marketcetera.trade.HasSuggestion;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.orderticket.OrderTicketViewFactory;
import org.marketcetera.ui.view.ContentViewFactory;

/* $License$ */

/**
 * Indicates that a trade suggestion has been triggered from market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataSuggestionEvent
        implements NewWindowEvent,HasSuggestion
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestion#getSuggestion()
     */
    @Override
    public Suggestion getSuggestion()
    {
        return suggestion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
     */
    @Override
    public String getWindowTitle()
    {
        return title;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getViewFactoryType()
     */
    @Override
    public Class<? extends ContentViewFactory> getViewFactoryType()
    {
        return OrderTicketViewFactory.class;
    }
    /**
     * Create a new MarketDataSuggestionEvent instance.
     *
     * @param inTitle a <code>String</code> value
     * @param inSuggestion a <code>Suggestion</code> value
     */
    public MarketDataSuggestionEvent(String inTitle,
                                     Suggestion inSuggestion)
    {
        title = inTitle;
        suggestion = inSuggestion;
    }
    /**
     * title value
     */
    private final String title;
    /**
     * suggestion value
     */
    private final Suggestion suggestion;
}
