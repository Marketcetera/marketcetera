package org.marketcetera.web.trade.report.view;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Report;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>Report</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReportPagedDataContainer
        extends PagedDataContainer<Report>
{
    /**
     * Create a new ReportPagedDataContainer instance.
     *
     * @param inReports a <code>Collection&lt;? extends Report&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ReportPagedDataContainer(Collection<? extends Report> inReports,
                                    PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Report.class,
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
        super(Report.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<Report> getDataContainerContents(PageRequest inPageRequest)
    {
        return TradeClientService.getInstance().getReports(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(Report inO1,
                                   Report inO2)
    {
        return new EqualsBuilder()
                .append(inO1.getMsgSeqNum(),inO2.getMsgSeqNum())
                .append(inO1.getActor(),inO2.getActor())
                .append(inO1.getActorID(),inO2.getActorID())
                .append(inO1.getBrokerID(),inO2.getBrokerID())
                .append(inO1.getFixMessage(),inO2.getFixMessage())
                .append(inO1.getHierarchy(),inO2.getHierarchy())
                .append(inO1.getOrderID(),inO2.getOrderID())
                .append(inO1.getOriginator(),inO2.getOriginator())
                .append(inO1.getReportID(),inO2.getReportID())
                .append(inO1.getReportType(),inO2.getReportType())
                .append(inO1.getSendingTime(),inO2.getSendingTime())
                .append(inO1.getSessionId(),inO2.getSessionId())
                .append(inO1.getText(),inO2.getText())
                .append(inO1.getTransactTime(),inO2.getTransactTime())
                .append(inO1.getViewer(),inO2.getViewer())
                .append(inO1.getViewerID(),inO2.getViewerID()).isEquals();
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
