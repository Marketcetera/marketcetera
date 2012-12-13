package org.marketcetera.core.trade;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.options.OptionUtils;

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
 * See {@link org.marketcetera.core.options.OptionUtils#normalizeEquityOptionExpiry(String)} for details
 * on how the expiry value is augmented. 
 * 
 * @version $Id: OptionImpl.java 16327 2012-10-26 21:14:08Z colin $
 * @since 2.0.0
 */
@ThreadSafe
@XmlRootElement(name="option")
@XmlAccessorType(XmlAccessType.NONE)
public class Option
        extends Instrument
{
    /**
     * Constructor. Note that trailing zeros are stripped from strikePrice.
     * 
     * @param inSymbol the option root symbol
     * @param inExpiry the option expiry
     * @param inStrikePrice the option strike price
     * @param inType the option type
     * @throws IllegalArgumentException if any argument is null, or if symbol or expiry is whitespace
     */
    public Option(String inSymbol,
                  String inExpiry,
                  BigDecimal inStrikePrice,
                  OptionType inType)
    {
        super(inSymbol);
        inSymbol = StringUtils.trimToNull(inSymbol);
        inExpiry = StringUtils.trimToNull(inExpiry);
        Validate.notNull(inSymbol);
        Validate.notNull(inType);
        Validate.notNull(inExpiry);
        Validate.notNull(inStrikePrice);
        mType = inType;
        mExpiry = inExpiry;
        String tmpAugmentedExpiry = null;
        try {
            tmpAugmentedExpiry = new OptionUtils().normalizeEquityOptionExpiry(mExpiry);
            if(mExpiry.equals(tmpAugmentedExpiry)) {
                tmpAugmentedExpiry = null;
            }
        } catch (IllegalArgumentException ignored) {}
        mAugmentedExpiry = tmpAugmentedExpiry;
        inStrikePrice = inStrikePrice.stripTrailingZeros();
        if(inStrikePrice.scale() < 0) {
            //reset the scale if the number is a multiple of 10
            inStrikePrice = inStrikePrice.setScale(0);
        }
        mStrikePrice = inStrikePrice;
    }
    /**
     * Always returns {@link SecurityType#Option}.
     *
     * @return {@link SecurityType#Option}
     */
    public SecurityType getSecurityType() {
        return SecurityType.Option;
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
                ? mExpiry.hashCode()
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
                ? mExpiry
                : mAugmentedExpiry;
        String otherExpiry = other.mAugmentedExpiry == null
                ? other.mExpiry
                : other.mAugmentedExpiry;
        return getSymbol().equals(other.getSymbol()) && mType.equals(other.mType)
                && mStrikePrice.equals(other.mStrikePrice)
                && expiry.equals(otherExpiry);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Option[symbol=").append(getSymbol()).append(",type=").append(mType).append(",expiry=").append(mExpiry)
                .append(",strikePrice=").append(mStrikePrice);
        if(mAugmentedExpiry != null) {
            builder.append(",augmentedExpiry=").append(mAugmentedExpiry);  //$NON-NLS-1$
        }
        builder.append("]");
        return builder.toString();
    }
    /**
     * Parameterless constructor for use only by JAXB.
     */
    @SuppressWarnings("unused")
    private Option()
    {
        mType = null;
        mExpiry = null;
        mAugmentedExpiry = null;
        mStrikePrice = null;
    }
    /**
     * option type value
     */
    @XmlAttribute
    private final OptionType mType;
    /**
     * expiry value
     */
    @XmlAttribute
    private final String mExpiry;
    /**
     * augmented expiry value
     */
    @XmlAttribute
    private final String mAugmentedExpiry;
    /**
     * strike price value
     */
    @XmlAttribute
    private final BigDecimal mStrikePrice;
    private static final long serialVersionUID = 1L;
}
