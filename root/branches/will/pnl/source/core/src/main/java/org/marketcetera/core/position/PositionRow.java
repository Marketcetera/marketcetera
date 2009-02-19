package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a unique position for a symbol, account, trader tuple.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionRow extends PositionRowBase {

    /**
     * Returns the symbol being held in this position.
     * 
     * @return the symbol of the position, should never be null
     */
    String getSymbol();

    /**
     * Returns the account to which the position is applied.
     * 
     * @return the account of the position, null if unknown
     */
    String getAccount();

    /**
     * Returns the trader id of the position.
     * 
     * @return the trader id of the position, null if unknown
     */
    String getTraderId();
}
