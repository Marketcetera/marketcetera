package com.marketcetera.marketdata.reuters;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.marketdata.FeedServices;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketdataClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.reuters.sfc.Session;
import com.reuters.sfc.SessionClient;
import com.reuters.sfc.SessionException;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersMarketdataClient.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id: ReutersMarketdataClient.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersMarketdataClient
        implements MarketdataClient<List<ReutersRequest>,ReutersFeedCredentials>, SessionClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#doMarketDataRequest(java.lang.Object)
     */
    @Override
    public List<String> doMarketDataRequest(List<ReutersRequest> inData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#isLoggedIn()
     */
    @Override
    public boolean isLoggedIn()
    {
        return session != null &&
               session.isConnected();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#doLogin(org.marketcetera.marketdata.MarketDataFeedCredentials)
     */
    @Override
    public boolean doLogin(ReutersFeedCredentials inCredentials)
    {
        try {
            session = Session.create(inCredentials);
            session.addClient(this);
        } catch (SessionException e) {
            SLF4JLoggerProxy.warn(ReutersMarketdataClient.class,
                                  e,
                                  "Error creating session");
            return false;
        }
        return session.isConnected();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#doLogout()
     */
    @Override
    public void doLogout()
    {
        if(session != null &&
           session.isConnected()) {
            Session.release(session);
            session = null;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#doCancel(java.lang.String)
     */
    @Override
    public void doCancel(String inHandle)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketdataClient#setFeedServices(org.marketcetera.marketdata.FeedServices)
     */
    @Override
    public void setFeedServices(FeedServices inServices)
    {
        feedServices = inServices;
    }
    /* (non-Javadoc)
     * @see com.reuters.sfc.SessionClient#processSessionDown(com.reuters.sfc.Session, java.lang.String, java.lang.String)
     */
    @Override
    public void processSessionDown(Session inSession,
                                   String inLocation,
                                   String inReason)
    {
        SLF4JLoggerProxy.warn(ReutersMarketdataClient.class,
                              "{} at {} is down because {}",
                              inSession,
                              inLocation,
                              inReason);
        // TODO release session and/or set to null?
        feedServices.setFeedStatus(FeedStatus.OFFLINE);
    }
    /* (non-Javadoc)
     * @see com.reuters.sfc.SessionClient#processSessionInfo(com.reuters.sfc.Session, java.lang.String)
     */
    @Override
    public void processSessionInfo(Session inSession,
                                   String inText)
    {
        SLF4JLoggerProxy.info(ReutersMarketdataClient.class,
                              "{} update: {}",
                              inSession,
                              inText);
    }
    /* (non-Javadoc)
     * @see com.reuters.sfc.SessionClient#processSessionUp(com.reuters.sfc.Session, java.lang.String)
     */
    @Override
    public void processSessionUp(Session inSession,
                                 String inLocation)
    {
        SLF4JLoggerProxy.info(ReutersMarketdataClient.class,
                              "{} at {} is up",
                              inSession,
                              inLocation);
    }
    /**
     * Reuters session
     */
    private Session session;
    /**
     * provides services of the feed infrastructure
     */
    private FeedServices feedServices;
}
