package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.ReportBase;

/**
 * Visitor Patter - visit all the messages and perfrom an operation on them
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public interface MessageVisitor {

    /** Visits each Execution Report that we have gathered for all the
     * outstanding open orders
     * @param message
     */
    public void visitOpenOrderExecutionReports(ReportBase report);

}
