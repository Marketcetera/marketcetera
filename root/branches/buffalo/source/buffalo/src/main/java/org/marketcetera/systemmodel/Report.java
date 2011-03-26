package org.marketcetera.systemmodel;

import java.util.Date;

import org.marketcetera.ors.history.ReportType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Report
{
    /**
     * 
     *
     *
     * @return
     */
    public OrderDestinationID getDestinationID();
    /**
     * 
     *
     *
     * @return
     */
    public String getRawMessage();
    /**
     * 
     *
     *
     * @return
     */
    public ReportType getReportType();
    /**
     * 
     *
     *
     * @return
     */
    public Date getSendingTime();
    /**
     * 
     *
     *
     * @return
     */
    public OrderID getOrderID();
    /**
     * 
     *
     *
     * @return
     */
    public User getOwner();
    /**
     * 
     *
     *
     * @return
     */
    public ReportSummary getSummary();
}
