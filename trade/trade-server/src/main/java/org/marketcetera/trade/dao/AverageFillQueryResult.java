package org.marketcetera.trade.dao;

import java.math.BigDecimal;

import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;

/* $License$ */

/**
 * Holds the direct results of {@link ExecutionReportDao#findAverageFillPrice(java.util.Set, org.springframework.data.domain.Pageable)}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AverageFillQueryResult
{
    /**
     * Get the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Sets the symbol value.
     *
     * @param inSymbol a <code>String</code> value
     */
    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /**
     * Get the securityType value.
     *
     * @return a <code>SecurityType</code> value
     */
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /**
     * Sets the securityType value.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     */
    public void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /**
     * Get the expiry value.
     *
     * @return a <code>String</code> value
     */
    public String getExpiry()
    {
        return expiry;
    }
    /**
     * Sets the expiry value.
     *
     * @param inExpiry a <code>String</code> value
     */
    public void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Get the strikePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /**
     * Sets the strikePrice value.
     *
     * @param inStrikePrice a <code>BigDecimal</code> value
     */
    public void setStrikePrice(BigDecimal inStrikePrice)
    {
        strikePrice = inStrikePrice;
    }
    /**
     * Get the optionType value.
     *
     * @return a <code>OptionType</code> value
     */
    public OptionType getOptionType()
    {
        return optionType;
    }
    /**
     * Sets the optionType value.
     *
     * @param inOptionType a <code>OptionType</code> value
     */
    public void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    public Side getSide()
    {
        return side;
    }
    /**
     * Sets the side value.
     *
     * @param inSide a <code>Side</code> value
     */
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /**
     * Get the weightedAveragePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getWeightedAveragePrice()
    {
        return weightedAveragePrice;
    }
    /**
     * Sets the weightedAveragePrice value.
     *
     * @param inWeightedAveragePrice a <code>BigDecimal</code> value
     */
    public void setWeightedAveragePrice(BigDecimal inWeightedAveragePrice)
    {
        weightedAveragePrice = inWeightedAveragePrice;
    }
    /**
     * Get the cumulativeQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /**
     * Sets the cumulativeQuantity value.
     *
     * @param inCumulativeQuantity a <code>BigDecimal</code> value
     */
    public void setCumulativeQuantity(BigDecimal inCumulativeQuantity)
    {
        cumulativeQuantity = inCumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AverageFillQueryResult [symbol=").append(symbol).append(", securityType=").append(securityType)
                .append(", expiry=").append(expiry).append(", strikePrice=").append(strikePrice).append(", optionType=")
                .append(optionType).append(", side=").append(side).append(", weightedAveragePrice=")
                .append(weightedAveragePrice).append(", cumulativeQuantity=").append(cumulativeQuantity).append("]");
        return builder.toString();
    }
    /**
     * Create a new AverageFillQueryResult instance.
     *
     * @param inSymbol
     * @param inSecurityType
     * @param inExpiry
     * @param inStrikePrice
     * @param inOptionType
     * @param inSide
     * @param inWeightedAveragePrice
     * @param inCumulativeQuantity
     */
    public AverageFillQueryResult(String inSymbol,
                                  SecurityType inSecurityType,
                                  String inExpiry,
                                  BigDecimal inStrikePrice,
                                  OptionType inOptionType,
                                  Side inSide,
                                  BigDecimal inWeightedAveragePrice,
                                  BigDecimal inCumulativeQuantity)
    {
        symbol = inSymbol;
        securityType = inSecurityType;
        expiry = inExpiry;
        strikePrice = inStrikePrice;
        optionType = inOptionType;
        side = inSide;
        weightedAveragePrice = inWeightedAveragePrice;
        cumulativeQuantity = inCumulativeQuantity;
    }
    /**
     * Create a new AverageFillQueryResult instance.
     */
    public AverageFillQueryResult() {}
    private String symbol;
    private SecurityType securityType;
    private String expiry;
    private BigDecimal strikePrice;
    private OptionType optionType;
    private Side side;
    private BigDecimal weightedAveragePrice;
    private BigDecimal cumulativeQuantity;
}
