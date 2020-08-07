package org.marketcetera.web.trade.report.view;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Report;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>DisplayReport</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DisplayReportPagedDataContainer
        extends PagedDataContainer<DisplayReport>
{
    /**
     * Create a new ReportPagedDataContainer instance.
     *
     * @param inReports a <code>Collection&lt;? extends DisplayReport&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public DisplayReportPagedDataContainer(Collection<? extends DisplayReport> inDisplayReports,
                                           PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DisplayReport.class,
              inDisplayReports,
              inPagedViewProvider);
    }
    /**
     * Create a new DisplayReportPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public DisplayReportPagedDataContainer(PagedViewProvider inPagedViewProvider)
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
        CollectionPageResponse<Report> reports = TradeClientService.getInstance().getReports(inPageRequest);
        CollectionPageResponse<DisplayReport> results = new CollectionPageResponse<>(reports);
        reports.getElements().forEach(report->results.getElements().add(new DisplayReport(report)));
        return results;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DisplayReport inO1,
                                   DisplayReport inO2)
    {
        return new EqualsBuilder()
                .append(inO1.getBrokerID(),inO2.getBrokerID())
                .append(inO1.getFixMessage(),inO2.getFixMessage())
                .append(inO1.getHierarchy(),inO2.getHierarchy())
                .append(inO1.getOrderID(),inO2.getOrderID())
                .append(inO1.getOriginator(),inO2.getOriginator())
                .append(inO1.getMsgSeqNum(),inO2.getMsgSeqNum())
                .append(inO1.getMsgType(),inO2.getMsgType())
                .append(inO1.getReportID(),inO2.getReportID())
                .append(inO1.getSendingTime(),inO2.getSendingTime())
                .append(inO1.getSessionID(),inO2.getSessionID())
                .append(inO1.getText(),inO2.getText())
                .append(inO1.getTrader(),inO2.getTrader())
                .append(inO1.getTransactTime(),inO2.getTransactTime()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "FIX Messages";
    }
    private static final long serialVersionUID = -1291084598278519365L;
}
