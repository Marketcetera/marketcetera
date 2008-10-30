package org.marketcetera.marketdata.marketcetera;

import javax.management.MXBean;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.module.DisplayName;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@MXBean(true)
@DisplayName("Management Interface for Marketcetera Marketdata Feed")
public interface MarketceteraFeedMXBean
    extends AbstractMarketDataModuleMXBean
{
    @DisplayName("The URL for the Marketcetera Exchange Server")
    public String getURL();
    @DisplayName("The URL for the Marketcetera Exchange Server")
    public void setURL(@DisplayName("The URL for the Marketcetera Exchange Server")
                       String inURL);
    @DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
    public String getSenderCompID();
    @DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
    public void setSenderCompID(@DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
                                String inSenderCompID);
    @DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
    public String getTargetCompID();
    @DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
    public void setTargetCompID(@DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
                                String inTargetCompID);
}
