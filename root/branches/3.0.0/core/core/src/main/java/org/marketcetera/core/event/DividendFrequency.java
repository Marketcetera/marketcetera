package org.marketcetera.core.event;

import org.marketcetera.api.attributes.ClassVersion;

/**
 * Indicates the frequency of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DividendFrequency.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DividendFrequency.java 16063 2012-01-31 18:21:55Z colin $")
public enum DividendFrequency
{
    /**
     * once per year
     */
    ANNUALLY,
    /**
     * once per month
     */
    MONTHLY,
    /**
     * once per quarter
     */
    QUARTERLY,
    /**
     * once every six months
     */
    SEMI_ANNUALLY,
    /**
     * an unknown frequency
     */
    OTHER
}