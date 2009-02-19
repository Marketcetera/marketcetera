package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRowBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple implementation of {@link PositionRowBase} with property change
 * notification.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class PositionRowBaseImpl implements PositionRowBase {

    private final BigDecimal incomingPosition;
    private volatile PositionMetrics positionMetrics;

    /**
     * Constructor providing the static incoming position.
     * 
     * @param incomingPosition
     *            the incoming position, may not be null
     */
    public PositionRowBaseImpl(BigDecimal incomingPosition) {
        Validate.notNull(incomingPosition);
        this.incomingPosition = incomingPosition;
        this.positionMetrics = new PositionMetricsImpl(incomingPosition, null, null, null, null,
                null);
    }

    @Override
    public BigDecimal getIncomingPosition() {
        return incomingPosition;
    }

    @Override
    public PositionMetrics getPositionMetrics() {
        return positionMetrics;
    }

    /**
     * Set to the position metrics to a new value. For thread safety, the
     * supplied PositionMetrics object must be immutable.
     * 
     * @param positionMetrics
     *            the new position metrics, must be immutable
     */
    void setPositionMetrics(PositionMetrics positionMetrics) {
        Validate.notNull(positionMetrics);
        propertyChangeSupport.firePropertyChange("positionMetrics", //$NON-NLS-1$
                this.positionMetrics, this.positionMetrics = positionMetrics);
    }

    // Boiler plate property change code

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * @return {@link PropertyChangeSupport} for this class
     */
    protected PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
    }
}
