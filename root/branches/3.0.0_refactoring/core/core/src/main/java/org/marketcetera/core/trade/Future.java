package org.marketcetera.core.trade;

import quickfix.field.MaturityMonthYear;

/* $License$ */

/**
 * Identifies a future contract.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Future
        extends Instrument
{
    /**
     * Get the expirationMonth value.
     *
     * @return a <code>FutureExpirationMonth</code> value
     */
    public FutureExpirationMonth getExpirationMonth();
    /**
     * Get the expirationYear value.
     *
     * @return an <code>int</code> value
     */
    public int getExpirationYear();
    /**
     * Get the expirationDay value.
     *
     * @return an <code>int</code> value containing either the expiration day or <code>-1</code> if the expiration
     *  day is not specified
     */
    public int getExpirationDay();
    /**
     * Gets the future maturity as a <code>MaturityMonthYear</code> value.
     *
     * @return a <code>MaturityMonthYear</code> value
     */
    public MaturityMonthYear getExpiryAsMaturityMonthYear();
    /**
     * Gets the instrument expiry as a <code>String</code> in the format <code>YYYYMM</code> or <code>YYYYMMDD</code> as appropriate.
     *
     * @return a <code>String</code> value
     */
    public String getExpiryAsString();
}
