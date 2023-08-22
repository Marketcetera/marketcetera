package org.marketcetera.ui.trade.view.averageprice;

import java.util.Properties;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.trade.view.AbstractFixMessageView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.layout.Region;

/* $License$ */

/**
 * Provides a view for average prices.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AveragePriceView
        extends AbstractFixMessageView<DisplayAverageFillPrice,AverageFillPrice>
{
    /**
     * Create a new AveragePriceView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public AveragePriceView(Region inParentWindow,
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
    protected CollectionPageResponse<AverageFillPrice> getClientReports(PageRequest inPageRequest)
    {
        return tradeClientService.getAveragePrice(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#createFixDisplayObject(java.lang.Object)
     */
    @Override
    protected DisplayAverageFillPrice createFixDisplayObject(AverageFillPrice inClientClazz)
    {
        return new DisplayAverageFillPrice(inClientClazz);
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeAccountColumn()
     */
    @Override
    protected boolean includeAccountColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeOrderQuantityColumn()
     */
    @Override
    protected boolean includeOrderQuantityColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeLastQuantityColumn()
     */
    @Override
    protected boolean includeLastQuantityColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeLastPriceColumn()
     */
    @Override
    protected boolean includeLastPriceColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeLeavesQuantityColumn()
     */
    @Override
    protected boolean includeLeavesQuantityColumn()
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
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeOrderStatusColumn()
     */
    @Override
    protected boolean includeOrderStatusColumn()
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeExchangeColumn()
     */
    @Override
    protected boolean includeTraderColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeBrokerIdColumn()
     */
    @Override
    protected boolean includeBrokerIdColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeTransactTimeColumn()
     */
    @Override
    protected boolean includeTransactTimeColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeSendingTimeColumn()
     */
    @Override
    protected boolean includeSendingTimeColumn()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractFixMessageView#includeOrderIdColumn()
     */
    @Override
    protected boolean includeOrderIdColumn()
    {
        return false;
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Average Price View";
}
