package org.marketcetera.api.systemmodel;

import java.io.Serializable;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Instrument
        extends Serializable
{
    public String getSymbol();
    public SecurityType getSecurityType();
}
