package org.marketcetera.ors.dao;

import java.util.List;

import org.marketcetera.ors.history.ExecutionReportSummary;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ExecutionReportDao
        extends JpaRepository<ExecutionReportSummary,Long>,QueryDslPredicateExecutor<ExecutionReportSummary>
{
    /**
     * 
     *
     *
     * @param inOrderID
     * @return
     */
    List<OrderID> findRootIDForOrderID(OrderID inOrderID);
}
