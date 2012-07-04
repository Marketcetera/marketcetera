package org.marketcetera.core.messagehistory;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.trade.ReportBase;

/**
 * Visitor Patter - visit all the messages and perfrom an operation on them
 * @author toli
 * @version $Id: MessageVisitor.java 82326 2012-04-10 16:27:07Z colin $
 */

@ClassVersion("$Id: MessageVisitor.java 82326 2012-04-10 16:27:07Z colin $")
public interface MessageVisitor {

    /** Visits each Execution Report that we have gathered for all the
     * outstanding open orders
     * @param report a <code>ReportBase</code> value
     */
    public void visitOpenOrderExecutionReports(ReportBase report);

}
