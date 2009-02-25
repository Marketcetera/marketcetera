package org.marketcetera.core.position;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/**
 * A PositionEngine manages a set of positions uniquely keyed by symbol,
 * account, and trader.
 * 
 * It can provide the data in a flat tabular form or a hierarchical form that
 * groups positions by key elements.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionEngine {

    /**
     * Base interface for position data. This is not intended to be implemented
     * directly.
     */
    @ClassVersion("$Id$")
    public interface PositionDataBase {

        /**
         * Notifies the position engine that this data is no longer needed so
         * that resources may be released.
         */
        void dispose();
    }

    /**
     * Flat position data.
     * 
     * @see PositionEngine#getFlatData()
     */
    @ClassVersion("$Id$")
    public interface FlatPositionData extends PositionDataBase {

        /**
         * Returns a dynamic view of the flat position data. Each element in the
         * list has a unique tuple of symbol, account, and trader.
         * 
         * @return the dynamic list of positions
         */
        EventList<PositionRow> getPositions();
    }

    /**
     * Grouped position data.
     * 
     * @see PositionEngine#getGroupedData(Grouping...)
     */
    @ClassVersion("$Id$")
    public interface GroupedPositionData extends PositionDataBase {

        /**
         * Returns a dynamic view of grouped position data. Elements in the list
         * are either actual positions or a summary of a group of positions.
         * {@link SummaryPositionRow} elements have children, which creates a
         * tree-like structure.
         * 
         * @return the dynamic list of positions
         */
        EventList<PositionRowBase> getPositions();
    }

    /**
     * Returns position data in flat, tabular form. Clients must call
     * {@link PositionDataBase#dispose()} when they are finished using the data.
     * 
     * @return the position data
     */
    FlatPositionData getFlatData();

    /**
     * Returns position data grouped according to the supplied parameters. The
     * order of {@link Grouping} parameters determines the order of grouping of
     * the data. For example,
     * <code>getGroupedData(Grouping.Symbol, Grouping.Account)</code> will
     * return data that is first grouped by symbol, then subdivided by account.
     * <p>
     * Clients must call {@link PositionDataBase#dispose()} when they are
     * finished using the data.
     * 
     * @param groupings
     *            the position key elements to be used in the grouping
     * 
     * @return the position data
     */
    GroupedPositionData getGroupedData(Grouping... groupings);

}
