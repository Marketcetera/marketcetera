package org.marketcetera.marketdata.yahoo;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link YahooClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface YahooClientFactory
{
    /**
     * Constructs a <code>YahooClient</code> object. 
     *
     * @return a <code>YahooClient</code> value
     */
    YahooClient getClient(YahooFeedServices inFeedServices);
}
