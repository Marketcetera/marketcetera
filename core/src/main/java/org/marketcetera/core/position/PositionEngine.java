package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/**
 * A PositionEngine manages a set of positions uniquely keyed by symbol, account, and trader.
 * 
 * It can provide the data in a flat tabular form or a hierarchical form that groups positions by
 * key elements.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionEngine {

    /**
     * Base interface for position data. This is not intended to be implemented directly.
     */
    @ClassVersion("$Id$")
    public interface PositionData {

        /**
         * Returns a dynamic view of the position data.
         * 
         * @return the dynamic list of positions
         */
        EventList<PositionRow> getPositions();

        /**
         * Notifies the position engine that this data is no longer needed so that resources may be
         * released.
         */
        void dispose();
    }

    /**
     * Returns position data in flat, tabular form. Clients must call
     * {@link PositionData#dispose()} when they are finished using the data.
     * 
     * @return the position data
     */
    PositionData getFlatData();

    /**
     * Returns position data grouped according to the supplied parameters. The order of
     * {@link Grouping} parameters determines the order of grouping of the data. For example,
     * <code>getGroupedData(Grouping.Symbol, Grouping.Account)</code> will return data that is first
     * grouped by symbol, then subdivided by account.
     * <p>
     * Clients must call {@link PositionData#dispose()} when they are finished using the data.
     * 
     * @param groupings
     *            the position key elements to be used in the grouping
     * 
     * @return the position data
     */
    PositionData getGroupedData(Grouping... groupings);

    /**
     * This method releases all resources held by the engine. After it is called, the engine can no
     * longer be used.
     */
    void dispose();

}
