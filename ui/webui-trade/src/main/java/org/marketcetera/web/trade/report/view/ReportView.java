package org.marketcetera.web.trade.report.view;

import java.util.Properties;

import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.OrderStatusConverter;
import org.marketcetera.web.converters.ReportTypeConverter;
import org.marketcetera.web.converters.StringFixMessageConverter;
import org.marketcetera.web.trade.executionreport.AbstractHasFixMessageView;
import org.marketcetera.web.trade.report.model.DisplayReport;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a view for reports.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportView
        extends AbstractHasFixMessageView<DisplayReport>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new SessionView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    public ReportView(Properties inViewProperties)
    {
        super(inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("transactTime",
                             "sendingTime",
                             "brokerID",
                             "sessionID",
                             "msgSeqNum",
                             "orderID",
                             "orderStatus",
                             "msgType",
                             "reportID",
                             "text",
                             "trader",
                             "fixMessage");
        // hierarchy
        // originator
        getGrid().getColumn("fixMessage").setConverter(StringFixMessageConverter.instance);
        getGrid().getColumn("orderStatus").setConverter(OrderStatusConverter.instance);
        getGrid().getColumn("msgType").setConverter(ReportTypeConverter.instance);
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<DisplayReport> createDataContainer()
    {
        return new DisplayReportPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "FIX Messages";
    }
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Messages";
    private static final long serialVersionUID = 1901286026590258969L;
}
