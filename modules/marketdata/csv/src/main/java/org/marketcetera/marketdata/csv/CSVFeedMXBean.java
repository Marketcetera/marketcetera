package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;
import org.marketcetera.marketdata.MarketDataModuleMXBean;

import javax.management.MXBean;

/**
 * Exposes {@link CSVFeedConfiguration} attributes.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@MXBean(true)
@DisplayName("Management Interface for CSV Market Data Adapter")
@ClassVersion("$Id$")
public interface CSVFeedMXBean
        extends MarketDataModuleMXBean
{
    /**
     * Gets the rate at which data is replayed.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The rate at which data is replayed")
    public String getReplayRate();
    /**
     * Sets the rate at which data is replayed.
     *
     * @param inReplayRate a <code>String</code> value
     */
    @DisplayName("The rate at which data is replayed")
    public void setReplayRate(String inReplayRate);
    /**
     * Gets the name of the directory in which to find market data.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The absolute path in which to find market data")
    public String getMarketdataDirectory();
    /**
     * Sets the name of the directory in which to find market data. 
     *
     * @param inDirectory a <code>String</code> value
     */
    @DisplayName("The absolute path in which to find market data")
    public void setMarketdataDirectory(@DisplayName("The absolute path in which to find market data")String inDirectory);
}
