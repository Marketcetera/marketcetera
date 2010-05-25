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
 * @since $Release$
 * @version $Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $
 */
@MXBean(true)
@DisplayName("Management Interface for CSV Market Data Adapter")
@ClassVersion("$Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $")
public interface CSVFeedMXBean
        extends AbstractMarketDataModuleMXBean
{
    /**
     * Gets the number of milliseconds to delay between market data events.
     *
     * @return a <code>String</code> value
     */
    @DisplayName("Delay in milliseconds")
    public String getDelay();
    /**
     * Sets the number of milliseconds to delay between market data events.
     *
     * @param inDelay a <code>String</code> value
     */
    @DisplayName("Delay in milliseconds")
    public void setDelay(String inDelay);
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