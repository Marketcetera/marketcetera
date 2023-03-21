package org.marketcetera.ui.trade.view.fills;

import java.util.Properties;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.AbstractDeletableFixMessageView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;

/* $License$ */

/**
 * Provides a view for fills.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FillsView
        extends AbstractDeletableFixMessageView<DisplayExecutionReportSummary,ExecutionReportSummary>
{
    /**
     * Create a new FillsView instance.
     *
     * @param inParentWindow a <code>Node</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public FillsView(Node inParentWindow,
                     NewWindowEvent inEvent,
                     Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#getClientReports(org.marketcetera.persist.PageRequest)
     */
    @Override
    protected CollectionPageResponse<ExecutionReportSummary> getClientReports(PageRequest inPageRequest)
    {
        return tradeClientService.getFills(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#createFixDisplayObject(java.lang.Object)
     */
    @Override
    protected DisplayExecutionReportSummary createFixDisplayObject(ExecutionReportSummary inClientClazz)
    {
        return new DisplayExecutionReportSummary(inClientClazz);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeOrderPriceColumn()
     */
    @Override
    protected boolean includeOrderPriceColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeOriginalOrderIdColumn()
     */
    @Override
    protected boolean includeOriginalOrderIdColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeExchangeColumn()
     */
    @Override
    protected boolean includeExchangeColumn()
    {
        return false;
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Fills View";
}
