package org.marketcetera.marketdata;

/* $License$ */

/**
 * Enumerates permissions for market data actions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MarketDataPermissions
{
    /**
     * permission to request updated market data
     */
    RequestMarketDataAction,
    /**
     * permission to request snapshot market data
     */
    RequestMarketDataSnapshotAction
}
