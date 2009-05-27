package org.marketcetera.core.position;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * Represents a row of position data. A PositionRow can either represent a unique position (for
 * symbol, account, trader tuple) or a summary of multiple positions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface PositionRow {

    /**
     * Returns the symbol being held in this position.
     * 
     * If this position row is a summary and symbol is not part of the grouping, the value is
     * undefined.
     * 
     * @return the symbol of the position
     */
    String getSymbol();

    /**
     * Returns the account to which the position is applied.
     * 
     * If this position row is a summary and account is not part of the grouping, the value is
     * undefined.
     * 
     * @return the account of the position, null if unknown
     */
    String getAccount();

    /**
     * Returns the trader id of the position.
     * 
     * If this position row is a summary and trader is not part of the grouping, the value is
     * undefined.
     * 
     * @return the trader id of the position, null if unknown
     */
    String getTraderId();

    /**
     * Returns the grouping in effect on this position. This property only applies if this is a
     * summary row.
     * 
     * @return the grouping in effect, null if this is not a summary row.
     */
    Grouping[] getGrouping();

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
    
    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
