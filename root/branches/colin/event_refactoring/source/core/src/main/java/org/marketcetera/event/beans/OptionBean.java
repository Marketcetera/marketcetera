package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public final class OptionBean
{
    /**
     * Get the underlyingEquity value.
     *
     * @return a <code>Equity</code> value
     */
    public Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     */
    public void setUnderlyingEquity(Equity inUnderlyingEquity)
    {
        underlyingEquity = inUnderlyingEquity;
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
     * @param a <code>String</code> value
     */
    public void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Get the strike value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrike()
    {
        return strike;
    }
    /**
     * Sets the strike value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setStrike(BigDecimal inStrike)
    {
        strike = inStrike;
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
     * @param a <code>OptionType</code> value
     */
    public void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Get the expirationType value.
     *
     * @return a <code>ExpirationType</code> value
     */
    public ExpirationType getExpirationType()
    {
        return expirationType;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     */
    public void setExpirationType(ExpirationType inExpirationType)
    {
        expirationType = inExpirationType;
    }
    /**
     * Get the multiplier value.
     *
     * @return a <code>int</code> value
     */
    public int getMultiplier()
    {
        return multiplier;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     */
    public void setMultiplier(int inMultiplier)
    {
        multiplier = inMultiplier;
    }
    /**
     * Get the hasDeliverable value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getHasDeliverable()
    {
        return hasDeliverable;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     */
    public void setHasDeliverable(boolean inHasDeliverable)
    {
        hasDeliverable = inHasDeliverable;
    }
    /**
     * 
     */
    private Equity underlyingEquity;
    /**
     * 
     */
    private String expiry;
    /**
     * 
     */
    private BigDecimal strike;
    /**
     * 
     */
    private OptionType optionType;
    /**
     * 
     */
    private ExpirationType expirationType;
    /**
     * 
     */
    private int multiplier;
    /**
     * 
     */
    private boolean hasDeliverable;
}
