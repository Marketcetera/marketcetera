package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * Represents a row of position data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionRow {

    /**
     * Returns the symbol being held in this position.
     * 
     * @return the symbol of the position, null if unknown
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

    /**
     * Returns the grouping value. This property only makes sense if this is a summary row, in which
     * case it will be the value controlling the grouping at the current level. It will be one of
     * {@link #getSymbol()}, {@link #getAccount()}, or {@link #getTraderId()}.
     * 
     * If this is not a summary row, the returned value will always be null.
     * 
     * @return the grouping value
     */
    String getGrouping();

    /**
     * Returns the latest value of the computed position-related metrics for this position.
     * 
     * @return the metrics for this position, should never be null
     */
    PositionMetrics getPositionMetrics();

    /**
     * Returns the child positions this position is summarizing. If it is not a summary row, this
     * returns null.
     * 
     * @return the child positions, or null if this is not a summary row
     */
    EventList<PositionRow> getChildren();
}
