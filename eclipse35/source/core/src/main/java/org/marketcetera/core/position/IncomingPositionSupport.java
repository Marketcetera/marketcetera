package org.marketcetera.core.position;

import java.math.BigDecimal;
import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for providing incoming position data to a {@link PositionEngine}. The incoming position
 * is used to calculate {@link PositionMetrics#getPositionPL() position PL}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface IncomingPositionSupport {

    /**
     * Returns the size of the incoming position for the given symbol, account, traderId tuple.
     * 
     * The returned value should be the size of the incoming position at the time the method is
     * called. Implementations are assumed to have an understanding of the time period that defines
     * the incoming position.
     * 
     * @param key
     *            the position key tuple
     * @return the size of the incoming position, cannot be null
     */
    BigDecimal getIncomingPositionFor(PositionKey key);
    
    /**
     * Returns all incoming positions, keyed by symbol, account, traderId tuple.
     * 
     * The returned values should be the size of the incoming positions at the time the method is
     * called. Implementations are assumed to have an understanding of the time period that defines
     * the incoming position.
     * 
     * @return the incoming positions, cannot be null
     */
    Map<? extends PositionKey, BigDecimal> getIncomingPositions();
}
