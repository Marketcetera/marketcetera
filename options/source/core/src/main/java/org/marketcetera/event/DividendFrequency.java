package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Indicates the frequency of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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