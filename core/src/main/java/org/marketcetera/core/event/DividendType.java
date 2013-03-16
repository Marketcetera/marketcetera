package org.marketcetera.core.event;

/**
 * Indicates the type of a dividend.
 *
 * @version $Id$
 * @since 2.0.0
 */
public enum DividendType
{
    /**
     * most recent {@link DividendStatus#OFFICIAL} dividend
     */
    CURRENT,
    /**
     * special, not-scheduled dividend
     */
    SPECIAL,
    /**
     * planned {@link DividendStatus#OFFICIAL} or {@link DividendStatus#UNOFFICIAL} dividend
     */
    FUTURE,
    /**
     * unknown dividend type
     */
    UNKNOWN
}