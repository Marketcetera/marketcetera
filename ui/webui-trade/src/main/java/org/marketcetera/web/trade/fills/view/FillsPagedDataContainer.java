package org.marketcetera.web.trade.fills.view;

import java.util.Collection;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.trade.executionreport.AbstractExecutionReportPagedDataContainer;
import org.marketcetera.web.trade.report.model.DisplayExecutionReportSummary;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for order fills values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FillsPagedDataContainer
        extends AbstractExecutionReportPagedDataContainer
{
    /**
     * Create a new FillsPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends DisplayExecutionReportSummary&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public FillsPagedDataContainer(Collection<? extends DisplayExecutionReportSummary> inCollection,
                                   PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new FillsPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public FillsPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<DisplayExecutionReportSummary> getDataContainerContents(PageRequest inPageRequest)
    {
        CollectionPageResponse<ExecutionReportSummary> response = TradeClientService.getInstance().getFills(inPageRequest);
        CollectionPageResponse<DisplayExecutionReportSummary> result = new CollectionPageResponse<>(response);
        response.getElements().forEach(executionReportSummary -> result.getElements().add(new DisplayExecutionReportSummary(executionReportSummary)));
        return result;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Fills";
    }
    private static final long serialVersionUID = 2972854915195150110L;
}
