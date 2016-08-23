package org.marketcetera.tradingclient;

import java.util.List;

import org.marketcetera.trade.ExecutionReport;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingClient
{
    List<ExecutionReport> getOpenOrders(int inPageNumber,
                                        int inPageSize);
}
