package org.marketcetera.core.position;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base interface for positions. This is not intended to be implemented
 * directly.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionRowBase {

    /**
     * Returns the number of shares at the start of the trading session. A
     * positive value indicates a long position and a negative value indicates a
     * short position.
     * 
     * @return the incoming position, null if unknown
     */
    BigDecimal getIncomingPosition();

    /**
     * Returns the latest value of the computed position-related metrics for
     * this position.
     * 
     * @return the metrics for this position, should never be null
     */
    PositionMetrics getPositionMetrics();

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(String,
     *      PropertyChangeListener)
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(String,
     *      PropertyChangeListener)
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}