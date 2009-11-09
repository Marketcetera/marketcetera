package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Indicates the type of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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