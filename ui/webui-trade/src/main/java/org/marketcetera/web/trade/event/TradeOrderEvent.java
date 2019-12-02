package org.marketcetera.web.trade.event;

import java.util.Properties;

import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.HasAverageFillPrice;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.trade.orderticket.view.OrderTicketViewFactory;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Indicates that the given average fill price is to be traded upon.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TradeOrderEvent
        implements HasAverageFillPrice,NewWindowEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasAverageFillPrice#getAverageFillPrice()
     */
    @Override
    public AverageFillPrice getAverageFillPrice()
    {
        return averageFillPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
     */
    @Override
    public String getWindowTitle()
    {
        return "Trade " + averageFillPrice.getInstrumentAsString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getViewFactory()
     */
    @Override
    public ContentViewFactory getViewFactory()
    {
        return applicationContext.getBean(OrderTicketViewFactory.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getProperties()
     */
    @Override
    public Properties getProperties()
    {
        return windowProperties;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TradeOrderEvent [averageFillPrice=").append(averageFillPrice).append(", windowProperties=")
                .append(windowProperties).append("]");
        return builder.toString();
    }
    /**
     * Create a new TradeOrderEvent instance.
     *
     * @param inAverageFillPrice an <code>ExecutionReport</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public TradeOrderEvent(AverageFillPrice inAverageFillPrice,
                           Properties inProperties)
    {
        averageFillPrice = inAverageFillPrice;
        windowProperties = inProperties;
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * average fill price value
     */
    private AverageFillPrice averageFillPrice;
    /**
     * properties with which to seed the window
     */
    private Properties windowProperties;
}
