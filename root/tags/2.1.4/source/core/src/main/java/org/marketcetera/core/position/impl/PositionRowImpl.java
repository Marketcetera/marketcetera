package org.marketcetera.core.position.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * Simple implementation of {@link PositionRow}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
class PositionRowImpl implements PositionRow {

    private final Instrument mInstrument;
    private final String mUnderlying;
    private final String mAccount;
    private final String mTraderId;
    private final Grouping[] mGrouping;
    private final EventList<PositionRow> mChildren;
    private volatile PositionMetrics mPositionMetrics;

    /**
     * Convenience constructor for summary position rows.
     * 
     * @param instrument
     *            the instrument
     * @param underlying
     *            the underlying symbol
     * @param account
     *            the account
     * @param trader
     *            the trader
     * @param grouping
     *            the grouping
     * @param children
     *            the children
     */
    PositionRowImpl(Instrument instrument, String underlying, String account,
            String trader, Grouping[] grouping, EventList<PositionRow> children) {
        this(instrument, underlying, account, trader, grouping, children,
                new PositionMetricsImpl());
    }

    /**
     * Convenience constructor when only the incoming position is known.
     * 
     * @param instrument
     *            the instrument
     * @param underlying
     *            the underlying symbol
     * @param account
     *            the account
     * @param trader
     *            the trader
     * @param incomingPosition
     *            the incoming position
     * @throws IllegalArgumentException
     *             if incomingPosition is null
     */
    PositionRowImpl(Instrument instrument, String underlying, String account,
            String trader, BigDecimal incomingPosition) {
        this(instrument, underlying, account, trader, new PositionMetricsImpl(
                incomingPosition));
    }

    /**
     * Convenience constructor.
     * 
     * @param instrument
     *            the instrument
     * @param underlying
     *            the underlying symbol
     * @param account
     *            the account
     * @param trader
     *            the trader
     * @param metrics
     *            the position metrics
     * @throws IllegalArgumentException
     *             if metrics is null
     */
    PositionRowImpl(Instrument instrument, String underlying, String account,
            String trader, PositionMetrics metrics) {
        this(instrument, underlying, account, trader, null, null, metrics);
    }

    /**
     * Constructor.
     * 
     * @param instrument
     *            the instrument
     * @param underlying
     *            the underlying symbol
     * @param account
     *            the account
     * @param trader
     *            the trader
     * @param grouping
     *            the grouping
     * @param children
     *            the children
     * @param metrics
     *            the position metrics
     * @throws IllegalArgumentException
     *             if metrics is null
     */
    PositionRowImpl(Instrument instrument, String underlying, String account,
            String trader, Grouping[] grouping,
            EventList<PositionRow> children, PositionMetrics metrics) {
        Validate.notNull(metrics);
        mInstrument = instrument;
        mUnderlying = underlying;
        mAccount = account;
        mTraderId = trader;
        mGrouping = grouping;
        mChildren = children;
        mPositionMetrics = metrics;
    }

    @Override
    public Instrument getInstrument() {
        return mInstrument;
    }

    @Override
    public String getUnderlying() {
        return mUnderlying;
    }

    @Override
    public String getAccount() {
        return mAccount;
    }

    @Override
    public String getTraderId() {
        return mTraderId;
    }

    @Override
    public Grouping[] getGrouping() {
        return mGrouping;
    }

    @Override
    public PositionMetrics getPositionMetrics() {
        return mPositionMetrics;
    }

    /**
     * Set to the position metrics to a new value. For thread safety, the
     * supplied PositionMetrics object must be immutable.
     * 
     * @param positionMetrics
     *            the new position metrics, must be immutable
     * @throws IllegalArgumentException
     *             if positionMetrics is null
     */
    void setPositionMetrics(PositionMetrics positionMetrics) {
        Validate.notNull(positionMetrics);
        propertyChangeSupport.firePropertyChange("positionMetrics", //$NON-NLS-1$
                mPositionMetrics, mPositionMetrics = positionMetrics);
    }

    @Override
    public EventList<PositionRow> getChildren() {
        return mChildren;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendToString(mInstrument.toString())
                .append("underlying", mUnderlying) //$NON-NLS-1$
                .append("account", mAccount) //$NON-NLS-1$
                .append("traderId", mTraderId) //$NON-NLS-1$
                .append("grouping", mGrouping) //$NON-NLS-1$
                .toString();
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
