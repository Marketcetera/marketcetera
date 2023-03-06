package org.marketcetera.ui.trade.event;

import java.net.URL;

import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.orderticket.OrderTicketViewFactory;
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
    /**
     * constructs order views
     */
    @Autowired
    private OrderTicketViewFactory orderViewFactory;
}
