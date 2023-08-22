package org.marketcetera.ui.trade.event;

import java.net.URL;

import org.marketcetera.core.Pair;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.orderticket.OrderTicketViewFactory;
import org.marketcetera.ui.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behavior for order ticket events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractOrderTicketEvent
        implements NewWindowEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.events.NewWindowEvent#getWindowIcon()
     */
    @Override
    public URL getWindowIcon()
    {
        return orderViewFactory.getMenuIcon();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.events.NewWindowEvent#getWindowSize()
     */
    @Override
    public Pair<Double,Double> getWindowSize()
    {
        return Pair.create(850.0, 
                           200.0);
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
     * constructs order views
     */
    @Autowired
    private OrderTicketViewFactory orderViewFactory;
}
