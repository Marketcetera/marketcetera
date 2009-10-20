package org.marketcetera.event.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link OptionEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class OptionBean
        implements Serializable
{
    /**
     * Gets the instrument.
     *
     * @return an <code>Option</code> value
     */
    public final Option getInstrument()
    {
        return (Option)instrument.getInstrument();
    }
    /**
     * Sets the instrument.
     *
     * @param inOption an <code>Option</code> value
     */
    public final void setInstrument(Option inOption)
    {
        instrument.setInstrument(inOption);
    }
    /**
     * Get the underlyingEquity value.
     *
     * @return a <code>Equity</code> value
     */
    public final Equity getUnderlyingEquity()
    {
        return underlyingEquity;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     */
    public final void setUnderlyingEquity(Equity inUnderlyingEquity)
    {
        underlyingEquity = inUnderlyingEquity;
    }
    /**
     * Get the expiry value.
     *
     * @return a <code>String</code> value
     */
    public final String getExpiry()
    {
        return expiry;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     */
    public final void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Get the strike value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getStrike()
    {
        return strike;
    }
    /**
     * Sets the strike value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setStrike(BigDecimal inStrike)
    {
        strike = inStrike;
    }
    /**
     * Get the optionType value.
     *
     * @return a <code>OptionType</code> value
     */
    public final OptionType getOptionType()
    {
        return optionType;
    }
    /**
     * Sets the optionType value.
     *
     * @param a <code>OptionType</code> value
     */
    public final void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Get the expirationType value.
     *
     * @return a <code>ExpirationType</code> value
     */
    public final ExpirationType getExpirationType()
    {
        return expirationType;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     */
    public final void setExpirationType(ExpirationType inExpirationType)
    {
        expirationType = inExpirationType;
    }
    /**
     * Get the multiplier value.
     *
     * @return a <code>int</code> value
     */
    public final int getMultiplier()
    {
        return multiplier;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     */
    public final void setMultiplier(int inMultiplier)
    {
        multiplier = inMultiplier;
    }
    /**
     * Get the hasDeliverable value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean hasDeliverable()
    {
        return hasDeliverable;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     */
    public final void setHasDeliverable(boolean inHasDeliverable)
    {
        hasDeliverable = inHasDeliverable;
    }
    /**
     * the underlying equity for the option
     */
    private volatile Equity underlyingEquity;
    /**
     * the expiry for the option - format is dependent on the market data provider
     */
    private volatile String expiry;
    /**
     * the strike of the option
     */
    private volatile BigDecimal strike;
    /**
     * the type of the option
     */
    private volatile OptionType optionType;
    /**
     * the expiration type of the option
     */
    private volatile ExpirationType expirationType;
    /**
     * the multiplier of the option
     */
    private volatile int multiplier;
    /**
     * indicates if the option includes deliverables
     */
    private volatile boolean hasDeliverable;
    /**
     * the instrument of the option
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private static final long serialVersionUID = 1L;
}
