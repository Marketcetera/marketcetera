package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class represents an option event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OptionEvent
        extends HasUnderlyingEquity, HasOption
{
    /**
     * Gets the expiry of the event option.
     *
     * @return a <code>String</code> value
     */
    public String getExpiry();
    /**
     * Gets the strike of the event.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrike();
    /**
     * Gets the option type of the event.
     *
     * @return an <code>OptionType</code> value
     */
    public OptionType getOptionType();
    /**
     * Gets the expiration type of the event. 
     *
     * @return an <code>ExpirationType</code> value
     */
    public ExpirationType getExpirationType();
    /**
     * Gets the multiplier value of the option event.
     *
     * @return an <code>int</code> value
     */
    public int getMultiplier();
    /**
     * Indicates if the option event has deliverables. 
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasDeliverable();
}
