package org.marketcetera.core.position.impl;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.Instrument;
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

    @XmlElementRef
    private final T mInstrument;
    private final String mAccount;
    private final String mTraderId;

    /**
     * Constructor. Note that account and traderId are converted to null if they
     * only contain whitespace.
     * 
     * @param instrument
     *            instrument, cannot be null
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
        mAccount = StringUtils.trimToNull(account);
        mTraderId = StringUtils.trimToNull(traderId);
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
}
