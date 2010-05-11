package org.marketcetera.trade;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an option.
 * <p>
 * Note that if the option expiry specifies the expiry date in the format
 * YYYYMM, an augmented form of the expiry is also stored as an attribute
 * of the option. The {@link #getAugmentedExpiry() augmented expiry} is used
 * for {@link #hashCode()} and {@link #equals(Object)} methods in preference
 * to the specified {@link #getExpiry() expiry}.
 * <p>
 * See {@link OptionUtils#normalizeEquityOptionExpiry(String)} for details
 * on how the expiry value is augmented. 
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Option extends ExpirableInstrument {

    private final OptionType mType;

    private final String mAugmentedExpiry;

    private final BigDecimal mStrikePrice;

    /**
     * Constructor. Note that trailing zeros are stripped from strikePrice.
     * 
     * @param symbol
     *            the option root symbol
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
        super(symbol,
              expiry);
        Validate.notNull(type);
        Validate.notNull(strikePrice);
        mType = type;
        mAugmentedExpiry = OptionUtils.normalizeEquityOptionExpiry(getExpiry());
        strikePrice = strikePrice.stripTrailingZeros();
        if(strikePrice.scale() < 0) {
            //reset the scale if the number is a multiple of 10
            strikePrice = strikePrice.setScale(0);
        }
        mStrikePrice = strikePrice;
    }

    /**
     * Parameterless constructor for use only by JAXB.
     */
    protected Option() {
        mType = null;
        mAugmentedExpiry = null;
        mStrikePrice = null;
    }

    /**
     * Always returns {@link SecurityType#Option}.
     *
     * @return {@link SecurityType#Option}
     */
    @Override
    public SecurityType getSecurityType() {
        return SecurityType.Option;
    }


    /**
     * Returns the augmented option expiry.
     * <p>
     * The augmented expiry is available if the supplied option
     * expiry doesn't include the expiry day, otherwise it is null.
     *
     * @return the augmented expiry, if the expiry was augmented, null otherwise.
     * 
     * @see OptionUtils#normalizeEquityOptionExpiry(String) 
     */
    public String getAugmentedExpiry() {
        return mAugmentedExpiry;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (mAugmentedExpiry == null
                ? getExpiry().hashCode()
                : mAugmentedExpiry.hashCode());
        result = prime * result + mStrikePrice.hashCode();
        result = prime * result + getSymbol().hashCode();
        result = prime * result + mType.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Option)) {
            return false;
        }
        Option other = (Option) obj;
        String expiry = mAugmentedExpiry == null
                ? getExpiry()
                : mAugmentedExpiry;
        String otherExpiry = other.mAugmentedExpiry == null
                ? other.getExpiry()
                : other.mAugmentedExpiry;
        return getSymbol().equals(other.getSymbol()) && mType.equals(other.mType)
                && mStrikePrice.equals(other.mStrikePrice)
                && expiry.equals(otherExpiry);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("symbol", getSymbol()) //$NON-NLS-1$
                .append("type", mType) //$NON-NLS-1$
                .append("expiry", getExpiry()) //$NON-NLS-1$
                .append("strikePrice", mStrikePrice); //$NON-NLS-1$
        if(mAugmentedExpiry != null) {
            builder.append("augmentedExpiry", mAugmentedExpiry);  //$NON-NLS-1$
        }
        return builder.toString();
    }
    private static final long serialVersionUID = 2L;
}
