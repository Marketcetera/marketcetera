package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.AbstractInstrument;
import org.marketcetera.core.position.Instrument;
import org.marketcetera.core.position.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link Option}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OptionImpl extends AbstractInstrument implements Option {

    private static final int TYPE_ID = 20;

    private final String mSymbol;

    private final Type mType;

    private final String mExpiry;

    private final BigDecimal mStrikePrice;

    /**
     * Constructor.
     * 
     * @param symbol
     *            the option symbol
     * @param type
     *            the option type
     * @param expiry
     *            the option expiry
     * @param strikePrice
     *            the option strike price
     * @throws IllegalArgumentException
     *             if any argument is null, or if symbol or expiry is empty
     */
    public OptionImpl(String symbol, Type type, String expiry,
            BigDecimal strikePrice) {
        Validate.notEmpty(symbol);
        Validate.notNull(type);
        Validate.notEmpty(expiry);
        Validate.notNull(strikePrice);
        mSymbol = symbol;
        mType = type;
        mExpiry = expiry;
        mStrikePrice = strikePrice;
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected OptionImpl() {
        mSymbol = null;
        mType = null;
        mExpiry = null;
        mStrikePrice = null;
    }

    @Override
    public String getUnderlying() {
        return mSymbol;
    }

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getSymbol() {
        return mSymbol;
    }

    @Override
    public Type getType() {
        return mType;
    }

    @Override
    public String getExpiry() {
        return mExpiry;
    }

    @Override
    public BigDecimal getStrikePrice() {
        return mStrikePrice;
    }

    @Override
    protected void enhanceHashCode(HashCodeBuilder builder) {
        /*
         * Trailing zeros are stripped from the strike price so that the hash
         * code will be consistent with equals.
         */
        builder.append(mSymbol).append(mType).append(mExpiry).append(
                mStrikePrice.stripTrailingZeros());
    }

    @Override
    protected void refineCompareTo(CompareToBuilder builder,
            Instrument otherInstrument) {
        Option otherOption = (Option) otherInstrument;
        /*
         * Note: using BigDecimal's compareTo here means that strike price
         * equality is determined by the value alone and not the scale. Since
         * this method is used for equality tests, two Options may be equal even
         * if their strike prices are unequal.
         */
        builder.append(mSymbol, otherOption.getSymbol()).append(mType,
                otherOption.getType()).append(mExpiry, otherOption.getExpiry())
                .append(mStrikePrice, otherOption.getStrikePrice());
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
