package org.marketcetera.core.event;

import org.marketcetera.core.attributes.ClassVersion;

/**
 * Indicates the type of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DividendType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DividendType.java 16063 2012-01-31 18:21:55Z colin $")
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