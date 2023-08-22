package org.marketcetera.ui.marketdata.event;

import org.marketcetera.core.Pair;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.trade.Instrument;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.marketdata.view.MarketDataDetailViewFactory;
import org.marketcetera.ui.view.ContentViewFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Indicates that a view market data detail action has been triggered from market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataDetailEvent
        implements NewWindowEvent,HasInstrument
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return instrument.getFullSymbol();
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
        return MarketDataDetailViewFactory.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.events.NewWindowEvent#getWindowSize()
     */
    @Override
    public Pair<Double,Double> getWindowSize()
    {
        return Pair.create(635.0,365.0);
    }
    /**
     * Create a new MarketDataDetailEvent instance.
     *
     * @param inTitle a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     */
    public MarketDataDetailEvent(String inTitle,
                                 Instrument inInstrument)
    {
        title = inTitle;
        instrument = inInstrument;
    }
    /**
     * title value
     */
    private final String title;
    /**
     * instrument value
     */
    private final Instrument instrument;
}
