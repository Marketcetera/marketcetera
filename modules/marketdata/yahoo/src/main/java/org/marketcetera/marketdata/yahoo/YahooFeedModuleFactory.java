package org.marketcetera.marketdata.yahoo;

import jakarta.annotation.PostConstruct;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Constructs {@link YahooFeedModule} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
public class YahooFeedModuleFactory
        extends ModuleFactory
{

    /**
     * Create a new YahooFeedModuleFactory instance.
     */
    public YahooFeedModuleFactory()
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
    public Module create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new YahooFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        marketDataService.reportMarketDataStatus(new MarketDataStatus() {
            @Override
            public FeedStatus getFeedStatus()
            {
                return FeedStatus.OFFLINE;
            }
            @Override
            public String getProvider()
            {
                return IDENTIFIER;
            }}
        );
    }
    /**
     * providers market data services
     */
    @Autowired
    private MarketDataService marketDataService;
    /**
     * provider name of the module 
     */
    public static final String IDENTIFIER = YahooFeedFactory.PROVIDER_NAME;
    /**
     * provider URN of the yahoo module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN of the yahoo module
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
