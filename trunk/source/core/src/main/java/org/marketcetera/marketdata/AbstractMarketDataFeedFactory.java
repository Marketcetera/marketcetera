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
@ClassVersion("$Id$")
public abstract class AbstractMarketDataFeedFactory<F extends IMarketDataFeed<? extends IMarketDataFeedToken<C>,C>,
                                                    C extends IMarketDataFeedCredentials>
        implements IMarketDataFeedFactory<F,C>
{
}
