package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;

import javax.management.MXBean;

/**
 * Exposes {@link CSVFeedCredentials} attributes.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $
 */
@MXBean(true)
@DisplayName("Management Interface for CSV Market Data Adapter")
@ClassVersion("$Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $")
public interface CSVFeedMXBean
        extends AbstractMarketDataModuleMXBean
{
    /**
     * Gets the rate at which data is replayed.
     *
     * @return a <code>double</code> value
     */
    @DisplayName("The rate at which data is replayed")
    public double getReplayRate();
    /**
     * Sets the rate at which data is replayed.
     *
     * @param inReplayRate a <code>double</code> value
     */
    @DisplayName("The rate at which data is replayed")
    public void setReplayRate(double inReplayRate);
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
    /**
     * Gets the fully-qualified class name of the CSV event translator.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("The fully-qualified class name of the CSV event translator to use")
    public String getEventTranslatorClassName();
    /**
     * Sets the fully-qualified class name of the CSV event translator.
     *
     * @param inEventTranslatorClassname a <code>String</code> value
     */
    @DisplayName("The fully-qualified class name of the CSV event translator to use")
    public void setEventTranslatorClassName(@DisplayName("The fully-qualified class name of the CSV event translator to use")String inEventTranslatorClassname);
}