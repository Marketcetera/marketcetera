package org.marketcetera.core.position.impl;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.Instrument;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link PositionKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PositionKeyImpl<T extends Instrument> implements PositionKey<T> {

    @XmlAnyElement(lax=true)
    private final T mInstrument;
    private final String mAccount;
    private final String mTraderId;

    /**
     * Constructor.
     * 
     * @param symbol
     *            symbol, cannot be null or empty
     * @param account
     *            account
     * @param traderId
     *            trader id
     * @throws IllegalArgumentException
     *             if instrument is null
     */
    public PositionKeyImpl(T instrument, @Nullable String account,
            @Nullable String traderId) {
        Validate.notNull(instrument);
        mInstrument = instrument;
        mAccount = account;
        mTraderId = traderId;
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected PositionKeyImpl() {
        mInstrument = null;
        mAccount = null;
        mTraderId = null;
    }

    @Override
    public T getInstrument() {
        return mInstrument;
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
    public int hashCode() {
        return new HashCodeBuilder().append(mInstrument).append(mAccount)
                .append(mTraderId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PositionKeyImpl<?> other = (PositionKeyImpl<?>) obj;
        return new EqualsBuilder().append(mInstrument, other.mInstrument)
                .append(mAccount, other.mAccount).append(mTraderId,
                        other.mTraderId).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendToString(mInstrument.toString()) //$NON-NLS-1$
                .append("account", mAccount) //$NON-NLS-1$
                .append("traderId", mTraderId) //$NON-NLS-1$
                .toString();
    }

    @Override
    public int compareTo(PositionKey<?> o) {
        /*
         * The order here affects the default order of positions returned by the
         * engine.
         */
        return new CompareToBuilder().append(mTraderId, o.getTraderId())
                .append(mInstrument, o.getInstrument()).append(mAccount,
                        o.getAccount()).toComparison();
    }
}
