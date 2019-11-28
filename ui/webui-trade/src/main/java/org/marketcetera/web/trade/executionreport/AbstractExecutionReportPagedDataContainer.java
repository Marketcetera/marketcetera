package org.marketcetera.web.trade.executionreport;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides an abstract <code>PagedDataContainer</code> implementation for views that use {@link ExecutionReport} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportPagedDataContainer
        extends PagedDataContainer<DisplayExecutionReportSummary>
{
    /**
     * Create a new AbstractExecutionReportPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends DisplayExecutionReportSummary&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public AbstractExecutionReportPagedDataContainer(Collection<? extends DisplayExecutionReportSummary> inCollection,
                                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayExecutionReportSummary.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new AbstractExecutionReportPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public AbstractExecutionReportPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayExecutionReportSummary.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DisplayExecutionReportSummary inO1,
                                   DisplayExecutionReportSummary inO2)
    {
        // this check is to determine if a particular row has changed
        boolean result = new EqualsBuilder()
                .append(inO1.getAccount(),inO2.getAccount())
                .append(inO1.getActor().getId(),inO2.getActor().getId())
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getAveragePrice(),inO2.getAveragePrice()),true)
                .append(inO1.getBrokerOrderId(),inO2.getBrokerOrderId())
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getCumulativeQuantity(),inO2.getCumulativeQuantity()),true)
                .append(inO1.getExecutionId(),inO2.getExecutionId())
                .append(inO1.getExecutionType(),inO2.getExecutionType())
                .append(inO1.getExpiry(),inO2.getExpiry())
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getLastPrice(),inO2.getLastPrice()),true)
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getLastQuantity(),inO2.getLastQuantity()),true)
                .append(inO1.getOptionType(),inO2.getOptionType())
                .append(inO1.getOrderID(),inO2.getOrderID())
                .append(inO1.getOrderStatus(),inO2.getOrderStatus())
                .append(inO1.getOriginalOrderID(),inO2.getOriginalOrderID())
                .append(inO1.getRootOrderID(),inO2.getRootOrderID())
                .append(inO1.getSecurityType(),inO2.getSecurityType())
                .append(inO1.getSendingTime(),inO2.getSendingTime())
                .append(inO1.getSide(),inO2.getSide())
                .append(BigDecimalUtil.isSameValueTreatNullAsZero(inO1.getStrikePrice(),inO2.getStrikePrice()),true)
                .append(inO1.getSymbol(),inO2.getSymbol())
                .append(inO1.getViewer().getId(),inO2.getViewer().getId()).isEquals();
        return result;
    }
    private static final long serialVersionUID = 3193849182365242078L;
}
