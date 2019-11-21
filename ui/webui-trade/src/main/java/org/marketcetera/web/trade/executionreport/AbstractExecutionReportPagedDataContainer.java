package org.marketcetera.web.trade.executionreport;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
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
        extends PagedDataContainer<ExecutionReport>
{
    /**
     * Create a new AbstractExecutionReportPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends ExecutionReport&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public AbstractExecutionReportPagedDataContainer(Collection<? extends ExecutionReport> inCollection,
                                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ExecutionReport.class,
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
        super(ExecutionReport.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(ExecutionReport inO1,
                                   ExecutionReport inO2)
    {
        // include values here that are likely to change over the course on an order
        return new EqualsBuilder()
                .append(inO1.getAccount(),inO2.getAccount())
                .append(inO1.getAveragePrice(),inO2.getAveragePrice())
                .append(inO1.getCumulativeQuantity(),inO2.getCumulativeQuantity())
                .append(inO1.getLastPrice(),inO2.getLastPrice())
                .append(inO1.getLastQuantity(),inO2.getLastQuantity())
                .append(inO1.getLeavesQuantity(),inO2.getLeavesQuantity())
                .append(inO1.getOrderID(),inO2.getOrderID())
                .append(inO1.getOrderQuantity(),inO2.getOrderQuantity())
                .append(inO1.getOrderStatus(),inO2.getOrderStatus())
                .append(inO1.getPrice(),inO2.getPrice())
                .append(inO1.getSendingTime(),inO2.getSendingTime())
                .append(inO1.getSide(),inO2.getSide())
                .append(inO1.getTransactTime(),inO2.getTransactTime()).isEquals();
    }
    private static final long serialVersionUID = 3193849182365242078L;
}
