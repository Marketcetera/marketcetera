package org.marketcetera.marketdata;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Credentials for establishing a connection to a {@link MarketDataFeed} that requires a URL.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface MarketDataFeedURLCredentials
        extends MarketDataFeedCredentials
{
    /**
     * Returns a URI describing how to connect to a data feed.
     *
     * @return a <code>String</code> value
     */
    public String getURL();
}
