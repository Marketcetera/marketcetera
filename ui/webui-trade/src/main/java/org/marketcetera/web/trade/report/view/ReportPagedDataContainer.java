package org.marketcetera.web.trade.report.view;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Report;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.trade.report.model.DisplayReport;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>DisplayReport</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReportPagedDataContainer
        extends PagedDataContainer<DisplayReport>
{
    /**
     * Create a new ReportPagedDataContainer instance.
     *
     * @param inReports a <code>Collection&lt;? extends DisplayReport&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ReportPagedDataContainer(Collection<? extends DisplayReport> inReports,
                                    PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayReport.class,
              inReports,
              inPagedViewProvider);
    }
    /**
     * Create a new ReportPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ReportPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayReport.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<DisplayReport> getDataContainerContents(PageRequest inPageRequest)
    {
        CollectionPageResponse<Report> actualReports = TradeClientService.getInstance().getReports(inPageRequest);
        CollectionPageResponse<DisplayReport> displayReports = new CollectionPageResponse<>(actualReports);
        actualReports.getElements().forEach(report -> displayReports.getElements().add(new DisplayReport(report)));
        return displayReports;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DisplayReport inO1,
                                   DisplayReport inO2)
    {
        // include values here that are likely to change (hint, they probably won't, just be added)
        return new EqualsBuilder().append(inO1.getBrokerId(),inO2.getBrokerId())
                .append(inO1.getMsgSeqNum(),inO2.getMsgSeqNum()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Reports";
    }
    private static final long serialVersionUID = -1291084598278519365L;
}
