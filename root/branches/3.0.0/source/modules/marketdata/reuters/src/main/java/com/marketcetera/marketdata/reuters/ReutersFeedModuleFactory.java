package com.marketcetera.marketdata.reuters;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedModuleFactory.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedModuleFactory.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new ReutersFeedModuleFactory instance.
     */
    public ReutersFeedModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        synchronized(this) {
            if(feed == null) {
                feed = new ReutersFeedFactory().getMarketDataFeed();
                ReutersMarketdataClient client = new ReutersMarketdataClient();
                client.setFeedServices(feed);
                feed.setClient(client);
            }
        }
        return new ReutersFeedModule(feed);
    }
    /**
     * 
     */
    private ReutersFeed feed;
    /**
     * 
     */
    public static final String IDENTIFIER = "reuters";  //$NON-NLS-1$
    /**
     * unique provider URN for the reuters feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the reuters feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
