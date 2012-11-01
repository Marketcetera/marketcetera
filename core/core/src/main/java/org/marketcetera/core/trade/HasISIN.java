package org.marketcetera.core.trade;

/* $License$ */

/**
 * Has an International Securities Identification Number (ISIN).
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasISIN
{
    /**
     * Gets the International Securities Identification Number (ISIN) for this instrument.
     *
     * @return a <code>String</code> value
     */
    public String getISIN();
}
