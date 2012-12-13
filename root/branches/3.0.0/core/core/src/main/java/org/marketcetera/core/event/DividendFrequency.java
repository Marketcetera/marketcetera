package org.marketcetera.core.event;

/**
 * Indicates the frequency of a dividend.
 *
 * @version $Id$
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