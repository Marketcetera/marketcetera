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
 * @version $Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $
 */
@MXBean(true)
@DisplayName("Management Interface for CSV Market Data Adapter")
@ClassVersion("$Id: CSVFeedMXBean.java 4348 2009-09-24 02:33:11Z toli $")
public interface CSVFeedMXBean
        extends AbstractMarketDataModuleMXBean
{
    /**
     * 
     *
     *
     * @return
     */
    @DisplayName("Delay in milliseconds")
    public long getDelay();
    /**
     * 
     *
     *
     * @param inDelay
     */
    @DisplayName("Delay in milliseconds")
    public void setDelay(long inDelay);
    /**
     * 
     */
    @DisplayName("The fully-qualified class name of the CSV event translator to use")
    public String getEventTranslatorClassName();
    /**
     * 
     *
     *
     * @param inEventTranslatorClassname
     */
    @DisplayName("The fully-qualified class name of the CSV event translator to use")
    public void setEventTranslatorClassName(@DisplayName("The fully-qualified class name of the CSV event translator to use")String inEventTranslatorClassname);
}