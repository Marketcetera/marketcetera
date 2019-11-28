package org.marketcetera.web.trade.executionreport.view;

import java.util.Properties;

import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.ExecutionTypeConverter;
import org.marketcetera.web.converters.OrderStatusConverter;
import org.marketcetera.web.converters.OrderTypeConverter;
import org.marketcetera.web.converters.SecurityTypeConverter;
import org.marketcetera.web.converters.SideConverter;
import org.marketcetera.web.converters.TimeInForceConverter;
import org.marketcetera.web.converters.UserConverter;

/* $License$ */

/**
 * Provides common behavior for views that display {@link DisplayExecutionReportSummary} values in a grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportView
        extends AbstractHasFixMessageView<DisplayExecutionReportSummary>
{
    /**
     * Create a new FillsView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractExecutionReportView(Properties inViewProperties)
    {
        super(inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("sendingTime",
                             "transactTime",
                             "rootOrderID",
                             "orderID",
                             "orderStatus",
                             "executionType",
                             "side",
                             "securityType",
                             "symbol",
                             "expiry",
                             "optionType",
                             "strikePrice",
                             "orderType",
                             "timeInForce",
                             "orderQuantity",
                             "cumulativeQuantity",
                             "leavesQuantity",
                             "price",
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
                             "brokerID",
                             "executionId",
                             "brokerOrderId",
                             "actor");
        getGrid().getColumn("actor").setHeaderCaption("Trader").setConverter(UserConverter.instance);
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
        getGrid().getColumn("brokerOrderId").setHeaderCaption("Broker Order ID");
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
        getGrid().getColumn("executionId").setHeaderCaption("Exec ID");
        getGrid().getColumn("executionType").setConverter(ExecutionTypeConverter.instance).setHeaderCaption("Exec Type");
        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty");
        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty");
        getGrid().getColumn("orderStatus").setConverter(OrderStatusConverter.instance);
        getGrid().getColumn("orderType").setHeaderCaption("Ord Type").setConverter(OrderTypeConverter.instance);
        getGrid().getColumn("price").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px");
        getGrid().getColumn("securityType").setConverter(SecurityTypeConverter.instance);
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("side").setConverter(SideConverter.instance);
        getGrid().getColumn("timeInForce").setConverter(TimeInForceConverter.instance);
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
//        getGrid().getColumn("instrument").setConverter(InstrumentConverter.instance);
    }
    private static final long serialVersionUID = -3203095665399884857L;
}
