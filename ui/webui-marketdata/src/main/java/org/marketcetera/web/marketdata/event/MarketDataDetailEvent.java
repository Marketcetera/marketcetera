package org.marketcetera.web.marketdata.event;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.trade.Instrument;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.marketdata.detail.view.MarketDataDetailViewFactory;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Indicates that a view market data detail action has been triggered from market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
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
