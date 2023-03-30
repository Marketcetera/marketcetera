package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Module for {@link BogusFeed}.
 * <p>
 * Module Features
 * <table summary="Describes the module attributes">
 * <tr><th>Factory:</th><td>{@link BogusFeedModuleFactory}</td></tr>
 * <tr><th colspan="2">See {@link AbstractMarketDataModule parent} for module features.</th></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@AutowiredModule
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class BogusFeedModule
        extends AbstractMarketDataModule<BogusFeedToken,
                                         BogusFeedCredentials>
{
    /**
     * Create a new BogusFeedModule instance.
     *
     * @throws CoreException if the module could not be created
     */
    BogusFeedModule()
            throws CoreException
    {
        super(BogusFeedModuleFactory.INSTANCE_URN,
              BogusFeedFactory.getInstance().getMarketDataFeed());
        getFeed().addFeedComponentListener(new IFeedComponentListener() {
            @Override
            public void feedComponentChanged(IFeedComponent inComponent)
            {
                SLF4JLoggerProxy.warn(BogusFeedModule.this,
                                      "COCO: received feed commponent: {}",
                                      inComponent.getFeedStatus());
                marketDataService.reportMarketDataStatus(new MarketDataStatus() {
                    @Override
                    public FeedStatus getFeedStatus()
                    {
                        return inComponent.getFeedStatus();
                    }
                    @Override
                    public String getProvider()
                    {
                        return BogusFeedModuleFactory.IDENTIFIER;
                    }}
                );
            }}
        );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected BogusFeedCredentials getCredentials()
            throws CoreException
    {
        return BogusFeedCredentials.getInstance();
    }
    /**
     * provides market data services
     */
    @Autowired
    private MarketDataService marketDataService;
}
