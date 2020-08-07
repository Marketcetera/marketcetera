package org.marketcetera.web.trade.event;

import java.util.Properties;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.HasExecutionReport;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.trade.orderticket.view.OrderTicketViewFactory;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Indicates that the given order is to be replaced.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReplaceOrderEvent
        implements HasExecutionReport,NewWindowEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasExecutionReport#getExecutionReport()
     */
    @Override
    public ExecutionReport getExecutionReport()
    {
        return executionReport;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
     */
    @Override
    public String getWindowTitle()
    {
        return "Replace " + executionReport.getOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getViewFactoryType()
     */
    @Override
    public Class<? extends ContentViewFactory> getViewFactoryType()
    {
        return OrderTicketViewFactory.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getProperties()
     */
    @Override
    public Properties getProperties()
    {
        return windowProperties;
    }
    /**
     * Create a new ReplaceOrderEvent instance.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public ReplaceOrderEvent(ExecutionReport inExecutionReport,
                             Properties inProperties)
    {
        executionReport = inExecutionReport;
        windowProperties = inProperties;
    }
    /**
     * execution report value
     */
    private ExecutionReport executionReport;
    /**
     * properties with which to seed the window
     */
    private Properties windowProperties;
}
