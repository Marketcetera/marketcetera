package org.marketcetera.core.position.impl;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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
@XmlAccessorType(XmlAccessType.FIELD)
public class PositionKeyImpl implements PositionKey {

    private final String mSymbol;
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
     *             if symbol is null or empty
     */
    public PositionKeyImpl(String symbol, String account, String traderId) {
        Validate.notEmpty(symbol);
        mSymbol = symbol;
        mAccount = account;
        mTraderId = traderId;
    }

    /**
     * Creates a new ID. This empty constructor is intended for use
     * by JAXB.
     */

    protected PositionKeyImpl() {
        mSymbol = null;
        mAccount = null;
        mTraderId = null;
    }

    @Override
    public String getSymbol() {
        return mSymbol;
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
        return new HashCodeBuilder().append(mSymbol).append(mAccount).append(mTraderId)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PositionKeyImpl other = (PositionKeyImpl) obj;
        return new EqualsBuilder().append(mSymbol, other.mSymbol).append(mAccount, other.mAccount)
                .append(mTraderId, other.mTraderId).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", mSymbol) //$NON-NLS-1$
                .append("account", mAccount) //$NON-NLS-1$
                .append("traderId", mTraderId) //$NON-NLS-1$
                .toString();
    }
}
