package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.PROVIDER_DESCRIPTION;

import jakarta.annotation.PostConstruct;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <code>ModuleFactory</code> implementation for the <code>CSVFeed</code> market data provider.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@Service
@ClassVersion("$Id$")
public class CSVFeedModuleFactory 
        extends ModuleFactory
{
    /**
     * Create a new CSVFeedModuleFactory instance.
     */
    public CSVFeedModuleFactory()
    {
        super(PROVIDER_URN,
              PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public CSVFeedModule create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new CSVFeedModule();
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
     * uniquely identifies this provider
     */
    public static final String IDENTIFIER = "csv";  //$NON-NLS-1$
    /**
     * unique provider URN for the CSV feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the CSV feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
