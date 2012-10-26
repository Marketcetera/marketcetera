package org.marketcetera.api.systemmodel.instruments;

import java.io.Serializable;

/* $License$ */

/**
 * Represents the common attributes of an <code>Instrument</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Instrument
        extends Serializable
{
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol();
    /**
     * Gets the full symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getFullSymbol();
    /**
     * Gets the security type value.
     *
     * @return a <code>SecurityType</code> value
     */
    public SecurityType getSecurityType();
}
