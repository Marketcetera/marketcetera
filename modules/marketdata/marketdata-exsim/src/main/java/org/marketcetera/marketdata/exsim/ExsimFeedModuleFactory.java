package org.marketcetera.marketdata.exsim;

import javax.annotation.PostConstruct;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for the <code>ExsimFeed</code> market data provider.
 * <p>
 * The factory has the following characteristics.
 * <table>
 * <caption>ExsimFeedModuleFactory characteristics</caption>
 * <tr><th>Provider URN:</th><td><code>metc:mdata:exsim</code></td></tr>
 * <tr><th>Cardinality:</th><td>Singleton</td></tr>
 * <tr><th>Instance URN:</th><td><code>metc:mdata:exsim:single</code></td></tr>
 * <tr><th>Auto-Instantiated:</th><td>No</td></tr>
 * <tr><th>Auto-Started:</th><td>No</td></tr>
 * <tr><th>Instantiation Arguments:</th><td>None</td></tr>
 * <tr><th>Module Type:</th><td>{@link ExsimFeedModule}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ExsimFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new ExsimFeedModuleFactory instance.
     */
    public ExsimFeedModuleFactory()
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
        return new ExsimFeedModule(INSTANCE_URN);
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
    public static final String IDENTIFIER = "exsim";  //$NON-NLS-1$
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
