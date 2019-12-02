package org.marketcetera.web.trade.avgpx.view;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for order fills values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AveragePricePagedDataContainer
        extends PagedDataContainer<AverageFillPrice>
{
    /**
     * Create a new AveragePricePagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;AverageFillPrice&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public AveragePricePagedDataContainer(Collection<AverageFillPrice> inCollection,
                                          PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(AverageFillPrice.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new AveragePricePagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public AveragePricePagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(AverageFillPrice.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<AverageFillPrice> getDataContainerContents(PageRequest inPageRequest)
    {
        return TradeClientService.getInstance().getAveragePrice(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Average Price";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(AverageFillPrice inO1,
                                   AverageFillPrice inO2)
    {
        // this check is to determine if a particular row has changed
        boolean result = new EqualsBuilder()
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getAveragePrice(),inO2.getAveragePrice()),true)
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getCumulativeQuantity(),inO2.getCumulativeQuantity()),true)
                .append(inO1.getInstrument().getFullSymbol(),inO2.getInstrument().getFullSymbol())
                .append(inO1.getSide(),inO2.getSide()).isEquals();
        return result;
    }
    private static final long serialVersionUID = -822396804472752165L;
}
