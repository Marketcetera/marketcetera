package org.marketcetera.marketdata.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the status of a market data provider feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FeedStatus.java 16325 2012-10-25 23:13:12Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum ProviderStatus
{
    /**
     * Feed is off-line -it may be started to be made available
     */
    OFFLINE,
    /**
     * Feed is off-line due to a problem - it may be restarted, but the start may not succeed 
     */
    ERROR,
    /**
     * Feed is available for requests
     */
    AVAILABLE,
    /**
     * Status of the feed is unknown
     */
    UNKNOWN;
}
