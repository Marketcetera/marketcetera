package org.marketcetera.systemmodel;

import java.util.List;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public interface ExecutionReportQuery
{
    /**
     * Returns the <code>ExecutionReport</code> objects related to the given <code>OrderID</code>.
     * 
     * <p>The objects returned are sorted in ascending order of execution date.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return
     */
    public List<ExecutionReport> getExecutionReports(OrderID inOrderID);
}
