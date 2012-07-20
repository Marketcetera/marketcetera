package org.marketcetera.messagehistory;

import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Visitor Patter - visit all the messages and perfrom an operation on them
 * @author toli
 * @version $Id: MessageVisitor.java 82384 2012-07-20 19:09:59Z colin $
 */

@ClassVersion("$Id: MessageVisitor.java 82384 2012-07-20 19:09:59Z colin $")
public interface MessageVisitor {

    /** Visits each Execution Report that we have gathered for all the
     * outstanding open orders
     * @param report a <code>ReportBase</code> value
     */
    public void visitOpenOrderExecutionReports(ReportBase report);

}
