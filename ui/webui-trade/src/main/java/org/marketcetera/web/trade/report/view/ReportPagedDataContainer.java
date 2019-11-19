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
     * @param inType a <code>Class&lt; ? extends Report&gt;</code> value
     * @param inCollection a <code>Collection&lt;? extends Report&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ReportPagedDataContainer(Collection<? extends Report> inCollection,
                                          PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Report.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new ReportPagedDataContainer instance.
     *
     * @param inType a <code>Class&lt; super Report&gt;</code> value
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
        // include values here that are likely to change (hint, they probably won't, just be added)
        return new EqualsBuilder().append(inO1.getBrokerID(),inO2.getBrokerID())
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
