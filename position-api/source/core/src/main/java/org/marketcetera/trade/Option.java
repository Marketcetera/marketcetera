package org.marketcetera.trade;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an option.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Option implements Instrument {

    private final String mSymbol;

    private final OptionType mType;

    private final String mExpiry;

    private final BigDecimal mStrikePrice;

    /**
     * Constructor. Note that trailing zeros are stripped from strikePrice.
     * 
     * @param symbol
     *            the option symbol
     * @param expiry
     *            the option expiry
     * @param strikePrice
     *            the option strike price
     * @param type
     *            the option type
     * @throws IllegalArgumentException
     *             if any argument is null, or if symbol or expiry is whitespace
     */
    public Option(String symbol, String expiry, BigDecimal strikePrice,
            OptionType type) {
        symbol = StringUtils.trimToNull(symbol);
        expiry = StringUtils.trimToNull(expiry);
        Validate.notNull(symbol);
        Validate.notNull(type);
        Validate.notNull(expiry);
        Validate.notNull(strikePrice);
        mSymbol = symbol;
        mType = type;
        mExpiry = expiry;
        mStrikePrice = strikePrice.stripTrailingZeros();
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected Option() {
        mSymbol = null;
        mType = null;
        mExpiry = null;
        mStrikePrice = null;
    }

    /**
     * Returns the option root symbol.
     * 
     * @return the option root symbol, never null
     */
    @Override
    public String getSymbol() {
        return mSymbol;
    }

    /**
     * Returns the option expiry.
     * 
     * @return the option expiry, never null
     */
    public String getExpiry() {
        return mExpiry;
    }

    /**
     * Returns the option strike price.
     * 
     * @return the option strike price, never null
     */
    public BigDecimal getStrikePrice() {
        return mStrikePrice;
    }

    /**
     * Returns the option type.
     * 
     * @return the option type, never null
     */
    public OptionType getType() {
        return mType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Option))
            return false;
        Option otherOption = (Option) obj;
        /*
         * Note: strike price equality is determined by the value alone and not
         * the scale. Thus two Options may be equal even if their strike prices
         * are technically unequal.
         */
        return new EqualsBuilder().append(mSymbol, otherOption.getSymbol())
                .append(mType, otherOption.getType()).append(mExpiry,
                        otherOption.getExpiry()).append(mStrikePrice,
                        otherOption.getStrikePrice().stripTrailingZeros())
                .isEquals();
    }

    @Override
    public int hashCode() {
        /*
         * Trailing zeros are stripped from the strike price so that the hash
         * code will be consistent with equals.
         */
        return new HashCodeBuilder().append(mSymbol).append(mType).append(
                mExpiry).append(mStrikePrice).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", mSymbol) //$NON-NLS-1$
                .append("type", mType) //$NON-NLS-1$
                .append("expiry", mExpiry) //$NON-NLS-1$
                .append("strikePrice", mStrikePrice) //$NON-NLS-1$
                .toString();
    }
}
