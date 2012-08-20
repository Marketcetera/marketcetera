package org.marketcetera.marketdata.yahoo;

/* $License$ */

/**
 * Services provided by the Yahoo feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooFeedServices.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
interface YahooFeedServices
{
    /**
     * Indicates receipt of market data.
     *
     * @param inHandle a <code>String</code> value
     * @param inData an <code>Object</code> value
     */
    void doDataReceived(String inHandle,
                        Object inData);
    /**
     * Gets the interval at which to refresh market data requests.
     *
     * @return an <code>int</code> value in ms
     */
    int getRefreshInterval();
}
