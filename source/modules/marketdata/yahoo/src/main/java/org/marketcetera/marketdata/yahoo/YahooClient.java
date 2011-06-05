package org.marketcetera.marketdata.yahoo;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides access to the Yahoo data source.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
interface YahooClient
        extends Lifecycle
{
    /**
     * Logs in to the Yahoo data source with the given credentials.
     *
     * @param inCredentials a <code>YahooFeedCredentials</code> value
     * @return a <code>boolean</code> value
     */
    boolean login(YahooFeedCredentials inCredentials);
    /**
     * 
     *
     *
     */
    void logout();
    /**
     * 
     *
     *
     * @return
     */
    boolean isLoggedIn();
    /**
     * 
     *
     *
     * @param inRequest
     */
    void request(YahooRequest inRequest);
    /**
     * 
     *
     *
     * @param inRequest
     */
    void cancel(YahooRequest inRequest);
    /**
     * 
     *
     *
     * @return
     */
    long getRequestCounter();
    /**
     * 
     *
     *
     */
    void resetRequestcounter();
}
