package org.marketcetera.ui.trade.event;

import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.orderticket.OrderTicketViewFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.image.Image;

/* $License$ */

/**
 *
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
    public Image getWindowIcon()
    {
        return orderViewFactory.getMenuIcon();
    }
    /**
     * constructs order views
     */
    @Autowired
    private OrderTicketViewFactory orderViewFactory;
}
