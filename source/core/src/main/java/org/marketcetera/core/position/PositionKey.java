package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The tuple that identifies a unique position.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionKey {
    
    /**
     * Returns the symbol of the key.
     * 
     * @return the symbol of the key, should never be null
     */
    String getSymbol();

    /**
     * Returns the account of the key.
     * 
     * @return the account of the key, null if unknown
     */
    String getAccount();

    /**
     * Returns the trader id of the key.
     * 
     * @return the trader id of the key, null if unknown
     */
    String getTraderId();

}
