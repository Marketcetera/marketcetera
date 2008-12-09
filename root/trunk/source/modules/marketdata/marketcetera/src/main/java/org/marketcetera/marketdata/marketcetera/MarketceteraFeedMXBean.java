package org.marketcetera.marketdata.marketcetera;

import javax.management.MXBean;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.module.DisplayName;

/* $License$ */

/**
 * Defines the set of attributes and operations available from the {@link MarketceteraFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@MXBean(true)
@DisplayName("Management Interface for Marketcetera Marketdata Feed")
public interface MarketceteraFeedMXBean
    extends AbstractMarketDataModuleMXBean
{
    /**
     * Returns the URL that describes the location of the Marketcetera Exchange server.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The URL for the Marketcetera Exchange Server")
    public String getURL();
    /**
     * Sets the URL that describes the location of the Marketcetera Exchange server.
     *
     * @param inURL a <code>String</code> value
     */
    @DisplayName("The URL for the Marketcetera Exchange Server")
    public void setURL(@DisplayName("The URL for the Marketcetera Exchange Server")
                       String inURL);
    /**
     * Gets the Sender Comp ID that is used to authenticate to the Marketcetera Exchange server.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
    public String getSenderCompID();
    /**
     * Sets the Sender Comp ID that is used to authenticate to the Marketcetera Exchange server.
     *
     * @param inSenderCompID a <code>String</code> value
     */
    @DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
    public void setSenderCompID(@DisplayName("The Sender Comp ID for the Marketcetera Exchange Server")
                                String inSenderCompID);
    /**
     * Gets the Target Comp ID that is used to authenticate to the Marketcetera Exchange server.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
    public String getTargetCompID();
    /**
     * Sets the Target Comp ID that is used to authenticate to the Marketcetera Exchange server.
     *
     * @param inTargetCompID a <code>String</code> value
     */
    @DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
    public void setTargetCompID(@DisplayName("The Target Comp ID for the Marketcetera Exchange Server")
                                String inTargetCompID);
}
