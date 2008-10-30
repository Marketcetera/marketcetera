package org.marketcetera.marketdata;

import javax.management.MXBean;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.module.DisplayName;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@MXBean(true)
@DisplayName("Management Interface for Marketdata Feeds")
public interface AbstractMarketDataModuleMXBean
{
    @DisplayName("The feed status for the market data feed")
    public String getFeedStatus();
}
