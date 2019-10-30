package org.marketcetera.web.trade.openorders.view;

import java.util.Collection;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>OrderSummary</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderSummaryPagedDataContainer
        extends PagedDataContainer<OrderSummary>
{
    /**
     * Create a new OrderSummaryPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; ? extends OrderSummary&gt;</code> value
     * @param inCollection a <code>Collection&lt;? extends OrderSummary&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public OrderSummaryPagedDataContainer(Collection<? extends OrderSummary> inCollection,
                                          PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(OrderSummary.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new OrderSummaryPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; super OrderSummary&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public OrderSummaryPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(OrderSummary.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<OrderSummary> getDataContainerContents(PageRequest inPageRequest)
    {
        return TradeClientService.getInstance().getOpenOrders(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(OrderSummary inO1,
                                   OrderSummary inO2)
    {
        return inO1.equals(inO2);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "OpenOrders";
    }
    private static final long serialVersionUID = -1643583263489594148L;
}
