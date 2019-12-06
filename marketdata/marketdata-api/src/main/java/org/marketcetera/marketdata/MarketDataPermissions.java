package org.marketcetera.marketdata;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 * Enumerates permissions for market data actions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MarketDataPermissions
        implements GrantedAuthority
{
    /**
     * permission to request updated market data
     */
    RequestMarketDataAction,
    /**
     * permission to request snapshot market data
     */
    RequestMarketDataSnapshotAction;
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return name();
    }
}
