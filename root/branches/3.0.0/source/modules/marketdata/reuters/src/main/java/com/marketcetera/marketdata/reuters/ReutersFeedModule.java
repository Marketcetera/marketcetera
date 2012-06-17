package com.marketcetera.marketdata.reuters;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * StrategyAgent module for {@link ReutersFeed}.
 * 
 * <p>Module Features
 * <table>
 * <tr><th>Factory:</th><td>{@link ReutersFeedModuleFactory}</td></tr>
 * <tr><th colspan="2">See {@link AbstractMarketDataModule parent} for module features.</th></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedModule.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedModule.java 82351 2012-05-04 21:46:58Z colin $")
public class ReutersFeedModule
        extends AbstractMarketDataModule<ReutersFeedToken,ReutersFeedCredentials>
        implements ReutersFeedMXBean
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ReutersFeedModule";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#setServerType(java.lang.String)
     */
    @Override
    public void setServerType(String inServerType)
    {
        serverType = StringUtils.trimToNull(inServerType);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#getServerType()
     */
    @Override
    public String getServerType()
    {
        return serverType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#setServerList(java.lang.String)
     */
    @Override
    public void setServerList(String inServerList)
    {
        serverList = StringUtils.trimToNull(inServerList);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#getServerList()
     */
    @Override
    public String getServerList()
    {
        return serverList;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#setPortNumber(java.lang.String)
     */
    @Override
    public void setPortNumber(String inPortNumberValue)
    {
        portNumber = Integer.parseInt(StringUtils.trimToNull(inPortNumberValue));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#getPortNumber()
     */
    @Override
    public String getPortNumber()
    {
        return String.valueOf(portNumber);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String inUsername)
    {
        username = StringUtils.trimToNull(inUsername);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.reuters.ReutersFeedMXBean#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /**
     * Create a new ReutersFeedModule instance.
     * 
     * @param inFeed a <code>ReutersFeed</code> value 
     */
    ReutersFeedModule(ReutersFeed inFeed)
    {
        super(ReutersFeedModuleFactory.INSTANCE_URN,
              inFeed);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected ReutersFeedCredentials getCredentials()
    {
        return new ReutersFeedCredentials(serverType,
                                          serverList,
                                          portNumber,
                                          username);
    }
    /**
     * server type value
     */
    private volatile String serverType = "sapi";
    /**
     * server list value
     */
    private volatile String serverList = "localhost";
    /**
     * port number value
     */
    private volatile int portNumber = 8101;
    /**
     * username value
     */
    private volatile String username;
}
