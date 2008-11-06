package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Base implementation of {@link IMarketDataFeedFactory}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class AbstractMarketDataFeedFactory<F extends MarketDataFeed<? extends MarketDataFeedToken,C>,
                                                    C extends MarketDataFeedCredentials>
        implements IMarketDataFeedFactory<F,C>
{
}
