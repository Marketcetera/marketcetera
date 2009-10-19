package org.marketcetera.event;

/**
 * Indicates the frequency of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum DividendFrequency
{
    /**
     * once per year
     */
    ANNUALY,
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