package org.marketcetera.core.event;

/**
 * Indicates the frequency of a dividend.
 *
 * @version $Id: DividendFrequency.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
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