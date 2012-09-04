package org.marketcetera.core.marketdata;

import java.util.List;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketdataClient<RequestType,CredentialsType extends MarketDataFeedCredentials>
{
    /**
     * 
     *
     *
     * @param inData
     * @return a <code>List&lt;String&gt;</code> value
     */
    public List<String> doMarketDataRequest(RequestType inData);
    /**
     * 
     *
     *
     * @return a <code>boolean</code> value
     */
    public boolean isLoggedIn();
    /**
     * 
     *
     *
     * @param inCredentials
     * @return a <code>boolean</code> value
     */
    public boolean doLogin(CredentialsType inCredentials);
    /**
     * 
     *
     *
     */
    public void doLogout();
    /**
     * 
     *
     *
     * @param inHandle
     */
    public void doCancel(String inHandle);
    /**
     * 
     *
     *
     * @param inServices
     */
    public void setFeedServices(FeedServices inServices);
}
