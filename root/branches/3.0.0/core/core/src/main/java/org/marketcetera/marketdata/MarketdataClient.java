package org.marketcetera.marketdata;

import java.util.List;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketdataClient<RequestType,CredentialsType extends MarketDataFeedCredentials>
{
    /**
     * 
     *
     *
     * @param inData
     * @return
     */
    public List<String> doMarketDataRequest(RequestType inData);
    /**
     * 
     *
     *
     * @return
     */
    public boolean isLoggedIn();
    /**
     * 
     *
     *
     * @param inCredentials
     * @return
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
