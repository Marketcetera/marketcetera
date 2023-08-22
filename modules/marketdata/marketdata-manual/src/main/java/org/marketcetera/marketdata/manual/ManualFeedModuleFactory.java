package org.marketcetera.marketdata.manual;

import javax.annotation.PostConstruct;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for the <code>ManualFeed</code> market data provider.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <caption>Manual Feed Module Capabilities</caption>
 * <tr><th>Provider URN:</th><td><code>metc:mdata:manual</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:mdata:manual:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type:</th><td>{@link ManualFeedModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ManualFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new ManualFeedModuleFactory instance.
     */
    public ManualFeedModuleFactory()
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
    public ManualFeedModule create(Object... inParameters)
            throws ModuleCreationException
    {
        return new ManualFeedModule(INSTANCE_URN);
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
     * market data provider identifier
     */
    public static final String IDENTIFIER = "manual";  //$NON-NLS-1$
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN value
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
